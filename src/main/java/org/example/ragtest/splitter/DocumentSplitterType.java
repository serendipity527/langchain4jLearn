package org.example.ragtest.splitter;

/**
 * 文档分割器类型枚举
 */
public enum DocumentSplitterType {
    /**
     * 按段落分割
     */
    BY_PARAGRAPH,
    
    /**
     * 按行分割
     */
    BY_LINE,
    
    /**
     * 按句子分割
     */
    BY_SENTENCE,
    
    /**
     * 按单词分割
     */
    BY_WORD,
    
    /**
     * 按字符分割
     */
    BY_CHARACTER,
    
    /**
     * 按正则表达式分割
     */
    BY_REGEX,
    
    /**
     * 递归分割（智能分割，推荐使用）
     */
    RECURSIVE
}
