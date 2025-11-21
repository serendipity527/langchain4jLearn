package org.example.ragtest.documentTransformer.impl;

import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.documentTransformer.DocumentTransformerStrategy;
import org.example.ragtest.documentTransformer.DocumentTransformerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 筛选文档转换器
 * 根据设定的规则过滤文档
 */
@Slf4j
@Component
public class FilteringDocumentTransformer implements DocumentTransformerStrategy {
    
    @Value("${document.transformer.filter.min-length:50}")
    private int minLength;
    
    @Value("${document.transformer.filter.max-length:50000}")
    private int maxLength;
    
    @Override
    public Document transform(Document document) {
        if (document == null) {
            return null;
        }
        
        String text = document.text();
        int length = text.length();
        
        // 长度过滤
        if (length < minLength) {
            log.debug("文档被过滤：长度 {} 小于最小长度 {}", length, minLength);
            return null;
        }
        
        if (length > maxLength) {
            log.debug("文档被过滤：长度 {} 大于最大长度 {}", length, maxLength);
            return null;
        }
        
        // 空白内容过滤
        if (text.trim().isEmpty()) {
            log.debug("文档被过滤：内容为空");
            return null;
        }
        
        // 可以添加更多过滤规则...
        
        return document;
    }
    
    @Override
    public List<Document> transformAll(List<Document> documents) {
        log.info("开始筛选 {} 个文档", documents.size());
        
        List<Document> filtered = documents.stream()
                .map(this::transform)
                .filter(doc -> doc != null)  // 移除被过滤的文档
                .collect(Collectors.toList());
        
        int removedCount = documents.size() - filtered.size();
        log.info("筛选完成，保留 {} 个文档，过滤 {} 个文档", filtered.size(), removedCount);
        
        return filtered;
    }
    
    @Override
    public DocumentTransformerType getTransformerType() {
        return DocumentTransformerType.FILTERING;
    }
    
    @Override
    public String getDescription() {
        return String.format("筛选转换器 (最小长度: %d, 最大长度: %d)", minLength, maxLength);
    }
}
