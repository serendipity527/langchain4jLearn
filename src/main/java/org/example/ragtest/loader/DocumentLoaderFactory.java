package org.example.ragtest.loader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档加载器工厂
 * 使用工厂模式管理和创建不同类型的文档加载器
 */
@Component
@RequiredArgsConstructor
public class DocumentLoaderFactory {
    
    /**
     * 策略映射表
     * Spring自动注入所有 DocumentLoaderStrategy 实现
     */
    private final List<DocumentLoaderStrategy> loaderStrategies;
    
    /**
     * 根据加载器类型获取对应的加载器策略
     * @param loaderType 加载器类型
     * @return 对应的加载器策略
     */
    public DocumentLoaderStrategy getLoader(DocumentLoaderType loaderType) {
        return loaderStrategies.stream()
                .filter(strategy -> strategy.getLoaderType() == loaderType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的加载器类型: " + loaderType));
    }
    
    /**
     * 获取所有可用的加载器类型
     * @return 加载器类型到策略的映射
     */
    public Map<DocumentLoaderType, DocumentLoaderStrategy> getAllLoaders() {
        return loaderStrategies.stream()
                .collect(Collectors.toMap(
                        DocumentLoaderStrategy::getLoaderType,
                        Function.identity()
                ));
    }
}
