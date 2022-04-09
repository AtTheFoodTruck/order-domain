package com.sesac.foodtruckorder.application.service;

import com.sesac.foodtruckorder.exception.OrderException;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderItemRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepository;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetItemsInfoDto;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreResponse;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.StoreClient;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.UserClient;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.OrderItemRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderItemResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final Response response;
    private final UserClient userClient;
    private final StoreClient storeClient;


    /**
     * 장바구니 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
     **/
    public List<OrderItemResponseDto.FetchOrderDto> fetchOrder(HttpServletRequest request, Long userId) {

        String authorization = request.getHeader("Authorization");

        // 1. order 정보 조회
        Order findOrder = orderRepository.findByUserId(userId).orElseThrow(
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
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(), GetItemsInfoDto::getItemName)
                );

        // 4. OrderItem정보 조회
        List<OrderItemResponseDto.FetchOrderDto> results = findOrder.getOrderItems()
                .stream()
                .map(orderItem ->
                        OrderItemResponseDto.FetchOrderDto.of(
                                findStore.getStoreName(),
                                findStore.getImgUrl(),
                                itemMap.get(orderItem.getItemId()),
                                orderItem))
                .collect(Collectors.toList());

        return results;
    }

    /**
     * 장바구니에 아이템 담기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Transactional
    public void addItemToCart(OrderItemRequestDto.OrderItemDto cartItemDto, Long storeId, Long userId) {

        // OrderItem Entity생성
        OrderItem orderItem = OrderItem.of(cartItemDto.getCartItemId(), cartItemDto.getUnitPrice(), cartItemDto.getCount());

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
            orderRepository.save(Order.of(userId, storeId, orderItem));
        }
    }

    /**
     * 장바구니 수량 조절
     *
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Transactional
    public void countControl(Long orderItemId, boolean plusMinus) {

        // OrderItem 조회
        OrderItem plusOrderItem = orderItemRepository.findById(orderItemId).get();
        int plusCount = plusOrderItem.getCount();

        if (plusMinus) {
            plusCount++;
        } else {
            plusCount--;
        }
    }
}


