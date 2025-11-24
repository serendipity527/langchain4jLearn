package org.example.ragtest.retrieval.retriever;

import dev.langchain4j.rag.content.retriever.ContentRetriever;

/**
 * 内容检索器策略接口
 * 定义内容检索器的统一行为
 */
public interface ContentRetrieverStrategy {
    
    /**
     * 获取 ContentRetriever 实例
     * @return ContentRetriever 实例
     */
    ContentRetriever getRetriever();
    
    /**
     * 获取检索器类型
     * @return 检索器类型
     */
    ContentRetrieverType getRetrieverType();
    
    /**
     * 获取检索器描述
     * @return 描述信息
     */
    String getDescription();
    
    /**
     * 检查是否启用该检索器
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
    
    /**
     * 获取最大检索结果数
     * @return 最大检索结果数
     */
    default int getMaxResults() {
        return 5;
    }
    
    /**
     * 获取最小相关性分数
     * @return 最小相关性分数 (0.0-1.0)
     */
    default double getMinScore() {
        return 0.0;
    }
}
