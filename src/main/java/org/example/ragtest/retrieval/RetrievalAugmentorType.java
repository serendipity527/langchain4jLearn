package org.example.ragtest.retrieval;

/**
 * 检索增强器类型枚举
 */
public enum RetrievalAugmentorType {
    /**
     * 默认检索增强器 - 使用 DefaultRetrievalAugmentor
     */
    DEFAULT,
    
    /**
     * 简单检索增强器 - 基础的向量检索
     */
    SIMPLE,
    
    /**
     * 高级检索增强器 - 包含查询压缩、重排序等高级功能
     */
    ADVANCED,
    
    /**
     * 混合检索增强器 - 结合多种检索策略
     */
    HYBRID,
    
    /**
     * 自定义检索增强器 - 完全自定义的检索逻辑
     */
    CUSTOM
}
