package org.example.ragtest.textSegmentTransformer;

import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

/**
 * 文本段转换器策略接口
 * 定义文本段转换的统一行为
 */
public interface TextSegmentTransformerStrategy {
    
    /**
     * 转换单个文本段
     * @param textSegment 待转换的文本段
     * @return 转换后的文本段（如果被过滤则返回null）
     */
    TextSegment transform(TextSegment textSegment);
    
    /**
     * 批量转换多个文本段
     * @param textSegments 待转换的文本段列表
     * @return 转换后的文本段列表（被过滤的文本段会被移除）
     */
    List<TextSegment> transformAll(List<TextSegment> textSegments);
    
    /**
     * 获取转换器类型
     * @return 转换器类型
     */
    TextSegmentTransformerType getTransformerType();
    
    /**
     * 获取转换器描述
     * @return 描述信息
     */
    String getDescription();
    
    /**
     * 检查是否启用该转换器
     * @return 是否启用
     */
    default boolean isEnabled() {
        return true;
    }
}
