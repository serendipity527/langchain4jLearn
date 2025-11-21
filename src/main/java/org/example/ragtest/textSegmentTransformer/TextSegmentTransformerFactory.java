package org.example.ragtest.textSegmentTransformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文本段转换器工厂
 * 使用工厂模式管理和创建不同类型的文本段转换器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextSegmentTransformerFactory {
    
    /**
     * 转换器策略列表
     * Spring自动注入所有 TextSegmentTransformerStrategy 实现
     */
    private final List<TextSegmentTransformerStrategy> transformerStrategies;
    
    /**
     * 根据转换器类型获取对应的转换器策略
     * @param transformerType 转换器类型
     * @return 对应的转换器策略
     */
    public TextSegmentTransformerStrategy getTransformer(TextSegmentTransformerType transformerType) {
        return transformerStrategies.stream()
                .filter(strategy -> strategy.getTransformerType() == transformerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的文本段转换器类型: " + transformerType));
    }
    
    /**
     * 创建默认的文本段增强流程（推荐）
     * 标题增强 → 元数据增强
     * @return 转换器列表
     */
    public List<TextSegmentTransformerStrategy> createDefaultPipeline() {
        log.info("创建默认文本段增强流程");
        return List.of(
                getTransformer(TextSegmentTransformerType.TITLE_ENHANCER),
                getTransformer(TextSegmentTransformerType.METADATA_ENHANCER)
        );
    }
    
    /**
     * 创建包含摘要的增强流程
     * 标题增强 → 摘要增强 → 元数据增强
     * @return 转换器列表
     */
    public List<TextSegmentTransformerStrategy> createFullEnhancementPipeline() {
        log.info("创建完整文本段增强流程");
        return List.of(
                getTransformer(TextSegmentTransformerType.TITLE_ENHANCER),
                getTransformer(TextSegmentTransformerType.SUMMARY_ENHANCER),
                getTransformer(TextSegmentTransformerType.METADATA_ENHANCER)
        );
    }
    
    /**
     * 获取所有可用的转换器类型
     * @return 转换器类型到策略的映射
     */
    public Map<TextSegmentTransformerType, TextSegmentTransformerStrategy> getAllTransformers() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        TextSegmentTransformerStrategy::getTransformerType,
                        Function.identity()
                ));
    }
    
    /**
     * 列出所有可用的转换器及其描述
     * @return 转换器类型和描述的映射
     */
    public Map<TextSegmentTransformerType, String> listAvailableTransformers() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        TextSegmentTransformerStrategy::getTransformerType,
                        TextSegmentTransformerStrategy::getDescription
                ));
    }
    
    /**
     * 检查是否支持某个转换器类型
     * @param transformerType 转换器类型
     * @return 是否支持
     */
    public boolean isSupported(TextSegmentTransformerType transformerType) {
        return transformerStrategies.stream()
                .anyMatch(strategy -> strategy.getTransformerType() == transformerType);
    }
}
