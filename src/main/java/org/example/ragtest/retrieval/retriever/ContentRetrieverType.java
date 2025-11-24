package org.example.ragtest.retrieval.retriever;

/**
 * 内容检索器类型枚举
 */
public enum ContentRetrieverType {
    /**
     * 嵌入式存储检索器 - 从 EmbeddingStore 检索内容
     */
    EMBEDDING_STORE,
    
    /**
     * 网络搜索检索器 - 从网络搜索引擎检索内容
     */
    WEB_SEARCH,
    
    /**
     * SQL 数据库检索器 - 从 SQL 数据库检索内容
     */
    SQL_DATABASE,
    
    /**
     * Azure AI 搜索检索器 - 从 Azure AI Search 检索内容
     */
    AZURE_AI_SEARCH,
    
    /**
     * 混合检索器 - 结合多种检索方式
     */
    HYBRID
}
