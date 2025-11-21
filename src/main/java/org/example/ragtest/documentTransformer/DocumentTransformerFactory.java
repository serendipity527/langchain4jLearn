package org.example.ragtest.documentTransformer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.documentTransformer.impl.CompositeDocumentTransformer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档转换器工厂
 * 使用工厂模式管理和创建不同类型的文档转换器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentTransformerFactory {
    
    /**
     * 转换器策略列表
     * Spring自动注入所有 DocumentTransformerStrategy 实现
     */
    private final List<DocumentTransformerStrategy> transformerStrategies;
    
    /**
     * 根据转换器类型获取对应的转换器策略
     * @param transformerType 转换器类型
     * @return 对应的转换器策略
     */
    public DocumentTransformerStrategy getTransformer(DocumentTransformerType transformerType) {
        return transformerStrategies.stream()
                .filter(strategy -> strategy.getTransformerType() == transformerType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的转换器类型: " + transformerType));
    }
    
    /**
     * 创建组合转换器（按顺序应用多个转换器）
     * @param transformerTypes 转换器类型列表
     * @return 组合转换器
     */
    public DocumentTransformerStrategy createCompositeTransformer(List<DocumentTransformerType> transformerTypes) {
        log.info("创建组合转换器，包含 {} 个转换器", transformerTypes.size());
        
        List<DocumentTransformerStrategy> transformers = transformerTypes.stream()
                .map(this::getTransformer)
                .collect(Collectors.toList());
        
        return new CompositeDocumentTransformer(transformers);
    }
    
    /**
     * 创建组合转换器（可变参数）
     * @param transformerTypes 转换器类型
     * @return 组合转换器
     */
    public DocumentTransformerStrategy createCompositeTransformer(DocumentTransformerType... transformerTypes) {
        return createCompositeTransformer(List.of(transformerTypes));
    }
    
    /**
     * 创建默认的文档处理流程（推荐）
     * 清理 → 增强元数据 → 筛选
     * @return 组合转换器
     */
    public DocumentTransformerStrategy createDefaultPipeline() {
        log.info("创建默认文档处理流程");
        return createCompositeTransformer(
                DocumentTransformerType.CLEANING,
                DocumentTransformerType.METADATA_ENHANCER,
                DocumentTransformerType.FILTERING
        );
    }
    
    /**
     * 获取所有可用的转换器类型
     * @return 转换器类型到策略的映射
     */
    public Map<DocumentTransformerType, DocumentTransformerStrategy> getAllTransformers() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentTransformerStrategy::getTransformerType,
                        Function.identity()
                ));
    }
    
    /**
     * 列出所有可用的转换器及其描述
     * @return 转换器类型和描述的映射
     */
    public Map<DocumentTransformerType, String> listAvailableTransformers() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentTransformerStrategy::getTransformerType,
                        DocumentTransformerStrategy::getDescription
                ));
    }
    
    /**
     * 检查是否支持某个转换器类型
     * @param transformerType 转换器类型
     * @return 是否支持
     */
    public boolean isSupported(DocumentTransformerType transformerType) {
        return transformerStrategies.stream()
                .anyMatch(strategy -> strategy.getTransformerType() == transformerType);
    }
}
