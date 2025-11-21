package org.example.ragtest.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档解析器工厂
 * 使用工厂模式管理和创建不同类型的文档解析器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentParserFactory {
    
    /**
     * 解析器策略列表
     * Spring自动注入所有 DocumentParserStrategy 实现
     */
    private final List<DocumentParserStrategy> parserStrategies;
    
    /**
     * 根据解析器类型获取对应的解析器策略
     * @param parserType 解析器类型
     * @return 对应的解析器策略
     */
    public DocumentParserStrategy getParser(DocumentParserType parserType) {
        return parserStrategies.stream()
                .filter(strategy -> strategy.getParserType() == parserType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的解析器类型: " + parserType + 
                        "。可能需要添加相应的依赖。"));
    }
    
    /**
     * 根据文件扩展名自动选择解析器
     * @param fileExtension 文件扩展名（不含点，如 "pdf", "txt"）
     * @return 对应的解析器策略
     */
    public DocumentParserStrategy getParserByExtension(String fileExtension) {
        String ext = fileExtension.toLowerCase();
        
        return parserStrategies.stream()
                .filter(strategy -> strategy.supports(ext))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("未找到支持 {} 扩展名的解析器，使用默认文本解析器", ext);
                    return getParser(DocumentParserType.TEXT);
                });
    }
    
    /**
     * 根据文件名自动选择解析器
     * @param fileName 文件名（如 "example.pdf"）
     * @return 对应的解析器策略
     */
    public DocumentParserStrategy getParserByFileName(String fileName) {
        String extension = getFileExtension(fileName);
        return getParserByExtension(extension);
    }
    
    /**
     * 获取所有可用的解析器类型
     * @return 解析器类型到策略的映射
     */
    public Map<DocumentParserType, DocumentParserStrategy> getAllParsers() {
        return parserStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentParserStrategy::getParserType,
                        Function.identity()
                ));
    }
    
    /**
     * 检查是否支持某个文件扩展名
     * @param fileExtension 文件扩展名
     * @return 是否支持
     */
    public boolean isSupported(String fileExtension) {
        return parserStrategies.stream()
                .anyMatch(strategy -> strategy.supports(fileExtension.toLowerCase()));
    }
    
    /**
     * 从文件名提取扩展名
     * @param fileName 文件名
     * @return 扩展名（不含点）
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return "";
    }
}
