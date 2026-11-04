# 测试环境导入与联调记录（2026-11-04）

数据库初始化和本轮非短信测试已完成。MySQL 使用 DBX `nas-mysql` 的 `how-blog` 库；Kafka 使用 DBX 中 `192.168.20.10:9092` 的连接。当前 DBX 未提供 ES 连接及 HTTP 执行工具，因此 ES 通过配置中的 `192.168.20.5:9200` 和业务搜索接口验证。

## 数据库与中间件

| 项目 | 执行结果 |
| --- | --- |
| 初始化脚本 | 通过 DBX 固定会话执行完整 `doc/sql/how-blog.sql`，19 条语句全部成功；库表已存在，未覆盖数据 |
| 表结构 | 16 张表、106 个字段，与脚本逐项核对名称、类型、可空性和 varchar 长度一致；InnoDB / utf8mb4_unicode_ci |
| MySQL 实际读写 | 评论、吐槽写入中文与 emoji；审核时间、状态、默认计数及原子计数更新正确；显式回滚后两条测试记录均为 0 |
| Redis | DBX PING 返回 PONG；真实文章接口验证首次读取、缓存读取及更新后缓存失效 |
| Kafka | 独立 topic `how-blog-smoke-20261104-1142` 创建成功；DBX 发送并读回同一 key / JSON，partition 0、offset 0；未提交消费位点，完成后删除该 topic |
| ES | 9.5.5，已安装同版本 analysis-smartcn；分词结果为「数据库」「搜索」「测试」；实际文章写入、搜索和更新成功 |
| Nacos | 当前 Nacos Client 3.1.1 实际加载配置、gRPC 注册与发现成功；四个服务均在 `192.168.20.3` 注册且 healthy=true |

四份非短信配置已发布到 public / DEFAULT_GROUP，并与本地 `doc/nacos-public` 原文逐份比对一致。修改前备份位于 `target/test-env-runtime/*.yaml.before`。短信配置未发布，本轮未启动 how_sms，未调用短信发送或短信注册接口。

搜索服务首次启动创建 article、channel、column、label、problem、reply 六个索引。测试后这六个索引文档数全部为 0。它们在单节点上使用默认副本配置，健康状态为 yellow，主分片可用；本轮未调整集群或副本策略。

## 自动化检查

使用 JDK 21、Maven Wrapper 3.9.16；文章、用户、搜索、网关及 common 构建成功。各套件最终报告合计 **17 项测试通过，失败 0，错误 0，跳过 0**：

| 套件 | 数量 |
| --- | ---: |
| NacosConfigurationContractTest | 5 |
| MySqlStorageJpaTest（独立 H2，不使用远程库） | 7 |
| JwtRequestContractTest | 3 |
| SearchArticleControllerTest | 1 |
| ErrorHandlerConfigTest | 1 |

执行过的构建命令（在后端根目录、JAVA_HOME 指向 JDK 21）：

```powershell
.\mvnw.cmd -B -ntp package -pl how_article,how_user,how_search,how_gateway_web -am
# 后续修复分别重建受影响的 user / gateway / search 模块，并执行对应回归测试。
```

可复跑的真实接口脚本：

```powershell
python doc/testing/smoke-test.py
```

前置条件是四个非短信服务已启动，端口分别为 9001、9002、9003、9005，并通过 Nacos 相互发现。脚本针对本机这些端口，**仅用于测试环境**，会创建唯一命名的临时数据和账号，结束时按本轮 ID 清理。结果写入 `target/test-env-runtime/smoke-results.json`，不写入账号密码或 JWT。

最终运行 `smoke_d92174beec4a` 的 **32 项断言全部通过**，包括：

- 三个 MVC 服务的 OpenAPI，以及经网关和服务发现转发的三个 OpenAPI。
- 网关拒绝无 JWT 的受保护请求；管理员 BCrypt、登录、错误密码拒绝、Bearer 信息查询；用户登录及受保护分页查询。
- 八类业务数据列表读取。
- 文章新增、中文及 emoji、缓存读取、MySQL 点赞、审核后经 Feign 写入 ES、全文搜索、更新及缓存失效。
- 评论 `_id` JSON 兼容性、审核及时间持久化、分页、跨服务读取用户名。
- 吐槽及回复新增、父级回复分页、计数器。

脚本清理错误为 0；之后再通过 DBX 查询全部 16 张表，行数均为 0。初始化脚本仅导入结构，本轮未保留演示业务数据。六个业务 ES 索引均为空，测试 topic 已删除。

## 本轮发现并修复的问题

1. Spring Boot 4 的 TestEntityManager 包迁移，及独立 JPA 测试误读取业务 Nacos 配置。
2. Alibaba BOM 选入 Jackson databind 2.12.7.1，与 Boot 管理的 Jackson 2 模块不兼容，导致 Hibernate 启动失败。根 POM 显式优先导入 Boot 所选 Jackson 2 BOM，同时保留 Jackson 3。
3. Jackson 3 下评论与吐槽 `_id` 属性名变化；为字段显式声明 JsonProperty，保留旧接口契约。
4. 网关异常处理器未实现 Boot 4 的 ErrorWebExceptionHandler，导致与自动配置的同名 bean 冲突。
5. 管理员信息接口未去除 Bearer 前缀；JWT 拦截器的 Claims 属性名及类型与控制器不一致。修复后管理员信息查询和临时用户删除均成功；网关同时停止记录 JWT 内容。
6. 搜索文章新增与更新接口缺少 RequestBody，Feign 发送 JSON 后被绑定为空对象，产生空 ES 文档。补充注解并增加实际 JSON 绑定回归测试；失败测试产生的唯一空文档已按 ID 删除。

## 当前运行状态与范围

四个非短信服务在本机运行，网关入口为 `http://localhost:9005`，文档入口为 `/swagger-ui/index.html`。运行日志及进程清单位于 `target/test-env-runtime/`；该目录由现有 target 忽略规则排除出版本控制。

本轮是后端初始化、回归和接口冒烟测试，未覆盖前端、性能、并发、故障恢复或完整权限审计。阿里云短信配置、短信服务启动、验证码发送和真实短信消费全部排除。
