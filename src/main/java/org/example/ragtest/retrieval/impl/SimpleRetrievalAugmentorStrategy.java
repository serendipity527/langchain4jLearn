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
import org.springframework.stereotype.Component;

/**
 * 简单检索增强器策略
 * 提供基础的向量检索功能，返回较少的结果
 * 
 * 适用场景：
 * - 快速响应场景
 * - 对检索结果数量要求不高
 * - 降低 token 消耗
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleRetrievalAugmentorStrategy implements RetrievalAugmentorStrategy {
    
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    private static final int MAX_RESULTS = 3;
    private static final double MIN_SCORE = 0.7;
    
    @Override
    public RetrievalAugmentor getAugmentor() {
        log.info("创建简单检索增强器，最大结果数: {}, 最小分数: {}", MAX_RESULTS, MIN_SCORE);
        
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(MAX_RESULTS)
                .minScore(MIN_SCORE)
                .build();
        
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }
    
    @Override
    public RetrievalAugmentorType getAugmentorType() {
        return RetrievalAugmentorType.SIMPLE;
    }
    
    @Override
    public String getDescription() {
        return String.format("简单检索增强器 (最大结果: %d, 最小分数: %.2f, 快速响应)", 
                MAX_RESULTS, MIN_SCORE);
    }
    
    @Override
    public int getMaxResults() {
        return MAX_RESULTS;
    }
    
    @Override
    public double getMinScore() {
        return MIN_SCORE;
    }
}
