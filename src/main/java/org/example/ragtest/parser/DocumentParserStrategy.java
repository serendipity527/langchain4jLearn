package org.example.ragtest.parser;

import dev.langchain4j.data.document.Document;

import java.io.InputStream;

/**
 * 文档解析器策略接口
 * 定义文档解析的统一行为
 */
public interface DocumentParserStrategy {
    
    /**
     * 解析文档
     * @param inputStream 输入流
     * @return 解析后的文档
     */
    Document parse(InputStream inputStream);
    
    /**
     * 获取解析器类型
     * @return 解析器类型
     */
    DocumentParserType getParserType();
    
    /**
     * 判断是否支持该文件扩展名
     * @param fileExtension 文件扩展名（不含点，如 "pdf", "txt"）
     * @return 是否支持
     */
    boolean supports(String fileExtension);
    
    /**
     * 获取解析器实例（用于与 LangChain4j 的 DocumentLoader 集成）
     * @return 解析器实例
     */
    Object getParserInstance();
}
