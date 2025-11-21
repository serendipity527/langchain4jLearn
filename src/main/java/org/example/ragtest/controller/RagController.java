package org.example.ragtest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.service.DocumentService;
import org.example.ragtest.service.NaiveRagService;
import org.springframework.web.bind.annotation.*;

/**
 * RAG 测试控制器
 * 提供文档摄取和查询的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final DocumentService documentService;
    private final NaiveRagService naiveRagService;

    /**
     * 摄取文档
     * @param request 文档内容
     * @return 成功消息
     */
    @PostMapping("/ingest")
    public String ingestDocument(@RequestBody IngestRequest request) {
        log.info("收到文档摄取请求");
        documentService.ingestDocument(request.text());
        return "文档摄取成功";
    }

    /**
     * 执行 RAG 查询
     * @param request 查询请求
     * @return 查询结果
     */
    @PostMapping("/query")
    public String query(@RequestBody QueryRequest request) {
        log.info("收到查询请求: {}", request.question());
        return naiveRagService.query(request.question());
    }

    /**
     * 执行带来源的 RAG 查询
     * @param request 查询请求
     * @return 包含回答和来源的详细响应
     */
    @PostMapping("/query-with-sources")
    public NaiveRagService.RagResponse queryWithSources(@RequestBody QueryRequest request) {
        log.info("收到带来源的查询请求: {}", request.question());
        return naiveRagService.queryWithSources(request.question());
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public String health() {
        return "RAG 服务运行正常";
    }

    // 请求对象
    public record IngestRequest(String text) {}
    public record QueryRequest(String question) {}
}
