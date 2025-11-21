package org.example.ragtest.splitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档分割器工厂
 * 使用工厂模式管理和创建不同类型的文档分割器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentSplitterFactory {
    
    /**
     * 分割器策略列表
     * Spring自动注入所有 DocumentSplitterStrategy 实现
     */
    private final List<DocumentSplitterStrategy> splitterStrategies;
    
    /**
     * 根据分割器类型获取对应的分割器策略
     * @param splitterType 分割器类型
     * @return 对应的分割器策略
     */
    public DocumentSplitterStrategy getSplitter(DocumentSplitterType splitterType) {
        return splitterStrategies.stream()
                .filter(strategy -> strategy.getSplitterType() == splitterType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的分割器类型: " + splitterType));
    }
    
    /**
     * 获取默认分割器（递归分割器）
     * @return 递归分割器策略
     */
    public DocumentSplitterStrategy getDefaultSplitter() {
        return getSplitter(DocumentSplitterType.RECURSIVE);
    }
    
    /**
     * 获取所有可用的分割器类型
     * @return 分割器类型到策略的映射
     */
    public Map<DocumentSplitterType, DocumentSplitterStrategy> getAllSplitters() {
        return splitterStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentSplitterStrategy::getSplitterType,
                        Function.identity()
                ));
    }
    
    /**
     * 列出所有可用的分割器及其描述
     * @return 分割器类型和描述的映射
     */
    public Map<DocumentSplitterType, String> listAvailableSplitters() {
        return splitterStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentSplitterStrategy::getSplitterType,
                        DocumentSplitterStrategy::getDescription
                ));
    }
    
    /**
     * 检查是否支持某个分割器类型
     * @param splitterType 分割器类型
     * @return 是否支持
     */
    public boolean isSupported(DocumentSplitterType splitterType) {
        return splitterStrategies.stream()
                .anyMatch(strategy -> strategy.getSplitterType() == splitterType);
    }
}
