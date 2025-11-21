package org.example.ragtest.documentTransformer;

import dev.langchain4j.data.document.Document;

import java.util.List;

/**
 * 文档转换器策略接口
 * 定义文档转换的统一行为
 */
public interface DocumentTransformerStrategy {
    
    /**
     * 转换单个文档
     * @param document 待转换的文档
     * @return 转换后的文档（如果被过滤则返回null）
     */
    Document transform(Document document);
    
    /**
     * 批量转换多个文档
     * @param documents 待转换的文档列表
     * @return 转换后的文档列表（被过滤的文档会被移除）
     */
    List<Document> transformAll(List<Document> documents);
    
    /**
     * 获取转换器类型
     * @return 转换器类型
     */
    DocumentTransformerType getTransformerType();
    
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
