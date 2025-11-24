package org.example.ragtest.retrieval.query.impl;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.query.QueryTransformerStrategy;
import org.example.ragtest.retrieval.query.QueryTransformerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 扩展查询转换器策略
 * 使用LLM将一个查询扩展为多个查询
 * 
 * 使用场景：
 * - 需要从多个角度检索内容
 * - 提高检索召回率
 * - 用户查询可能有多种表达方式
 * 
 * 原理：
 * LLM会以各种方式重述和重新表述查询，生成多个查询变体
 * 例如："Spring Boot配置" → "Spring Boot如何配置", "Spring Boot配置方法", "配置Spring Boot应用"
 * 然后对每个查询变体进行检索，获取更全面的结果
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class ExpandingQueryTransformerStrategy implements QueryTransformerStrategy {
    
    private final ChatModel chatLanguageModel;
    
    @Value("${rag.query.expanding.count:3}")
    private int expandCount;
    
    @Override
    public QueryTransformer getTransformer() {
        log.info("创建扩展查询转换器（扩展为{}个查询）", expandCount);
        return ExpandingQueryTransformer.builder()
                .chatModel(chatLanguageModel)
                // 可以配置扩展的查询数量
                .build();
    }
    
    @Override
    public QueryTransformerType getTransformerType() {
        return QueryTransformerType.EXPANDING;
    }
    
    @Override
    public String getDescription() {
        return String.format("扩展查询转换器 (使用LLM将查询扩展为%d个变体，提高召回率)", expandCount);
    }
}
