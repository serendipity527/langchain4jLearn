package org.example.ragtest.parser.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.parser.DocumentParserStrategy;
import org.example.ragtest.parser.DocumentParserType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 文本文档解析器策略
 * 支持纯文本格式（TXT, HTML, MD等）
 */
@Slf4j
@Component
public class TextDocumentParserStrategy implements DocumentParserStrategy {
    
    private final TextDocumentParser parser = new TextDocumentParser();
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        "txt", "text", "html", "htm", "md", "markdown", "log", "csv", "json", "xml"
    );
    
    @Override
    public Document parse(InputStream inputStream) {
        log.debug("使用文本解析器解析文档");
        return parser.parse(inputStream);
    }
    
    @Override
    public DocumentParserType getParserType() {
        return DocumentParserType.TEXT;
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
