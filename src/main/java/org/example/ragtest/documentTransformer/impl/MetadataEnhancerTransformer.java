package org.example.ragtest.documentTransformer.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.documentTransformer.DocumentTransformerStrategy;
import org.example.ragtest.documentTransformer.DocumentTransformerType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元数据增强转换器
 * 为文档添加额外的元数据信息，如字数统计、处理时间等
 */
@Slf4j
@Component
public class MetadataEnhancerTransformer implements DocumentTransformerStrategy {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public Document transform(Document document) {
        if (document == null) {
            return null;
        }
        
        log.debug("增强文档元数据");
        
        // 获取现有元数据或创建新的
        Metadata metadata = document.metadata();
        
        // 添加字数统计
        int wordCount = countWords(document.text());
        metadata.put("word_count", wordCount);
        
        // 添加字符数统计
        int charCount = document.text().length();
        metadata.put("char_count", charCount);
        
        // 添加处理时间
        String processTime = LocalDateTime.now().format(FORMATTER);
        metadata.put("processed_at", processTime);
        
        // 添加文本长度分类
        String lengthCategory = categorizeLength(charCount);
        metadata.put("length_category", lengthCategory);
        
        // 检测语言（简单判断）
        String language = detectLanguage(document.text());
        metadata.put("language", language);
        
        log.debug("元数据增强完成: words={}, chars={}, category={}", 
                wordCount, charCount, lengthCategory);
        
        return Document.from(document.text(), metadata);
    }
    
    @Override
    public List<Document> transformAll(List<Document> documents) {
        log.info("开始批量增强 {} 个文档的元数据", documents.size());
        
        List<Document> enhanced = documents.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        
        log.info("批量元数据增强完成");
        return enhanced;
    }
    
    /**
     * 统计单词数（简单按空白字符分割）
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
    
    /**
     * 根据字符数对文档长度进行分类
     */
    private String categorizeLength(int charCount) {
        if (charCount < 500) {
            return "short";
        } else if (charCount < 2000) {
            return "medium";
        } else if (charCount < 10000) {
            return "long";
        } else {
            return "very_long";
        }
    }
    
    /**
     * 检测文档语言（简单判断：是否包含中文）
     */
    private String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        
        // 检查是否包含中文字符
        boolean hasChinese = text.chars()
                .anyMatch(c -> Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        
        return hasChinese ? "zh" : "en";
    }
    
    @Override
    public DocumentTransformerType getTransformerType() {
        return DocumentTransformerType.METADATA_ENHANCER;
    }
    
    @Override
    public String getDescription() {
        return "元数据增强转换器 (添加字数、字符数、处理时间、语言等信息)";
    }
}
