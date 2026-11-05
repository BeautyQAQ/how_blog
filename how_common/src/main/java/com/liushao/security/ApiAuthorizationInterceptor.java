package com.liushao.security;

import com.liushao.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

public class ApiAuthorizationInterceptor implements HandlerInterceptor {
    private final JwtUtil jwt;
    private final AccessPolicy.Service service;
    public ApiAuthorizationInterceptor(JwtUtil jwt, AccessPolicy.Service service) {
        this.jwt = jwt; this.service = service;
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        try {
            var claims = JwtAccess.authorize(jwt, AccessPolicy.required(service, request.getMethod(), path),
                    request.getHeader("Authorization"));
            if (claims != null) {
                request.setAttribute("jwt_claims", claims);
                request.setAttribute("admin".equals(claims.get("roles")) ? "admin_claims" : "user_claims", claims);
                if ("admin".equals(claims.get("roles"))) request.setAttribute("adminId", claims.getId());
            }
            return true;
        } catch (JwtAccess.Rejected e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(e.status()), e.getMessage());
        }
    }
}
