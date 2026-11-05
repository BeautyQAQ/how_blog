# Markdown、图片与文章摘要

## 编辑与阅读

管理端「文章管理 → 新增文章」默认使用 Markdown。工具栏支持标题、加粗、列表、代码块、链接和图片；可选择编辑、对照或预览。支持 CommonMark、GFM 表格和删除线；代码块保留格式，目前没有语法着色。预览防抖请求后端，与前台详情使用同一份渲染规则。

历史文章仍是纯文本，保留字面 Markdown 符号和换行；编辑时可手动切换格式。原始 HTML 始终按文字显示；危险链接/图片协议、脚本与事件属性不会成为可执行 HTML。

「上传封面」会填写封面链接，也可手填有效的 http/https 地址（沿用现有字段，最多100字符）。「插入图片」会把上传结果作为 Markdown 图片插入当前选区，图片说明可直接修改。仅支持 PNG/JPEG，每张输入及重编码后最多5MB，宽高各不超过8192像素，总像素不超过1600万。上传失败保留文章输入；上传过程中不能保存或关闭弹窗。移除封面只清空字段。

摘要最多300个 Unicode 字符，手填时优先使用；空字符串表示改用正文自动摘要，更新时省略字段表示保留。自动摘要去掉 Markdown 格式、合并空白，最多180个 Unicode 字符，超长追加省略号。首页、详情摘要和 description/og:description 使用同一摘要。自动摘要目前会包含代码块文字。

新增文章仍是待审核，公开且审核通过后才可阅读。编辑已发布文章继续同步 ES，隐藏/删除继续撤下详情、列表及索引。上传文件的可见性单独管理，见下文。

## 数据库迁移

新库初始化使用 `doc/sql/how-blog.sql`。已有库先备份 `tb_article`，确认 `contentformat`、`summary`、`excerpt` 尚不存在，再执行一次 `doc/sql/migrations/20261105-article-markdown.sql`。MySQL ALTER 会隐式提交，不能通过事务回滚。

迁移把 `content` 扩大为 LONGTEXT，增加格式、手工摘要和生成摘要；历史文章标为 `text` 并回填摘要，不修改原正文、状态、日期和计数。应用限制正文20万 UTF-16 字符。直接调用新增 API 时省略格式仍按 `text`，兼容旧客户端；管理端显式提交 `markdown`。

## API

以下为文章服务（9001）路径，经网关（9005）再加 `/article`；浏览器通过同源 `/api/article/...` 访问。管理接口沿用管理员 Authorization，缺失/无效401、普通用户403。

| 方法与路径 | 行为 |
| --- | --- |
| POST `/article`、PUT `/article/{id}` | 新增/修改，新增字段 `contentformat`（text/markdown）、`summary`；`excerpt` 服务端生成，不能由输入覆盖 |
| POST `/article/preview` | 管理预览，请求 `{content, contentformat}`，返回 Result.data `{html, summary}` |
| GET `/article/public` | 公开分页摘要 DTO，字段 id/title/summary/image/channelid/columnid/istop/日期；JPA 投影不选取完整正文，不返回 content/contentHtml/excerpt |
| GET `/article/public/{id}` | 公开详情，含 contentformat、服务端清洗后的 contentHtml、最终 summary 及封面；保留原始 content 以兼容已有调用 |
| POST `/media` | 管理上传，multipart 字段 `file`；Result.data 为 `{url, filename, width, height, size}`，size 为重编码后字节数 |
| GET/HEAD `/media/{filename}` | 公开读取 PNG/JPEG；404 表示不存在，正确 MIME、nosniff、public/max-age=31536000/immutable |
| DELETE `/media/{filename}` | 管理删除；封面或正文中仍引用该 URL（含草稿）返回409；不存在404 |

上传返回例：`/api/article/media/0123456789abcdef0123456789abcdef.png`。格式和扩展名由实际解码内容决定，原始文件名不用于路径。空文件/缺字段/无效格式400，超限413，存储 IO 故障503。列表由返回完整正文改为摘要 DTO，旧调用方若需正文应改用公开详情接口。

## 图片存储与部署

默认保存在文章服务工作目录下的 `data/uploads/`，已被 Git 忽略。建议部署时显式指定绝对路径：

```powershell
$env:BLOG_UPLOADS_DIRECTORY='D:/how-blog-data/uploads'
java -jar how_article/target/how_article.jar
```

也可设置 Spring 属性 `blog.uploads.directory`。目录需可写、持久化并备份；容器部署应挂载数据卷，不能只保存在容器临时层。多个文章实例需要共享同一目录，目前没有实现对象存储。

网关路由和两端同源 `/api/` 代理需保留图片路径。反向代理请求体上限至少6MiB（含 multipart 开销），服务端文件限制仍为5MiB。网关与文章服务均需更新至本轮版本以放行图片读取。

图片 URL 是独立公开资源；隐藏文章、删除文章、取消编辑或替换图片都不会撤回原图片和已发出的浏览器缓存。不用于私密附件。孤立文件暂时保留，管理员可通过 DELETE 接口删除未被引用的文件；自动清理、媒体管理界面和对象存储后续实现。

渲染依赖为 [commonmark-java 0.32.0](https://github.com/commonmark/commonmark-java/blob/main/README.md)（含表格、删除线扩展）与 [jsoup 1.24.1](https://jsoup.org/download)。渲染器按官方建议在 Markdown 输出后继续执行 HTML 白名单清洗；预览和 SSR 均不直接渲染原始正文。
