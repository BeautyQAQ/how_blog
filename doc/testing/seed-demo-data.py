"""Keep reusable development fixtures; no SMS. Run from backend root with four services running.

Existing fixtures are preserved, including manual edits. Credentials and IDs are stored under
ignored target/demo-data. This script never deletes data and must only be used in development.
"""
import json
import secrets
from datetime import datetime, timezone, timedelta
from pathlib import Path
from urllib.request import Request, urlopen
from test_support import admin_credentials

OUT = Path("target/demo-data")
OUT.mkdir(parents=True, exist_ok=True)
MANIFEST = OUT / "manifest.json"
ACCOUNTS = OUT / "accounts.json"
manifest = json.loads(MANIFEST.read_text(encoding="utf8")) if MANIFEST.exists() else {}
credentials = json.loads(ACCOUNTS.read_text(encoding="utf8")) if ACCOUNTS.exists() else {
    "admin": "demo_admin", "reader": "demo_reader", "password": "Demo-" + secrets.token_urlsafe(15)
}
ACCOUNTS.write_text(json.dumps(credentials, indent=2), encoding="utf8")
token = None
now = datetime.now(timezone(timedelta(hours=8))).strftime("%Y-%m-%d %H:%M:%S")


def api(port, path, method="GET", body=None):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    if token:
        headers["Authorization"] = token
    request = Request(f"http://127.0.0.1:{port}{path}", method=method, headers=headers,
                      data=json.dumps(body, ensure_ascii=False).encode() if body is not None else None)
    with urlopen(request, timeout=45) as response:
        result = json.load(response)
    if not result.get("flag"):
        raise RuntimeError(path + ": " + str(result.get("message")))
    return result.get("data")


def save_manifest():
    MANIFEST.write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding="utf8")


def ensure(kind, key, fields, field="name", port=9001, published=False):
    group = manifest.setdefault(kind, {})
    rows = api(port, "/" + kind)
    row = next((r for r in rows if (r.get("id") or r.get("_id")) == group.get(key)), None)
    if row is None:
        row = next((r for r in rows if r.get(field) == fields.get(field)), None)
    created = row is None
    if created:
        api(port, "/" + kind, "POST", fields)
        row = next(r for r in api(port, "/" + kind) if r.get(field) == fields.get(field))
    row_id = row.get("id") or row.get("_id")
    group[key] = row_id
    save_manifest()
    if created and published:
        api(port, "/" + kind + "/examine/" + row_id, "PUT")
    return row_id


token = api(9005, "/user/admin/login", "POST", admin_credentials())["token"]
admins = api(9003, "/admin/search", "POST", {"loginname": credentials["admin"]})
admin = next((r for r in admins if r["loginname"] == credentials["admin"]), None)
if admin is None:
    api(9003, "/admin", "POST", {"loginname": credentials["admin"], "password": credentials["password"], "state": "1"})
    admin = api(9003, "/admin/search", "POST", {"loginname": credentials["admin"]})[0]
token = api(9005, "/user/admin/login", "POST", {"loginname": credentials["admin"], "password": credentials["password"]})["token"]
manifest["admin"] = admin["id"]
reader = ensure("user", "reader", {"mobile": credentials["reader"], "nickname": "演示读者", "password": credentials["password"], "fanscount": 0, "followcount": 0}, "mobile", 9003)
reader_token = api(9005, "/user/user/login", "POST", {"mobile": credentials["reader"], "password": credentials["password"]})["token"]

channels = [ensure("channel", name, {"name": name, "state": "1"}) for name in ["后端开发", "前端实践", "项目随笔"]]
for name in ["Java", "Vue", "数据库", "工程实践"]:
    ensure("label", name, {"labelname": name, "state": "1", "recommend": "1", "count": 0, "fans": 0}, "labelname")
for name, hot in [("上海", "1"), ("杭州", "1"), ("深圳", "0")]:
    ensure("city", name, {"name": name, "ishot": hot})
columns = [ensure("column", "practice", {"name": "全栈实践笔记", "summary": "记录从接口到页面的实现过程。", "userid": reader, "state": "1", "createtime": now, "checktime": now}),
           ensure("column", "pending", {"name": "待审核专栏示例", "summary": "用于测试管理端专栏审核。", "userid": reader, "state": "0", "createtime": now})]

topics = [
    ("欢迎使用怎样博客", 2, "这是一套保留供手工测试的演示数据。\n\n你可以在管理端新增、编辑、审核文章，也可以切换公开、隐藏和置顶。\n前台只展示已审核且公开的文章。🙂"),
    ("一次文章发布会经过哪些步骤", 0, "文章保存后进入待审核状态。审核通过后，公开文章会加入搜索索引并出现在前台。\n\n隐藏或删除文章后，公开详情将返回404。"),
    ("Vue 3：让页面状态保持清晰", 1, "把表单、列表和请求状态分开管理，可以让页面更容易维护。\nComposition API 让相关逻辑写在一起，Pinia 负责会话状态。"),
    ("Nuxt 4 的页面与数据加载", 1, "首页和详情使用服务端渲染，首屏HTML包含文章内容。\n页面通过同源API访问网关，浏览器无需知道中间件地址。"),
    ("数据库查询中的精确匹配", 0, "分类ID应该精确匹配。频道1与频道11是两个不同的频道。\n关键词查询需要区分用户输入中的普通字符与SQL通配符。"),
    ("Redis缓存何时应该失效", 0, "文章编辑、审核和计数更新之后，应清理旧缓存。\n在事务提交之后处理缓存，可以减少回滚时带来的不一致。"),
    ("搜索索引与业务数据的一致性", 0, "数据库与搜索引擎承担不同职责。\n同步调用能处理常见错误；更完善的可靠性还需要消息重试、对账与补偿。"),
    ("设计一个可以重试的表单", 1, "保存失败时保留用户输入，并展示清楚的失败原因。\n请求执行期间禁用重复提交，成功后再关闭编辑窗口。"),
    ("空状态也是产品的一部分", 1, "没有文章、没有搜索结果和服务暂时不可用，是不同的页面状态。\n明确的提示和可用的重试入口能减少困惑。"),
    ("如何验证一个端到端功能", 2, "从界面操作开始，检查数据库写入，再检查公开接口和搜索结果。\n测试还应覆盖失败、权限和数据清理。"),
    ("一份小型项目的迭代清单", 2, "先完成核心内容链路，再完善账号权限、评论互动与内容编辑。\n每一轮保留可运行的页面和可复现的验收方法。"),
]
article_ids = []
for index, (title, channel, content) in enumerate(topics):
    article_ids.append(ensure("article", "published-" + str(index + 1), {"title": title, "content": content, "userid": reader, "channelid": channels[channel], "columnid": columns[0], "ispublic": "1", "istop": "1" if index == 0 else "0"}, "title", published=True))
for index in [1, 2]:
    ensure("article", "draft-" + str(index), {"title": "待审核文章示例 " + str(index), "content": "这篇文章用于测试审核流程。\n保存后的草稿不会出现在公开列表或详情中。", "userid": reader, "channelid": channels[0], "columnid": columns[0]}, "title")
ensure("article", "private", {"title": "已审核但未公开的文章", "content": "这篇文章用于测试公开开关。\n即使审核通过，只要未公开，前台仍然无法访问。", "userid": reader, "channelid": channels[2], "ispublic": "0"}, "title", published=True)

# Comments expose only article-scoped GET, unlike most resources.
for index, (content, approved) in enumerate([("欢迎来到怎样博客，第一篇留言！🙂", True), ("这是一条待审核评论，可以在评论管理中审核。", False), ("期待后续的Markdown编辑与图片上传。", True)]):
    rows = api(9001, "/comment/search/1/100", "POST", {"articleid": article_ids[0]})["rows"]
    existing_id = manifest.get("comment", {}).get(str(index))
    row = next((r for r in rows if r.get("id", r.get("_id")) == existing_id or r["content"] == content), None)
    if row is None:
        admin_token = token
        try:
            token = reader_token
            row = api(9001, "/comment", "POST", {"articleid": article_ids[0], "content": content})
        finally:
            token = admin_token
        if approved:
            api(9001, "/comment/examine/" + row["_id"], "PUT")
    manifest.setdefault("comment", {})[str(index)] = row.get("id", row.get("_id"))
    save_manifest()

spit = ensure("spit", "main", {"content": "今天把文章发布链路跑通了，记录一下这个小进展。🙂", "userid": reader, "nickname": "演示读者", "state": "1"}, "content")
ensure("spit", "reply", {"content": "回复示例：接下来继续完善评论和账号权限。", "userid": reader, "nickname": "演示读者", "parentid": spit, "state": "1"}, "content")

account_text = "怎样博客本地开发演示账号\n\n管理端：http://127.0.0.1:9528\n管理员：" + credentials["admin"] + "\n密码：" + credentials["password"] + "\n\n普通用户API登录标识（mobile）：" + credentials["reader"] + "\n密码与管理员演示账号相同。当前前台尚未提供普通用户登录页面。\n\n仅用于开发环境，未发送短信。演示数据保留，不自动清理。\n"
(OUT / "登录信息.txt").write_text(account_text, encoding="utf8")
save_manifest()
print(json.dumps({"retained": {key: len(value) if isinstance(value, dict) else 1 for key, value in manifest.items()}, "credentials_file": str(OUT / "登录信息.txt")}, ensure_ascii=False), flush=True)
