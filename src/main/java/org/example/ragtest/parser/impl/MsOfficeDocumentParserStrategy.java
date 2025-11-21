package org.example.ragtest.parser.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.parser.DocumentParserStrategy;
import org.example.ragtest.parser.DocumentParserType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * MS Office 文档解析器策略
 * 使用 Apache POI 解析 MS Office 文件格式
 * 支持: DOC, DOCX, PPT, PPTX, XLS, XLSX 等
 * 
 * 需要添加依赖: langchain4j-document-parser-apache-poi
 */
@Slf4j
@Component
@ConditionalOnClass(name = "dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser")
public class MsOfficeDocumentParserStrategy implements DocumentParserStrategy {
    
    private final ApachePoiDocumentParser parser = new ApachePoiDocumentParser();
    
    /**
     * 支持的 MS Office 文件扩展名
     */
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        "doc",   // Word 97-2003
        "docx",  // Word 2007+
        "ppt",   // PowerPoint 97-2003
        "pptx",  // PowerPoint 2007+
        "xls",   // Excel 97-2003
        "xlsx"   // Excel 2007+
    );
    
    @Override
    public Document parse(InputStream inputStream) {
        log.debug("使用 MS Office 解析器解析文档");
        return parser.parse(inputStream);
    }
    
    @Override
    public DocumentParserType getParserType() {
        return DocumentParserType.MS_OFFICE;
    }
    
    @Override
    public boolean supports(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }
    
    @Override
    public Object getParserInstance() {
        return parser;
    }
}
