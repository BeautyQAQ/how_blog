"""Permission and comment regression against the four local non-SMS services.

Uses an existing admin, creates isolated fixtures and removes only those fixtures.
Credentials come from test_support; no passwords or JWTs are written to reports.
"""
import json
import secrets
from pathlib import Path
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from test_support import admin_credentials

RUN = "security_" + secrets.token_hex(6)
checks = []
fixtures = {}
cleanup_errors = []
owner = None


def http(port, path, method="GET", body=None, token=None, expected=200):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    if token:
        headers["Authorization"] = token
    req = Request(f"http://127.0.0.1:{port}{path}", method=method, headers=headers,
                  data=json.dumps(body).encode() if body is not None else None)
    try:
        with urlopen(req, timeout=40) as response:
            status, result = response.status, json.load(response)
    except HTTPError as error:
        status, result = error.code, json.load(error)
    assert status == expected, f"{method} :{port}{path}: expected {expected}, got {status}"
    return result


def api(port, path, method="GET", body=None, token=None):
    result = http(port, path, method, body, token or owner)
    assert result.get("flag") is True, (path, result.get("message"))
    return result.get("data")


def check(name, condition=True):
    assert condition, name
    checks.append(name)
    print("PASS " + name, flush=True)


failure = None
try:
    owner = api(9005, "/user/admin/login", "POST", admin_credentials())["token"]
    password = secrets.token_urlsafe(20)
    api(9003, "/admin", "POST", {"loginname": RUN, "password": password, "state": "1"})
    admin = api(9003, "/admin/search", "POST", {"loginname": RUN})[0]
    fixtures["admin"] = admin["id"]
    check("Admin responses omit password", "password" not in admin)
    api(9003, "/user", "POST", {"mobile": RUN, "nickname": RUN, "password": password})
    reader = api(9003, "/user/search", "POST", {"mobile": RUN})[0]
    fixtures["user"] = reader["id"]
    check("User responses omit password", "password" not in reader)
    user_token = api(9005, "/user/user/login", "POST", {"mobile": RUN, "password": password})["token"]
    check("Plaintext user creation hashes password and supports login", bool(user_token))

    for port, path in [(9003, "/admin"), (9003, "/user"), (9001, "/init/data"),
                       (9001, "/comment/search/1/10"), (9002, "/search/article/test/1/10"),
                       (9005, "/user/admin"), (9005, "/search/search/article/test/1/10")]:
        method, body = ("POST", {}) if "comment/search" in path else ("GET", None)
        for token, expected in [(None, 401), ("Bearer invalid", 401), (user_token, 403)]:
            http(port, path, method, body, token, expected)
        check(f"Protected read rejects anonymous invalid and user :{port}{path}")
    for resource in ("article", "channel", "city", "column", "label", "comment", "problem", "reply", "spit"):
        for port, prefix in [(9001, ""), (9005, "/article")]:
            path = prefix + "/" + resource + "/" + RUN
            http(port, path, "DELETE", expected=401)
            http(port, path, "DELETE", token=user_token, expected=403)
        check("Protected direct and gateway writes " + resource)
    for port, path in [(9002, "/search/article/delete/" + RUN), (9005, "/search/search/article/delete/" + RUN)]:
        http(port, path, "DELETE", expected=401)
        http(port, path, "DELETE", token=user_token, expected=403)
        check("Protected search write :" + str(port))

    api(9003, "/admin/" + admin["id"], "PUT", {"loginname": RUN})
    check("Admin edits preserve omitted password", bool(api(9003, "/admin/login", "POST", {"loginname": RUN, "password": password})["token"]))
    new_password = secrets.token_urlsafe(20)
    api(9003, "/admin/" + admin["id"], "PUT", {"password": new_password})
    check("Old admin password stops working", not http(9003, "/admin/login", "POST", {"loginname": RUN, "password": password})["flag"])
    check("Changed admin password works", bool(api(9003, "/admin/login", "POST", {"loginname": RUN, "password": new_password})["token"]))
    api(9003, "/admin/" + admin["id"], "PUT", {"state": "0"})
    check("Disabled admin cannot log in", not http(9003, "/admin/login", "POST", {"loginname": RUN, "password": new_password})["flag"])

    article = api(9005, "/article/article", "POST", {"title": RUN, "content": RUN})
    fixtures["article"] = article["id"]
    http(9001, "/comment", "POST", {"articleid": article["id"], "content": "hidden"}, user_token, 404)
    check("Cannot comment on an unpublished article")
    api(9005, "/article/article/examine/" + article["id"], "PUT")
    check("Admin identity crosses Feign to protected search", api(9005, "/article/article/public/" + article["id"])["state"] == "1")
    comment = api(9005, "/article/comment", "POST", {"articleid": article["id"], "userid": "forged", "content": RUN,
                                                         "state": "1", "checktime": "2020-01-01 00:00:00"}, user_token)
    fixtures["comment"] = comment["_id"]
    check("Comment author and moderation fields are server controlled", comment["userid"] == reader["id"] and comment["state"] == "0" and not comment.get("checktime"))
    check("Pending comments stay private", not http(9005, "/article/comment/article/" + article["id"])["data"])
    page = api(9005, "/article/comment/search/1/10", "POST", {"articleid": article["id"]})
    check("Admin comment lookup crosses Feign to protected user service", page["rows"][0]["user"] == RUN)
    api(9005, "/article/comment/examine/" + comment["_id"], "PUT")
    check("Approved comment becomes public", len(http(9005, "/article/comment/article/" + article["id"])["data"]) == 1)
    api(9001, "/article/" + article["id"], "PUT", {"title": RUN, "content": RUN, "ispublic": "0"})
    http(9001, "/comment/article/" + article["id"], expected=404)
    check("Hidden article comments are inaccessible")
    api(9001, "/article/" + article["id"], "DELETE")
    check("Deleting article removes associated comments", api(9001, "/comment/search/1/10", "POST", {"articleid": article["id"]})["total"] == 0)
    fixtures.pop("article"); fixtures.pop("comment")
    check("Public reading remains available", http(9005, "/article/article/public")["flag"] and http(9005, "/article/channel")["flag"])
except Exception as error:
    failure = str(error)
    print("FAIL " + failure, flush=True)
finally:
    for kind, port, path in [("comment", 9001, "/comment/"), ("article", 9001, "/article/"),
                             ("user", 9003, "/user/"), ("admin", 9003, "/admin/")]:
        if kind in fixtures:
            try:
                api(port, path + fixtures[kind], "DELETE")
            except Exception as error:
                cleanup_errors.append(kind + ": " + str(error))
    report = {"run": RUN, "checks": checks, "failure": failure, "cleanup_errors": cleanup_errors,
              "passed": failure is None and not cleanup_errors}
    output = Path("target/security-runtime/security-smoke-results.json")
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf8")
    print(json.dumps({"checks": len(checks), "failure": failure, "cleanup_errors": cleanup_errors}), flush=True)
raise SystemExit(0 if report["passed"] else 1)
