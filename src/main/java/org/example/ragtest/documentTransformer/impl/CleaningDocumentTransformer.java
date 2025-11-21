package org.example.ragtest.documentTransformer.impl;

import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.documentTransformer.DocumentTransformerStrategy;
import org.example.ragtest.documentTransformer.DocumentTransformerType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 清理文档转换器
 * 去除文档中的不必要噪音：多余空白、特殊字符等
 */
@Slf4j
@Component
public class CleaningDocumentTransformer implements DocumentTransformerStrategy {
    
    @Override
    public Document transform(Document document) {
        if (document == null) {
            return null;
        }
        
        log.debug("清理文档，原始长度: {}", document.text().length());
        
        String cleanedText = cleanText(document.text());
        
        log.debug("清理完成，清理后长度: {}", cleanedText.length());
        
        // 创建新的文档对象，保留原有元数据
        return Document.from(cleanedText, document.metadata());
    }
    
    @Override
    public List<Document> transformAll(List<Document> documents) {
        log.info("开始批量清理 {} 个文档", documents.size());
        
        List<Document> cleaned = documents.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        
        log.info("批量清理完成");
        return cleaned;
    }
    
    /**
     * 清理文本
     * @param text 原始文本
     * @return 清理后的文本
     */
    private String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        return text
                // 去除多余的空白行（连续的换行符）
                .replaceAll("\\n{3,}", "\n\n")
                // 去除行尾空白
                .replaceAll("[ \\t]+\\n", "\n")
                // 去除行首空白（保留段落缩进）
                .replaceAll("\\n[ \\t]+", "\n")
                // 将多个空格替换为单个空格
                .replaceAll(" {2,}", " ")
                // 去除特殊控制字符（保留换行、制表符）
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                // 去除零宽字符
                .replaceAll("[\\u200B-\\u200D\\uFEFF]", "")
                // 去除首尾空白
                .trim();
    }
    
    @Override
    public DocumentTransformerType getTransformerType() {
        return DocumentTransformerType.CLEANING;
    }
    
    @Override
    public String getDescription() {
        return "清理转换器 (去除多余空白、特殊字符等噪音)";
    }
}
