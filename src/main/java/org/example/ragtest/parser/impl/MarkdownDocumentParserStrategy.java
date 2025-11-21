package org.example.ragtest.parser.impl;

import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.parser.DocumentParserStrategy;
import org.example.ragtest.parser.DocumentParserType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Markdown 文档解析器策略
 * 需要添加依赖: langchain4j-document-parser-markdown
 * 
 * 如果依赖不存在，此类不会被加载
 */
@Slf4j
@Component
@ConditionalOnClass(name = "dev.langchain4j.data.document.parser.markdown.MarkdownDocumentParser")
public class MarkdownDocumentParserStrategy implements DocumentParserStrategy {
    
    private final Object parser;
    
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("md", "markdown");
    
    public MarkdownDocumentParserStrategy() {
        try {
            // 使用反射创建 MarkdownDocumentParser 实例
            Class<?> parserClass = Class.forName("dev.langchain4j.data.document.parser.markdown.MarkdownDocumentParser");
            this.parser = parserClass.getDeclaredConstructor().newInstance();
            log.info("Markdown 解析器已加载");
        } catch (Exception e) {
            throw new RuntimeException("无法加载 Markdown 解析器，请添加依赖: langchain4j-document-parser-markdown", e);
        }
    }
    
    @Override
    public Document parse(InputStream inputStream) {
        log.debug("使用 Markdown 解析器解析文档");
        try {
            return (Document) parser.getClass()
                    .getMethod("parse", InputStream.class)
                    .invoke(parser, inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Markdown 解析失败", e);
        }
    }
    
    @Override
    public DocumentParserType getParserType() {
        return DocumentParserType.MARKDOWN;
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
