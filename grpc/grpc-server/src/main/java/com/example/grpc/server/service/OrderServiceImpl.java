package com.example.grpc.server.service;

import com.example.grpc.order.OrderRequest;
import com.example.grpc.order.OrderResponse;
import com.example.grpc.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Override
    public void getOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        String orderId = request.getOrderId();
        System.out.println(" 收到訂單查詢請求，ID: " + orderId);

        OrderResponse response = OrderResponse.newBuilder()
                .setOrderId(orderId)
                .setProductName("台積電現股 - 1000 股")
                .setPrice(985000.00)
                .setStatus("SUCCESS_FILLED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}