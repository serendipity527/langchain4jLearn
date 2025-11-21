package org.example.ragtest.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ragtest.service.DocumentService;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时向向量数据库中加载示例知识
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DocumentService documentService;

    @PostConstruct
    public void initializeData() {
        log.info("开始初始化向量数据库...");

        // 添加关于 Java 的知识
        String javaKnowledge = """
                Java 是一种广泛使用的面向对象编程语言,由 Sun Microsystems 公司于 1995 年推出。
                Java 的主要特点包括:
                1. 平台无关性: "一次编写,到处运行"(Write Once, Run Anywhere)
                2. 面向对象: 支持封装、继承和多态
                3. 自动内存管理: 通过垃圾回收器(GC)自动管理内存
                4. 丰富的标准库: 提供大量的 API 用于网络、I/O、数据结构等
                5. 多线程支持: 内置对并发编程的支持
                
                Java 主要应用场景包括企业级应用、Android 应用开发、大数据处理、Web 应用等。
                """;
        documentService.ingestDocument(javaKnowledge);

        // 添加关于 Spring Boot 的知识
        String springBootKnowledge = """
                Spring Boot 是基于 Spring 框架的快速开发框架,简化了 Spring 应用的配置和部署。
                Spring Boot 的核心特性:
                1. 自动配置(Auto-configuration): 根据类路径自动配置 Spring 应用
                2. 起步依赖(Starter Dependencies): 简化依赖管理
                3. 嵌入式服务器: 内置 Tomcat、Jetty 等服务器,无需部署 WAR 文件
                4. 生产就绪特性: 提供健康检查、指标监控等功能
                5. 无代码生成: 不需要 XML 配置
                
                Spring Boot 3.x 版本要求 Java 17 或更高版本,并引入了对 Jakarta EE 的支持。
                常用注解包括 @SpringBootApplication、@RestController、@Service、@Autowired 等。
                """;
        documentService.ingestDocument(springBootKnowledge);

        // 添加关于 LangChain4j 的知识
        String langchain4jKnowledge = """
                LangChain4j 是 Java 平台上的 LLM(大语言模型)应用开发框架,类似于 Python 的 LangChain。
                主要功能包括:
                1. 统一的 API: 支持多种 LLM 提供商(OpenAI、Azure、Google 等)
                2. RAG 支持: 提供检索增强生成(Retrieval-Augmented Generation)功能
                3. 向量存储: 支持多种向量数据库(Milvus、Pinecone、Chroma 等)
                4. 文档处理: 支持多种文档格式(PDF、Word、TXT 等)的加载和分割
                5. Embedding 模型: 支持本地和云端的文本向量化模型
                6. AI 服务: 通过简单的接口定义即可创建 AI 服务
                
                LangChain4j 与 Spring Boot 集成良好,提供了专门的 starter 依赖。
                """;
        documentService.ingestDocument(langchain4jKnowledge);

        // 添加关于 RAG 的知识
        String ragKnowledge = """
                RAG(Retrieval-Augmented Generation,检索增强生成)是一种结合信息检索和文本生成的 AI 技术。
                RAG 的工作流程:
                1. 文档加载: 加载知识库文档
                2. 文档分割: 将长文档分割成较小的片段
                3. 向量化: 使用 Embedding 模型将文本转换为向量
                4. 存储: 将向量存储在向量数据库中
                5. 检索: 根据用户查询检索最相关的文档片段
                6. 生成: 将检索到的上下文和用户问题一起发送给 LLM 生成回答
                
                RAG 的优势:
                - 降低幻觉: 基于真实文档生成答案
                - 知识更新: 无需重新训练模型即可更新知识
                - 可追溯性: 可以提供答案的来源
                - 成本效益: 相比微调模型成本更低
                """;
        documentService.ingestDocument(ragKnowledge);

        // 添加关于向量数据库的知识
        String vectorDBKnowledge = """
                向量数据库(Vector Database)是专门用于存储和检索高维向量的数据库系统。
                主要特点:
                1. 高效的相似度搜索: 使用 ANN(近似最近邻)算法快速检索相似向量
                2. 可扩展性: 支持数十亿级别的向量存储
                3. 多种距离度量: 支持余弦相似度、欧几里得距离等
                4. 元数据过滤: 可以结合向量和传统数据进行混合查询
                
                常见的向量数据库包括:
                - Milvus: 开源、云原生、高性能
                - Pinecone: 云服务、易用
                - Weaviate: 开源、支持多种模型
                - Chroma: 轻量级、适合开发测试
                - Qdrant: 开源、Rust 实现、高性能
                
                在内存中也可以使用 InMemoryEmbeddingStore 进行开发和测试。
                """;
        documentService.ingestDocument(vectorDBKnowledge);

        // 添加关于 Embedding 模型的知识
        String embeddingKnowledge = """
                Embedding 模型用于将文本转换为固定长度的向量表示,捕获文本的语义信息。
                常用的 Embedding 模型:
                1. OpenAI text-embedding-ada-002: 高质量、1536 维
                2. all-MiniLM-L6-v2: 轻量级、384 维、可本地运行
                3. BGE 系列: 中文支持好、性能优秀
                4. M3E: 专为中文优化
                
                Embedding 的应用场景:
                - 语义搜索: 根据语义相似度检索文档
                - 文本分类: 作为分类器的输入特征
                - 聚类分析: 将相似文本聚类
                - 推荐系统: 基于内容的推荐
                
                选择 Embedding 模型时需要考虑:
                - 向量维度: 影响存储和计算成本
                - 语言支持: 是否支持目标语言
                - 性能: 编码速度和质量的平衡
                - 部署方式: 本地还是云端
                """;
        documentService.ingestDocument(embeddingKnowledge);

        log.info("向量数据库初始化完成! 已添加 6 个知识文档");
    }
}
