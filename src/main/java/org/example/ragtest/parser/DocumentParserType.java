package org.example.ragtest.parser;

/**
 * 文档解析器类型枚举
 */
public enum DocumentParserType {
    /**
     * 纯文本解析器（TXT, HTML, MD等）
     */
    TEXT,
    
    /**
     * PDF 解析器
     */
    PDF,
    
    /**
     * MS Office 解析器（DOC, DOCX, PPT, PPTX, XLS, XLSX等）
     */
    MS_OFFICE,
    
    /**
     * Apache Tika 自动检测解析器（支持几乎所有格式）
     */
    AUTO_DETECT,
    
    /**
     * Markdown 解析器
     */
    MARKDOWN,
    
    /**
     * YAML 解析器
     */
    YAML
}
