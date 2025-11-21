package org.example.ragtest.splitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

/**
 * 文档分割器策略接口
 * 定义文档分割的统一行为
 */
public interface DocumentSplitterStrategy {
    
    /**
     * 分割单个文档
     * @param document 待分割的文档
     * @return 分割后的文本段列表
     */
    List<TextSegment> split(Document document);
    
    /**
     * 批量分割多个文档
     * @param documents 待分割的文档列表
     * @return 分割后的文本段列表
     */
    List<TextSegment> splitAll(List<Document> documents);
    
    /**
     * 获取分割器类型
     * @return 分割器类型
     */
    DocumentSplitterType getSplitterType();
    
    /**
     * 获取 LangChain4j 的 DocumentSplitter 实例
     * @return DocumentSplitter 实例
     */
    DocumentSplitter getSplitterInstance();
    
    /**
     * 获取分割器配置描述
     * @return 配置描述
     */
    String getDescription();
}
