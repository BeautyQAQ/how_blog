package com.liushao.config;

import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.security.AccessPolicy;
import com.liushao.security.JwtAccess;
import com.liushao.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

final class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwt;
    private final ObjectMapper mapper;
    JwtAuthenticationFilter(JwtUtil jwt, ObjectMapper mapper) { this.jwt = jwt; this.mapper = mapper; }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            var claims = JwtAccess.authorize(jwt, AccessPolicy.required(AccessPolicy.Service.USER,
                    request.getMethod(), request.getRequestURI().substring(request.getContextPath().length())),
                    request.getHeader("Authorization"));
            if (claims != null) {
                request.setAttribute("jwt_claims", claims);
                request.setAttribute("admin".equals(claims.get("roles")) ? "admin_claims" : "user_claims", claims);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(claims.getId(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("roles")))));
                SecurityContextHolder.setContext(context);
            }
        } catch (JwtAccess.Rejected e) {
            response.setStatus(e.status());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(new Result(false, StatusCode.ACCESSERROR, e.getMessage())));
            return;
        }
        chain.doFilter(request, response);
    }
}
