package org.example.ragtest.retrieval.router;

import dev.langchain4j.rag.query.router.QueryRouter;

/**
 * 查询路由器策略接口
 * 定义查询路由器的统一行为
 * 
 * QueryRouter 负责将查询路由到相应的 ContentRetriever
 */
public interface QueryRouterStrategy {
    
    /**
     * 获取 QueryRouter 实例
     * @return QueryRouter 实例
     */
    QueryRouter getRouter();
    
    /**
     * 获取路由器类型
     * @return 路由器类型
     */
    QueryRouterType getRouterType();
    
    /**
     * 获取路由器描述
     * @return 描述信息
     */
    String getDescription();
    
    /**
     * 检查是否启用该路由器
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
}
