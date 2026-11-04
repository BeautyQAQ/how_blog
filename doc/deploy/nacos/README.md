# r-nacos 单机部署

NAS 当前使用 **r-nacos v0.8.8**，部署目录为 `/home/ubuntu/nacos`，容器名为 `how-blog-nacos`，Compose 项目名为 `nacos`。镜像固定版本及摘要，避免 `stable` 标签更新后意外升级。r-nacos 是 Rust 实现的兼容服务，有自己的版本号，并非官方 Nacos 3.3.0 的 Rust 发行包。

## 访问和凭据

| 用途 | 地址 |
| --- | --- |
| 无鉴权 Web 控制台（内网使用） | http://192.168.20.5:8848/rnacos/ |
| 鉴权 Web 控制台 | http://192.168.20.5:10848/rnacos/ |
| 应用连接地址 | 192.168.20.5:8848 |
| SDK gRPC | 192.168.20.5:9848 |
| 健康检查 | http://192.168.20.5:8848/health |

已按内网使用需求设置 `RNACOS_ENABLE_NO_AUTH_CONSOLE=true` 和 `RNACOS_ENABLE_OPEN_API_AUTH=false`，通过 `8848/rnacos/` 可直接管理配置与服务，无需登录。v0.8.8 在 OpenAPI 鉴权开启时会关闭无鉴权控制台，因此两个参数需要一起调整，应用 SDK / OpenAPI 也无需鉴权。`10848/rnacos/` 保留鉴权控制台。开关逻辑见 [v0.8.8 源码](https://github.com/nacos-group/r-nacos/blob/v0.8.8/src/web_config.rs)。

管理员用户名为 `nacos`。当前内网账号密码已直接填写在 `compose.yaml` 和五个应用的 `application.properties` 中，应用启动无需设置环境变量；该账号也用于 `10848` 的鉴权控制台。NAS 原 `/home/ubuntu/nacos/.env` 可保留作原部署记录。旧的 `8848/nacos/` 控制台入口已改为上表中的 r-nacos 控制台入口。

## 迁移记录（2026-11-04）

原服务是 `nacos/nacos-server:v2.5.5`，在 `/home/ubuntu/how-blog/nacos` 运行。迁移前 Java 堆已降至初始 96 MiB、最大 192 MiB，容器仍约占 450 MiB。r-nacos 旁路验证时约占 9 MiB，正式切换后验证结束采样约为 7 MiB；这是 5 份配置、少量临时 SDK 操作下的采样，不能代表高负载上限。新容器的内存上限设为 256 MiB。

通过 DBX 的 `nas-mysql` 连接确认，`nacos_config` 内有 10 张表、5 份 public / DEFAULT_GROUP 业务配置和 1 个管理员。迁移时保存了 SQL 备份，通过旧服务 API 导出配置，并逐份比对数据库 MD5、导出内容及新服务读取结果，保留原始换行和 YAML 内容：

- `how-article-dev.yaml`
- `how-user-dev.yaml`
- `how-search-dev.yaml`
- `how-sms-dev.yaml`
- `how-gateway-web-dev.yaml`

旁路实例仅绑定 NAS 回环端口。使用迁移时项目的 **Nacos Client 2.5.5** 验证五份配置读取、配置发布与 gRPC 变更监听，以及服务注册、发现和注销，全部通过后切换原来的 `8848/9848` 端口。新容器使用同一数据目录重新创建，并再次核对配置，验证文件持久化。正式端口上的 SDK 检查也全部通过；临时配置、服务实例和空服务记录已清理，未启动业务应用。

上述验证发生在应用升级前，使用 Spring Boot 2.2.1、Spring Cloud Hoxton、Spring Cloud Alibaba 2.2.0 及 Nacos Client 2.5.5。当前代码已升级为 JDK 21 / Spring Boot 4.0 / Spring Cloud 2025.1 / Alibaba 2025.1，BOM 选择 Nacos Client 3.1.1；新组合尚未联调，旧 SDK 的结果不能替代新版本验证。详见 [应用升级记录](../../upgrade-java21.md)。五个模块继续显式设置配置中心和注册中心的 `context-path=/nacos`；此路径是客户端 API 路径，与新的 Web 控制台地址不同。

升级后的本地配置已调整 Redis、Elasticsearch 和 Gateway 配置项，用户与短信服务已从 RabbitMQ 切换为 Kafka `192.168.20.10:9092`，搜索服务使用 ES `http://192.168.20.5:9200` 和 `elastic` 账号。连接信息、密码和 JWT 密钥均已直接填入 YAML；这些改动尚未发布到 NAS。后续联调前需将 `doc/nacos-public` 中的 5 份配置更新到服务端。

## 存储与日常管理

r-nacos 使用自己的本地持久化文件，宿主机 `/home/ubuntu/nacos/data` 挂载到容器 `/io/nacos_db`，**不再连接 MySQL**。原 `nacos_config` 库完整保留供备份和回退使用，新服务的修改不会回写到该库。业务数据库 `how-blog` 不受影响。

日志写入 Docker stdout/stderr，设置每个文件 10 MiB、保留 3 个文件。健康检查使用镜像自带 bash 请求 `/health`；上游镜像未附带 curl/wget。

```sh
cd /home/ubuntu/nacos
docker compose ps
docker compose logs --tail=100 nacos
docker stats how-blog-nacos --no-stream
docker compose restart nacos
```

`compose.yaml` 中的 `RNACOS_INIT_ADMIN_USERNAME` / `RNACOS_INIT_ADMIN_PASSWORD` 仅在空数据目录首次启动时初始化管理员。已有用户需要在控制台的用户管理中修改密码，并同步应用的 `application.properties`，不能仅修改 Compose 来重置已有密码。`RNACOS_CLUSTER_TOKEN` 沿用现有的独立集群密钥。

## 在空目录新建实例

把本目录的 `compose.yaml` 复制到目标目录，直接编辑其中的管理员用户名、密码和集群密钥。当前文件已填写本项目的内网值，无需创建 `.env`。然后启动：

```sh
docker network inspect how >/dev/null 2>&1 || docker network create how
docker compose config --quiet
docker compose up -d
docker compose ps
```

此操作只创建空实例，不会自动导入旧 MySQL 配置。需要时从控制台导入，或通过 SDK / OpenAPI 发布 `doc/nacos-public` 中已填写实际值的配置。当前连接值也可查阅 [连接值参考](../app.env.example)。

## 备份与回退

迁移备份位于 NAS `/home/ubuntu/nacos/backups/`：

- `nacos_config-2.5.5.sql`：旧 MySQL 库的 schema、数据及历史记录。
- `nacos-2.5.5-configs.json`：5 份原始配置与 MD5。
- `legacy-compose.yaml`：切换前实际运行的 Java Compose。

旧部署目录 `/home/ubuntu/how-blog/nacos` 保留，旧容器重命名为 `how-blog-nacos-legacy-2.5.5`，已停止且关闭自动重启。其 `prod/` 是此前保存的 Java 部署参考，不属于当前 r-nacos 部署。不要同时启动新旧实例竞争端口。

日常可使用 r-nacos 控制台「数据迁移」导出备份；也可停止新服务后备份完整 `data/` 目录，再启动服务，避免在写入过程中直接复制持久化文件。

如需回退到迁移前状态：

```sh
cd /home/ubuntu/nacos
docker compose down
docker rename how-blog-nacos-legacy-2.5.5 how-blog-nacos
docker update --restart=unless-stopped how-blog-nacos
docker start how-blog-nacos
```

回退后使用旧部署 `.env` 中的原管理员凭据，并同步应用的 `application.properties`。切换到 r-nacos 后新增或修改的配置，需先另行导出并同步到旧服务；旧 MySQL 备份只代表迁移时状态。旧服务的管理员凭据仍保存在 NAS 原部署目录的环境文件中。

## 方案评估与依据

本次选择 r-nacos：当前需求是小规模配置中心与服务发现，现有 SDK 经实际验证可兼容，且有项目文档直接推荐的固定版本 Docker 镜像。代价是切换实现及存储格式，不能继续将 MySQL 表当作新服务的实时配置源，也不能假定官方 Nacos 的全部管理接口和插件均兼容。

GraalVM Native 确实能减少 JVM 启动与运行内存开销，但本次没有编译或测量 Nacos Native，不能给出同负载内存数字。Nacos 官方 3.3.0 Native 文档要求 GraalVM 25+，编译堆上限默认 8 GiB，插件及部分结构配置变化需要重新构建；目前无官方 Native Docker 镜像。此次检索也未找到可核实、持续维护、面向 3.3.0 的第三方 Native 镜像，因此不将它用于当前 NAS 迁移。普通 `nacos/nacos-server:v3.3.0` / `-slim` 仍是 JVM 镜像。

- [r-nacos v0.8.8 发布记录](https://github.com/nacos-group/r-nacos/releases/tag/v0.8.8)
- [r-nacos 项目、SDK 兼容性及 Docker 镜像](https://github.com/nacos-group/r-nacos)
- [运行参数](https://r-nacos.github.io/docs/04.运行参数说明.html)
- [数据备份与迁移](https://r-nacos.github.io/docs/07.数据备份与迁移.html)
- [Nacos 3.3 Native 官方指南](https://www.nacos.io/docs/latest/quickstart/quick-start-native/)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
