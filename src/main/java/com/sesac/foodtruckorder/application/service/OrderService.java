package com.sesac.foodtruckorder.application.service;

import com.sesac.foodtruckorder.exception.OrderException;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderItemRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepositoryCustom;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.*;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.global.Result;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.StoreClient;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.UserClient;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.OrderItemRequestDto;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderItemResponseDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final Response response;
    private final UserClient userClient;
    private final StoreClient storeClient;

    /**
     * 장바구니 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
     **/
    public OrderItemResponseDto.ResCartListDto fetchOrder(HttpServletRequest request, Long userId) {

        String authorization = request.getHeader("Authorization");

        // 1. order 정보 조회
        Order findOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING).orElseThrow(
                () -> new OrderException("장바구니 정보를 찾을 수 없습니다")
        );

        // 2. feign 통신, store 정보 조회
        GetStoreResponse findStore = storeClient.getStore(authorization, String.valueOf(findOrder.getStoreId())).getData();

        // 3. feign 통신 item 정보 조회
        List<GetItemsInfoDto> data = storeClient.getItem(authorization, findOrder.getOrderItems().stream()
                .map(orderItem -> orderItem.getItemId())
                .filter(obj -> Objects.nonNull(obj))
                .collect(Collectors.toUnmodifiableList())
        ).getData();

        Map<Long, String> itemMap = data.stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(), getItemsInfoDto -> getItemsInfoDto.getItemName())
                );

        Map<Long, String> itemImgMap = data.stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(), getItemsInfoDto -> getItemsInfoDto.getItemImgUrl())
                );

        Map<Long, Long> itemUnitPriceMap = data.stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(), getItemsInfoDto -> getItemsInfoDto.getItemPrice())
                );

        // 4. OrderItem정보 조회
        List<OrderItemResponseDto.FetchOrderDto> results = findOrder.getOrderItems()
                .stream()
                .map(orderItem ->
                        OrderItemResponseDto.FetchOrderDto.of(
                                itemMap.get(orderItem.getItemId()),
                                itemImgMap.get(orderItem.getItemId()),
                                itemUnitPriceMap.get(orderItem.getItemId()),
                                orderItem))
                .collect(Collectors.toList());

        OrderItemResponseDto.ResCartListDto resCartListDto = new OrderItemResponseDto.ResCartListDto(findStore.getStoreName(), results);

        return resCartListDto;
    }

    /**
     * 장바구니에 아이템 담기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Transactional
    public void addItemToCart(OrderItemRequestDto.OrderItemDto orderItemDto, Long storeId, Long userId) {

        // OrderItem Entity생성
        OrderItem orderItem = OrderItem.of(orderItemDto.getItemId(), orderItemDto.getUnitPrice(), orderItemDto.getCount());

        // 사용자에게 장바구니가 2개이상인지 체크
        Long cartStatus = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if (cartStatus >= 2) throw new OrderException("장바구니 데이터는 2건 이상일 수 없습니다.");

        // 장바구니에 여러 푸드트럭의 메뉴를 담을 순 없음
        // 1. Pending 상태의 정보를 갖고옴
        Optional<Order> order = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        // 2. Pending 상태의 장바구니가 존재한다면,
        if (order.isPresent()) {
            // 3. 장바구니 상태의 주문의 정보 중 storeId가 인자로 받은 storeID와 같은지 파악 => 동일한 푸드트럭
            if (!order.get().addOrderItem(orderItem).getStoreId().equals(storeId)) {
                // 4. 같지 않다면 에러 처리
                throw new OrderException("장바구니에 여러 푸드트럭의 메뉴를 담을 수 없습니다.");
            }
        } else {
            // 5. 같다면 장바구니에 아이템 저장
            orderRepository.save(Order.of(userId, storeId, OrderStatus.PENDING, orderItem));
        }
    }

    /**
     * 장바구니 수량 조절
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Transactional
    public void countControl(Long orderId, Long orderItemId, boolean plusMinus) {

        Order findOrder = orderRepository.findById(orderId).get();
        OrderItem findOrderItem = findOrder.getOrderItems().stream()
                .filter(orderItem -> orderItem.getId().equals(orderItemId))
                .findFirst().get();

        long price = findOrderItem.getPrice();
        int count = findOrderItem.getCount();

        int cal = (plusMinus == true) ? 1 : -1;

        getCount(cal, findOrder, findOrderItem, price, count);
    }

    private void getCount(int cal, Order findOrder, OrderItem findOrderItem, long price, int count) {
        count += cal;
        long plusPrice = price * 1;
        findOrderItem.changeCount(count);
        findOrder.changeOrderPrice(cal, plusPrice);
    }

    /**
     * 주문 내역 조회 (사용자)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    public Page<OrderResponseDto.OrderHistory> findOrderHistory(Pageable pageable, HttpServletRequest request, Long userId) {
        String authorization = request.getHeader("Authorization");
        // 주문 정보 조회 - 페이징
//        Page<Order> findOrder = orderRepository.findByUserIdAndPaging(pageable, userId, OrderStatus.PENDING);
        // 주문 내역 조회 - QueryDsl - refactoring
        Page<Order> findOrder = orderRepositoryCustom.findOrderHistory(pageable, userId);

        // 주문 정보 dto 변환
        List<OrderResponseDto.OrderHistory> orderHistoryList = findOrder.getContent().stream()
                .map(order -> OrderResponseDto.OrderHistory.of(order))
                .collect(Collectors.toList());

        Set<Long> storeIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        // 주문한 storeId, itemId set객체에 저장

        for (OrderResponseDto.OrderHistory orderHistory : orderHistoryList) {
            storeIds.add(orderHistory.getStoreId());
            for (OrderResponseDto._OrderItems orderItems : orderHistory.getOrderItems()) {
                itemIds.add(orderItems.getItemId());
            }
        }

        // store, item 정보 추출, storeName, itemName
        List<GetStoreResponse> data = storeClient.getStoreNameImageMap(authorization, storeIds).getData();

        Map<Long, String> storeNameMap = data.stream().collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId()
                        , getStoreResponse -> getStoreResponse.getStoreName())
        );
        Map<Long, String> storeImgMap = data.stream().collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(),
                        getStoreResponse -> getStoreResponse.getImgUrl())
        );

//        Map<Long, String> storeNameMap = storeClient.getStoreInfoMap(request, storeIds);            // 가게 정보 조회(StoreName)
//        Map<Long, String> storeImgaeMap = storeClient.getStoreImageInfoMap(request, storeIds);      // 가게 정보 조회(StoreImageUrl)
//        Result<List<GetItemsInfoDto>> itemNameMap = storeClient.getItem(authorization, itemIds);    // 아이템 정보 조회(itemName)
        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, itemIds);// 아이템 정보 조회(itemName)

        for (OrderResponseDto.OrderHistory orderHistory : orderHistoryList) {
            String storeName = storeNameMap.get(orderHistory.getStoreId());
            String storeImgUrl = storeImgMap.get(orderHistory.getStoreId());
            orderHistory.changeStoreName(storeName);
            orderHistory.changeStoreImgUrl(storeImgUrl);
            for (OrderResponseDto._OrderItems orderItems : orderHistory.getOrderItems()) {
                orderItems.changeItemName(itemInfoMap.get(orderItems.getItemId()));
            }
        }

        return PageableExecutionUtils.getPage(orderHistoryList, pageable, () -> findOrder.getTotalElements());
//        return orderHistoryList;
    }

    /**
     * 주문 조회 페이지 (점주)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    public OrderResponseDto.OrderMainDto findOrderMainPage(HttpServletRequest request, OrderRequestDto.OrderSearchCondition condition, Pageable pageable) {
        String authorization = request.getHeader("Authorization");

        Long userId = condition.getUserId();
        LocalDateTime start = condition.getOrderStartTime(); // 영업 시작 시간
        LocalDateTime end = condition.getOrderEndTime();//영업 종료 시간

        // 가게 정보 조회
        GetStoreInfoByUserId storeInfo = storeClient.getStoreInfoByUserId(authorization, userId).getData();

        // 주문 정보 조회 -> Slice
//        List<Order> findOrders = orderRepository.findOrderMainPage(start, end, storeInfo.getStoreId(), OrderStatus.PENDING, pageable).getContent();
        SliceImpl<Order> findOrders = orderRepositoryCustom.findOrderMain(storeInfo.getStoreId(), condition, pageable);

        // 사용자 정보, 아이템 정보 조회
        Set<Long> userIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        OrderResponseDto.OrderMainDto returnDto = OrderResponseDto.OrderMainDto.of(findOrders.getContent(), findOrders.hasNext());
        List<OrderResponseDto._Order> orders = returnDto.getOrders();

        // userId, ItemId set에 추가
        for (OrderResponseDto._Order order : orders) {
            userIds.add(order.getUserId());
            for (OrderResponseDto._OrderItem orderItem : order.getOrderItems()) {
                itemIds.add(orderItem.getItemId());
            }
        }

        // user name 조회, dto에 추가
        Map<Long, String> userNameMap = userClient.getUserNameMap(authorization, userIds);

        // item name 조회, dto에 추가
        Map<Long, String> itemMap = storeClient.getItem(authorization, itemIds).getData().stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(), getItemsInfoDto -> getItemsInfoDto.getItemName())
                );

        for (OrderResponseDto._Order order : orders) {
            order.changeUserName(userNameMap.get(order.getUserId()));
            for (OrderResponseDto._OrderItem orderItem : order.getOrderItems()) {
                orderItem.changeItemName(itemMap.get(orderItem.getItemId()));
            }
        }

        return returnDto;

    }

    /**
     * 이전 주문 내역 조회(점주)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    public Page<OrderResponseDto.PrevOrderDto> findPrevOrderList(HttpServletRequest request,
                                                           OrderRequestDto.PrevOrderSearch prevOrderSearch,
                                                           Pageable pageable) {
        String authorization = request.getHeader("Authorization");
        Long userId = prevOrderSearch.getUserId();
        LocalDateTime startDateTime = prevOrderSearch.getStartDateTime();
        LocalDateTime endDateTime = prevOrderSearch.getEndDateTime();

        // Store 정보 가져오기
        GetStoreInfoByUserId storeInfo = storeClient.getStoreInfoByUserId(authorization, userId).getData();
        Long storeId = storeInfo.getStoreId();

        // 주문 내역을 보여줘야 함
//        List<Order> orderList = orderRepository.findByStoreId(startDateTime, endDateTime, storeId, OrderStatus.PENDING, pageable).getContent();
        Page<Order> orderList = orderRepositoryCustom.findPrevOrderList(prevOrderSearch, pageable, storeId);
        List<OrderResponseDto.PrevOrderDto> prevOrderList = orderList.getContent().stream()
                .map(order -> OrderResponseDto.PrevOrderDto.of(order))
                .collect(Collectors.toList());

        // 상품명, 사용자 닉네임 조회
        Set<Long> userIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        for (OrderResponseDto.PrevOrderDto orderDto : prevOrderList) {
            userIds.add(orderDto.getUserId());
            for (OrderResponseDto.PrevOrderDto._PrevOrderItem prevOrderItem : orderDto.getPrevOrderItems()) {
                itemIds.add(prevOrderItem.getItemId());
            }
        }

        // feign client 사용자 닉네임 조회, feign client 상품명 조회
        Map<Long, String> usersInfoMap = userClient.getUserNameMap(authorization, userIds);
        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, itemIds);

        for (OrderResponseDto.PrevOrderDto prevOrderDto : prevOrderList) {
            prevOrderDto.changeUserName(usersInfoMap.get(prevOrderDto.getUserId()));
            for (OrderResponseDto.PrevOrderDto._PrevOrderItem prevOrderItem : prevOrderDto.getPrevOrderItems()) {
                prevOrderItem.changeItemName(itemInfoMap.get(prevOrderItem.getItemId()));
            }
        }

        return PageableExecutionUtils.getPage(prevOrderList, pageable, () -> orderList.getTotalElements());
//        return prevOrderList;
    }

    /**
     * 주문 상세 내역 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    public OrderResponseDto.OrderDetailDto findOrderDetail(HttpServletRequest request, Long orderId) {
        String authorization = request.getHeader("Authorization");

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(orderId + "는 없는 주문 번호입니다")
        );
        Long userId = findOrder.getUserId();
        // ItemId 수집
        Set<Long> items = new HashSet<>();
        for (OrderItem orderItem : findOrder.getOrderItems()) {
            items.add(orderItem.getItemId());
        }

        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, items);

        // feign client - store domain
        String storeName = storeClient.getStoreInfoByUserId(authorization, userId).getData().getStoreName();

        // feign client - user domain, 주문 고객 조회 용
        CreateUserDto createUserDto = userClient.userInfo(authorization, findOrder.getUserId());
        OrderResponseDto.OrderDetailUser orderDetailUser =
                new OrderResponseDto.OrderDetailUser(createUserDto.getUserId(), createUserDto.getUsername(), createUserDto.getPhoneNum());

        List<OrderResponseDto.OrderDetailItem> orderDetailItems = new ArrayList<>();

        for (OrderItem orderItem :findOrder.getOrderItems()) {
            OrderResponseDto.OrderDetailItem detailItem = OrderResponseDto.OrderDetailItem.of(orderItem);
            detailItem.changeItemName(itemInfoMap.get(detailItem.getItemId()));

            orderDetailItems.add(detailItem);
        }

        return OrderResponseDto.OrderDetailDto.of(findOrder, storeName, orderDetailUser, orderDetailItems);
    }

    /**
     * 주문 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/13
    **/
    @Transactional
    public void saveOrder(Long userId) {
        orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING).orElseThrow(
                () -> new OrderException("장바구니 정보를 찾을 수 없습니다.")
        ).changeOrderStatus();
    }

    /**
     * 점주) 주문 접수
     * 주문 접수 처리 후 store에 대기번호 +1, 주문 테이블에 대기번호 생성
     * @author jaemin
     * @version 1.0.1
     * 작성일 2022/04/15
    **/
    @Transactional
    public void acceptOrder(HttpServletRequest request, OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        String authorization = request.getHeader("Authorization");

        Long orderId = changeOrderStatus.getOrderId();
        Order findOrder = orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ORDER).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeAcceptOrder();

        // 가게 정보 총 대기번호 +1 하고 대기번호를 받아옴
        ResWaitingCount resWaitingCount = storeClient.saveWaitingCount(authorization, findOrder.getStoreId());

        // 받아온 대기번호를 주문에 넣음
        findOrder.changeWaitingCount(resWaitingCount.getCurrentWaitingCount());
    }

    /**
     * 점주) 주문 거절
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    @Transactional
    public void rejectOrder(OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        Long orderId = changeOrderStatus.getOrderId();
        orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ORDER).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeRejectOrder();
    }

    /**
     * 점주) 메뉴 조리 완료
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
     **/
    @Transactional
    public void complete(OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        Long orderId = changeOrderStatus.getOrderId();
        orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ACCEPTED).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeCompleteOrder();
    }

}



