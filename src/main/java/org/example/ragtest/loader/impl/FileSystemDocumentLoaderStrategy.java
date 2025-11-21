package org.example.ragtest.loader.impl;

import dev.langchain4j.data.document.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.loader.DocumentLoaderStrategy;
import org.example.ragtest.loader.DocumentLoaderType;
import org.example.ragtest.parser.DocumentParserFactory;
import org.example.ragtest.parser.DocumentParserStrategy;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件系统文档加载器策略实现
 * 负责从本地文件系统加载文档
 * 使用解析器工厂自动选择合适的解析器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileSystemDocumentLoaderStrategy implements DocumentLoaderStrategy {
    
    private final DocumentParserFactory parserFactory;
    
    @Override
    @SuppressWarnings("unchecked")
    public Document loadDocument(String sourcePath) {
        log.info("使用文件系统加载器加载文档: {}", sourcePath);
        
        Path path = Paths.get(sourcePath);
        
        // 使用解析器工厂根据文件名自动选择合适的解析器
        DocumentParserStrategy parserStrategy = parserFactory.getParserByFileName(sourcePath);
        log.debug("选择解析器: {}", parserStrategy.getParserType());
        
        // 加载文档 - 使用类型擦除的泛型处理
        Object parserInstance = parserStrategy.getParserInstance();
        Document document = loadDocumentWithParser(path, parserInstance);
        
        log.info("文件系统加载器加载完成: {}", sourcePath);
        return document;
    }
    
    /**
     * 使用指定解析器加载文档
     * 这个方法使用反射来避免类型检查问题
     */
    private Document loadDocumentWithParser(Path path, Object parser) {
        try {
            Class<?> loaderClass = dev.langchain4j.data.document.loader.FileSystemDocumentLoader.class;
            return (Document) loaderClass
                    .getMethod("loadDocument", Path.class, Object.class)
                    .invoke(null, path, parser);
        } catch (NoSuchMethodException e) {
            // 尝试直接调用，依赖类型擦除
            return dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument(
                    path, (dev.langchain4j.data.document.parser.TextDocumentParser) parser);
        } catch (Exception e) {
            throw new RuntimeException("加载文档失败: " + path, e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Document> loadDocuments(String sourcePath) {
        log.info("使用文件系统加载器批量加载文档，目录: {}", sourcePath);
        
        Path path = Paths.get(sourcePath);
        
        // 批量加载时使用文本解析器作为默认解析器
        // 如果需要支持多种格式的混合目录，可以遍历文件分别处理
        DocumentParserStrategy parserStrategy = parserFactory.getParserByFileName("default.txt");
        
        List<Document> documents = loadDocumentsWithParser(path, parserStrategy.getParserInstance());
        
        log.info("文件系统加载器批量加载完成，共 {} 个文档", documents.size());
        return documents;
    }
    
    /**
     * 使用指定解析器批量加载文档
     */
    private List<Document> loadDocumentsWithParser(Path path, Object parser) {
        try {
            Class<?> loaderClass = dev.langchain4j.data.document.loader.FileSystemDocumentLoader.class;
            return (List<Document>) loaderClass
                    .getMethod("loadDocuments", Path.class, Object.class)
                    .invoke(null, path, parser);
        } catch (NoSuchMethodException e) {
            // 尝试直接调用，依赖类型擦除
            return dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments(
                    path, (dev.langchain4j.data.document.parser.TextDocumentParser) parser);
        } catch (Exception e) {
            throw new RuntimeException("批量加载文档失败: " + path, e);
        }
    }
    
    @Override
    public DocumentLoaderType getLoaderType() {
        return DocumentLoaderType.FILE_SYSTEM;
    }
}
