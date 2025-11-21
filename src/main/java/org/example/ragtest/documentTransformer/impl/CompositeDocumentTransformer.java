package org.example.ragtest.documentTransformer.impl;

import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.documentTransformer.DocumentTransformerStrategy;
import org.example.ragtest.documentTransformer.DocumentTransformerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组合文档转换器
 * 按顺序应用多个转换器
 * 
 * 典型使用场景：
 * 1. 先清理 → 2. 再增强元数据 → 3. 最后筛选
 */
@Slf4j
public class CompositeDocumentTransformer implements DocumentTransformerStrategy {
    
    private final List<DocumentTransformerStrategy> transformers;
    
    public CompositeDocumentTransformer(List<DocumentTransformerStrategy> transformers) {
        this.transformers = new ArrayList<>(transformers);
    }
    
    public CompositeDocumentTransformer(DocumentTransformerStrategy... transformers) {
        this.transformers = Arrays.asList(transformers);
    }
    
    @Override
    public Document transform(Document document) {
        if (document == null) {
            return null;
        }
        
        log.debug("开始组合转换，应用 {} 个转换器", transformers.size());
        
        Document result = document;
        
        for (DocumentTransformerStrategy transformer : transformers) {
            if (!transformer.isEnabled()) {
                log.debug("跳过未启用的转换器: {}", transformer.getTransformerType());
                continue;
            }
            
            result = transformer.transform(result);
            
            // 如果某个转换器返回null（文档被过滤），则终止处理
            if (result == null) {
                log.debug("文档在 {} 转换器中被过滤", transformer.getTransformerType());
                return null;
            }
        }
        
        log.debug("组合转换完成");
        return result;
    }
    
    @Override
    public List<Document> transformAll(List<Document> documents) {
        log.info("开始组合批量转换 {} 个文档，使用 {} 个转换器", 
                documents.size(), transformers.size());
        
        List<Document> result = documents.stream()
                .map(this::transform)
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
        
        int removedCount = documents.size() - result.size();
        log.info("组合转换完成，保留 {} 个文档，过滤 {} 个文档", result.size(), removedCount);
        
        return result;
    }
    
    @Override
    public DocumentTransformerType getTransformerType() {
        return DocumentTransformerType.COMPOSITE;
    }
    
    @Override
    public String getDescription() {
        String transformerNames = transformers.stream()
                .map(t -> t.getTransformerType().toString())
                .collect(Collectors.joining(" → "));
        return String.format("组合转换器 (%s)", transformerNames);
    }
    
    /**
     * 添加转换器到链中
     */
    public CompositeDocumentTransformer addTransformer(DocumentTransformerStrategy transformer) {
        transformers.add(transformer);
        return this;
    }
    
    /**
     * 获取转换器链
     */
    public List<DocumentTransformerStrategy> getTransformers() {
        return new ArrayList<>(transformers);
    }
}
