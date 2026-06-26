package com.example.grpc.client.controller;

import com.example.grpc.client.service.OrderClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderClientController {

    private final OrderClientService orderClientService;

    // 透過建構子注入剛剛寫好的 gRPC Client 服務
    public OrderClientController(OrderClientService orderClientService) {
        this.orderClientService = orderClientService;
    }

    // 瀏覽器造訪：http://localhost:8080/order?id=TX-2026
    @GetMapping("/order")
    public String getOrderDetails(@RequestParam("id") String orderId) {
        return orderClientService.queryOrder(orderId);
    }
}