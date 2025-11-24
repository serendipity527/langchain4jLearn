package org.example.ragtest.retrieval.query.impl;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.retrieval.query.QueryTransformerStrategy;
import org.example.ragtest.retrieval.query.QueryTransformerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 压缩查询转换器策略
 * 使用LLM压缩查询并整合对话历史
 * 
 * 使用场景：
 * - 用户提出后续问题时
 * - 问题涉及之前的对话内容
 * - 需要理解上下文的查询
 * 
 * 示例：
 * User: "Tell me about John Doe"
 * AI: "John Doe was a ..."
 * User: "Where did he live?"  
 * 
 * 转换后: "Where did John Doe live?"  ← 整合了上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class CompressingQueryTransformerStrategy implements QueryTransformerStrategy {
    
    private final ChatModel chatLanguageModel;
    
    @Override
    public QueryTransformer getTransformer() {
        log.info("创建压缩查询转换器（整合对话历史）");
        return CompressingQueryTransformer.builder()
                .chatModel(chatLanguageModel)
                .build();
    }
    
    @Override
    public QueryTransformerType getTransformerType() {
        return QueryTransformerType.COMPRESSING;
    }
    
    @Override
    public String getDescription() {
        return "压缩查询转换器 (使用LLM压缩查询并整合对话历史，适用于后续问题)";
    }
}
