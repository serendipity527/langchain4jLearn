package org.example.ragtest.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Naive RAG 服务实现
 * 实现基础的检索增强生成流程：
 * 1. 检索相关文档片段
 * 2. 构建提示词
 * 3. 生成回答
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NaiveRagService {

    private final ChatModel chatLanguageModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    // 提示词模板
    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.from(
            """
            根据以下上下文信息回答问题。如果上下文中没有相关信息，请说"我不知道"。
            
            上下文信息：
            {{context}}
            
            问题：{{question}}
            
            回答：
            """
    );

    /**
     * 执行 RAG 查询
     * @param question 用户问题
     * @return AI 生成的回答
     */
    public String query(String question) {
        log.info("收到 RAG 查询: {}", question);
        
        // 步骤 1: 检索相关文档
        List<TextSegment> relevantSegments = retrieveRelevantDocuments(question, 3);
        
        if (relevantSegments.isEmpty()) {
            log.warn("未找到相关文档片段");
            return "抱歉，我在知识库中没有找到相关信息。";
        }
        
        // 步骤 2: 构建上下文
        String context = buildContext(relevantSegments);
        log.info("构建的上下文长度: {}", context.length());
        
        // 步骤 3: 生成提示词
        Map<String, Object> variables = new HashMap<>();
        variables.put("context", context);
        variables.put("question", question);
        Prompt prompt = PROMPT_TEMPLATE.apply(variables);
        
        // 步骤 4: 调用 LLM 生成回答
        log.info("调用 LLM 生成回答");
        AiMessage aiMessage = chatLanguageModel.chat(prompt.toUserMessage()).aiMessage();
        String answer = aiMessage.text();
        
        log.info("生成回答完成，长度: {}", answer.length());
        return answer;
    }

    /**
     * 检索相关文档片段
     * @param query 查询文本
     * @param maxResults 最大返回结果数
     * @return 相关文档片段列表
     */
    private List<TextSegment> retrieveRelevantDocuments(String query, int maxResults) {
        log.info("检索相关文档，查询: {}, 最大结果数: {}", query, maxResults);
        
        // 将查询转换为向量
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        
        // 构建搜索请求
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.5)  // 最小相似度阈值
                .build();
        
        // 在向量存储中搜索最相关的片段
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(searchRequest).matches();
        
        log.info("找到 {} 个相关片段", matches.size());
        
        // 提取文本片段并打印相似度分数
        return matches.stream()
                .peek(match -> log.info("片段相似度: {}", match.score()))
                .map(EmbeddingMatch::embedded)
                .collect(Collectors.toList());
    }

    /**
     * 从文档片段列表构建上下文字符串
     * @param segments 文档片段列表
     * @return 上下文字符串
     */
    private String buildContext(List<TextSegment> segments) {
        return segments.stream()
                .map(TextSegment::text)
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * 获取详细的 RAG 响应（包含检索的文档）
     * @param question 用户问题
     * @return 包含回答和来源文档的响应
     */
    public RagResponse queryWithSources(String question) {
        log.info("收到带来源的 RAG 查询: {}", question);
        
        // 检索相关文档
        List<TextSegment> relevantSegments = retrieveRelevantDocuments(question, 3);
        
        if (relevantSegments.isEmpty()) {
            return new RagResponse(
                    "抱歉，我在知识库中没有找到相关信息。",
                    List.of()
            );
        }
        
        // 构建上下文
        String context = buildContext(relevantSegments);
        
        // 生成提示词
        Map<String, Object> variables = new HashMap<>();
        variables.put("context", context);
        variables.put("question", question);
        Prompt prompt = PROMPT_TEMPLATE.apply(variables);
        
        // 调用 LLM
        AiMessage aiMessage = chatLanguageModel.chat(prompt.toUserMessage()).aiMessage();
        String answer = aiMessage.text();
        
        // 提取来源文本
        List<String> sources = relevantSegments.stream()
                .map(TextSegment::text)
                .collect(Collectors.toList());
        
        return new RagResponse(answer, sources);
    }

    /**
     * RAG 响应对象
     */
    public record RagResponse(String answer, List<String> sources) {}
}
