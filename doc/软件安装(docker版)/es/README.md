# 历史 Elasticsearch 安装文件

本目录的 `es.yml` / `kibana.yml` 是原项目的 Elasticsearch 7.8 安装资料，保留供查阅。当前 JDK 21 / Spring Boot 4.0 应用使用 Elasticsearch 9.x HTTP 客户端，不能按此处的旧版本新建服务。

当前应用连接现有 NAS Elasticsearch 的 9200 HTTP 端口，并使用 `analysis-smartcn` 插件。新版客户端与服务端的联调暂缓，版本关系、索引迁移及后续验证见 [升级记录](../../upgrade-java21.md)。本次未调整已有 Elasticsearch 服务，也未操作索引。
