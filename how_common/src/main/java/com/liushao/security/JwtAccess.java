package com.liushao.security;

import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public final class JwtAccess {
    private JwtAccess() {}

    public static Claims authorize(JwtUtil jwt, AccessPolicy.Access access, String header) {
        if (access == AccessPolicy.Access.PUBLIC) return null;
        if (header == null || !header.startsWith("Bearer ") || header.length() <= 7)
            throw new Rejected(401, "请先登录");
        Claims claims;
        try {
            claims = jwt.parseJWT(header.substring(7));
            if (claims.getId() == null || claims.getId().isBlank() || claims.getExpiration() == null)
                throw new IllegalArgumentException("Missing identity or expiry");
        } catch (JwtException | IllegalArgumentException e) {
            throw new Rejected(401, "登录已失效，请重新登录");
        }
        Object role = claims.get("roles");
        boolean allowed = access == AccessPolicy.Access.ADMIN ? "admin".equals(role) : "user".equals(role);
        if (!allowed) throw new Rejected(403, access == AccessPolicy.Access.ADMIN ? "需要管理员权限" : "需要普通用户账号");
        return claims;
    }

    public static final class Rejected extends RuntimeException {
        private final int status;
        public Rejected(int status, String message) { super(message); this.status = status; }
        public int status() { return status; }
    }
}
