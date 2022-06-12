package com.sesac.foodtruckorder.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.ui.dto.request.OrderItemRequestDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 주문 신청
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/06/11
    **/
    public void orderPlaced(Order order) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        KafkaSendOrderDto kafkaSendOrderDto = KafkaSendOrderDto.createPrimitiveField(order);
        String jsonInString = objectMapper.writeValueAsString(kafkaSendOrderDto);
        kafkaTemplate.send("orderPlaced", jsonInString);
        log.info("kafka Producer sent data from the Order Microservice: " + kafkaSendOrderDto);

    }

    /**
     * 주문 수락
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/06/12
    **/
    public void orderAccepted(Order order) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        KafkaSendOrderDto kafkaSendOrderDto = KafkaSendOrderDto.createPrimitiveField(order);
        String json = objectMapper.writeValueAsString(kafkaSendOrderDto);
        kafkaTemplate.send("orderAccepted", json);
        log.info("[OrderSender] orderAccepted = {}", json);

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class KafkaSendOrderDto {
        private Long id;

        private Long userId;

        private String userName;

        private long orderPrice;

        private Long storeId;

        private LocalDateTime orderTime;

        private OrderStatus orderStatus;

        private List<OrderItemRequestDto.OrderItemDto> orderItemDtoList;

        public static KafkaSendOrderDto createPrimitiveField(Order order) {
            return KafkaSendOrderDto.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .orderPrice(order.getOrderPrice())
                    .orderTime(order.getOrderTime())
                    .storeId(order.getStoreId())
                    .orderStatus(order.getOrderStatus())
                    .build();
        }
    }

}
