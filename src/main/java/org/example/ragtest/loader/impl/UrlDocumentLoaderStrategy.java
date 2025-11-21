package org.example.ragtest.loader.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.loader.DocumentLoaderStrategy;
import org.example.ragtest.loader.DocumentLoaderType;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * URL文档加载器策略实现
 * 负责从URL加载文档
 */
@Slf4j
@Component
public class UrlDocumentLoaderStrategy implements DocumentLoaderStrategy {
    
    @Override
    public Document loadDocument(String sourcePath) {
        log.info("使用URL加载器加载文档: {}", sourcePath);
        
        try {
            URL url = new URL(sourcePath);
            Document document = UrlDocumentLoader.load(url, new TextDocumentParser());
            
            log.info("URL加载器加载完成: {}", sourcePath);
            return document;
        } catch (Exception e) {
            log.error("从URL加载文档失败: {}", sourcePath, e);
            throw new RuntimeException("从URL加载文档失败: " + sourcePath, e);
        }
    }
    
    @Override
    public List<Document> loadDocuments(String sourcePath) {
        // URL加载器不支持批量加载，将单个文档包装成列表返回
        log.info("URL加载器仅支持单个文档加载，将作为单个文档处理: {}", sourcePath);
        return Collections.singletonList(loadDocument(sourcePath));
    }
    
    @Override
    public DocumentLoaderType getLoaderType() {
        return DocumentLoaderType.URL;
    }
}
