package org.example.ragtest.splitter.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.splitter.DocumentSplitterStrategy;
import org.example.ragtest.splitter.DocumentSplitterType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 按段落分割文档策略
 * 按段落边界分割文档，适合结构化的文档
 */
@Slf4j
@Component
public class ParagraphDocumentSplitterStrategy implements DocumentSplitterStrategy {
    
    private final DocumentSplitter splitter;
    private final int maxSegmentSize;
    private final int maxOverlapSize;
    
    public ParagraphDocumentSplitterStrategy(
            @Value("${document.splitter.max-segment-size:500}") int maxSegmentSize,
            @Value("${document.splitter.max-overlap-size:50}") int maxOverlapSize) {
        this.maxSegmentSize = maxSegmentSize;
        this.maxOverlapSize = maxOverlapSize;
        this.splitter = new DocumentByParagraphSplitter(maxSegmentSize, maxOverlapSize);
        log.info("初始化段落分割器: maxSegmentSize={}, maxOverlapSize={}", maxSegmentSize, maxOverlapSize);
    }
    
    @Override
    public List<TextSegment> split(Document document) {
        log.debug("使用段落分割器分割文档");
        return splitter.split(document);
    }
    
    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        log.debug("使用段落分割器批量分割 {} 个文档", documents.size());
        return splitter.splitAll(documents);
    }
    
    @Override
    public DocumentSplitterType getSplitterType() {
        return DocumentSplitterType.BY_PARAGRAPH;
    }
    
    @Override
    public DocumentSplitter getSplitterInstance() {
        return splitter;
    }
    
    @Override
    public String getDescription() {
        return String.format("段落分割器 (按段落分割, 块大小: %d, 重叠: %d)", maxSegmentSize, maxOverlapSize);
    }
}
