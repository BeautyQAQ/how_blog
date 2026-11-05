"""Live test-environment smoke test. Requires the four non-SMS services.

Creates uniquely named fixtures, exercises MySQL/Redis/Feign/ES and Gateway,
and deletes only this run's fixtures. Does not call SMS or registration APIs.
Run from the backend root: python doc/testing/smoke-test.py
"""
import json
import secrets
import sys
import time
import traceback
from pathlib import Path
from urllib.error import HTTPError
from urllib.parse import quote
from urllib.request import Request, urlopen
from test_support import admin_credentials

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "target/test-env-runtime"
OUT.mkdir(parents=True, exist_ok=True)
RUN = "smoke_" + secrets.token_hex(6)
checks = []
fixtures = {}
cleanup_errors = []


def check(name, condition):
    checks.append({"name": name, "passed": bool(condition)})
    if not condition:
        raise AssertionError(name)
    print("PASS " + name, flush=True)


def http(port, path, method="GET", body=None, token=None, expected=200):
    headers = {"Accept": "application/json"}
    if token:
        headers["Authorization"] = token
    data = None
    if body is not None:
        headers["Content-Type"] = "application/json; charset=utf-8"
        data = json.dumps(body, ensure_ascii=False).encode("utf-8")
    req = Request(f"http://127.0.0.1:{port}{path}", data=data, headers=headers, method=method)
    try:
        with urlopen(req, timeout=40) as response:
            status, raw = response.status, response.read()
    except HTTPError as error:
        status, raw = error.code, error.read()
    result = json.loads(raw) if raw else None
    if status != expected:
        raise AssertionError(f"{method} :{port}{path}: HTTP {status}: {result}")
    return result


def api(port, path, method="GET", body=None, token=None):
    if token is None and globals().get("admin_token"):
        token = admin_token
    result = http(port, path, method, body, token)
    if result.get("flag") is not True or result.get("code") != 20000:
        raise AssertionError(f"{method} :{port}{path}: {result}")
    return result.get("data")


def indexed(keyword, article_id):
    for _ in range(12):
        page = api(9005, f"/search/search/article/{quote(keyword)}/1/10")
        if any(row["id"] == article_id for row in page["rows"]):
            return True
        time.sleep(0.5)
    return False


def cleanup():
    # Discover IDs even when a create request succeeded before a later assertion failed.
    for kind, port, path, field in [
        ("comment", 9001, "/comment/search/1/100", "content"),
        ("article", 9001, "/article/search", "title"),
        ("user", 9003, "/user/search", "nickname"),
        ("admin", 9003, "/admin/search", "loginname"),
    ]:
        try:
            value = RUN if kind != "comment" else RUN + " 评论🙂"
            found = api(port, path, "POST", {field: value})
            rows = found["rows"] if isinstance(found, dict) else found
            for row in rows:
                if row.get(field, "").startswith(RUN):
                    row_id = row.get("id") or row.get("_id")
                    fixtures.setdefault(kind, row_id)
        except Exception as error:
            cleanup_errors.append(f"discover {kind}: {error}")
    try:
        for row in api(9001, "/spit"):
            if row.get("content", "").startswith(RUN):
                api(9001, "/spit/" + row["_id"], "DELETE")
    except Exception as error:
        cleanup_errors.append(f"spit: {error}")
    for kind, port, path in [
        ("comment", 9001, "/comment/"),
        ("article", 9001, "/article/"),
        ("user", 9003, "/user/"),
        ("admin", 9003, "/admin/"),
    ]:
        if kind in fixtures:
            try:
                api(port, path + fixtures[kind], "DELETE", token=admin_token)
            except Exception as error:
                cleanup_errors.append(f"delete {kind} {fixtures[kind]}: {error}")


admin_token = None
failure = None
try:
    for port in (9001, 9002, 9003):
        spec = http(port, "/v3/api-docs")
        check(f"OpenAPI :{port}", spec["openapi"].startswith("3.") and bool(spec["paths"]))
    for prefix in ("article", "user", "search"):
        spec = http(9005, f"/{prefix}/v3/api-docs")
        check(f"Gateway discovery /{prefix}/v3/api-docs", bool(spec["paths"]))
    denied = http(9005, "/user/admin", expected=401)
    check("Gateway rejects missing JWT", denied["flag"] is False)
    admin_token = api(9005, "/user/admin/login", "POST", admin_credentials())["token"]
    for resource in ("channel", "city", "column", "label", "problem", "reply", "spit"):
        check(f"MySQL list {resource}", isinstance(api(9005, f"/article/{resource}"), list))

    password = secrets.token_urlsafe(20)
    api(9003, "/admin", "POST", {"loginname": RUN, "password": password, "state": "1"})
    admin = api(9003, "/admin/search", "POST", {"loginname": RUN})[0]
    fixtures["admin"] = admin["id"]
    check("Admin API omits password", "password" not in admin)
    login = api(9005, "/user/admin/login", "POST", {"loginname": RUN, "password": password})
    admin_token = login["token"]
    check("Admin JWT login", admin_token.startswith("Bearer "))
    check("MySQL list article", isinstance(api(9005, "/article/article"), list))
    info = api(9005, "/user/admin/info", token=admin_token)
    check("Gateway forwards authenticated admin", info["roles"] == "admin")
    bad = http(9005, "/user/admin/login", "POST", {"loginname": RUN, "password": "wrong"})
    check("Admin wrong password rejected", bad["flag"] is False)

    api(9003, "/user", "POST", {"mobile": RUN, "password": password, "nickname": RUN,
                                   "fanscount": 0, "followcount": 0})
    user = api(9003, "/user/search", "POST", {"nickname": RUN})[0]
    fixtures["user"] = user["id"]
    login = api(9005, "/user/user/login", "POST", {"mobile": RUN, "password": password})
    user_token = login["token"]
    check("User JWT login without SMS", login["name"] == RUN)
    users = api(9005, "/user/user/search/1/10", "POST", {"nickname": RUN}, token=admin_token)
    check("Authenticated user pagination", users["total"] == 1)

    article_body = {"title": RUN + " 数据库搜索联调", "content": RUN + " 中文正文🙂",
                    "userid": user["id"], "thumbup": 0, "visits": 0, "comment": 0,
                    "state": "0", "ispublic": "1"}
    api(9005, "/article/article", "POST", article_body)
    article = api(9001, "/article/search", "POST", {"title": RUN})[0]
    article_id = fixtures["article"] = article["id"]
    check("Article defaults initialized", article["state"] == "0" and article["istop"] == "0" and bool(article["createtime"]) and bool(article["updatetime"]))
    check("Draft hidden from public detail", http(9005, "/article/article/public/" + article_id, expected=404)["flag"] is False)
    check("Draft hidden from public list", not http(9005, "/article/article/public?keyword=" + quote(RUN))["data"]["rows"])
    check("Raw article read requires admin", http(9001, "/article/" + article_id, expected=401)["flag"] is False)
    check("Anonymous review denied", http(9005, "/article/article/examine/" + article_id, "PUT", expected=401)["flag"] is False)
    check("Invalid pagination rejected", http(9005, "/article/article/public?page=0", expected=400)["flag"] is False)
    for _ in range(2):
        stored = api(9005, "/article/article/" + article_id)
        check("Article read / Redis cached read", stored["content"] == article_body["content"])
    api(9005, "/article/article/thumbup/" + article_id, "PUT")
    page = api(9001, "/article/search/1/10", "POST", {"id": article_id})
    check("Article MySQL counter", page["rows"][0]["thumbup"] == 1)
    api(9005, "/article/article/examine/" + article_id, "PUT")
    check("Article review -> Feign -> ES search", indexed(RUN, article_id))
    check("Published public detail", http(9005, "/article/article/public/" + article_id)["data"]["state"] == "1")
    check("Published public list", http(9005, "/article/article/public?keyword=" + quote(RUN))["data"]["total"] == 1)
    check("Review invalidates cached state", api(9001, "/article/" + article_id)["state"] == "1")
    article_body["title"] = RUN + " updated"
    article_body["content"] = RUN + " updated正文🙂"
    api(9005, "/article/article/" + article_id, "PUT", article_body)
    check("Article update clears Redis", api(9001, "/article/" + article_id)["content"] == article_body["content"])
    check("Article update reaches ES", indexed("updated", article_id))
    check("Article edit preserves counter", api(9001, "/article/" + article_id)["thumbup"] == 1)
    article_body["ispublic"] = "0"
    api(9005, "/article/article/" + article_id, "PUT", article_body)
    check("Hidden article detail is 404", http(9005, "/article/article/public/" + article_id, expected=404)["flag"] is False)
    check("Hidden article removed from public list", not http(9005, "/article/article/public?keyword=" + quote(RUN))["data"]["rows"])
    for _ in range(12):
        if not api(9005, f"/search/search/article/{quote(RUN)}/1/10")["rows"]:
            break
        time.sleep(0.5)
    check("Hidden article removed from ES", not api(9005, f"/search/search/article/{quote(RUN)}/1/10")["rows"])
    article_body["ispublic"] = "1"
    api(9005, "/article/article/" + article_id, "PUT", article_body)
    check("Republished article restored to ES", indexed(RUN, article_id))

    comment = api(9005, "/article/comment", "POST", {"articleid": article_id, "userid": "forged", "content": RUN + " 评论🙂"}, token=user_token)
    check("Comment identity comes from JWT", comment["userid"] == user["id"])
    check("Pending comment hidden from public", not http(9005, "/article/comment/article/" + article_id)["data"])
    fixtures["comment"] = comment["_id"]
    check("Comment legacy _id and Chinese emoji", "id" not in comment and comment["content"].endswith("🙂"))
    api(9005, "/article/comment/examine/" + comment["_id"], "PUT")
    page = api(9005, "/article/comment/search/1/10", "POST", {"articleid": article_id})
    check("Comment pagination and User Feign", page["total"] == 1 and page["rows"][0]["user"] == RUN)
    check("Comment review persisted", page["rows"][0]["state"] == "1" and bool(page["rows"][0]["checktime"]))

    api(9005, "/article/spit", "POST", {"content": RUN + " parent🙂", "userid": user["id"], "nickname": RUN})
    parent = next(row for row in api(9001, "/spit") if row["content"] == RUN + " parent🙂")
    api(9005, "/article/spit", "POST", {"content": RUN + " reply🙂", "parentid": parent["_id"], "userid": user["id"]})
    replies = api(9005, f"/article/spit/comment/{parent['_id']}/1/10")
    check("Spit reply pagination", replies["total"] == 1)
    parent = api(9001, "/spit/" + parent["_id"])
    check("Spit reply counter", parent["comment"] == 1 and parent["visits"] == 0)
except Exception as error:
    failure = str(error)
    traceback.print_exc()
finally:
    cleanup()
    report = {"run": RUN, "passed": failure is None and not cleanup_errors, "checks": checks,
              "failure": failure, "fixtures": fixtures, "cleanup_errors": cleanup_errors,
              "sms": "excluded"}
    (OUT / "smoke-results.json").write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps({"run": RUN, "checks": len(checks), "failure": failure,
                      "cleanup_errors": cleanup_errors}, ensure_ascii=True), flush=True)
sys.exit(0 if report["passed"] else 1)
