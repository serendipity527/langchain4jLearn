package org.example.ragtest.retrieval.query;

import dev.langchain4j.rag.query.transformer.QueryTransformer;

/**
 * 查询转换器策略接口
 * 定义查询转换器的统一行为
 */
public interface QueryTransformerStrategy {
    
    /**
     * 获取 QueryTransformer 实例
     * @return QueryTransformer 实例
     */
    QueryTransformer getTransformer();
    
    /**
     * 获取转换器类型
     * @return 转换器类型
     */
    QueryTransformerType getTransformerType();
    
    /**
     * 获取转换器描述
     * @return 描述信息
     */
    String getDescription();
    
    /**
     * 检查是否启用该转换器
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
}
