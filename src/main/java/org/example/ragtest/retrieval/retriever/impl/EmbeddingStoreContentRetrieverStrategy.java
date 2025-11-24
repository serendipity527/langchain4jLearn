package org.example.ragtest.retrieval.retriever.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.retriever.ContentRetrieverStrategy;
import org.example.ragtest.retrieval.retriever.ContentRetrieverType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 嵌入式存储内容检索器策略
 * 使用 EmbeddingModel 从 EmbeddingStore 中检索相关内容
 * 
 * 特点：
 * - 基于向量相似度检索
 * - 支持最大结果数和最小分数配置
 * - 支持动态过滤器
 * 
 * 适用场景：
 * - 标准的 RAG 检索场景
 * - 基于语义相似度的内容检索
 * - 私有知识库检索
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingStoreContentRetrieverStrategy implements ContentRetrieverStrategy {
    
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    @Value("${rag.retrieval.max-results:5}")
    private int maxResults;
    
    @Value("${rag.retrieval.min-score:0.6}")
    private double minScore;
    
    @Override
    public ContentRetriever getRetriever() {
        log.info("创建嵌入式存储内容检索器，最大结果数: {}, 最小分数: {}", maxResults, minScore);
        
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                // 可以添加动态配置
                // .dynamicMaxResults(query -> maxResults)
                // .dynamicMinScore(query -> minScore)
                // .dynamicFilter(query -> {
                //     // 根据查询动态生成过滤器
                //     return null;
                // })
                .build();
    }
    
    @Override
    public ContentRetrieverType getRetrieverType() {
        return ContentRetrieverType.EMBEDDING_STORE;
    }
    
    @Override
    public String getDescription() {
        return String.format("嵌入式存储检索器 (基于向量相似度，最大结果: %d, 最小分数: %.2f)", 
                maxResults, minScore);
    }
    
    @Override
    public int getMaxResults() {
        return maxResults;
    }
    
    @Override
    public double getMinScore() {
        return minScore;
    }
}
