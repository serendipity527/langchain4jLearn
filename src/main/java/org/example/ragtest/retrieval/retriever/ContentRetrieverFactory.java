package org.example.ragtest.retrieval.retriever;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内容检索器工厂
 * 使用工厂模式管理和创建不同类型的内容检索器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContentRetrieverFactory {
    
    /**
     * 检索器策略列表
     * Spring 自动注入所有 ContentRetrieverStrategy 实现
     */
    private final List<ContentRetrieverStrategy> retrieverStrategies;
    
    /**
     * 根据检索器类型获取对应的检索器策略
     * @param retrieverType 检索器类型
     * @return 对应的检索器策略
     */
    public Optional<ContentRetrieverStrategy> getStrategy(ContentRetrieverType retrieverType) {
        return retrieverStrategies.stream()
                .filter(strategy -> strategy.getRetrieverType() == retrieverType)
                .findFirst();
    }
    
    /**
     * 获取默认的内容检索器策略
     * 默认使用嵌入式存储检索器
     * @return 默认策略
     */
    public ContentRetrieverStrategy getDefaultStrategy() {
        log.info("获取默认内容检索器策略（嵌入式存储）");
        return getStrategy(ContentRetrieverType.EMBEDDING_STORE)
                .orElseThrow(() -> new IllegalStateException("默认内容检索器未找到"));
    }
    
    /**
     * 获取嵌入式存储检索器策略
     * @return 嵌入式存储检索器策略
     */
    public ContentRetrieverStrategy getEmbeddingStoreStrategy() {
        log.info("获取嵌入式存储检索器策略");
        return getStrategy(ContentRetrieverType.EMBEDDING_STORE)
                .orElseThrow(() -> new IllegalStateException("嵌入式存储检索器未找到"));
    }
    
    /**
     * 获取网络搜索检索器策略
     * @return 网络搜索检索器策略（如果可用）
     */
    public Optional<ContentRetrieverStrategy> getWebSearchStrategy() {
        log.info("获取网络搜索检索器策略");
        return getStrategy(ContentRetrieverType.WEB_SEARCH);
    }
    
    /**
     * 获取SQL数据库检索器策略
     * @return SQL数据库检索器策略（如果可用）
     */
    public Optional<ContentRetrieverStrategy> getSqlDatabaseStrategy() {
        log.info("获取SQL数据库检索器策略");
        return getStrategy(ContentRetrieverType.SQL_DATABASE);
    }
    
    /**
     * 获取所有可用的检索器策略
     * @return 检索器类型到策略的映射
     */
    public Map<ContentRetrieverType, ContentRetrieverStrategy> getAllStrategies() {
        return retrieverStrategies.stream()
                .collect(Collectors.toMap(
                        ContentRetrieverStrategy::getRetrieverType,
                        Function.identity(),
                        // 如果有重复的键（同一类型多个实现），保留第一个
                        (existing, replacement) -> existing
                ));
    }
    
    /**
     * 列出所有可用的检索器及其描述
     * @return 检索器类型和描述的映射
     */
    public Map<ContentRetrieverType, String> listAvailableRetrievers() {
        return retrieverStrategies.stream()
                .collect(Collectors.toMap(
                        ContentRetrieverStrategy::getRetrieverType,
                        ContentRetrieverStrategy::getDescription,
                        // 如果有重复的键，保留第一个
                        (existing, replacement) -> existing
                ));
    }
    
    /**
     * 检查是否支持某个检索器类型
     * @param retrieverType 检索器类型
     * @return 是否支持
     */
    public boolean isSupported(ContentRetrieverType retrieverType) {
        return retrieverStrategies.stream()
                .anyMatch(strategy -> strategy.getRetrieverType() == retrieverType);
    }
    
    /**
     * 获取所有启用的检索器策略
     * @return 启用的策略列表
     */
    public List<ContentRetrieverStrategy> getEnabledStrategies() {
        return retrieverStrategies.stream()
                .filter(ContentRetrieverStrategy::isEnabled)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取检索器数量
     * @return 可用检索器数量
     */
    public int getRetrieverCount() {
        return retrieverStrategies.size();
    }
}
