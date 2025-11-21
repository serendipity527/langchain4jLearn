package org.example.ragtest.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文档处理服务
 * 负责文档的加载、分割和向量化存储
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 摄取文档到向量存储
     * @param text 文档文本内容
     */
    public void ingestDocument(String text) {
        log.info("开始摄取文档，文本长度: {}", text.length());
        
        // 创建文档对象
        Document document = Document.from(text);
        
        // 配置文档分割器
        DocumentSplitter splitter = DocumentSplitters.recursive(
                300,  // 每个块的最大字符数
                50    // 块之间的重叠字符数
        );
        
        // 创建嵌入存储摄取器
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        
        // 摄取文档
        ingestor.ingest(document);
        
        log.info("文档摄取完成");
    }

    /**
     * 批量摄取多个文档
     * @param documents 文档列表
     */
    public void ingestDocuments(List<Document> documents) {
        log.info("开始批量摄取文档，数量: {}", documents.size());
        
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        
        ingestor.ingest(documents);
        
        log.info("批量文档摄取完成");
    }
}
