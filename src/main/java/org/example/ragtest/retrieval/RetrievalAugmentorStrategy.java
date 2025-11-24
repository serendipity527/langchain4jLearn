package org.example.ragtest.retrieval;

import dev.langchain4j.rag.RetrievalAugmentor;

/**
 * 检索增强器策略接口
 * 定义检索增强器的统一行为
 */
public interface RetrievalAugmentorStrategy {
    
    /**
     * 获取 RetrievalAugmentor 实例
     * @return RetrievalAugmentor 实例
     */
    RetrievalAugmentor getAugmentor();
    
    /**
     * 获取增强器类型
     * @return 增强器类型
     */
    RetrievalAugmentorType getAugmentorType();
    
    /**
     * 获取增强器描述
     * @return 描述信息
     */
    String getDescription();
    
    /**
     * 检查是否启用该增强器
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
