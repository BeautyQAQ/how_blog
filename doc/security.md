# 账号与接口权限（2026-11-05）

网关和三个业务服务共用 `AccessPolicy`，按 HTTP 方法和完整服务路径判断权限。不能仅依赖网关；直连文章、用户和搜索服务执行相同权限规则。缺失、无效、过期或无过期时间的 JWT 返回 HTTP 401；角色不符返回 HTTP 403，响应保留 Result JSON 格式。

## 权限范围

下表是业务服务路径；经过网关时分别加 `/article`、`/user` 或 `/search`。

| 入口 | 权限 |
| --- | --- |
| GET/HEAD `/article/public`、`/article/public/{id}` | 公开，仅已审核且公开文章 |
| GET/HEAD `/channel` | 公开，供首页筛选 |
| GET/HEAD `/media/{32位小写十六进制文件名}.png或.jpg` | 公开图片资源；与文章公开状态独立 |
| GET/HEAD `/comment/article/{articleid}` | 公开，仅公开文章的已审核评论；隐藏/不存在文章返回404 |
| POST `/admin/login`、`/user/login` | 公开密码登录 |
| POST `/comment` | 普通用户 JWT，作者取自 JWT；管理员不能以自己的管理员ID提交读者评论 |
| OpenAPI、Swagger 静态资源 GET/HEAD；OPTIONS | 公开 |
| 原始文章查询、其余历史 CRUD、评论搜索/审核/删除、`/init/data` | 管理员 |
| POST `/article/preview`、POST `/media`、DELETE `/media/{filename}` | 管理员；被文章引用的图片不能删除 |
| 搜索服务所有业务接口，包括查询、写入及 `/search/init` | 管理员 |
| 用户/管理员查询和维护、历史交友接口、短信相关接口 | 管理员；短信和交友暂不开放给前台用户 |

未知入口默认要求管理员；公开入口不使用字符串包含匹配。未规范化的路径不会匹配公开白名单。普通用户目前只能密码登录、提交评论，前台账号页面和评论界面另行迭代。

文章服务调用搜索/用户服务、搜索服务调用文章初始化接口时，通过 Feign 转发当前请求的 Authorization。没有请求身份时不会生成隐式管理员凭据；后台异步任务未来应单独设计受限的服务身份。

## 初始化第一个管理员

匿名 POST `/admin` 已关闭。空数据库需要在用户服务启动时显式启用一次初始化，密码不写入源码、命令行参数或日志。PowerShell 示例：

```powershell
$env:SECURITY_BOOTSTRAP_ENABLED='true'
$env:HOW_BOOTSTRAP_ADMIN_LOGIN='自行选择管理员登录名'
$env:HOW_BOOTSTRAP_ADMIN_PASSWORD='自行设置至少8字符且不超过72字节的密码'
java -jar how_user/target/how_user.jar
```

初始化使用串行化事务，仅允许管理员表为空时执行。已有管理员会拒绝重复初始化；参数缺失或不合法也会失败。完成后在后续启动的终端移除以上三个环境变量，再正常启动。已有管理员通过鉴权后的管理接口维护账号。

管理员和用户密码都按 BCrypt 保存，新增接口接收原始密码；修改时省略密码或传空字符串会保留原密码，提交新密码会重新加密。密码字段仅允许输入，Jackson 2/3 的所有账号响应均不输出密码。禁用管理员不能再登录。

## 评论数据边界

评论提交验证文章公开状态、非空正文和长度；回复对象必须是同篇文章下已审核的评论。作者、ID、发布时间、待审核状态和审核时间由服务端控制。管理分页可以看待审核内容；公开查询不能看待审核内容。删除文章在同一 MySQL 事务中清理关联评论，管理查询兼容历史遗留的已删除文章/用户。

## 验证与剩余事项

Markdown 先转义原始 HTML，再经过 commonmark URL 过滤和 jsoup HTML 白名单；管理预览与公开详情共用同一渲染器，前端只插入其 `contentHtml`。纯文本保留字面语法并转义 HTML。上传只接受真实解码成功的 PNG/JPEG，限制输入/处理后体积与像素，重编码丢弃原文件名、元数据和附加内容；不接收 SVG。图片响应固定 MIME 并带 `nosniff`。上传后的 URL 是独立公开资源，文章隐藏不会撤回图片或客户端缓存，不用于敏感附件。

```powershell
.\mvnw.cmd -B -ntp package -pl how_article,how_user,how_search,how_gateway_web -am
python doc/testing/security-smoke-test.py
python doc/testing/draft-smoke-test.py
python doc/testing/smoke-test.py
python doc/testing/markdown-smoke-test.py
```

接口脚本需要已有管理员。优先读取 `HOW_TEST_ADMIN_LOGIN`、`HOW_TEST_ADMIN_PASSWORD`，否则读取本机忽略的 `target/demo-data/accounts.json`。脚本创建唯一命名的临时数据并清理，不再依赖匿名创建管理员或从API读取密码哈希。演示数据脚本同样先登录管理员；不会覆盖既有演示记录。

搜索服务新增 JWT 配置，`doc/nacos-public` 和 `doc/nacos-dev` 必须与用户、文章、网关使用同一把密钥。

JWT 必须有过期时间，新签发有效期最多一小时。当前退出仍只清理浏览器会话；单个令牌的主动撤销、禁用账号后立即失效、刷新令牌和登录限流尚未实现。紧急全量失效需同步更换四个服务的签名密钥。MySQL/ES 补偿、评论计数与本人删除、前台账号和互动页面仍在后续范围，本轮不构成生产上线验收。
