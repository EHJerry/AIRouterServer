# LLM Proxy

## 项目介绍

LLM Proxy 是一个基于 Java Spring Boot 的大模型代理服务，提供统一的 RESTful API 接口，支持多种大模型（如 MiniMax、GLM、OpenAI、DeepSeek）的调用。

**核心特性**:
- ✅ **高并发支持**: 集成线程池，支持同时处理多条请求
- ✅ **异步处理**: `/v1/chat` 接口已改为异步调用，提升响应速度
- ✅ **HTTP/2 支持**: 优化 HTTP 客户端配置，支持 HTTP/2 协议
- ✅ **连接池管理**: 配置可复用的 HTTP 连接池
- ✅ **完善的日志系统**: 请求日志和系统日志分离，按日期自动分割

## 项目结构

```
aitools/
├── LLM/                    # 大模型客户端模块
│   ├── src/main/java/com/aitools/llm/
│   │   ├── LLMClient.java          # 客户端接口（含异步方法）
│   │   ├── LLMFactory.java         # 工厂类
│   │   ├── LLMResponse.java        # 响应封装
│   │   ├── Message.java            # 消息模型
│   │   └── impl/                   # 模型实现
│   │       ├── OpenAICompatibleClient.java  # 基础客户端
│   │       ├── MiniMaxClient.java
│   │       ├── ZhipuClient.java
│   │       ├── OpenAIClient.java
│   │       └── DeepSeekClient.java
│   └── pom.xml
├── server/                  # Spring Boot 服务端
│   ├── src/main/java/com/aitools/
│   │   ├── LLmProxyApplication.java    # 启动类
│   │   ├── controller/                 # REST 控制器
│   │   │   └── V1Controller.java       # /v1/chat 异步接口
│   │   ├── service/                    # 服务层（异步支持）
│   │   │   └── LLmService.java
│   │   ├── dto/                        # 数据传输对象
│   │   ├── config/                     # 配置类
│   │   │   ├── HttpClientConfig.java   # HTTP 客户端配置
│   │   │   ├── ThreadPoolConfig.java   # 线程池配置
│   │   │   └── LLmConfig.java          # LLM 配置
│   │   └── interceptor/                # 请求拦截器
│   ├── src/main/resources/
│   │   ├── application.yml             # 应用配置
│   │   └── logback.xml                 # 日志配置
│   └── pom.xml
├── log/                     # 日志目录（运行时自动生成）
└── pom.xml                  # 父项目配置
```

## 技术栈

- **Java**: 17+
- **框架**: Spring Boot 3.2.0
- **日志**: Logback
- **HTTP客户端**: Java HttpClient (HTTP/2 支持)
- **JSON处理**: Jackson
- **异步处理**: CompletableFuture + 线程池

## 快速开始

### 1. 配置 API Key

编辑 `server/src/main/resources/application.yml`，配置各模型的 API Key：

```yaml
server:
  port: 8090

llm:
  http:
    connect-timeout: 10000      # 连接超时（毫秒）
    read-timeout: 60000         # 读取超时（毫秒）
    connection-pool-size: 20    # 连接池大小
  minimax:
    api-key: your-minimax-api-key
    model: MiniMax-M3
  glm:
    api-key: your-glm-api-key
    model: glm-4
  openai:
    api-key: your-openai-api-key
    model: gpt-3.5-turbo
  deepseek:
    api-key: your-deepseek-api-key
    model: deepseek-chat
```

### 2. 编译运行

```bash
# 进入项目目录
cd aitools

# 安装 LLM 模块
mvn install -pl LLM -DskipTests -q

# 启动服务端
mvn spring-boot:run -pl server
```

服务启动在 `http://localhost:8091`

## API 接口

### POST /v1/chat

**异步聊天接口**（支持高并发），用于调用大模型进行对话。

**请求体**:
```json
{
  "modelType": "minimax",    // 必填，模型类型: minimax, glm, openai, deepseek
  "modelName": "MiniMax-M3", // 可选，模型名称，不填则使用配置默认值
  "question": "你好"         // 必填，用户问题
}
```

**响应体**:
```json
{
  "success": true,           // 请求是否成功
  "answer": "你好！我是 MiniMax-M3...", // 模型回复内容
  "model": "MiniMax-M3",     // 使用的模型名称
  "tokens": 389,             // 消耗的 token 数量
  "error": null              // 错误信息（失败时返回）
}
```

### 使用示例

**curl**:
```bash
curl -X POST http://localhost:8091/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"modelType":"minimax","modelName":"MiniMax-M3","question":"你好，请介绍一下你自己"}'
```

**PowerShell**:
```powershell
Invoke-WebRequest -Uri http://localhost:8091/v1/chat `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"modelType":"minimax","modelName":"MiniMax-M3","question":"你好"}' `
  -UseBasicParsing | Select-Object -ExpandProperty Content
```

**JavaScript**:
```javascript
fetch('http://localhost:8091/v1/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    modelType: 'minimax',
    modelName: 'MiniMax-M3',
    question: '你好'
  })
}).then(res => res.json()).then(console.log);
```

## 性能优化配置

### HTTP 客户端配置

```yaml
llm:
  http:
    connect-timeout: 10000      # 连接超时，建议 5000-30000ms
    read-timeout: 60000         # 读取超时，建议 30000-120000ms（大模型响应较慢）
    connection-pool-size: 20    # 连接池大小，建议 CPU核心数 × 2
```

### 线程池配置

线程池配置位于 `server/src/main/java/com/aitools/config/ThreadPoolConfig.java`：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| 核心线程数 | CPU × 2 | 保持活跃的线程数 |
| 最大线程数 | CPU × 4 | 最大并发线程数 |
| 队列容量 | 1000 | 等待队列大小 |
| 拒绝策略 | CallerRunsPolicy | 队列满时由调用线程执行 |

## 日志配置

日志文件输出到项目根目录下的 `log/` 文件夹：

- `llm-proxy.log` - 主日志文件（包含请求日志和系统日志）
- 日志按日期自动分割，保留30天历史
- 请求日志包含：请求方法、URI、客户端IP、响应状态码、耗时

## 支持的模型

| 模型类型 | API端点 | 说明 |
|---------|--------|------|
| minimax | https://api.minimax.chat/v1/chat/completions | MiniMax 模型 |
| glm | https://open.bigmodel.cn/api/paas/v4/chat/completions | 智谱 GLM 模型 |
| openai | https://api.openai.com/v1/chat/completions | OpenAI 模型 |
| deepseek | https://api.deepseek.com/v1/chat/completions | DeepSeek 模型 |

## 常见问题

### Q: 如何修改服务端口？
A: 修改 `application.yml` 中的 `server.port` 配置

### Q: 如何配置域名访问？
A: 修改 `server.port` 为 80，并配置 DNS 或 hosts 文件映射

### Q: 遇到 401 错误怎么办？
A: 检查 `application.yml` 中的 API Key 是否正确配置

### Q: 如何调整并发能力？
A: 修改线程池配置 `ThreadPoolConfig.java` 中的参数

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 许可证

MIT License
