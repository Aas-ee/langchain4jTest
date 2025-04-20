package com.aasee.langchain4jtest.config;

import dev.ai4j.openai4j.OpenAiClient;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RagConfig {
    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);
 
    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;
    
    @Bean
    public ChatMemoryStore chatMemoryStore() {
        log.info("==========初始化ChatMemoryStore");
        return new InMemoryChatMemoryStore();
    }
 
    @Bean
    public EmbeddingStore createEmbeddingStore() {
 
        log.info("==========开始创建Milvus的Collection");
        MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName("langchain_01")
                .dimension(1536)
                .indexType(IndexType.FLAT)
                .metricType(MetricType.COSINE)
                .username("root")
                .password("milvus??2001")
                .consistencyLevel(ConsistencyLevelEnum.EVENTUALLY)
                .autoFlushOnInsert(true)
                .idFieldName("id")
                .textFieldName("text")
                .metadataFieldName("metadata")
                .vectorFieldName("vector")
                .build();
        log.info("==========创建Milvus的Collection完成");
        return store;
    }

    @Bean
    public OpenAiChatModel qwenChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("http://localhost:8000/v1")
                .apiKey("jCc$oy6boEMKcfZugAazpi9lp5TSVsusIImUwY8nAO*sy70Bfbyzzp_8Gn1gsBy1ogROHqapzX0d0")
                .modelName("qwen")
                .temperature(0.7)
                .build();
    }
    @Bean
    public OpenAiStreamingChatModel qwenStreamChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl("http://localhost:8000/v1")
                .apiKey("jCc$oy6boEMKcfZugAazpi9lp5TSVsusIImUwY8nAO*sy70Bfbyzzp_8Gn1gsBy1ogROHqapzX0d0")
                .modelName("qwen")
                .temperature(0.7)
                .logRequests(true) // 开启请求日志
                .logResponses(true) // 开启响应日志
                .responseFormat("text/event-stream")
                .build();
    }
}