package org.example.ragtest.retrieval.impl;

import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.RetrievalAugmentorStrategy;
import org.example.ragtest.retrieval.RetrievalAugmentorType;
import org.example.ragtest.retrieval.query.QueryTransformerFactory;
import org.example.ragtest.retrieval.query.QueryTransformerStrategy;
import org.example.ragtest.retrieval.router.QueryRouterFactory;
import org.example.ragtest.retrieval.router.QueryRouterStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 增强型检索增强器策略
 * 提供完整的高级检索功能，包括：
 * - 查询转换器（可选）
 * - 查询路由器（可选）⭐ 新增
 * - 智能检索器选择
 * 
 * 适用场景：
 * - 需要高准确率的场景
 * - 有多个不同类型的检索器
 * - 需要智能路由和查询优化
 * - 对检索质量要求极高的场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnhancedRetrievalAugmentorStrategy implements RetrievalAugmentorStrategy {
    
    private final QueryTransformerFactory queryTransformerFactory;
    private final QueryRouterFactory queryRouterFactory;
    
    @Value("${rag.query.transformer.enabled:false}")
    private boolean queryTransformerEnabled;
    
    @Value("${rag.query.router.enabled:false}")
    private boolean queryRouterEnabled;
    
    @Override
    public RetrievalAugmentor getAugmentor() {
        log.info("创建增强型检索增强器，查询转换器: {}, 查询路由器: {}", 
                queryTransformerEnabled, queryRouterEnabled);
        
        // 创建增强器构建器
        DefaultRetrievalAugmentor.DefaultRetrievalAugmentorBuilder builder = DefaultRetrievalAugmentor.builder();
        
        // 1. 添加查询转换器（如果启用）
        if (queryTransformerEnabled) {
            Optional<QueryTransformer> queryTransformer = getQueryTransformer();
            if (queryTransformer.isPresent()) {
                builder.queryTransformer(queryTransformer.get());
                log.info("✓ 已启用查询转换器");
            } else {
                log.warn("✗ 查询转换器已配置启用但未找到可用的实现（需要ChatModel）");
            }
        }
        
        // 2. 添加查询路由器（如果启用）
        if (queryRouterEnabled) {
            Optional<QueryRouter> queryRouter = getQueryRouter();
            if (queryRouter.isPresent()) {
                builder.queryRouter(queryRouter.get());
                log.info("✓ 已启用查询路由器");
            } else {
                log.warn("✗ 查询路由器已配置启用但未找到可用的实现");
            }
        } else {
            // 如果没有启用查询路由器，使用默认路由器
            log.info("使用默认查询路由器（路由到所有检索器）");
            QueryRouter defaultRouter = queryRouterFactory.getDefaultStrategy().getRouter();
            builder.queryRouter(defaultRouter);
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
    
    /**
     * 获取查询路由器
     * 优先使用智能路由器（语言模型路由器）
     */
    private Optional<QueryRouter> getQueryRouter() {
        // 优先使用语言模型路由器（智能路由）
        Optional<QueryRouterStrategy> strategy = queryRouterFactory.getLanguageModelStrategy();
        if (strategy.isPresent()) {
            log.debug("使用语言模型查询路由器（智能路由）");
            return Optional.of(strategy.get().getRouter());
        }
        
        // 如果语言模型路由器不可用，使用默认路由器
        log.debug("使用默认查询路由器（路由到所有检索器）");
        return Optional.of(queryRouterFactory.getDefaultStrategy().getRouter());
    }
    
    @Override
    public RetrievalAugmentorType getAugmentorType() {
        return RetrievalAugmentorType.ADVANCED;
    }
    
    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder("增强型检索增强器 (");
        
        if (queryTransformerEnabled) {
            desc.append("查询转换✓, ");
        }
        
        if (queryRouterEnabled) {
            desc.append("智能路由✓");
        } else {
            desc.append("默认路由");
        }
        
        desc.append(")");
        return desc.toString();
    }
    
    @Override
    public int getMaxResults() {
        return 10; // 高级检索器通常返回更多结果
    }
    
    @Override
    public double getMinScore() {
        return 0.5; // 更宽松的阈值
    }
}
