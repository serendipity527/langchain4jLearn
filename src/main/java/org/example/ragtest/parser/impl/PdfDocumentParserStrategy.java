package org.example.ragtest.parser.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.parser.DocumentParserStrategy;
import org.example.ragtest.parser.DocumentParserType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * PDF 文档解析器策略
 * 使用 Apache PDFBox 解析 PDF 文件
 */
@Slf4j
@Component
public class PdfDocumentParserStrategy implements DocumentParserStrategy {
    
    private final ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("pdf");
    
    @Override
    public Document parse(InputStream inputStream) {
        log.debug("使用 PDF 解析器解析文档");
        return parser.parse(inputStream);
    }
    
    @Override
    public DocumentParserType getParserType() {
        return DocumentParserType.PDF;
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
