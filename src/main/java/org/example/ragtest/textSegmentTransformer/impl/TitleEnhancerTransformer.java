package org.example.ragtest.textSegmentTransformer.impl;

import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerStrategy;
import org.example.ragtest.textSegmentTransformer.TextSegmentTransformerType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标题增强转换器
 * 在每个文本段前添加文档标题，提高检索效果
 * 
 * 使用场景：
 * - 当文档有明确的标题时，将标题添加到每个文本段
 * - 帮助RAG系统更好地理解文本段的上下文
 * - 提高检索的准确性
 */
@Slf4j
@Component
public class TitleEnhancerTransformer implements TextSegmentTransformerStrategy {
    
    private static final String TITLE_KEY = "title";
    private static final String DOCUMENT_TITLE_KEY = "document_title";
    
    @Override
    public TextSegment transform(TextSegment textSegment) {
        if (textSegment == null) {
            return null;
        }
        
        // 从元数据中获取标题
        String title = getTitle(textSegment);
        
        if (title == null || title.trim().isEmpty()) {
            log.debug("文本段没有标题信息，跳过增强");
            return textSegment;
        }
        
        // 在文本段前添加标题
        String enhancedText = String.format("【文档标题: %s】\n\n%s", 
                title.trim(), textSegment.text());
        
        log.debug("为文本段添加标题: {}", title);
        
        return TextSegment.from(enhancedText, textSegment.metadata());
    }
    
    @Override
    public List<TextSegment> transformAll(List<TextSegment> textSegments) {
        log.info("开始为 {} 个文本段添加标题", textSegments.size());
        
        List<TextSegment> enhanced = textSegments.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        
        log.info("标题增强完成");
        return enhanced;
    }
    
    /**
     * 从元数据中获取标题
     * 尝试多个可能的标题字段
     */
    private String getTitle(TextSegment textSegment) {
        if (textSegment.metadata() == null) {
            return null;
        }
        
        // 尝试多个可能的标题字段
        String title = textSegment.metadata().getString(TITLE_KEY);
        if (title == null || title.trim().isEmpty()) {
            title = textSegment.metadata().getString(DOCUMENT_TITLE_KEY);
        }
        if (title == null || title.trim().isEmpty()) {
            title = textSegment.metadata().getString("file_name");
        }
        
        return title;
    }
    
    @Override
    public TextSegmentTransformerType getTransformerType() {
        return TextSegmentTransformerType.TITLE_ENHANCER;
    }
    
    @Override
    public String getDescription() {
        return "标题增强转换器 (在每个文本段前添加文档标题，提高检索效果)";
    }
}
