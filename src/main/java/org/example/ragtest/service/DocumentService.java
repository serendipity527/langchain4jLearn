package org.example.ragtest.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.loader.DocumentLoaderFactory;
import org.example.ragtest.loader.DocumentLoaderStrategy;
import org.example.ragtest.loader.DocumentLoaderType;
import org.example.ragtest.splitter.DocumentSplitterFactory;
import org.example.ragtest.splitter.DocumentSplitterStrategy;
import org.example.ragtest.splitter.DocumentSplitterType;
import org.example.ragtest.documentTransformer.DocumentTransformerFactory;
import org.example.ragtest.documentTransformer.DocumentTransformerStrategy;
import org.example.ragtest.documentTransformer.DocumentTransformerType;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerFactory;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文档处理服务
 * 负责文档的加载、转换、分割和向量化存储
 * 使用策略模式和工厂模式管理不同类型的文档加载器、转换器、分割器和文本段转换器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentLoaderFactory loaderFactory;
    private final DocumentSplitterFactory splitterFactory;
    private final DocumentTransformerFactory transformerFactory;
    private final TextSegmentTransformerFactory textSegmentTransformerFactory;

    /**
     * 摄取文本到向量存储（使用默认分割器）
     * @param text 文本内容
     */
    public void ingestDocument(String text) {
        log.info("开始摄取文档，文本长度: {}", text.length());
        Document document = Document.from(text);
        ingestDocuments(List.of(document));
        log.info("文档摄取完成");
    }

    /**
     * 批量摄取多个文档（使用默认分割器）
     * @param documents 文档列表
     */
    public void ingestDocuments(List<Document> documents) {
        ingestDocuments(documents, DocumentSplitterType.RECURSIVE);
    }
    
    /**
     * 批量摄取多个文档（指定分割器类型）
     * @param documents 文档列表
     * @param splitterType 分割器类型
     */
    public void ingestDocuments(List<Document> documents, DocumentSplitterType splitterType) {
        ingestDocuments(documents, splitterType, false);
    }
    
    /**
     * 批量摄取多个文档（指定分割器类型和是否应用转换器）
     * @param documents 文档列表
     * @param splitterType 分割器类型
     * @param applyTransformation 是否应用默认转换流程
     */
    public void ingestDocuments(List<Document> documents, DocumentSplitterType splitterType, boolean applyTransformation) {
        ingestDocuments(documents, splitterType, applyTransformation, true);
    }
    
    /**
     * 批量摄取多个文档（完整配置）
     * @param documents 文档列表
     * @param splitterType 分割器类型
     * @param applyDocTransformation 是否应用文档转换器
     * @param applySegmentEnhancement 是否应用文本段增强
     */
    public void ingestDocuments(List<Document> documents, DocumentSplitterType splitterType, 
                                boolean applyDocTransformation, boolean applySegmentEnhancement) {
        log.info("开始批量摄取文档，数量: {}, 分割器: {}, 文档转换: {}, 文本段增强: {}", 
                documents.size(), splitterType, applyDocTransformation, applySegmentEnhancement);
        
        // 1. 如果需要，先应用文档转换器
        List<Document> processedDocuments = documents;
        if (applyDocTransformation) {
            DocumentTransformerStrategy transformer = transformerFactory.createDefaultPipeline();
            processedDocuments = transformer.transformAll(documents);
            log.info("文档转换后数量: {}", processedDocuments.size());
        }
        
        // 2. 使用工厂获取指定类型的分割器
        DocumentSplitterStrategy splitterStrategy = splitterFactory.getSplitter(splitterType);
        DocumentSplitter splitter = splitterStrategy.getSplitterInstance();
        log.debug("分割器配置: {}", splitterStrategy.getDescription());
        
        // 3. 创建嵌入存储摄取器（配置文本段转换器）
        var builder = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore);
        
        // 4. 如果需要，添加文本段转换器
        if (applySegmentEnhancement) {
            List<TextSegmentTransformerStrategy> segmentTransformers = 
                    textSegmentTransformerFactory.createDefaultPipeline();
            
            // 将文本段转换器应用到摄取过程（逐个应用）
            builder.textSegmentTransformer(segment -> {
                TextSegment result = segment;
                for (TextSegmentTransformerStrategy transformer : segmentTransformers) {
                    result = transformer.transform(result);
                    if (result == null) {
                        break;  // 如果被过滤，停止处理
                    }
                }
                return result;
            });
            
            log.info("已配置 {} 个文本段转换器", segmentTransformers.size());
        }
        
        EmbeddingStoreIngestor ingestor = builder.build();
        ingestor.ingest(processedDocuments);
        
        log.info("批量文档摄取完成");
    }

    // ==================== 使用策略模式的文档加载方法 ====================

    /**
     * 从文件系统加载并摄取单个文档
     * @param filePath 文件路径
     */
    public void ingestDocumentFromFile(String filePath) {
        DocumentLoaderStrategy loader = loaderFactory.getLoader(DocumentLoaderType.FILE_SYSTEM);
        Document document = loader.loadDocument(filePath);
        ingestDocuments(List.of(document));
    }

    /**
     * 从文件系统批量加载并摄取文档（从目录）
     * @param directoryPath 目录路径
     */
    public void ingestDocumentsFromDirectory(String directoryPath) {
        DocumentLoaderStrategy loader = loaderFactory.getLoader(DocumentLoaderType.FILE_SYSTEM);
        List<Document> documents = loader.loadDocuments(directoryPath);
        ingestDocuments(documents);
    }

    /**
     * 从 URL 加载并摄取文档
     * @param urlString URL 地址
     */
    public void ingestDocumentFromUrl(String urlString) {
        DocumentLoaderStrategy loader = loaderFactory.getLoader(DocumentLoaderType.URL);
        Document document = loader.loadDocument(urlString);
        ingestDocuments(List.of(document));
    }

    /**
     * 从类路径加载并摄取文档
     * @param resourcePath 类路径资源路径（相对于 resources 目录）
     */
    public void ingestDocumentFromClasspath(String resourcePath) {
        DocumentLoaderStrategy loader = loaderFactory.getLoader(DocumentLoaderType.CLASSPATH);
        Document document = loader.loadDocument(resourcePath);
        ingestDocuments(List.of(document));
    }

    /**
     * 动态选择加载器类型并加载文档（推荐使用）
     * @param loaderType 加载器类型：FILE_SYSTEM, URL, CLASSPATH
     * @param sourcePath 源路径（文件路径、URL 或类路径）
     */
    public void ingestDocumentByLoaderType(DocumentLoaderType loaderType, String sourcePath) {
        log.info("使用 {} 加载器加载文档: {}", loaderType, sourcePath);
        
        DocumentLoaderStrategy loader = loaderFactory.getLoader(loaderType);
        Document document = loader.loadDocument(sourcePath);
        ingestDocuments(List.of(document));
    }

    /**
     * 批量加载文档（根据加载器类型）
     * @param loaderType 加载器类型
     * @param sourcePath 源路径
     */
    public void ingestDocumentsByLoaderType(DocumentLoaderType loaderType, String sourcePath) {
        log.info("使用 {} 加载器批量加载文档: {}", loaderType, sourcePath);
        
        DocumentLoaderStrategy loader = loaderFactory.getLoader(loaderType);
        List<Document> documents = loader.loadDocuments(sourcePath);
        ingestDocuments(documents);
    }
    
    // ==================== 使用策略模式的文档分割方法 ====================
    
    /**
     * 动态选择分割器并摄取文档
     * @param loaderType 加载器类型
     * @param sourcePath 源路径
     * @param splitterType 分割器类型
     */
    public void ingestDocumentWithCustomSplitter(
            DocumentLoaderType loaderType, 
            String sourcePath, 
            DocumentSplitterType splitterType) {
        log.info("使用 {} 加载器和 {} 分割器处理文档: {}", loaderType, splitterType, sourcePath);
        
        DocumentLoaderStrategy loader = loaderFactory.getLoader(loaderType);
        Document document = loader.loadDocument(sourcePath);
        ingestDocuments(List.of(document), splitterType);
    }
    
    /**
     * 获取可用的分割器列表
     * @return 分割器类型和描述的映射
     */
    public java.util.Map<DocumentSplitterType, String> getAvailableSplitters() {
        return splitterFactory.listAvailableSplitters();
    }
    
    // ==================== 使用策略模式的文档转换方法 ====================
    
    /**
     * 使用指定转换器处理文档并摄取
     * @param loaderType 加载器类型
     * @param sourcePath 源路径
     * @param transformerTypes 转换器类型列表
     * @param splitterType 分割器类型
     */
    public void ingestDocumentWithTransformers(
            DocumentLoaderType loaderType,
            String sourcePath,
            List<DocumentTransformerType> transformerTypes,
            DocumentSplitterType splitterType) {
        log.info("使用 {} 加载器、{} 个转换器和 {} 分割器处理文档: {}", 
                loaderType, transformerTypes.size(), splitterType, sourcePath);
        
        // 加载文档
        DocumentLoaderStrategy loader = loaderFactory.getLoader(loaderType);
        Document document = loader.loadDocument(sourcePath);
        
        // 应用转换器
        DocumentTransformerStrategy transformer = transformerFactory.createCompositeTransformer(transformerTypes);
        Document transformed = transformer.transform(document);
        
        if (transformed != null) {
            ingestDocuments(List.of(transformed), splitterType);
        } else {
            log.warn("文档在转换过程中被过滤");
        }
    }
    
    /**
     * 获取可用的转换器列表
     * @return 转换器类型和描述的映射
     */
    public java.util.Map<DocumentTransformerType, String> getAvailableTransformers() {
        return transformerFactory.listAvailableTransformers();
    }

}
