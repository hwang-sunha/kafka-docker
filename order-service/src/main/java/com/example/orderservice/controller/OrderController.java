package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @Timed(value="orders.status",longTask=true)
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in Order Service"
                +"\nenv.getProperty(gateway.ip)=" + env.getProperty("gateway.ip")
                +"\nenv.getProperty(local.server.port)=" + env.getProperty("local.server.port")
                +"\nenv.getProperty(server.port)=" + env.getProperty("server.port")
                +"\nenv.getProperty(token.secret)=" + env.getProperty("token.secret")
                +"\nenv.getProperty(token.experation_time)=" + env.getProperty("token.experation_time")
        );
    }

    @Timed(value="orders.creatOrder",longTask=true)
    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity creatOrder(
            @PathVariable("userId") String userId,
            @RequestBody RequestOrder order){

        log.info("Before add order data");
        ModelMapper mapper = new ModelMapper();
        OrderDto orderDto = mapper.map(order, OrderDto.class);
        orderDto.setUserId(userId);
        /*jpa*/
        OrderDto order1 = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(order1, ResponseOrder.class);

        /*kafka*/
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(order.getQty() * order.getUnitPrice());
//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        /* send this order to the kafka */
        kafkaProducer.send("example-catalog-topic",orderDto);
//        orderProducer.send("orders",orderDto);

        log.info("After added order data");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseOrder);
    }

    @Timed(value="orders.getOrders",longTask=true)
    @GetMapping("/{userId}/orders")
    public ResponseEntity getOrders(
            @PathVariable("userId") String userId) throws Exception{

        log.info("Before retrieve order data");
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach( v -> {
            result.add( new ModelMapper().map(v, ResponseOrder.class));
        });
        //TODO Resilience4J 테스트 코드
//        try {
//            Thread.sleep(1000);
//            throw new Exception("장애발생");
//        }catch (InterruptedException ex){
//            log.error(ex.getMessage());
//        }


        log.info("Add retrieve order data");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
