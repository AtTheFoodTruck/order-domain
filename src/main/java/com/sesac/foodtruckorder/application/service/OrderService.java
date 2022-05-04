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

    public OrderItemResponseDto.ResCartListDto fetchOrder(HttpServletRequest request, Long userId) {

        String authorization = request.getHeader("Authorization");

        Order findOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING).orElseThrow(
                () -> new OrderException("장바구니 정보를 찾을 수 없습니다")
        );

        GetStoreResponse findStore = storeClient.getStore(authorization, String.valueOf(findOrder.getStoreId())).getData();

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

    @Transactional
    public void addItemToCart(OrderItemRequestDto.OrderItemDto orderItemDto, Long storeId, Long userId) {

        OrderItem orderItem = OrderItem.of(orderItemDto.getItemId(), orderItemDto.getUnitPrice(), orderItemDto.getCount());

        Long cartStatus = orderRepository.countByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if (cartStatus >= 2) throw new OrderException("장바구니 데이터는 2건 이상일 수 없습니다.");

        Optional<Order> order = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);

        if (order.isPresent()) {
            if (!order.get().addOrderItem(orderItem).getStoreId().equals(storeId)) {
                throw new OrderException("장바구니에 여러 푸드트럭의 메뉴를 담을 수 없습니다.");
            }
        } else {
            orderRepository.save(Order.of(userId, storeId, OrderStatus.PENDING, orderItem));
        }
    }

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

    public Page<OrderResponseDto.OrderHistory> findOrderHistory(Pageable pageable, HttpServletRequest request, Long userId) {
        String authorization = request.getHeader("Authorization");
        Page<Order> findOrder = orderRepositoryCustom.findOrderHistory(pageable, userId);

        List<OrderResponseDto.OrderHistory> orderHistoryList = findOrder.getContent().stream()
                .map(order -> OrderResponseDto.OrderHistory.of(order))
                .collect(Collectors.toList());

        Set<Long> storeIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        for (OrderResponseDto.OrderHistory orderHistory : orderHistoryList) {
            storeIds.add(orderHistory.getStoreId());
            for (OrderResponseDto._OrderItems orderItems : orderHistory.getOrderItems()) {
                itemIds.add(orderItems.getItemId());
            }
        }

        List<GetStoreResponse> data = storeClient.getStoreNameImageMap(authorization, storeIds).getData();

        Map<Long, String> storeNameMap = data.stream().collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId()
                        , getStoreResponse -> getStoreResponse.getStoreName())
        );
        Map<Long, String> storeImgMap = data.stream().filter(obj -> Objects.nonNull(obj.getImgUrl())).collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(),
                        getStoreResponse -> getStoreResponse.getImgUrl())

        );

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
    }

    public OrderResponseDto.OrderMainDto findOrderMainPage(HttpServletRequest request, OrderRequestDto.OrderSearchCondition condition, Pageable pageable) {
        String authorization = request.getHeader("Authorization");

        Long userId = condition.getUserId();
        LocalDateTime start = condition.getOrderStartTime();
        LocalDateTime end = condition.getOrderEndTime();

        GetStoreInfoByUserId storeInfo = storeClient.getStoreInfoByUserId(authorization, userId).getData();

        SliceImpl<Order> findOrders = null;
        try {
            findOrders = orderRepositoryCustom.findOrderMain(storeInfo.getStoreId(), condition, pageable);
        }catch (Exception e){
            throw new OrderException(e.getMessage());
        }

        Set<Long> userIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        OrderResponseDto.OrderMainDto returnDto = OrderResponseDto.OrderMainDto.of(findOrders.getContent(), findOrders.hasNext());
        List<OrderResponseDto._Order> orders = returnDto.getOrders();

        for (OrderResponseDto._Order order : orders) {
            userIds.add(order.getUserId());
            for (OrderResponseDto._OrderItem orderItem : order.getOrderItems()) {
                itemIds.add(orderItem.getItemId());
            }
        }

        Map<Long, String> userNameMap = userClient.getUserNameMap(authorization, userIds);

        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, itemIds);

        for (OrderResponseDto._Order order : orders) {
            order.changeUserName(userNameMap.get(order.getUserId()));
            for (OrderResponseDto._OrderItem orderItem : order.getOrderItems()) {
                orderItem.changeItemName(itemInfoMap.get(orderItem.getItemId()));
            }
        }

        return returnDto;

    }

    public Page<OrderResponseDto.PrevOrderDto> findPrevOrderList(HttpServletRequest request,
                                                           OrderRequestDto.PrevOrderSearch prevOrderSearch,
                                                           Pageable pageable) {
        String authorization = request.getHeader("Authorization");
        Long userId = prevOrderSearch.getUserId();
        LocalDateTime startDateTime = prevOrderSearch.getStartDateTime();
        LocalDateTime endDateTime = prevOrderSearch.getEndDateTime();

        GetStoreInfoByUserId storeInfo = storeClient.getStoreInfoByUserId(authorization, userId).getData();
        Long storeId = storeInfo.getStoreId();

        Page<Order> orderList = orderRepositoryCustom.findPrevOrderList(prevOrderSearch, pageable, storeId);
        List<OrderResponseDto.PrevOrderDto> prevOrderList = orderList.getContent().stream()
                .map(order -> OrderResponseDto.PrevOrderDto.of(order))
                .collect(Collectors.toList());

        Set<Long> userIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();

        for (OrderResponseDto.PrevOrderDto orderDto : prevOrderList) {
            userIds.add(orderDto.getUserId());
            for (OrderResponseDto.PrevOrderDto._PrevOrderItem prevOrderItem : orderDto.getPrevOrderItems()) {
                itemIds.add(prevOrderItem.getItemId());
            }
        }

        Map<Long, String> usersInfoMap = userClient.getUserNameMap(authorization, userIds);
        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, itemIds);

        for (OrderResponseDto.PrevOrderDto prevOrderDto : prevOrderList) {
            prevOrderDto.changeUserName(usersInfoMap.get(prevOrderDto.getUserId()));
            for (OrderResponseDto.PrevOrderDto._PrevOrderItem prevOrderItem : prevOrderDto.getPrevOrderItems()) {
                prevOrderItem.changeItemName(itemInfoMap.get(prevOrderItem.getItemId()));
            }
        }

        return PageableExecutionUtils.getPage(prevOrderList, pageable, () -> orderList.getTotalElements());
    }

    public OrderResponseDto.OrderDetailDto findOrderDetail(HttpServletRequest request, Long orderId) {
        String authorization = request.getHeader("Authorization");

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderException(orderId + "는 없는 주문 번호입니다")
        );
        Long userId = findOrder.getUserId();
        Set<Long> items = new HashSet<>();
        for (OrderItem orderItem : findOrder.getOrderItems()) {
            items.add(orderItem.getItemId());
        }

        Map<Long, String> itemInfoMap = storeClient.getItemInfoMap(request, items);

        String storeName = storeClient.getStoreInfoByUserId(authorization, userId).getData().getStoreName();

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

    @Transactional
    public void saveOrder(Long userId) {
        orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING).orElseThrow(
                () -> new OrderException("장바구니 정보를 찾을 수 없습니다.")
        ).changeOrderStatus();
    }

    @Transactional
    public void acceptOrder(HttpServletRequest request, OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        String authorization = request.getHeader("Authorization");

        Long orderId = changeOrderStatus.getOrderId();
        Order findOrder = orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ORDER).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeAcceptOrder();

        ResWaitingCount resWaitingCount = storeClient.saveWaitingCount(authorization, findOrder.getStoreId());

        findOrder.changeWaitingCount(resWaitingCount.getCurrentWaitingCount());
    }

    @Transactional
    public void rejectOrder(OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        Long orderId = changeOrderStatus.getOrderId();
        orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ORDER).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeRejectOrder();
    }

    @Transactional
    public void complete(OrderRequestDto.ChangeOrderStatus changeOrderStatus) {
        Long orderId = changeOrderStatus.getOrderId();
        orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.ACCEPTED).orElseThrow(
                () -> new OrderException("주문 정보를 찾을 수 없습니다.")
        ).changeCompleteOrder();
    }

}



