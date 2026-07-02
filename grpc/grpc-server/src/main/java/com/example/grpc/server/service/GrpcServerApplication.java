package com.example.grpc.server.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GrpcServerApplication implements CommandLineRunner {

    private final OrderServiceImpl orderService;
    private Server server;

    public GrpcServerApplication(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 保證在此時 Spring Boot 已經完全 Started 完畢
        this.server = ServerBuilder.forPort(9090)
                .addService(orderService)
                .build()
                .start();

        System.out.println("[金融級微服務] gRPC 伺服器已於 Port 9090 完美就緒，開始接收連線...");

        // 金融級防阻塞優化：讓主執行緒守候，直到服務被關閉
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        awaitThread.setDaemon(false); // 必須是 Non-Daemon，防止 JVM 提早結束
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