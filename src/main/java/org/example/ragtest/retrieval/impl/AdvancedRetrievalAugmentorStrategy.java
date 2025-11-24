package org.example.ragtest.retrieval.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.RetrievalAugmentorStrategy;
import org.example.ragtest.retrieval.RetrievalAugmentorType;
import org.example.ragtest.retrieval.query.QueryTransformerFactory;
import org.example.ragtest.retrieval.query.QueryTransformerStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 高级检索增强器策略
 * 提供高级检索功能，包括：
 * - 更多的检索结果
 * - 查询转换（可选）
 * - 结果重排序（可选）
 * 
 * 适用场景：
 * - 需要高准确率的场景
 * - 复杂查询场景
 * - 对检索质量要求高的场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvancedRetrievalAugmentorStrategy implements RetrievalAugmentorStrategy {
    
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final QueryTransformerFactory queryTransformerFactory;
    
    @Value("${rag.retrieval.advanced.max-results:10}")
    private int maxResults;
    
    @Value("${rag.retrieval.advanced.min-score:0.5}")
    private double minScore;
    
    @Value("${rag.query.transformer.enabled:false}")
    private boolean queryTransformerEnabled;
    
    @Override
    public RetrievalAugmentor getAugmentor() {
        log.info("创建高级检索增强器，最大结果数: {}, 最小分数: {}, 查询转换器: {}", 
                maxResults, minScore, queryTransformerEnabled);
        
        // 创建内容检索器（检索更多结果）
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
        
        // 创建高级检索增强器构建器
        DefaultRetrievalAugmentor.DefaultRetrievalAugmentorBuilder builder = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever);
        
        // 如果启用，添加查询转换器
        if (queryTransformerEnabled) {
            Optional<QueryTransformer> queryTransformer = getQueryTransformer();
            if (queryTransformer.isPresent()) {
                builder.queryTransformer(queryTransformer.get());
                log.info("已启用查询转换器");
            } else {
                log.warn("查询转换器已启用但未找到可用的实现（需要ChatLanguageModel）");
            }
        }
        
        return builder.build();
    }
    
    /**
     * 获取查询转换器
     * 优先使用压缩查询转换器（适合对话场景）
     */
    private Optional<QueryTransformer> getQueryTransformer() {
        // 优先尝试压缩查询转换器（最常用）
        Optional<QueryTransformerStrategy> strategy = queryTransformerFactory.getCompressingStrategy();
        if (strategy.isPresent()) {
            log.debug("使用压缩查询转换器");
            return Optional.of(strategy.get().getTransformer());
        }
        
        // 如果没有压缩转换器，尝试扩展转换器
        strategy = queryTransformerFactory.getExpandingStrategy();
        if (strategy.isPresent()) {
            log.debug("使用扩展查询转换器");
            return Optional.of(strategy.get().getTransformer());
        }
        
        // 都没有则返回空
        return Optional.empty();
    }
    
    @Override
    public RetrievalAugmentorType getAugmentorType() {
        return RetrievalAugmentorType.ADVANCED;
    }
    
    @Override
    public String getDescription() {
        return String.format("高级检索增强器 (最大结果: %d, 最小分数: %.2f, 支持查询转换和重排序)", 
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
