"""Test non-SMS draft operations without requiring Elasticsearch. Run from backend root."""
import json
import secrets
from pathlib import Path
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from test_support import admin_credentials

run = "draft_" + secrets.token_hex(6)
checks = []
admin_id = article_id = token = None


def http(port, path, method="GET", body=None, auth=None, expected=200):
    headers = {"Content-Type": "application/json"}
    if auth:
        headers["Authorization"] = auth
    request = Request(f"http://127.0.0.1:{port}{path}", method=method, headers=headers,
                      data=json.dumps(body).encode() if body is not None else None)
    try:
        with urlopen(request, timeout=20) as response:
            status, payload = response.status, response.read()
    except HTTPError as error:
        status, payload = error.code, error.read()
    result = json.loads(payload)
    assert status == expected, (path, status, result.get("message"))
    return result


def api(port, path, method="GET", body=None, auth=None):
    result = http(port, path, method, body, auth)
    assert result["flag"] is True, (path, result.get("message"))
    return result.get("data")


def check(name, valid):
    assert valid, name
    checks.append(name)
    print("PASS " + name, flush=True)


try:
    owner_token = api(9005, "/user/admin/login", "POST", admin_credentials())["token"]
    password = secrets.token_urlsafe(20)
    api(9003, "/admin", "POST", {"loginname": run, "password": password, "state": "1"}, owner_token)
    admin_id = api(9003, "/admin/search", "POST", {"loginname": run}, owner_token)[0]["id"]
    token = api(9005, "/user/admin/login", "POST", {"loginname": run, "password": password})["token"]
    check("Administrator login", bool(token))
    article = api(9005, "/article/article", "POST", {"title": run, "content": "中文🙂\n正文", "state": "1", "thumbup": 99}, token)
    article_id = article["id"]
    check("Server draft defaults", article["state"] == "0" and article["thumbup"] == 0 and article["ispublic"] == "1")
    check("Anonymous raw read denied", http(9001, "/article/" + article_id, expected=401)["flag"] is False)
    check("Anonymous write denied", http(9005, "/article/article/examine/" + article_id, "PUT", expected=401)["flag"] is False)
    check("Draft public detail is 404", http(9005, "/article/article/public/" + article_id, expected=404)["flag"] is False)
    check("Draft excluded from public list", not api(9005, "/article/article/public?keyword=" + run)["rows"])
    check("Invalid pagination is 400", http(9005, "/article/article/public?page=0", expected=400)["flag"] is False)
    check("Blank content is 400", http(9005, "/article/article", "POST", {"title": " ", "content": " "}, token, 400)["flag"] is False)
    api(9005, "/article/article/thumbup/" + article_id, "PUT", auth=token)
    stored = api(9005, "/article/article/" + article_id, auth=token)
    check("Counter read and Redis invalidation", stored["thumbup"] == 1)
    api(9005, "/article/article/" + article_id, "PUT", {"title": run + " edited", "content": "已修改🙂", "thumbup": 90, "state": "1"}, token)
    stored = api(9005, "/article/article/" + article_id, auth=token)
    check("Draft editing independent of ES", stored["content"] == "已修改🙂" and stored["state"] == "0")
    check("Edits preserve counters and creation time", stored["thumbup"] == 1 and stored["createtime"] == article["createtime"])
    api(9005, "/article/article/" + article_id, "DELETE", auth=token)
    check("Draft deletion independent of ES", http(9005, "/article/article/" + article_id, auth=token, expected=404)["flag"] is False)
    article_id = None
finally:
    if article_id:
        api(9001, "/article/" + article_id, "DELETE", auth=token)
    if admin_id:
        api(9003, "/admin/" + admin_id, "DELETE", auth=token)
    output = Path("target/core-runtime/draft-smoke-results.json")
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps({"run": run, "checks": checks, "passed": len(checks) == 12}, indent=2), encoding="utf8")
