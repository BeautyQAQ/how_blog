# Nacos 2.5.5 单机部署

此目录只启动 Nacos，连接已存在的 MySQL 实例和 `nacos_config` 数据库，不启动 MySQL、Redis，也不执行建表。镜像固定为 `nacos/nacos-server:v2.5.5`；Docker Hub 已验证提供 amd64、arm64 镜像。

## 本次 NAS 部署记录（2026-10-29）

已在 `192.168.20.5` 的 `/home/ubuntu/how-blog/nacos` 部署容器 `how-blog-nacos`，控制台为 `http://192.168.20.5:8848/nacos/`，客户端使用 `192.168.20.5:8848`，同时需要能访问 `9848`。已确认容器为 healthy，MySQL 的 `nacos_config` 包含 10 张表，管理员已初始化，public / DEFAULT_GROUP 中已导入下面说明的 5 个业务配置。

本机私密文件 `doc/deploy/app.env` 已填写 Nacos 登录凭据、MySQL/Redis 连接参数和共享 JWT 密钥；服务端私密参数保存在本机本目录的 `.env`，并同步到 NAS 部署目录的 `.env`（权限 600）。这些文件均不提交 Git。Nacos 登录用户名为 `nacos`，密码查看 `app.env` 中的 `NACOS_PASSWORD`。应用启动前，仍需按根 README 将 `app.env` 中对应的变量配置到应用进程环境。

已通过实际 Nacos 2.x SDK 的配置读取、gRPC 注册/发现/注销，并在独立缓存目录下启动文章服务，验证 MySQL 查询接口及 Redis 密码鉴权。临时文章服务已关闭。全部模块编译和 12 项测试通过；本机验证使用 JDK 8 并临时传入 `-Djava.version=1.8 -Dmaven.compiler.source=8 -Dmaven.compiler.target=8`，项目的 JDK 11 设置保持不变。

旧版 Spring Cloud Alibaba 会把空 `contextPath` 传给新版 SDK，覆盖默认的 `/nacos` 并导致登录 HTTP 404。5 个模块的 bootstrap 已显式设置配置中心和注册中心的 `context-path=/nacos`，回归测试覆盖这一兼容要求。

## 准备配置

将本目录复制到运行 Docker 的 NAS。在此目录执行：

```sh
cp .env.example .env
```

编辑 `.env`，填写 MySQL 账号和密码，以及三个 Nacos 鉴权参数。真实 `.env` 已加入 Git 忽略规则。

`MYSQL_HOST=host.docker.internal` 指向 Docker 所在 NAS；`MYSQL_PORT` 填 MySQL 在 NAS 上发布的端口。Compose 使用 `host-gateway`，要求 Docker Engine 20.10 或更高版本。如果 MySQL 在另一台设备，填写其可达地址。如果 MySQL 与 Nacos 已连接同一 Docker 网络，也可使用 MySQL 容器名和容器内部端口。

MySQL 账号需要能从 Nacos 容器连接并读写 `nacos_config`。数据库连接参数按同一内网的 MySQL 设置；MySQL 使用 TLS 时，应相应修改 Compose 中的 `MYSQL_SERVICE_DB_PARAM`。

生成随机 token 和独立的服务身份凭据，例如：

```sh
openssl rand -base64 48
openssl rand -hex 32
```

将第一条结果填入 `NACOS_AUTH_TOKEN`，第二条结果填入 `NACOS_AUTH_IDENTITY_VALUE`。`NACOS_AUTH_IDENTITY_KEY` 自定义一个 HTTP 请求头名称，例如 `X-How-Blog-Nacos-Identity`。token 必须是 Base64 字符串，解码后的原始密钥长度至少 32 个字符。这些参数属于 Nacos 服务端鉴权设置，与控制台登录密码分别配置。

## 启动并初始化管理员

项目的其他容器使用外部网络 `how`。首次运行前确保此网络存在：

```sh
docker network inspect how >/dev/null 2>&1 || docker network create how
docker compose config --quiet
docker compose up -d
docker compose ps
docker compose logs --tail=100 nacos
```

首次启动可能需要一段时间。健康检查访问 `GET /nacos/v1/console/health/readiness`；容器状态变为 `healthy` 后，打开 `http://NAS地址:8848/nacos/`，按页面提示初始化 `nacos` 管理员密码，再登录。该版本不预置 `nacos/nacos` 登录密码；已有管理员时无需重新初始化。

如需用 API 初始化，官方接口为 `POST /nacos/v1/auth/users/admin`，表单参数为 `password`；仅在尚无全局管理员时允许调用。可以省略 `password` 让服务端生成随机密码，此时务必保存响应中的密码。

客户端连接地址仍填写 `NAS地址:8848`。Nacos 2.x 客户端还会连接 `NAS地址:9848`，因此两个端口都要可达。单机无需向宿主机发布集群端口 `9849`。当前端口映射保持默认偏移关系，修改 `8848` 对外端口时也要对应调整客户端 gRPC 端口。

堆内存初始值为 256 MiB、最大值为 512 MiB，新生代为 128 MiB；进程总内存还包含线程、元空间和直接内存，因此会高于 Java 堆上限。运行数据和日志保存在当前目录的 `data/`、`logs/`，配置和用户持久化在外部 `nacos_config`。

登录后，在 public 命名空间中逐份新建 `doc/nacos-public` 的 5 个配置：Data ID 使用完整文件名（例如 `how-article-dev.yaml`），Group 为 `DEFAULT_GROUP`，格式为 YAML，内容原样复制。保留 `${MYSQL_PASSWORD}` 等环境变量占位符；业务密码由应用进程的启动环境提供，应用获取配置后解析占位符。

本目录 `.env` 仅提供 Nacos 服务端容器使用的 MySQL 与鉴权参数。业务应用分别设置自己的环境变量，参考 [应用环境变量示例](../app.env.example) 和 [本地应用启动说明](../../../README.md#nacos-与本地应用启动)；普通 `.env` 文件不会自动被 Spring Boot 读取。`nacos_config` 用于 Nacos 自身，业务服务仍连接 `how-blog`。

## 官方依据

- [2.5.5 镜像标签](https://hub.docker.com/r/nacos/nacos-server/tags?name=v2.5.5)
- [2.5.5 Docker 镜像配置及环境变量](https://github.com/nacos-group/nacos-docker/blob/v2.5.5/build/conf/application.properties)
- [2.5.5 Docker 启动脚本](https://github.com/nacos-group/nacos-docker/blob/v2.5.5/build/bin/docker-startup.sh)
- [Nacos 2.x 鉴权配置](https://nacos.io/docs/v2/guide/user/auth/)
- [2.5.5 管理员初始化接口实现](https://github.com/alibaba/nacos/blob/2.5.5/plugin-default-impl/nacos-default-auth-plugin/src/main/java/com/alibaba/nacos/plugin/auth/impl/controller/UserController.java)
