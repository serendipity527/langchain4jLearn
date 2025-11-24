package org.example.ragtest.retrieval.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询转换器工厂
 * 使用工厂模式管理和创建不同类型的查询转换器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueryTransformerFactory {
    
    /**
     * 转换器策略列表
     * Spring自动注入所有 QueryTransformerStrategy 实现
     */
    private final List<QueryTransformerStrategy> transformerStrategies;
    
    /**
     * 根据转换器类型获取对应的转换器策略
     * @param transformerType 转换器类型
     * @return 对应的转换器策略
     */
    public Optional<QueryTransformerStrategy> getStrategy(QueryTransformerType transformerType) {
        return transformerStrategies.stream()
                .filter(strategy -> strategy.getTransformerType() == transformerType)
                .findFirst();
    }
    
    /**
     * 获取默认的查询转换器策略
     * @return 默认策略
     */
    public QueryTransformerStrategy getDefaultStrategy() {
        log.debug("获取默认查询转换器策略");
        return getStrategy(QueryTransformerType.DEFAULT)
                .orElseThrow(() -> new IllegalStateException("默认查询转换器未找到"));
    }
    
    /**
     * 获取压缩查询转换器策略
     * @return 压缩策略（如果可用）
     */
    public Optional<QueryTransformerStrategy> getCompressingStrategy() {
        log.info("获取压缩查询转换器策略");
        return getStrategy(QueryTransformerType.COMPRESSING);
    }
    
    /**
     * 获取扩展查询转换器策略
     * @return 扩展策略（如果可用）
     */
    public Optional<QueryTransformerStrategy> getExpandingStrategy() {
        log.info("获取扩展查询转换器策略");
        return getStrategy(QueryTransformerType.EXPANDING);
    }
    
    /**
     * 获取所有可用的转换器策略
     * @return 转换器类型到策略的映射
     */
    public Map<QueryTransformerType, QueryTransformerStrategy> getAllStrategies() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        QueryTransformerStrategy::getTransformerType,
                        Function.identity()
                ));
    }
    
    /**
     * 列出所有可用的转换器及其描述
     * @return 转换器类型和描述的映射
     */
    public Map<QueryTransformerType, String> listAvailableTransformers() {
        return transformerStrategies.stream()
                .collect(Collectors.toMap(
                        QueryTransformerStrategy::getTransformerType,
                        QueryTransformerStrategy::getDescription
                ));
    }
    
    /**
     * 检查是否支持某个转换器类型
     * @param transformerType 转换器类型
     * @return 是否支持
     */
    public boolean isSupported(QueryTransformerType transformerType) {
        return transformerStrategies.stream()
                .anyMatch(strategy -> strategy.getTransformerType() == transformerType);
    }
    
    /**
     * 获取所有启用的转换器策略
     * @return 启用的策略列表
     */
    public List<QueryTransformerStrategy> getEnabledStrategies() {
        return transformerStrategies.stream()
                .filter(QueryTransformerStrategy::isEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查是否有需要LLM的转换器可用
     * @return 是否有高级转换器可用
     */
    public boolean hasAdvancedTransformers() {
        return isSupported(QueryTransformerType.COMPRESSING) || 
               isSupported(QueryTransformerType.EXPANDING);
    }
}
