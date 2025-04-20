package com.aasee.langchain4jtest.controller;

import com.aasee.langchain4jtest.service.RagService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@CrossOrigin
@RequestMapping("/rag")
public class RagController {
    @Resource
    private RagService ragService;

    @GetMapping(value = "/dbinit")
    public String dbInit() {
        ragService.importDocuments();
        return "OK";
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam(value = "chatId",required = false,defaultValue = "1") String chatId,
                                   @RequestParam(value = "message") String message,
                                   @RequestParam(value = "fileUrl",required = false) String fileUrl) {
        return ragService.chatStream(chatId, message,fileUrl).map(text -> "data: " + text + "\n\n") // 符合 SSE 格式
                .concatWith(Flux.just("data: [DONE]\n\n")) // 明确结束标志
                .doOnComplete(() -> {
                    // 主动关闭SSE连接
                    System.out.println("SSE connection closed.");
                })
                .doOnError(error -> {
                    // 处理错误并关闭SSE连接
                    System.out.println("SSE error occurred: " + error.getMessage());
                });
    }
}