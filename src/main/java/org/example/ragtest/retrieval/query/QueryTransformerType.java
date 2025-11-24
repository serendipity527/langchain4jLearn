package org.example.ragtest.retrieval.query;

/**
 * 查询转换器类型枚举
 */
public enum QueryTransformerType {
    /**
     * 默认查询转换器 - 不做任何修改，直接传递
     */
    DEFAULT,
    
    /**
     * 压缩查询转换器 - 使用LLM压缩查询并整合对话历史
     */
    COMPRESSING,
    
    /**
     * 扩展查询转换器 - 使用LLM将查询扩展为多个查询
     */
    EXPANDING,
    
    /**
     * 重写查询转换器 - 使用LLM重写查询以提高检索效果
     */
    REWRITING
}
