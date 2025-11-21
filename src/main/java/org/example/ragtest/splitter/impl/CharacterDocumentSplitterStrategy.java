package org.example.ragtest.splitter.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.splitter.DocumentSplitterStrategy;
import org.example.ragtest.splitter.DocumentSplitterType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 按字符分割文档策略
 * 按固定字符数分割，不考虑语义边界
 * 适合需要精确控制块大小的场景
 */
@Slf4j
@Component
public class CharacterDocumentSplitterStrategy implements DocumentSplitterStrategy {
    
    private final DocumentSplitter splitter;
    private final int maxSegmentSize;
    private final int maxOverlapSize;
    
    public CharacterDocumentSplitterStrategy(
            @Value("${document.splitter.max-segment-size:200}") int maxSegmentSize,
            @Value("${document.splitter.max-overlap-size:20}") int maxOverlapSize) {
        this.maxSegmentSize = maxSegmentSize;
        this.maxOverlapSize = maxOverlapSize;
        this.splitter = new DocumentByCharacterSplitter(maxSegmentSize, maxOverlapSize);
        log.info("初始化字符分割器: maxSegmentSize={}, maxOverlapSize={}", maxSegmentSize, maxOverlapSize);
    }
    
    @Override
    public List<TextSegment> split(Document document) {
        log.debug("使用字符分割器分割文档");
        return splitter.split(document);
    }
    
    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        log.debug("使用字符分割器批量分割 {} 个文档", documents.size());
        return splitter.splitAll(documents);
    }
    
    @Override
    public DocumentSplitterType getSplitterType() {
        return DocumentSplitterType.BY_CHARACTER;
    }
    
    @Override
    public DocumentSplitter getSplitterInstance() {
        return splitter;
    }
    
    @Override
    public String getDescription() {
        return String.format("字符分割器 (按字符数分割, 块大小: %d, 重叠: %d)", maxSegmentSize, maxOverlapSize);
    }
}
