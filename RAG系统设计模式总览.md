# RAG系统设计模式总览

## 重构概述

本项目使用**策略模式（Strategy Pattern）**和**工厂模式（Factory Pattern）**对整个RAG系统进行了全面重构，实现了一个完整的、企业级的、可扩展的RAG应用架构。

---

## 完整的 RAG 流程

```
┌─────────────────────────────────────────────────────────────┐
│              文档准备阶段（离线，一次性）                       │
└─────────────────────────────────────────────────────────────┘

1️⃣ DocumentLoader（文档加载器）
   ↓ 从文件系统/URL/数据库加载原始文档
   
2️⃣ DocumentParser（文档解析器）
   ↓ 解析PDF/TXT/DOC等格式
   
3️⃣ DocumentTransformer（文档转换器）
   ↓ 清理、过滤、增强文档
   
4️⃣ DocumentSplitter（文档分割器）
   ↓ 将文档分割成文本段
   
5️⃣ TextSegmentTransformer（文本段转换器）
   ↓ 为文本段添加标题/摘要
   
6️⃣ EmbeddingModel + EmbeddingStore
   ↓ 向量化并存储

┌─────────────────────────────────────────────────────────────┐
│              查询处理阶段（在线，每次查询）                     │
└─────────────────────────────────────────────────────────────┘

用户提问
   ↓
7️⃣ QueryTransformer（查询转换器）
   ↓ 压缩/扩展/重写查询
   
8️⃣ QueryRouter（查询路由器）⭐ 新增
   ↓ 智能选择检索器
   
9️⃣ ContentRetriever（内容检索器）
   ↓ 从选定的数据源检索相关内容
   
🔟 RetrievalAugmentor（检索增强器）
   ↓ 聚合结果并注入到用户消息
   
1️⃣1️⃣ LLM（大语言模型）
   ↓ 基于检索内容生成回答
   
用户收到回答
```

---

## 系统架构总览

### 完整的组件矩阵

| 阶段 | 序号 | 功能模块 | 策略数量 | 工厂类 | 核心价值 | 状态 |
|-----|-----|---------|---------|-------|---------|------|
| **离线** | 1️⃣ | DocumentLoader | 3种 | DocumentLoaderFactory | 多源加载 | ✅ |
| **离线** | 2️⃣ | DocumentParser | 3种 | DocumentParserFactory | 智能解析 | ✅ |
| **离线** | 3️⃣ | DocumentTransformer | 4种 | DocumentTransformerFactory | 文档预处理 | ✅ |
| **离线** | 4️⃣ | DocumentSplitter | 4种 | DocumentSplitterFactory | 灵活分割 | ✅ |
| **离线** | 5️⃣ | TextSegmentTransformer | 3种 | TextSegmentTransformerFactory | 提升检索 | ✅ |
| **在线** | 6️⃣ | QueryTransformer | 3种 | QueryTransformerFactory | 查询优化 | ✅ |
| **在线** | 7️⃣ | QueryRouter | 2种 | QueryRouterFactory | 智能路由 ⭐ | ✅ |
| **在线** | 8️⃣ | ContentRetriever | 2种+ | ContentRetrieverFactory | 内容检索 | ✅ |
| **在线** | 9️⃣ | RetrievalAugmentor | 4种 | RetrievalAugmentorFactory | RAG入口 | ✅ |

**总计**: **27+种策略** + **9个工厂** = **完整的企业级RAG系统**

---

## 详细文档索引

### 离线处理阶段

1. **文档加载器** - `文档加载器设计模式说明.md`（如需要可创建）
   - FileSystemDocumentLoader - 从文件系统加载
   - UrlDocumentLoader - 从URL加载
   - DatabaseDocumentLoader - 从数据库加载

2. **文档解析器** - `文档解析器设计模式说明.md`（如需要可创建）
   - AutoDetectDocumentParser - 自动检测格式
   - ApacheTikaDocumentParser - Apache Tika解析
   - TextDocumentParser - 纯文本解析

3. **文档转换器** - `文档转换器设计模式说明.md`（如需要可创建）
   - CleaningDocumentTransformer - 清理文档
   - FilteringDocumentTransformer - 过滤文档
   - EnhancingDocumentTransformer - 增强文档
   - ValidatingDocumentTransformer - 验证文档

4. **文档分割器** - `文档分割器设计模式说明.md`（如需要可创建）
   - CharacterSplitter - 按字符分割
   - SentenceSplitter - 按句子分割
   - ParagraphSplitter - 按段落分割
   - RecursiveSplitter - 递归分割

5. **文本段转换器** - `文本段转换器设计模式说明.md`（如需要可创建）
   - TitleEnhancer - 添加标题
   - SummaryEnhancer - 添加摘要
   - MetadataEnhancer - 增强元数据

### 在线查询阶段

6. **查询转换器** - `查询转换器设计模式说明.md`（如需要可创建）
   - DefaultQueryTransformer - 不做修改
   - CompressingQueryTransformer - 压缩查询并整合对话历史
   - ExpandingQueryTransformer - 扩展查询为多个变体

7. **查询路由器** - **`查询路由器设计模式说明.md`** ⭐ 已创建
   - DefaultQueryRouter - 路由到所有检索器
   - LanguageModelQueryRouter - LLM智能选择检索器

8. **内容检索器** - **`内容检索器设计模式说明.md`** ⭐ 已创建
   - EmbeddingStoreContentRetriever - 基础嵌入式存储检索
   - AdvancedEmbeddingStoreRetriever - 高级检索（支持动态配置）
   - WebSearchContentRetriever - 网络搜索（可扩展）
   - SqlDatabaseContentRetriever - SQL数据库（可扩展）

9. **检索增强器** - `检索增强器设计模式说明.md`（如需要可创建）
   - DefaultRetrievalAugmentor - 标准RAG
   - SimpleRetrievalAugmentor - 快速响应
   - AdvancedRetrievalAugmentor - 高级功能（查询转换）
   - EnhancedRetrievalAugmentor - 完整功能（查询转换+智能路由）⭐ 新增

---

## 项目结构

```
src/main/java/org/example/ragtest/
├── 📁 loader/                       # 1️⃣ 文档加载器
│   ├── DocumentLoaderStrategy.java
│   ├── DocumentLoaderType.java
│   ├── DocumentLoaderFactory.java
│   └── impl/
│       ├── FileSystemDocumentLoader.java
│       ├── UrlDocumentLoader.java
│       └── DatabaseDocumentLoader.java
│
├── 📁 parser/                       # 2️⃣ 文档解析器
│   ├── DocumentParserStrategy.java
│   ├── DocumentParserType.java
│   ├── DocumentParserFactory.java
│   └── impl/
│       ├── AutoDetectDocumentParser.java
│       ├── ApacheTikaDocumentParser.java
│       └── TextDocumentParser.java
│
├── 📁 transformer/                  # 3️⃣ 文档转换器
│   ├── DocumentTransformerStrategy.java
│   ├── DocumentTransformerType.java
│   ├── DocumentTransformerFactory.java
│   └── impl/
│       ├── CleaningDocumentTransformer.java
│       ├── FilteringDocumentTransformer.java
│       ├── EnhancingDocumentTransformer.java
│       └── ValidatingDocumentTransformer.java
│
├── 📁 splitter/                     # 4️⃣ 文档分割器
│   ├── DocumentSplitterStrategy.java
│   ├── DocumentSplitterType.java
│   ├── DocumentSplitterFactory.java
│   └── impl/
│       ├── CharacterSplitterStrategy.java
│       ├── SentenceSplitterStrategy.java
│       ├── ParagraphSplitterStrategy.java
│       └── RecursiveSplitterStrategy.java
│
├── 📁 textsegment/                  # 5️⃣ 文本段转换器
│   ├── TextSegmentTransformerStrategy.java
│   ├── TextSegmentTransformerType.java
│   ├── TextSegmentTransformerFactory.java
│   └── impl/
│       ├── TitleEnhancerTransformer.java
│       ├── SummaryEnhancerTransformer.java
│       └── MetadataEnhancerTransformer.java
│
└── 📁 retrieval/                    # 检索相关组件
    ├── 📁 query/                    # 6️⃣ 查询转换器
    │   ├── QueryTransformerStrategy.java
    │   ├── QueryTransformerType.java
    │   ├── QueryTransformerFactory.java
    │   └── impl/
    │       ├── DefaultQueryTransformerStrategy.java
    │       ├── CompressingQueryTransformerStrategy.java
    │       └── ExpandingQueryTransformerStrategy.java
    │
    ├── 📁 router/                   # 7️⃣ 查询路由器 ⭐ 新增
    │   ├── QueryRouterStrategy.java
    │   ├── QueryRouterType.java
    │   ├── QueryRouterFactory.java
    │   └── impl/
    │       ├── DefaultQueryRouterStrategy.java
    │       └── LanguageModelQueryRouterStrategy.java
    │
    ├── 📁 retriever/                # 8️⃣ 内容检索器
    │   ├── ContentRetrieverStrategy.java
    │   ├── ContentRetrieverType.java
    │   ├── ContentRetrieverFactory.java
    │   └── impl/
    │       ├── EmbeddingStoreContentRetrieverStrategy.java
    │       └── AdvancedEmbeddingStoreRetrieverStrategy.java
    │
    ├── 📁 impl/                     # 9️⃣ 检索增强器
    │   ├── DefaultRetrievalAugmentorStrategy.java
    │   ├── SimpleRetrievalAugmentorStrategy.java
    │   ├── AdvancedRetrievalAugmentorStrategy.java
    │   └── EnhancedRetrievalAugmentorStrategy.java ⭐ 新增
    │
    ├── RetrievalAugmentorStrategy.java
    ├── RetrievalAugmentorType.java
    └── RetrievalAugmentorFactory.java
```

---

## 设计模式应用

### 1. 策略模式（Strategy Pattern）

**目的**: 定义一系列算法，把它们一个个封装起来，并且使它们可以互相替换。

**应用**:
- 每个功能模块都有一个策略接口（如 `ContentRetrieverStrategy`）
- 提供多个具体实现（如 `EmbeddingStoreContentRetriever`、`WebSearchContentRetriever`）
- 客户端可以灵活选择和切换策略

**优势**:
- ✅ 开闭原则：添加新策略无需修改现有代码
- ✅ 单一职责：每个策略专注于一种算法
- ✅ 易于测试：可以单独测试每个策略

### 2. 工厂模式（Factory Pattern）

**目的**: 提供一个创建对象的接口，让子类决定实例化哪一个类。

**应用**:
- 每个功能模块都有一个工厂类（如 `ContentRetrieverFactory`）
- 工厂负责管理和提供策略实例
- 支持根据类型获取、列出所有策略等功能

**优势**:
- ✅ 集中管理：统一的创建入口
- ✅ 解耦：客户端不需要知道具体实现
- ✅ 易于扩展：添加新策略只需注册到工厂

---

## 核心特性

### 1. 完整的 RAG 流程 ⭐

从文档准备到查询响应的完整链路：
- **离线阶段**: 加载 → 解析 → 转换 → 分割 → 增强 → 向量化
- **在线阶段**: 查询转换 → 内容检索 → 增强 → LLM生成

### 2. 高度可配置

每个组件都支持配置：
- 通过 `application.properties` 统一配置
- 支持动态配置（运行时调整）
- 支持环境变量和配置文件

### 3. 易于扩展

添加新功能只需三步：
1. 实现策略接口
2. 添加 `@Component` 注解
3. Spring 自动注册到工厂

### 4. 企业级质量

- ✅ 完整的日志记录
- ✅ 异常处理
- ✅ 类型安全
- ✅ 单元测试支持

---

## 配置总览

### application.properties

```properties
# 文档分割器配置
document.splitter.max-segment-size=500
document.splitter.max-overlap-size=50

# 文档转换器配置
document.transformer.filter.min-length=10
document.transformer.filter.max-length=10000

# RAG 检索增强器配置
rag.retrieval.max-results=5
rag.retrieval.min-score=0.6
rag.retrieval.advanced.max-results=10
rag.retrieval.advanced.min-score=0.5

# RAG 查询转换器配置
rag.query.transformer.enabled=false
rag.query.expanding.count=3

# RAG 内容检索器配置
rag.retrieval.dynamic.enabled=false
```

---

## 使用示例

### 完整的 RAG 应用示例

```java
@Service
@RequiredArgsConstructor
public class RagService {
    
    // 离线阶段：文档准备
    private final DocumentLoaderFactory loaderFactory;
    private final DocumentParserFactory parserFactory;
    private final DocumentTransformerFactory transformerFactory;
    private final DocumentSplitterFactory splitterFactory;
    private final TextSegmentTransformerFactory segmentTransformerFactory;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    // 在线阶段：查询处理
    private final QueryTransformerFactory queryTransformerFactory;
    private final ContentRetrieverFactory contentRetrieverFactory;
    private final RetrievalAugmentorFactory augmentorFactory;
    private final ChatModel chatModel;
    
    /**
     * 离线阶段：摄取文档
     */
    public void ingestDocuments(String filePath) {
        // 1. 加载文档
        var loader = loaderFactory.getDefaultStrategy();
        Document document = loader.loadDocument(filePath);
        
        // 2. 解析文档（如果需要）
        // ...
        
        // 3. 转换文档
        var transformer = transformerFactory.getCleaningStrategy();
        document = transformer.transform(document);
        
        // 4. 分割文档
        var splitter = splitterFactory.getDefaultStrategy();
        List<TextSegment> segments = splitter.split(document);
        
        // 5. 增强文本段
        var segmentTransformer = segmentTransformerFactory.getTitleEnhancerStrategy();
        segments = segmentTransformer.transformAll(segments);
        
        // 6. 向量化并存储
        embeddingStore.addAll(
            embeddingModel.embedAll(segments).content(),
            segments
        );
    }
    
    /**
     * 在线阶段：查询处理
     */
    public String chat(String userMessage) {
        // 获取内容检索器
        var retrieverStrategy = contentRetrieverFactory.getDefaultStrategy();
        ContentRetriever retriever = retrieverStrategy.getRetriever();
        
        // 创建检索增强器
        var augmentorStrategy = augmentorFactory.getAdvancedStrategy();
        RetrievalAugmentor augmentor = augmentorStrategy.getAugmentor();
        
        // 创建 AI 助手
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(augmentor)
                .build();
        
        // 查询并返回
        return assistant.chat(userMessage);
    }
}
```

---

## 性能考虑

### 离线阶段优化

- **并行处理**: 可以并行处理多个文档
- **批量操作**: 批量向量化和存储
- **增量更新**: 只处理新增或修改的文档

### 在线阶段优化

- **缓存**: 缓存常用查询的结果
- **异步处理**: 异步执行非关键路径
- **限流**: 控制并发查询数量

---

## 最佳实践

### 1. 文档准备阶段

- ✅ 清理文档，去除无关内容
- ✅ 合理分割，保持语义完整性
- ✅ 添加元数据，便于过滤和追溯
- ✅ 验证质量，过滤低质量内容

### 2. 查询处理阶段

- ✅ 使用查询转换器优化查询
- ✅ 调整检索参数平衡质量和性能
- ✅ 启用动态配置支持多场景
- ✅ 监控检索质量，持续优化

### 3. 系统监控

- 📊 文档数量和向量数量
- 📊 平均检索时间
- 📊 检索结果相关性分数分布
- 📊 查询响应时间
- 📊 LLM token 消耗

---

## 总结

### 🎯 核心成就

1. **完整的 RAG 系统**: 8个核心组件，25+种策略
2. **设计模式应用**: 策略模式 + 工厂模式
3. **企业级质量**: 可配置、可扩展、易维护
4. **最佳实践**: 与 LangChain4j 完美集成

### 🌟 系统亮点

- **离线处理**: 完整的文档处理链（5层）
- **在线查询**: 智能的查询处理链（3层）
- **核心检索**: 灵活的内容检索器（支持多数据源）⭐
- **动态配置**: 运行时调整检索策略

### 📚 技术栈

- **框架**: Spring Boot
- **RAG**: LangChain4j
- **设计模式**: Strategy + Factory
- **向量存储**: EmbeddingStore（可扩展）

---

## 未来扩展方向

### 短期（可立即实现）

- [ ] WebSearchContentRetriever - 网络搜索检索器
- [ ] HybridContentRetriever - 混合检索器（向量+全文）
- [ ] ContentReranker - 结果重排序器
- [ ] ContentAggregator - 内容聚合器

### 中期（需要额外集成）

- [ ] SqlDatabaseContentRetriever - SQL数据库检索器
- [ ] AzureAiSearchContentRetriever - Azure AI Search
- [ ] Neo4jContentRetriever - 图数据库检索器
- [ ] ElasticsearchRetriever - Elasticsearch检索器

### 长期（需要深度定制）

- [ ] 多模态RAG（文本+图片+音频）
- [ ] 实时RAG（流式处理）
- [ ] 分布式RAG（大规模部署）
- [ ] 自适应RAG（AI自动优化参数）

---

**这是一个展示设计模式在企业级RAG系统中深度应用的完整案例！** 🎉
