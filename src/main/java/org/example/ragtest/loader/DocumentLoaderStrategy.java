package org.example.ragtest.loader;

import dev.langchain4j.data.document.Document;

import java.util.List;

/**
 * 文档加载器策略接口
 * 定义文档加载的统一行为
 */
public interface DocumentLoaderStrategy {
    
    /**
     * 加载单个文档
     * @param sourcePath 源路径（可以是文件路径、URL或类路径）
     * @return 加载的文档
     */
    Document loadDocument(String sourcePath);
    
    /**
     * 批量加载文档
     * @param sourcePath 源路径（通常是目录路径）
     * @return 加载的文档列表
     */
    List<Document> loadDocuments(String sourcePath);
    
    /**
     * 获取加载器类型
     * @return 加载器类型
     */
    DocumentLoaderType getLoaderType();
}
