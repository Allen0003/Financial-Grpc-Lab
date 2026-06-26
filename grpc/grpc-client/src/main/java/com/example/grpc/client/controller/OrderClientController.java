package com.example.grpc.client.controller;

import com.example.grpc.client.service.OrderClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderClientController {

    private final OrderClientService orderClientService;


    public OrderClientController(OrderClientService orderClientService) {
        this.orderClientService = orderClientService;
    }

    @GetMapping("/order")
    public String getOrderDetails(@RequestParam("id") String orderId) {
        return orderClientService.queryOrder(orderId);
    }
}