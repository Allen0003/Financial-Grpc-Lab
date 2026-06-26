package com.example.grpc.client.controller;

import com.example.grpc.client.service.OrderClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.grpc.order.MarketDataResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

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

    @GetMapping(value = "/market/stream", produces = "text/event-stream") //  設定為 SSE 串流型態
    public SseEmitter streamMarketData(@RequestParam("symbol") String symbol) {
        SseEmitter emitter = new SseEmitter(0L); // 0 代表永不超時

        // 開一個新執行緒去聽 gRPC，不卡住 Tomcat 主執行緒
        new Thread(() -> {
            try {
                orderClientService.subscribeMarketData(symbol, new StreamObserver<MarketDataResponse>() {
                    @Override
                    public void onNext(MarketDataResponse value) {
                        try {
                            // 每當 gRPC Server 推來一筆報價，立刻轉手噴給瀏覽器
                            String data = String.format("股票: %s | 即時價: %.2f ",
                                    value.getSymbol(), value.getCurrentPrice(), value.getCurrentPrice());

                            emitter.send(SseEmitter.event().data(data));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.completeWithError(t);
                    }

                    @Override
                    public void onCompleted() {
                        emitter.complete();
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}