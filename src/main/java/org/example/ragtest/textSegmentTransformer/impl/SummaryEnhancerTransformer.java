package org.example.ragtest.textSegmentTransformer.impl;

import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerStrategy;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 摘要增强转换器
 * 在每个文本段前添加文档摘要，提供上下文信息
 * 
 * 使用场景：
 * - 当文档有摘要时，将摘要添加到每个文本段
 * - 提供文档级别的上下文信息
 * - 帮助理解文本段在整个文档中的位置和作用
 */
@Slf4j
@Component
public class SummaryEnhancerTransformer implements TextSegmentTransformerStrategy {
    
    private static final String SUMMARY_KEY = "summary";
    private static final String DOCUMENT_SUMMARY_KEY = "document_summary";
    
    @Override
    public TextSegment transform(TextSegment textSegment) {
        if (textSegment == null) {
            return null;
        }
        
        // 从元数据中获取摘要
        String summary = getSummary(textSegment);
        
        if (summary == null || summary.trim().isEmpty()) {
            log.debug("文本段没有摘要信息，跳过增强");
            return textSegment;
        }
        
        // 在文本段前添加摘要
        String enhancedText = String.format("【文档摘要: %s】\n\n%s", 
                summary.trim(), textSegment.text());
        
        log.debug("为文本段添加摘要，摘要长度: {}", summary.length());
        
        return TextSegment.from(enhancedText, textSegment.metadata());
    }
    
    @Override
    public List<TextSegment> transformAll(List<TextSegment> textSegments) {
        log.info("开始为 {} 个文本段添加摘要", textSegments.size());
        
        List<TextSegment> enhanced = textSegments.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        
        log.info("摘要增强完成");
        return enhanced;
    }
    
    /**
     * 从元数据中获取摘要
     */
    private String getSummary(TextSegment textSegment) {
        if (textSegment.metadata() == null) {
            return null;
        }
        
        // 尝试多个可能的摘要字段
        String summary = textSegment.metadata().getString(SUMMARY_KEY);
        if (summary == null || summary.trim().isEmpty()) {
            summary = textSegment.metadata().getString(DOCUMENT_SUMMARY_KEY);
        }
        
        return summary;
    }
    
    @Override
    public TextSegmentTransformerType getTransformerType() {
        return TextSegmentTransformerType.SUMMARY_ENHANCER;
    }
    
    @Override
    public String getDescription() {
        return "摘要增强转换器 (在每个文本段前添加文档摘要，提供上下文信息)";
    }
}
