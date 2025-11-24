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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 高级嵌入式存储内容检索器策略
 * 提供更多配置选项和动态功能
 * 
 * 特点：
 * - 支持动态最大结果数配置
 * - 支持动态最小分数配置
 * - 支持动态过滤器
 * - 更高的检索结果数
 * 
 * 适用场景：
 * - 需要更全面检索结果的场景
 * - 需要动态调整检索参数的场景
 * - 需要基于用户/查询动态过滤的场景
 */
@Slf4j
@Component
@Primary  // 设为默认实现
@RequiredArgsConstructor
public class AdvancedEmbeddingStoreRetrieverStrategy implements ContentRetrieverStrategy {
    
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    @Value("${rag.retrieval.advanced.max-results:10}")
    private int maxResults;
    
    @Value("${rag.retrieval.advanced.min-score:0.5}")
    private double minScore;
    
    @Value("${rag.retrieval.dynamic.enabled:false}")
    private boolean dynamicEnabled;
    
    @Override
    public ContentRetriever getRetriever() {
        log.info("创建高级嵌入式存储内容检索器，最大结果数: {}, 最小分数: {}, 动态配置: {}", 
                maxResults, minScore, dynamicEnabled);
        
        EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder builder = 
                EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(maxResults)
                        .minScore(minScore);
        
        // 如果启用动态配置
        if (dynamicEnabled) {
            // 动态最大结果数：可根据查询复杂度调整
            builder.dynamicMaxResults(query -> {
                // 示例：根据查询文本长度调整结果数
                // 实际应用中可以根据业务逻辑动态调整
                return query.text().length() > 100 ? maxResults + 5 : maxResults;
            });
            
            // 动态最小分数：可根据查询类型调整
            builder.dynamicMinScore(query -> {
                // 示例：某些查询可以降低阈值
                return minScore;
            });
            
            // 动态过滤器：可根据用户权限等过滤
            builder.dynamicFilter(query -> {
                // 示例：根据查询参数获取用户ID并过滤
                // String userId = query.metadata().invocationParameters().get("userId");
                // return metadataKey("userId").isEqualTo(userId);
                return null;  // 不使用过滤器
            });
            
            log.debug("已启用动态配置功能");
        }
        
        return builder.build();
    }
    
    @Override
    public ContentRetrieverType getRetrieverType() {
        return ContentRetrieverType.EMBEDDING_STORE;
    }
    
    @Override
    public String getDescription() {
        return String.format("高级嵌入式存储检索器 (支持动态配置，最大结果: %d, 最小分数: %.2f)", 
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
