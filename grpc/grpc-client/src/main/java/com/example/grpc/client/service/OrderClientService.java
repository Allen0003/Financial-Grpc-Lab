package com.example.grpc.client.service;

import com.example.grpc.order.OrderRequest;
import com.example.grpc.order.OrderResponse;
import com.example.grpc.order.OrderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class OrderClientService {

    private ManagedChannel channel;
    private OrderServiceGrpc.OrderServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        // 1. 建立指向本地 9090 Port 的連線通道，usePlaintext 代表不使用 SSL 加密
        this.channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // 2. 建立一個「阻塞型（同步）」的 Stub，用來呼叫遠端方法
        this.blockingStub = OrderServiceGrpc.newBlockingStub(channel);
        System.out.println("【gRPC 客戶端】已成功連線至 localhost:9090");
    }

    public String queryOrder(String orderId) {
        // 3. 組裝發往 Server 端的 Protobuf Request
        OrderRequest request = OrderRequest.newBuilder()
                .setOrderId(orderId)
                .build();

        try {
            // 4. 像呼叫本地方法一樣，直接進行遠端 RPC 呼叫
            OrderResponse response = blockingStub.getOrder(request);

            // 5. 格式化回傳結果給 Controller
            return String.format("【RPC 呼叫成功】商品: %s, 金額: %.0f, 狀態: %s",
                    response.getProductName(), response.getPrice(), response.getStatus());
        } catch (Exception e) {
            return "【RPC 呼叫失敗】原因: " + e.getMessage();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            System.out.println("正在關閉 gRPC 客戶端連線...");
            channel.shutdown();
        }
    }
}