# JDK 21 与 Spring 依赖升级（2026-11-04）

## 版本基线

| 组件 | 原版本 | 当前版本 |
| --- | --- | --- |
| JDK / 编译 release / 容器运行时 | 11（项目原配置） | 21 |
| Maven | 未统一 | Wrapper 3.9.16，要求 Maven 3.9.x |
| Spring Boot | 2.2.1.RELEASE | 4.0.9 |
| Spring Cloud | Hoxton | 2025.1.4 |
| Spring Cloud Alibaba | 2.2.0.RELEASE | 2025.1.0.0 |
| Nacos Client | 显式固定 2.5.5 | Alibaba BOM 管理的 3.1.1 |
| API 文档 | Springfox / Knife4j 2.0.6 | springdoc-openapi 3.0.3 |
| JJWT | 0.9.1 | 0.13.0 |
| Hutool | 旧版 | 5.8.48 |
| 阿里云短信 Core SDK | 旧版 | 4.7.10 |
| JSON | Jackson 2 | Boot 管理的 Jackson 3 |
| MySQL、Hibernate、Redis、Kafka、Lombok 等 | 部分显式指定 | Spring Boot BOM 管理 |

表中的 JDK 11 来自升级前仓库的配置：根 POM 的 `java.version` 为 `11`，业务 Dockerfile 使用 `adoptopenjdk/openjdk11`。这不表示本机安装过 JDK 11。

2026-11-04 检查本机安装目录、卸载注册项、环境变量、IDE SDK 设置及用户目录中的 Java 安装和下载文件，未发现 JDK 11 安装或残留，无可卸载、删除的 JDK 11 目标。本机独立 JDK 为 `C:/Program Files/Java/jdk8u412-b08`（8.0.412）和 `C:/Program Files/Java/graalvm-jdk-21.0.3+7.1`（21.0.3）；系统 PATH 当前仍指向 JDK 8，本项目构建需使用 JDK 21。

Alibaba 官方将 2025.1.0.0 对应到 Boot 4.0、Cloud 2025.1 和 Nacos Client 3.1.1，因此采用同一系列的 Boot / Cloud 修订版本。springdoc 的官方矩阵将 Boot 4.0 对应到 3.0.x，因此使用 3.0.3。未单独覆盖 Spring Framework、Hibernate 或 Elasticsearch Java Client 的版本。

## 代码与配置变化

- 全部六个模块使用 Java 21；根 POM 限定 JDK 21 和 Maven 3.9.x，并显式配置 Lombok 注解处理器。五个业务镜像统一为 `eclipse-temurin:21-jre`。
- JPA、Servlet、Validation 等 Java EE API 改为 `jakarta.*`；`javax.crypto`、`javax.sql` 属于 JDK API，保留原包名。好友和非好友实体使用独立的 `FriendId` 复合主键，DAO 主键类型同步修正。
- Spring Security 使用 `SecurityFilterChain`；用户服务继续使用原控制器和 JWT 拦截器的授权规则。MVC 配置改为 `WebMvcConfigurer`，保留自动配置与 Swagger UI 静态资源处理。
- 替换 Springfox 注解和 Docket 配置为 OpenAPI 3 / springdoc；网关通过三个 `/v3/api-docs` 地址聚合文档。UI 入口为 `/swagger-ui/index.html`，原 Knife4j `/doc.html` 入口停用。
- JJWT 使用新 builder / parser API，继续使用 Base64 解码后的 HS256 密钥与原 claims。文章、用户、网关的 `jwt.config.key` 必须相同，解码后至少 32 字节，当前已在 YAML 中填写同一把现有密钥。移除旧 JAXB 兼容依赖。
- Jackson 数据绑定使用 `tools.jackson.*`；Jackson 注解仍使用 `com.fasterxml.jackson.annotation.*`。网关异常处理改用 Spring 的 `WebExceptionHandler`，保留原 JSON 返回结构。
- Alibaba 2025.1 不再使用 `bootstrap.properties`；五个服务通过 `application.properties` 中的 `spring.config.import=nacos:...` 加载配置，保留用户名、密码、namespace、group 和 `/nacos` API 路径。
- Redis 配置改为 `spring.data.redis.*`；Gateway 路由改为 `spring.cloud.gateway.server.webflux.*`。Feign 使用现行超时配置和 Resilience4j 熔断回退，移除旧 Hystrix 配置。
- 搜索使用 Boot 的 Elasticsearch starter 和 HTTP Java Client，`spring.elasticsearch.uris` 为 `http://192.168.20.5:9200`。移除旧 Transport Client、文档 type 和未使用的旧高亮辅助类。中文字段改用 NAS 已安装插件提供的 `smartcn`。
- 后续 ES / MQ 整改：ES 地址为 `http://192.168.20.5:9200`、用户名 `elastic`，账号密码直接填写在 YAML 中。用户和短信模块使用 Boot Kafka starter 替换 AMQP，连接 `192.168.20.10:9092`，通过 JSON 传输 `mobile`、`code`；序列化器使用与 Jackson 3 对应的 `JacksonJsonSerializer` / `JacksonJsonDeserializer`。短信服务声明 topic 并使用固定消费组、逐条提交 offset，忽略过期验证码，发送失败则抛出异常触发消费重试。两套 Nacos 配置和连接值参考文件同步更新，远程 Nacos 尚未发布，本次不执行测试。
- 内网调试配置：五个模块的 Nacos 启动配置、两套业务 YAML，以及当前 r-nacos Compose 均改为直接填写实际值，不再依赖环境变量占位符。MySQL 使用 `192.168.20.5:3306/how-blog` 和 `root` 账号，Redis 使用 `192.168.20.5:6379`；阿里云短信四项尚无实际值，保留空字符串并标记待填写。
- GitHub Actions 使用 Temurin 21、Maven Wrapper 和 Docker CLI。移除过时的 Spotify Docker Maven 插件及对应调用，镜像构建上下文是各模块目录。保留现有发布流程跳过测试的策略及原触发条件，本次未触发 CI。
- 现有测试源码已适配新 API 和配置路径，JPA 测试显式禁用远程配置导入；未执行测试。

## 本地构建

将 `JAVA_HOME`、IDE Project SDK、Maven Runner JRE 和各启动项 JRE 均设置为 JDK 21。不要继续使用系统默认的旧 JDK。建议使用已更新安全补丁的 JDK 21 发行版。

从后端根目录只编译生产源码：

```powershell
.\mvnw.cmd -B -ntp compile
```

Linux / macOS 使用 `bash ./mvnw -B -ntp compile`。首次运行 Wrapper 会下载 Maven，并校验已固定的 SHA-256。只编译不会进入测试阶段，也不会启动应用。

后续打包可以使用 `package`；需要临时跳过测试时传入 `-DskipTests`，该选项仍编译测试源码。打包后从各模块运行 `java -jar target/模块名.jar`，或在后端根目录使用 `docker build -t how_article:local how_article` 等命令构建对应镜像。应用内网连接配置见根 README。

## 部署与后续验证

按本次要求暂缓测试和应用启动。本地生产源码编译通过仅证明编译兼容性，不代表业务、数据库或中间件联调通过。

NAS 上的 r-nacos 部署及当前配置尚未因本次应用升级而修改。`doc/nacos-public` 和 `doc/nacos-dev` 已更新为包含实际连接值的新格式；启动新版应用前需要将其中一套的五份配置发布到对应 namespace / group。应用无需加载 env 文件，直接编辑各模块的 `application.properties` 和业务 YAML。

Elasticsearch 需使用兼容的 9.x 服务与同版本 `analysis-smartcn` 插件。Spring Data Elasticsearch 6.0.x 的官方版本表列出的基线为 Elasticsearch 9.2.2；NAS 的 9.5.5 与本客户端的实际行为仍待联调。旧 `doc/软件安装(docker版)/es` 的 7.8 安装文件已标明为历史资料，不能用于当前应用。若存在 IK 或旧 type 的搜索索引，需要先备份并规划重建；本次未删除或重建任何索引。

后续统一验证覆盖：

1. 现有配置契约和 H2 JPA 测试，以及五个应用的上下文启动。
2. Nacos Client 3.1.1 对 r-nacos 0.8.8 的鉴权、五份配置读取、变更监听、服务注册及发现。此前 2.5.5 SDK 的验证结果仅适用于旧客户端。
3. MySQL 读写、复合主键和计数更新；Redis 缓存、Kafka 验证码消息的生产确认、消费组、重试与过期处理；Feign 负载均衡、超时回退。
4. 网关路由、跨域、JWT 签发和校验、旧有效令牌兼容性，以及 OpenAPI UI / JSON 地址。
5. Elasticsearch 新索引映射、中文检索、分页与数据同步；短信模块使用隔离配置，避免验证时发送真实短信。

## 官方依据

- [Spring Cloud Alibaba 版本关系](https://sca.aliyun.com/en/docs/2025.x/overview/version-explain/)
- [Nacos 新版配置加载说明](https://sca.aliyun.com/docs/2025.x/user-guide/nacos/advanced-guide/)
- [Spring Boot 4.0 迁移指南](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
- [Spring Cloud 受支持版本](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions)
- [springdoc 兼容矩阵及 Springfox 迁移](https://springdoc.org/)
- [Spring Data Elasticsearch 版本关系](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/versions.html)
- [analysis-smartcn 插件](https://www.elastic.co/docs/reference/elasticsearch/plugins/analysis-smartcn)
- [JJWT](https://github.com/jwtk/jjwt)
- [Maven Wrapper](https://maven.apache.org/wrapper/)
- [Spring Boot Kafka 自动配置和 JSON 序列化](https://docs.spring.io/spring-boot/4.0/reference/messaging/kafka.html)
