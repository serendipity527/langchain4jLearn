package org.example.ragtest.retrieval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 检索增强器工厂
 * 使用工厂模式管理和创建不同类型的检索增强器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetrievalAugmentorFactory {
    
    /**
     * 增强器策略列表
     * Spring 自动注入所有 RetrievalAugmentorStrategy 实现
     */
    private final List<RetrievalAugmentorStrategy> augmentorStrategies;
    
    /**
     * 根据增强器类型获取对应的增强器策略
     * @param augmentorType 增强器类型
     * @return 对应的增强器策略
     */
    public RetrievalAugmentorStrategy getStrategy(RetrievalAugmentorType augmentorType) {
        return augmentorStrategies.stream()
                .filter(strategy -> strategy.getAugmentorType() == augmentorType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的检索增强器类型: " + augmentorType));
    }
    
    /**
     * 获取默认的检索增强器策略
     * @return 默认策略
     */
    public RetrievalAugmentorStrategy getDefaultStrategy() {
        log.info("获取默认检索增强器策略");
        return getStrategy(RetrievalAugmentorType.DEFAULT);
    }
    
    /**
     * 获取简单检索增强器策略（快速响应）
     * @return 简单策略
     */
    public RetrievalAugmentorStrategy getSimpleStrategy() {
        log.info("获取简单检索增强器策略");
        return getStrategy(RetrievalAugmentorType.SIMPLE);
    }
    
    /**
     * 获取高级检索增强器策略（高准确率）
     * @return 高级策略
     */
    public RetrievalAugmentorStrategy getAdvancedStrategy() {
        log.info("获取高级检索增强器策略");
        return getStrategy(RetrievalAugmentorType.ADVANCED);
    }
    
    /**
     * 获取所有可用的增强器策略
     * @return 增强器类型到策略的映射
     */
    public Map<RetrievalAugmentorType, RetrievalAugmentorStrategy> getAllStrategies() {
        return augmentorStrategies.stream()
                .collect(Collectors.toMap(
                        RetrievalAugmentorStrategy::getAugmentorType,
                        Function.identity()
                ));
    }
    
    /**
     * 列出所有可用的增强器及其描述
     * @return 增强器类型和描述的映射
     */
    public Map<RetrievalAugmentorType, String> listAvailableAugmentors() {
        return augmentorStrategies.stream()
                .collect(Collectors.toMap(
                        RetrievalAugmentorStrategy::getAugmentorType,
                        RetrievalAugmentorStrategy::getDescription
                ));
    }
    
    /**
     * 检查是否支持某个增强器类型
     * @param augmentorType 增强器类型
     * @return 是否支持
     */
    public boolean isSupported(RetrievalAugmentorType augmentorType) {
        return augmentorStrategies.stream()
                .anyMatch(strategy -> strategy.getAugmentorType() == augmentorType);
    }
    
    /**
     * 获取所有启用的增强器策略
     * @return 启用的策略列表
     */
    public List<RetrievalAugmentorStrategy> getEnabledStrategies() {
        return augmentorStrategies.stream()
                .filter(RetrievalAugmentorStrategy::isEnabled)
                .collect(Collectors.toList());
    }
}
