package org.example.ragtest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.loader.DocumentLoaderType;
import org.example.ragtest.service.DocumentService;
import org.example.ragtest.service.NaiveRagService;
import org.example.ragtest.splitter.DocumentSplitterType;
import org.example.ragtest.documentTransformer.DocumentTransformerType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // ==================== 使用文档加载器的新接口 ====================

    /**
     * 从文件路径加载并摄取文档
     * @param request 文件路径请求
     * @return 成功消息
     */
    @PostMapping("/ingest-from-file")
    public String ingestFromFile(@RequestBody FilePathRequest request) {
        log.info("收到从文件加载文档请求: {}", request.filePath());
        documentService.ingestDocumentFromFile(request.filePath());
        return "文件文档摄取成功: " + request.filePath();
    }

    /**
     * 从 URL 加载并摄取文档
     * @param request URL 请求
     * @return 成功消息
     */
    @PostMapping("/ingest-from-url")
    public String ingestFromUrl(@RequestBody UrlRequest request) {
        log.info("收到从 URL 加载文档请求: {}", request.url());
        documentService.ingestDocumentFromUrl(request.url());
        return "URL 文档摄取成功: " + request.url();
    }

    /**
     * 从文件夹批量加载并摄取文档
     * @param request 文件夹路径请求
     * @return 成功消息
     */
    @PostMapping("/ingest-from-directory")
    public String ingestFromDirectory(@RequestBody DirectoryPathRequest request) {
        log.info("收到从文件夹批量加载文档请求: {}", request.directoryPath());
        documentService.ingestDocumentsFromDirectory(request.directoryPath());
        return "文件夹文档批量摄取成功: " + request.directoryPath();
    }

    /**
     * 从类路径加载并摄取文档
     * @param request 类路径请求
     * @return 成功消息
     */
    @PostMapping("/ingest-from-classpath")
    public String ingestFromClasspath(@RequestBody ClasspathRequest request) {
        log.info("收到从类路径加载文档请求: {}", request.resourcePath());
        documentService.ingestDocumentFromClasspath(request.resourcePath());
        return "类路径文档摄取成功: " + request.resourcePath();
    }

    /**
     * 动态选择加载器类型并加载文档
     * @param request 动态加载请求
     * @return 成功消息
     */
    @PostMapping("/ingest-dynamic")
    public String ingestDynamic(@RequestBody DynamicLoaderRequest request) {
        log.info("收到动态加载请求，类型: {}, 路径: {}", request.loaderType(), request.sourcePath());
        documentService.ingestDocumentByLoaderType(request.loaderType(), request.sourcePath());
        return String.format("使用 %s 加载器摄取文档成功: %s", request.loaderType(), request.sourcePath());
    }

    // ==================== 文档分割器相关接口 ====================

    /**
     * 使用自定义分割器摄取文档
     * @param request 自定义分割请求
     * @return 成功消息
     */
    @PostMapping("/ingest-with-splitter")
    public String ingestWithCustomSplitter(@RequestBody CustomSplitterRequest request) {
        log.info("收到自定义分割器请求，加载器: {}, 分割器: {}, 路径: {}", 
                request.loaderType(), request.splitterType(), request.sourcePath());
        documentService.ingestDocumentWithCustomSplitter(
                request.loaderType(), 
                request.sourcePath(), 
                request.splitterType());
        return String.format("使用 %s 加载器和 %s 分割器摄取文档成功: %s", 
                request.loaderType(), request.splitterType(), request.sourcePath());
    }

    /**
     * 获取所有可用的分割器列表
     * @return 分割器类型和描述的映射
     */
    @GetMapping("/splitters")
    public Map<DocumentSplitterType, String> getAvailableSplitters() {
        log.info("查询可用的分割器列表");
        return documentService.getAvailableSplitters();
    }

    // ==================== 文档转换器相关接口 ====================

    /**
     * 使用自定义转换器处理文档
     * @param request 转换器请求
     * @return 成功消息
     */
    @PostMapping("/ingest-with-transformers")
    public String ingestWithTransformers(@RequestBody TransformerRequest request) {
        log.info("收到转换器请求，加载器: {}, 转换器数量: {}, 分割器: {}, 路径: {}", 
                request.loaderType(), request.transformerTypes().size(), 
                request.splitterType(), request.sourcePath());
        
        documentService.ingestDocumentWithTransformers(
                request.loaderType(),
                request.sourcePath(),
                request.transformerTypes(),
                request.splitterType());
        
        return String.format("使用 %s 加载器、%d 个转换器和 %s 分割器摄取文档成功: %s", 
                request.loaderType(), request.transformerTypes().size(), 
                request.splitterType(), request.sourcePath());
    }

    /**
     * 获取所有可用的转换器列表
     * @return 转换器类型和描述的映射
     */
    @GetMapping("/transformers")
    public Map<DocumentTransformerType, String> getAvailableTransformers() {
        log.info("查询可用的转换器列表");
        return documentService.getAvailableTransformers();
    }

    // 请求对象
    public record IngestRequest(String text) {}
    public record QueryRequest(String question) {}
    public record FilePathRequest(String filePath) {}
    public record UrlRequest(String url) {}
    public record DirectoryPathRequest(String directoryPath) {}
    public record ClasspathRequest(String resourcePath) {}
    public record DynamicLoaderRequest(DocumentLoaderType loaderType, String sourcePath) {}
    public record CustomSplitterRequest(
            DocumentLoaderType loaderType, 
            String sourcePath, 
            DocumentSplitterType splitterType) {}
    public record TransformerRequest(
            DocumentLoaderType loaderType,
            String sourcePath,
            List<DocumentTransformerType> transformerTypes,
            DocumentSplitterType splitterType) {}
}
