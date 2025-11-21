package org.example.ragtest.splitter.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.splitter.DocumentSplitterStrategy;
import org.example.ragtest.splitter.DocumentSplitterType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 递归文档分割器策略
 * 使用递归方式智能分割文档，这是最推荐的分割方式
 * 
 * 特点：
 * - 智能分割：优先按段落、句子等自然边界分割
 * - 可配置：支持自定义块大小和重叠大小
 * - 通用性强：适用于大多数场景
 */
@Slf4j
@Component
public class RecursiveDocumentSplitterStrategy implements DocumentSplitterStrategy {
    
    private final DocumentSplitter splitter;
    
    @Value("${document.splitter.max-segment-size:300}")
    private int maxSegmentSize;
    
    @Value("${document.splitter.max-overlap-size:50}")
    private int maxOverlapSize;
    
    public RecursiveDocumentSplitterStrategy(
            @Value("${document.splitter.max-segment-size:300}") int maxSegmentSize,
            @Value("${document.splitter.max-overlap-size:50}") int maxOverlapSize) {
        this.maxSegmentSize = maxSegmentSize;
        this.maxOverlapSize = maxOverlapSize;
        this.splitter = DocumentSplitters.recursive(maxSegmentSize, maxOverlapSize);
        log.info("初始化递归分割器: maxSegmentSize={}, maxOverlapSize={}", maxSegmentSize, maxOverlapSize);
    }
    
    @Override
    public List<TextSegment> split(Document document) {
        log.debug("使用递归分割器分割文档");
        return splitter.split(document);
    }
    
    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        log.debug("使用递归分割器批量分割 {} 个文档", documents.size());
        return splitter.splitAll(documents);
    }
    
    @Override
    public DocumentSplitterType getSplitterType() {
        return DocumentSplitterType.RECURSIVE;
    }
    
    @Override
    public DocumentSplitter getSplitterInstance() {
        return splitter;
    }
    
    @Override
    public String getDescription() {
        return String.format("递归分割器 (智能分割, 块大小: %d, 重叠: %d)", maxSegmentSize, maxOverlapSize);
    }
}
