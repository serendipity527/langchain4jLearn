package org.example.ragtest.retrieval.router.impl;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.router.QueryRouterStrategy;
import org.example.ragtest.retrieval.router.QueryRouterType;
import org.example.ragtest.retrieval.retriever.ContentRetrieverFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认查询路由器策略
 * 将每个查询路由到所有已配置的 ContentRetriever
 * 
 * 特点：
 * - 查询所有可用的检索器
 * - 聚合所有检索结果
 * - 适合大多数场景
 * 
 * 适用场景：
 * - 标准的RAG应用
 * - 需要综合多个数据源的结果
 * - 不确定哪个检索器最合适时
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultQueryRouterStrategy implements QueryRouterStrategy {
    
    private final ContentRetrieverFactory contentRetrieverFactory;
    
    @Override
    public QueryRouter getRouter() {
        log.info("创建默认查询路由器（路由到所有检索器）");
        
        // 获取所有启用的检索器
        List<ContentRetriever> retrievers = contentRetrieverFactory.getEnabledStrategies()
                .stream()
                .map(strategy -> {
                    log.debug("添加检索器到路由: {}", strategy.getDescription());
                    return strategy.getRetriever();
                })
                .collect(Collectors.toList());
        
        if (retrievers.isEmpty()) {
            log.warn("没有可用的内容检索器");
            throw new IllegalStateException("至少需要一个内容检索器");
        }
        
        log.info("默认查询路由器已创建，包含 {} 个检索器", retrievers.size());
        
        // DefaultQueryRouter 会将查询路由到所有提供的检索器
        return new DefaultQueryRouter(retrievers);
    }
    
    @Override
    public QueryRouterType getRouterType() {
        return QueryRouterType.DEFAULT;
    }
    
    @Override
    public String getDescription() {
        int retrieverCount = contentRetrieverFactory.getEnabledStrategies().size();
        return String.format("默认查询路由器 (路由到所有 %d 个检索器)", retrieverCount);
    }
}
