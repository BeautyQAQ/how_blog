"""Local-only admin credentials for test fixtures; no anonymous account provisioning."""
import json
import os
from pathlib import Path


def admin_credentials():
    login = os.environ.get("HOW_TEST_ADMIN_LOGIN")
    password = os.environ.get("HOW_TEST_ADMIN_PASSWORD")
    if login and password:
        return {"loginname": login, "password": password}
    accounts = Path(__file__).resolve().parents[2] / "target/demo-data/accounts.json"
    if accounts.exists():
        data = json.loads(accounts.read_text(encoding="utf8"))
        return {"loginname": data["admin"], "password": data["password"]}
    raise RuntimeError("先按 doc/security.md 初始化管理员，再设置 HOW_TEST_ADMIN_LOGIN/HOW_TEST_ADMIN_PASSWORD")
