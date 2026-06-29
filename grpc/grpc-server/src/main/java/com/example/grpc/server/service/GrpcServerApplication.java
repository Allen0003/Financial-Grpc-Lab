package com.example.grpc.server.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import java.io.IOException;

@SpringBootApplication
@EnableAsync
public class GrpcServerApplication {

    private final OrderServiceImpl orderService;
    private Server server;
    
    public GrpcServerApplication(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);
    }

    @PostConstruct
    public void startGrpcServer() throws IOException {
        // 使用注入進來的 orderService 啟動服務
        this.server = ServerBuilder.forPort(9090)
                .addService(orderService)
                .build()
                .start();

        System.out.println("gRPC 伺服器】已於 Port 9090 順利啟動...");

        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (server != null) {
            System.out.println("正在關閉 gRPC 伺服器...");
            server.shutdown();
        }
    }
}