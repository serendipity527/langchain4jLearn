package org.example.ragtest.retrieval.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.RetrievalAugmentorStrategy;
import org.example.ragtest.retrieval.RetrievalAugmentorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 默认检索增强器策略
 * 使用 LangChain4j 提供的 DefaultRetrievalAugmentor
 * 
 * 适用场景：
 * - 标准的 RAG 应用场景
 * - 基于向量相似度的检索
 * - 开箱即用，无需复杂配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRetrievalAugmentorStrategy implements RetrievalAugmentorStrategy {
    
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    @Value("${rag.retrieval.max-results:5}")
    private int maxResults;
    
    @Value("${rag.retrieval.min-score:0.6}")
    private double minScore;
    
    @Override
    public RetrievalAugmentor getAugmentor() {
        log.info("创建默认检索增强器，最大结果数: {}, 最小分数: {}", maxResults, minScore);
        
        // 创建内容检索器
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
        
        // 创建默认检索增强器
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }
    
    @Override
    public RetrievalAugmentorType getAugmentorType() {
        return RetrievalAugmentorType.DEFAULT;
    }
    
    @Override
    public String getDescription() {
        return String.format("默认检索增强器 (最大结果: %d, 最小分数: %.2f)", 
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
