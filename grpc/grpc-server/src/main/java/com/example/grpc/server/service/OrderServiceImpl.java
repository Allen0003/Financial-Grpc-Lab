package com.example.grpc.server.service;

import com.example.grpc.order.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    @Override
    public void streamMarketData(MarketDataRequest request, StreamObserver<MarketDataResponse> responseObserver) {
        String symbol = request.getSymbol();
        System.out.println("客戶端訂閱了股票行情: " + symbol + "，開始即時推播...");

        Random random = new Random();
        double basePrice = "2330".equals(symbol) ? 985.0 : 150.0; // 模擬台積電與其他股票基價

        try {
            // 模擬金融交易系統：每秒產生一次新價格，連續推播 10 次（真實系統中會是 while(true) 直到斷線）
            for (int i = 0; i < 10; i++) {
                // 模擬股價隨機跳動（正負 2 元）
                double currentPrice = basePrice + (random.nextDouble() * 4 - 2);

                MarketDataResponse response = MarketDataResponse.newBuilder()
                        .setSymbol(symbol)
                        .setCurrentPrice(currentPrice)
                        .build();

                // 呼叫 onNext 發送這秒的報價，通道不會斷，繼續留著等下一次
                responseObserver.onNext(response);
                System.out.println(String.format(" 已推播 [%s] 最新價: %.2f", symbol, currentPrice));

                // 歇一秒，模擬真實行情跳動間隔
                Thread.sleep(1000);
            }

            // 10 次推完了，通知客戶端：「今天盤後共時結束，關閉串流」
            responseObserver.onCompleted();
            System.out.println("行情推播結束。");

        } catch (InterruptedException e) {
            System.out.println("客戶端主動中斷訂閱");
            Thread.currentThread().interrupt();
        }
    }

}