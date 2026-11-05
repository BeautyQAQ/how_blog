package com.liushao.service;

import java.nio.charset.StandardCharsets;

final class AccountValidation {
    private AccountValidation() {}
    static void password(String password) {
        if (password == null || password.isBlank() || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException("密码至少8个字符且不能超过72字节");
    }
    static String name(String value, int max, String label) {
        if (value == null || value.isBlank() || value.trim().length() > max)
            throw new IllegalArgumentException(label + "不能为空且不能超过" + max + "字");
        return value.trim();
    }
}
