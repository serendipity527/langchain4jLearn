package org.example.ragtest.retrieval.query.impl;

import dev.langchain4j.rag.query.transformer.DefaultQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.query.QueryTransformerStrategy;
import org.example.ragtest.retrieval.query.QueryTransformerType;
import org.springframework.stereotype.Component;

/**
 * 默认查询转换器策略
 * 不对查询进行任何修改，直接传递
 * 
 * 适用场景：
 * - 不需要查询转换的场景
 * - 简单直接的查询
 * - 默认行为
 */
@Slf4j
@Component
public class DefaultQueryTransformerStrategy implements QueryTransformerStrategy {
    
    @Override
    public QueryTransformer getTransformer() {
        log.debug("创建默认查询转换器（不做任何修改）");
        return new DefaultQueryTransformer();
    }
    
    @Override
    public QueryTransformerType getTransformerType() {
        return QueryTransformerType.DEFAULT;
    }
    
    @Override
    public String getDescription() {
        return "默认查询转换器 (不做任何修改，直接传递查询)";
    }
}
