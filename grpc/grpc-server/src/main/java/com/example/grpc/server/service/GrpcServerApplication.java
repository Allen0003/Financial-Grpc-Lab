package com.example.grpc.server.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.io.IOException;

@SpringBootApplication
public class GrpcServerApplication {

    private Server server;

    // 🎯 修正 1：利用 @Autowired 讓 Spring 把建好的 Bean 注入進來，打破循環
    @Autowired
    private OrderServiceImpl orderService;

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);
    }

    // 🎯 註冊業務邏輯（純粹宣告，不由內部手動呼叫）
    @Bean
    public OrderServiceImpl orderServiceBean() {
        return new OrderServiceImpl();
    }

    // 🎯 將 gRPC 的生命週期直接綁定在 Spring 啟動時
    @PostConstruct
    public void startGrpcServer() throws IOException {
        // 🎯 修正 2：使用注入進來的屬性變數 orderService，不再呼叫方法
        this.server = ServerBuilder.forPort(9090)
                .addService(orderService)
                .build()
                .start();

        System.out.println("🚀 【金融級 gRPC 伺服器】已於 Port 9090 順利啟動...");

        // 防止 Spring 秒退的守護執行緒
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
            System.out.println("🛑 正在關閉 gRPC 伺服器...");
            server.shutdown();
        }
    }
}