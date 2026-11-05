# 文章链路与前端升级验收（2026-11-05）

## 技术栈和构建

- 后端使用现有 JDK 21、Spring Boot 4.0.9。四个非短信模块及 common 构建通过。文章模块运行副本位于 target/core-runtime，避免 Windows 锁住构建 jar。
- 管理端：Vue 3.5.44、Vite 8.3.5、Element Plus 2.14.7、Pinia 4.1.0、Vue Router 5.4.0、Axios 1.21.0；`npm run lint`、`npm run build` 通过。
- 前台：Nuxt 4.6.4、Vue 3.5.44、TypeScript 6.0.3；`npm run typecheck`、`npm run build` 通过，Nitro node-server 输出已实际启动。Vue Router 5.4.0，Nuxt builder 固定为 4.6.4。
- 两端在 Node.js 24.21.0 验证；package-lock.json 已纳入版本控制范围。活动依赖中已无 Vue 2 / Nuxt 1 / Webpack / Node Sass / Element UI / Vuex。
- Nuxt 类型检查器尚不兼容 TypeScript 7 的编译器包导出，已固定到验证通过的 TypeScript 6.0.3。Nuxt builder 所需的 acorn 显式固定为 8.19.0。

## 自动化结果

全部 Java 套件共 **28 项通过、0 失败、0 错误、0 跳过**。其中新增文章生命周期 8 项、文章权限 3 项；其余为既有回归测试。

文章生命周期覆盖服务端默认值、强制待审核、长文本及中文 emoji、审核的最新状态、缓存失效、ES 失败回滚、草稿不依赖 ES、计数/创建时间保护、公开过滤、精确频道匹配、字面关键词、隐藏/重新公开、删除和非法参数。权限测试覆盖缺失、伪造 JWT、普通用户和管理员。

真实环境脚本：

```powershell
python doc/testing/draft-smoke-test.py
python doc/testing/smoke-test.py
```

- 不依赖 ES 的草稿脚本 **12 项断言全部通过**，临时账号和文章清理成功。结果：target/core-runtime/draft-smoke-results.json。
- MySQL / Redis / Feign / ES / Gateway 全链路脚本 **46 项断言全部通过**，run 为 smoke_533b8af87ecc，failure=null、cleanup_errors=[]。结果：target/test-env-runtime/smoke-results.json。
- 后者验证审核后索引写入、公开查询、缓存中审核状态更新、编辑后索引和计数保留、隐藏后公开详情404/列表排除/ES移除、重新公开索引恢复，以及评论和吐槽原有接口。
- 未发送验证码，未调用短信注册，未启动 how_sms。

## 浏览器和 SSR

使用真实浏览器检查新版管理端和 Nuxt 生产输出：

- 管理员登录成功；真实 JWT 过期后清理会话并返回登录页；控制台未发现 JavaScript error。
- 空文章表单显示必填错误；新增文章写入 MySQL，默认待审核、计数为零；城市新增及列表读取成功。
- 草稿前台列表不可见，直接访问详情返回 HTTP 404，并展示中文错误页面。
- 管理端审核后，文章出现在前台；详情标题、SEO、中文 emoji 与正文换行正常。原始 HTML 检查也包含正文，确认 SSR 生效。
- 文章隐藏后刷新公开详情为404；通过管理端编辑、公开和置顶后重新出现，更新后的正文正确。
- 窄视口中发现 Element Plus 行内下拉框宽度不足，已设置稳定宽度；弹窗增加视口最大宽度限制。桌面和窄视口均做了布局观察，未做完整设备兼容测试。

截图在 target/core-runtime/public-article.png 与 manager-articles.png。截图中的 UI 临时验收数据在完成后已清理；之后按用户要求另外保留正式演示数据。

## 演示与启动

保留数据通过 `python doc/testing/seed-demo-data.py` 创建；该脚本不发送短信，不删除既有数据，重跑保留手工编辑。登录信息在 target/demo-data/登录信息.txt，清单在 target/demo-data/manifest.json。

DBX 已核对：14篇文章（11篇公开已审核、2篇待审核、1篇已审核未公开）、1个管理员、1个普通用户、3个频道、4个标签、3个城市、2个专栏、3条评论、2条吐槽（含回复）。公开文章超过一页，便于测试分页；保留待审核文章、专栏和评论，便于测试审核按钮。演示管理员登录名为 demo_admin，普通用户 API 登录标识为 demo_reader，密码只存放在上述本机文件。

数据脚本重复运行后数量保持一致。真实 API 验证公开分页为10+1篇、置顶排序、频道过滤、ES检索与首页SSR；浏览器也验证从第一页到第二页再返回。当前保留数据首页截图为 target/core-runtime/demo-home.png。

入口：前台 http://127.0.0.1:3000，管理端 http://127.0.0.1:9528，网关 http://127.0.0.1:9005。四个非短信后端及两个前端已在本机启动；服务进程和日志位于 target/core-runtime，前端日志在各自 dev.log。

本轮 DBX 使用 nas-mysql 及 how-blog 库核对实际持久化；可用连接列表仍未显示 ES，因此 ES 通过原配置地址和业务搜索接口验证。ES 最初掉线，用户重启后恢复，搜索服务已重新启动并完成上述全链路测试。

## 剩余限制

- 正文当前是纯文本；前台普通用户登录、评论互动、问答/吐槽页面、上传等仍待开发。历史静态页面保留在 legacy，未伪装为已接入业务。
- 其余历史接口尚未完成统一权限治理，见项目推进记录；本轮不是生产上线验收。
- MySQL 与 ES 无分布式事务，仍需 outbox/重试/对账；Redis 异常时缓存清理补偿仍待补齐。
- 管理端生产依赖 npm audit 为 0 项；前台根依赖审计为 8 项中等级、2 项低等级、0 高/严重，集中在 Nitro 构建依赖链的 glob/minimatch/deepmerge 等。未按 npm 的回退/破坏性修复建议降级 Nuxt，需继续跟踪上游版本。本轮未对部署输出作全面安全审计。
- 管理端全量 Element Plus 产生大于500KB的主包提示，后续可按组件拆分；本轮未进行性能/并发/浏览器矩阵测试。
