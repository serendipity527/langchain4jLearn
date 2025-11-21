package org.example.ragtest.textSegmentTransformer.impl;

import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerStrategy;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本段元数据增强转换器
 * 为文本段添加额外的元数据信息
 */
@Slf4j
@Component
public class MetadataEnhancerSegmentTransformer implements TextSegmentTransformerStrategy {
    
    @Override
    public TextSegment transform(TextSegment textSegment) {
        if (textSegment == null) {
            return null;
        }
        
        // 添加字符数统计
        int charCount = textSegment.text().length();
        textSegment.metadata().put("segment_char_count", charCount);
        
        // 添加单词数统计
        int wordCount = countWords(textSegment.text());
        textSegment.metadata().put("segment_word_count", wordCount);
        
        // 添加行数统计
        int lineCount = textSegment.text().split("\n").length;
        textSegment.metadata().put("segment_line_count", lineCount);
        
        log.debug("元数据增强完成: chars={}, words={}, lines={}", 
                charCount, wordCount, lineCount);
        
        return textSegment;
    }
    
    @Override
    public List<TextSegment> transformAll(List<TextSegment> textSegments) {
        log.info("开始为 {} 个文本段增强元数据", textSegments.size());
        
        List<TextSegment> enhanced = textSegments.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        
        log.info("文本段元数据增强完成");
        return enhanced;
    }
    
    /**
     * 统计单词数
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
    
    @Override
    public TextSegmentTransformerType getTransformerType() {
        return TextSegmentTransformerType.METADATA_ENHANCER;
    }
    
    @Override
    public String getDescription() {
        return "文本段元数据增强转换器 (添加字符数、单词数、行数等统计信息)";
    }
}
