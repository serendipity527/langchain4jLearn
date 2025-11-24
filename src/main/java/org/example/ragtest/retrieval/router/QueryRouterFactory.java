package org.example.ragtest.retrieval.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询路由器工厂
 * 使用工厂模式管理和创建不同类型的查询路由器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueryRouterFactory {
    
    /**
     * 路由器策略列表
     * Spring 自动注入所有 QueryRouterStrategy 实现
     */
    private final List<QueryRouterStrategy> routerStrategies;
    
    /**
     * 根据路由器类型获取对应的路由器策略
     * @param routerType 路由器类型
     * @return 对应的路由器策略
     */
    public Optional<QueryRouterStrategy> getStrategy(QueryRouterType routerType) {
        return routerStrategies.stream()
                .filter(strategy -> strategy.getRouterType() == routerType)
                .findFirst();
    }
    
    /**
     * 获取默认的查询路由器策略
     * 默认使用 DefaultQueryRouter（路由到所有检索器）
     * @return 默认策略
     */
    public QueryRouterStrategy getDefaultStrategy() {
        log.info("获取默认查询路由器策略");
        return getStrategy(QueryRouterType.DEFAULT)
                .orElseThrow(() -> new IllegalStateException("默认查询路由器未找到"));
    }
    
    /**
     * 获取语言模型路由器策略
     * @return 语言模型路由器策略（如果可用）
     */
    public Optional<QueryRouterStrategy> getLanguageModelStrategy() {
        log.info("获取语言模型查询路由器策略");
        return getStrategy(QueryRouterType.LANGUAGE_MODEL);
    }
    
    /**
     * 获取基于规则的路由器策略
     * @return 基于规则的路由器策略（如果可用）
     */
    public Optional<QueryRouterStrategy> getRuleBasedStrategy() {
        log.info("获取基于规则的查询路由器策略");
        return getStrategy(QueryRouterType.RULE_BASED);
    }
    
    /**
     * 获取所有可用的路由器策略
     * @return 路由器类型到策略的映射
     */
    public Map<QueryRouterType, QueryRouterStrategy> getAllStrategies() {
        return routerStrategies.stream()
                .collect(Collectors.toMap(
                        QueryRouterStrategy::getRouterType,
                        Function.identity(),
                        // 如果有重复的键（同一类型多个实现），保留第一个
                        (existing, replacement) -> existing
                ));
    }
    
    /**
     * 列出所有可用的路由器及其描述
     * @return 路由器类型和描述的映射
     */
    public Map<QueryRouterType, String> listAvailableRouters() {
        return routerStrategies.stream()
                .collect(Collectors.toMap(
                        QueryRouterStrategy::getRouterType,
                        QueryRouterStrategy::getDescription,
                        // 如果有重复的键，保留第一个
                        (existing, replacement) -> existing
                ));
    }
    
    /**
     * 检查是否支持某个路由器类型
     * @param routerType 路由器类型
     * @return 是否支持
     */
    public boolean isSupported(QueryRouterType routerType) {
        return routerStrategies.stream()
                .anyMatch(strategy -> strategy.getRouterType() == routerType);
    }
    
    /**
     * 获取所有启用的路由器策略
     * @return 启用的策略列表
     */
    public List<QueryRouterStrategy> getEnabledStrategies() {
        return routerStrategies.stream()
                .filter(QueryRouterStrategy::isEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取路由器数量
     * @return 可用路由器数量
     */
    public int getRouterCount() {
        return routerStrategies.size();
    }
    
    /**
     * 获取最佳路由器策略
     * 优先使用语言模型路由器（如果可用），否则使用默认路由器
     * @return 最佳路由器策略
     */
    public QueryRouterStrategy getBestStrategy() {
        log.info("获取最佳查询路由器策略");
        
        // 优先尝试语言模型路由器（智能路由）
        Optional<QueryRouterStrategy> languageModelStrategy = getLanguageModelStrategy();
        if (languageModelStrategy.isPresent()) {
            log.info("使用语言模型查询路由器（智能路由）");
            return languageModelStrategy.get();
        }
        
        // 如果语言模型路由器不可用，使用默认路由器
        log.info("使用默认查询路由器（路由到所有检索器）");
        return getDefaultStrategy();
    }
}
