package org.example.ragtest.loader.impl;

import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.loader.DocumentLoaderStrategy;
import org.example.ragtest.loader.DocumentLoaderType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * 类路径文档加载器策略实现
 * 负责从类路径（resources目录）加载文档
 */
@Slf4j
@Component
public class ClasspathDocumentLoaderStrategy implements DocumentLoaderStrategy {
    
    @Override
    public Document loadDocument(String sourcePath) {
        log.info("从类路径加载文档: {}", sourcePath);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("类路径资源未找到: " + sourcePath);
            }
            
            // 读取输入流内容
            String content = new String(inputStream.readAllBytes());
            Document document = Document.from(content);
            
            log.info("类路径加载器加载完成: {}", sourcePath);
            return document;
        } catch (Exception e) {
            log.error("从类路径加载文档失败: {}", sourcePath, e);
            throw new RuntimeException("从类路径加载文档失败: " + sourcePath, e);
        }
    }
    
    @Override
    public List<Document> loadDocuments(String sourcePath) {
        // 类路径加载器不支持批量加载，将单个文档包装成列表返回
        log.info("类路径加载器仅支持单个文档加载，将作为单个文档处理: {}", sourcePath);
        return Collections.singletonList(loadDocument(sourcePath));
    }
    
    @Override
    public DocumentLoaderType getLoaderType() {
        return DocumentLoaderType.CLASSPATH;
    }
}
