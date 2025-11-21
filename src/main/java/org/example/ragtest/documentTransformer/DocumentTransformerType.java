package org.example.ragtest.documentTransformer;

/**
 * 文档转换器类型枚举
 */
public enum DocumentTransformerType {
    /**
     * 清理转换器 - 去除不必要的噪音（空白字符、特殊符号等）
     */
    CLEANING,
    
    /**
     * 筛选转换器 - 根据条件过滤文档
     */
    FILTERING,
    
    /**
     * 元数据增强转换器 - 添加额外的元数据信息
     */
    METADATA_ENHANCER,
    
    /**
     * HTML转文本转换器 - 从HTML中提取纯文本
     */
    HTML_TO_TEXT,
    
    /**
     * 摘要转换器 - 生成文档摘要并添加到元数据
     */
    SUMMARIZER,
    
    /**
     * 组合转换器 - 按顺序应用多个转换器
     */
    COMPOSITE
}
