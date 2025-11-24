package org.example.ragtest.retrieval.router;

/**
 * 查询路由器类型枚举
 */
public enum QueryRouterType {
    /**
     * 默认路由器 - 路由到所有检索器
     */
    DEFAULT,
    
    /**
     * 语言模型路由器 - 使用LLM智能选择检索器
     */
    LANGUAGE_MODEL,
    
    /**
     * 基于规则的路由器 - 基于规则选择检索器
     */
    RULE_BASED,
    
    /**
     * 轮询路由器 - 轮流使用不同的检索器
     */
    ROUND_ROBIN
}
