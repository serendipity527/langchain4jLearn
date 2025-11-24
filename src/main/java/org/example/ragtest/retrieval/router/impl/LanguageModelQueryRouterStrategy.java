package org.example.ragtest.retrieval.router.impl;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.router.QueryRouterStrategy;
import org.example.ragtest.retrieval.router.QueryRouterType;
import org.example.ragtest.retrieval.retriever.ContentRetrieverFactory;
import org.example.ragtest.retrieval.retriever.ContentRetrieverType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 语言模型查询路由器策略
 * 使用 LLM 来决定将给定的查询路由到哪个 ContentRetriever
 * 
 * 特点：
 * - 使用 LLM 智能选择最合适的检索器
 * - 可以路由到一个或多个检索器
 * - 适合有多个不同类型数据源的场景
 * 
 * 适用场景：
 * - 有多个不同类型的检索器（如：向量存储、网络搜索、SQL数据库）
 * - 需要根据查询意图选择最合适的检索器
 * - 希望优化检索性能和准确性
 * 
 * 示例：
 * - "什么是Spring Boot?" → 向量存储（知识库查询）
 * - "今天的新闻" → 网络搜索（实时信息）
 * - "2023年销售额" → SQL数据库（结构化数据查询）
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class LanguageModelQueryRouterStrategy implements QueryRouterStrategy {
    
    private final ChatModel chatModel;
    private final ContentRetrieverFactory contentRetrieverFactory;
    
    @Override
    public QueryRouter getRouter() {
        log.info("创建语言模型查询路由器（使用LLM智能选择检索器）");
        
        // 构建检索器映射：key 为检索器描述，value 为检索器实例
        Map<ContentRetriever, String> retrieverToDescription = new LinkedHashMap<>();
        
        // 添加所有启用的检索器
        contentRetrieverFactory.getEnabledStrategies().forEach(strategy -> {
            ContentRetriever retriever = strategy.getRetriever();
            String description = buildRetrieverDescription(strategy);
            retrieverToDescription.put(retriever, description);
            log.debug("添加检索器到智能路由: {}", description);
        });
        
        if (retrieverToDescription.isEmpty()) {
            log.warn("没有可用的内容检索器");
            throw new IllegalStateException("至少需要一个内容检索器");
        }
        
        log.info("语言模型查询路由器已创建，包含 {} 个检索器", retrieverToDescription.size());
        
        // 创建 LanguageModelQueryRouter
        // LLM 会根据查询内容和检索器描述来决定使用哪个检索器
        return LanguageModelQueryRouter.builder()
                .chatModel(chatModel)
                .retrieverToDescription(retrieverToDescription)
                // 可选：自定义 LLM 的提示词
                // .prompt(customPrompt)
                // 可选：设置是否允许路由到多个检索器
                // .fallback(fallbackRetriever)
                .build();
    }
    
    /**
     * 为检索器构建详细描述，帮助 LLM 做出更好的路由决策
     */
    private String buildRetrieverDescription(org.example.ragtest.retrieval.retriever.ContentRetrieverStrategy strategy) {
        ContentRetrieverType type = strategy.getRetrieverType();
        
        switch (type) {
            case EMBEDDING_STORE:
                return "私有知识库检索器 - 用于查询已存储的文档和知识，适合：产品文档、技术资料、历史记录等静态知识查询";
                
            case WEB_SEARCH:
                return "网络搜索检索器 - 用于搜索互联网上的最新信息，适合：实时新闻、当前事件、最新趋势等动态信息查询";
                
            case SQL_DATABASE:
                return "SQL数据库检索器 - 用于查询结构化数据，适合：销售数据、用户信息、统计报表等业务数据查询";
                
            case AZURE_AI_SEARCH:
                return "Azure AI搜索检索器 - 企业级混合搜索（向量+全文），适合：大规模文档检索、企业知识管理";
                
            case HYBRID:
                return "混合检索器 - 结合多种检索方式，适合：需要综合多个数据源的复杂查询";
                
            default:
                return strategy.getDescription();
        }
    }
    
    @Override
    public QueryRouterType getRouterType() {
        return QueryRouterType.LANGUAGE_MODEL;
    }
    
    @Override
    public String getDescription() {
        int retrieverCount = contentRetrieverFactory.getEnabledStrategies().size();
        return String.format("语言模型查询路由器 (使用LLM智能选择 %d 个检索器中最合适的)", retrieverCount);
    }
}
