package org.example.ragtest.textSegmentTransformer;

/**
 * 文本段转换器类型枚举
 */
public enum TextSegmentTransformerType {
    /**
     * 标题增强转换器 - 在每个文本段中添加文档标题
     */
    TITLE_ENHANCER,
    
    /**
     * 摘要增强转换器 - 在每个文本段中添加文档摘要
     */
    SUMMARY_ENHANCER,
    
    /**
     * 元数据增强转换器 - 为文本段添加额外的元数据信息
     */
    METADATA_ENHANCER,
    
    /**
     * 清理转换器 - 清理文本段中的噪音
     */
    CLEANING,
    
    /**
     * 筛选转换器 - 根据条件过滤文本段
     */
    FILTERING,
    
    /**
     * 组合转换器 - 按顺序应用多个转换器
     */
    COMPOSITE
}
