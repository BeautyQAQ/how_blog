"""Markdown, uploads and summaries against local services; isolated fixtures only.

Run from the backend root. Uses existing local test credentials, does not call SMS.
Reports contain check names and fixture IDs, never credentials or tokens.
"""
import html
import json
import secrets
import struct
import time
import zlib
from pathlib import Path
from urllib.error import HTTPError
from urllib.parse import quote
from urllib.request import Request, urlopen
from test_support import admin_credentials

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "target/article-content-runtime"
OUT.mkdir(parents=True, exist_ok=True)
RUN = "markdown_" + secrets.token_hex(6)
checks, cleanup_errors, images = [], [], []
article_id = owner = None


def request(port, path, method="GET", body=None, token=None, expected=200, content_type=None):
    headers = {}
    if token:
        headers["Authorization"] = token
    if body is not None and not isinstance(body, bytes):
        body = json.dumps(body, ensure_ascii=False).encode("utf8")
        content_type = "application/json; charset=utf-8"
    if content_type:
        headers["Content-Type"] = content_type
    req = Request(f"http://127.0.0.1:{port}{path}", data=body, headers=headers, method=method)
    try:
        with urlopen(req, timeout=40) as response:
            status, result, response_headers = response.status, response.read(), response.headers
    except HTTPError as error:
        status, result, response_headers = error.code, error.read(), error.headers
    assert status == expected, f"{method} :{port}{path}: expected {expected}, got {status}"
    return result, response_headers


def api(path, method="GET", body=None, token=None, port=9005, expected=200):
    result = json.loads(request(port, path, method, body, token, expected)[0])
    if expected == 200:
        assert result["flag"] is True, (path, result.get("message"))
    return result.get("data")


def check(name, condition=True):
    assert condition, name
    checks.append(name)
    print("PASS " + name, flush=True)


def png(width=32, height=20):
    def chunk(kind, value):
        return struct.pack(">I", len(value)) + kind + value + struct.pack(">I", zlib.crc32(kind + value))
    pixels = b"".join(b"\x00" + bytes([35, 100, 180]) * width for _ in range(height))
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)) + chunk(b"IDAT", zlib.compress(pixels)) + chunk(b"IEND", b"")


def upload(data, token=None, expected=200, filename="photo.png", port=9005, include_file=True):
    boundary = "----howBlog" + secrets.token_hex(12)
    body = (f'--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="{filename}"\r\nContent-Type: application/octet-stream\r\n\r\n'.encode() + data + b"\r\n") if include_file else b""
    body += f"--{boundary}--\r\n".encode()
    path = "/media" if port == 9001 else "/article/media"
    raw = request(port, path, "POST", body, token, expected, "multipart/form-data; boundary=" + boundary)[0]
    result = json.loads(raw)
    if expected == 200:
        assert result["flag"]
        images.append(result["data"]["filename"])
        return result["data"]
    assert result["flag"] is False


def indexed(wanted):
    for _ in range(16):
        page = api("/search/search/article/" + quote(RUN) + "/1/10", token=owner)
        if any(row["id"] == article_id for row in page["rows"]) == wanted:
            return True
        time.sleep(0.4)
    return False


failure = None
try:
    owner = api("/user/admin/login", "POST", admin_credentials())["token"]
    accounts = json.loads((ROOT / "target/demo-data/accounts.json").read_text(encoding="utf8"))
    reader = api("/user/user/login", "POST", {"mobile": accounts["reader"], "password": accounts["password"]})["token"]
    for port in (9001, 9005):
        upload(png(), port=port, expected=401)
        upload(png(), token=reader, port=port, expected=403)
    check("Direct and gateway uploads require administrator")
    api("/article/article/preview", "POST", {"content": "## hidden", "contentformat": "markdown"}, expected=401)
    api("/article/article/preview", "POST", {"content": "## hidden"}, token=reader, expected=403)
    check("Preview requires administrator")
    upload(b'<svg onload="alert(1)"></svg>', token=owner, expected=400, filename="fake.png")
    upload(png()[:30], token=owner, expected=400)
    check("SVG and damaged images are rejected")
    upload(b"x" * (5 * 1024 * 1024 + 1), token=owner, expected=413)
    check("Actual multipart upload over 5MB returns 413")
    upload(b"", token=owner, expected=400)
    upload(b"", token=owner, expected=400, include_file=False)
    check("Empty and missing file return 400")
    original = png() + b'<script>alert("appended")</script>'
    image = upload(original, token=owner, filename="misleading.svg")
    check("Actual image decode determines safe filename and dimensions", image["filename"].endswith(".png") and image["width"] == 32 and image["height"] == 20)
    for port, prefix in ((9001, "/media/"), (9005, "/article/media/"), (3000, "/api/article/media/"), (9528, "/api/article/media/")):
        binary, headers = request(port, prefix + image["filename"])
        check(f"Public image via :{port} has safe MIME and pixels", binary.startswith(b"\x89PNG") and b"<script>" not in binary and headers.get_content_type() == "image/png" and headers.get("X-Content-Type-Options") == "nosniff")
    _, headers = request(9005, "/article/media/" + image["filename"], "HEAD")
    check("HEAD supports immutable public image caching", "immutable" in headers.get("Cache-Control", ""))
    request(9005, "/article/media/" + ("0" * 32) + ".png", expected=404)
    check("Missing well-formed image returns 404")
    manual = "手工摘要：Markdown、图片和发布链路。🙂"
    body = (f"## Markdown验收 {RUN}\n\n**中文🙂加粗** 和 ~~删除线~~\n\n"
            "- 列表一\n- 列表二\n\n> 引用\n\n"
            "| 名称 | 状态 |\n| --- | --- |\n| 图片 | 就绪 |\n\n"
            "```javascript\nconst value = '<script>alert(1)</script>';\n```\n\n"
            f"![测试图片]({image['url']})\n\n[安全链接](https://example.com)\n\n"
            '<script>window.markdownPwned=1</script>\n\n<img src=x onerror="window.markdownPwned=1">\n\n'
            '[危险链接](javascript:alert(1))\n\n![危险图片](data:image/svg+xml;base64,PHN2Zz4=)\n\n'
            + "数据库长正文验证汉🙂" * 4500)
    assert len(body.encode("utf8")) > 65535
    draft = api("/article/article", "POST", {"title": RUN, "content": body, "contentformat": "markdown", "summary": manual, "image": image["url"]}, owner)
    article_id = draft["id"]
    stored = api("/article/article/" + article_id, token=owner)
    check("MySQL LONGTEXT stores long Unicode Markdown and cover", stored["content"] == body and stored["contentformat"] == "markdown" and stored["image"] == image["url"])
    check("Automatic excerpt is server controlled and Unicode safe", len(stored["excerpt"]) == 181 and stored["excerpt"].endswith("…") and "\ufffd" not in stored["excerpt"])
    api("/article/article/public/" + article_id, expected=404)
    check("Draft detail remains private")
    preview = api("/article/article/preview", "POST", {"content": body, "contentformat": "markdown"}, owner)
    rendered = preview["html"]
    check("Markdown renders headings emphasis lists tables code and images", all(value in rendered for value in ("<h2>", "<strong>", "<del>", "<ul>", "<blockquote>", "<table>", "<pre><code", image["url"])))
    check("Rendered HTML blocks active scripts and unsafe URLs", "<script>" not in rendered and "<img src=\"x\"" not in rendered and "javascript:alert" not in rendered and "data:image" not in rendered)
    check("Preview uses normalized automatic summary", preview["summary"] == stored["excerpt"])
    api("/article/media/" + image["filename"], "DELETE", token=owner, expected=409)
    check("Referenced draft image cannot be deleted")
    for field, value in (("summary", "摘" * 301), ("contentformat", "html"), ("image", "javascript:alert(1)"), ("content", "x" * 200001)):
        api("/article/article", "POST", {"title": RUN, "content": "正文", field: value}, owner, expected=400)
    check("Summary length format cover protocol and body limit validated")
    api("/article/article/examine/" + article_id, "PUT", token=owner)
    check("Publication synchronizes Markdown to Elasticsearch", indexed(True))
    detail = api("/article/article/public/" + article_id)
    check("Published detail uses same renderer and manual summary", detail["contentHtml"] == rendered and detail["summary"] == manual and detail["contentformat"] == "markdown")
    page = api("/article/article/public?keyword=" + RUN)
    card = next(row for row in page["rows"] if row["id"] == article_id)
    check("Public summary cards omit full body and rendered HTML", card["summary"] == manual and card["image"] == image["url"] and "content" not in card and "contentHtml" not in card and "excerpt" not in card)
    ssr = request(3000, "/article/" + article_id)[0].decode("utf8")
    check("Nuxt SSR includes semantic Markdown HTML and cover", "<h2>Markdown验收" in ssr and "<table>" in ssr and image["url"] in ssr)
    check("Nuxt description metadata uses article summary", 'name="description" content="' + html.escape(manual, quote=True) + '"' in ssr)
    homepage = request(3000, "/?keyword=" + RUN)[0].decode("utf8")
    check("Homepage SSR contains summary without full Markdown body", manual in homepage and "数据库长正文验证" not in homepage)
    created = stored["createtime"]
    api("/article/article/" + article_id, "PUT", {"title": RUN, "content": "## 更新标题\n\n新正文🙂", "image": image["url"]}, owner)
    detail = api("/article/article/public/" + article_id)
    check("Editing preserves omitted summary and format", detail["summary"] == manual and detail["contentformat"] == "markdown" and detail["createtime"] == created)
    api("/article/article/" + article_id, "PUT", {"title": RUN, "content": "## 更新标题\n\n新正文🙂", "summary": "", "image": image["url"]}, owner)
    check("Clearing manual summary switches to automatic", api("/article/article/public/" + article_id)["summary"] == "更新标题 新正文🙂")
    legacy = api("/article/article/preview", "POST", {"content": "## 字面标题\n<script>alert(1)</script>", "contentformat": "text"}, owner)
    check("Legacy plain text keeps Markdown literal and HTML escaped", "## 字面标题<br>" in legacy["html"] and "&lt;script&gt;" in legacy["html"] and "<h2>" not in legacy["html"])
    api("/article/article/" + article_id, "PUT", {"title": RUN, "content": "隐藏正文", "ispublic": "0", "image": image["url"]}, owner)
    api("/article/article/public/" + article_id, expected=404)
    request(3000, "/article/" + article_id, expected=404)
    check("Hidden Markdown article leaves list detail SSR and ES", not api("/article/article/public?keyword=" + RUN)["rows"] and indexed(False))
    # Upload URLs are standalone public assets even when an article is hidden.
    request(9005, "/article/media/" + image["filename"])
    check("Uploaded media remains a public asset independent of article visibility")
    api("/article/article/" + article_id, "DELETE", token=owner)
    article_id = None
    api("/article/media/" + image["filename"], "DELETE", expected=401)
    api("/article/media/" + image["filename"], "DELETE", token=reader, expected=403)
    api("/article/media/" + image["filename"], "DELETE", token=owner)
    images.remove(image["filename"])
    request(9005, "/article/media/" + image["filename"], expected=404)
    check("Administrator can delete unreferenced images")
except Exception as error:
    failure = f"{type(error).__name__}: {error}"
    raise
finally:
    if article_id and owner:
        try:
            api("/article/article/" + article_id, "DELETE", token=owner)
        except Exception as error:
            cleanup_errors.append("article: " + str(error))
    for filename in images:
        try:
            api("/article/media/" + filename, "DELETE", token=owner)
        except Exception as error:
            cleanup_errors.append("image: " + str(error))
    (OUT / "markdown-smoke-results.json").write_text(json.dumps({"run": RUN, "checks": checks, "passed": failure is None and not cleanup_errors, "failure": failure, "cleanup_errors": cleanup_errors}, ensure_ascii=False, indent=2), encoding="utf8")
