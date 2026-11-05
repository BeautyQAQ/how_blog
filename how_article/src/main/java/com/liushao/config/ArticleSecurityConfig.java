package com.liushao.config;

import com.liushao.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtUtil.class)
public class ArticleSecurityConfig implements WebMvcConfigurer {
    private final JwtUtil jwt;
    public ArticleSecurityConfig(JwtUtil jwt) { this.jwt = jwt; }

    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminArticleInterceptor(jwt))
                .addPathPatterns("/article", "/article/**")
                .excludePathPatterns("/article/public", "/article/public/**");
    }

    public static class AdminArticleInterceptor implements HandlerInterceptor {
        private final JwtUtil jwt;
        public AdminArticleInterceptor(JwtUtil jwt) { this.jwt = jwt; }

        @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if ("OPTIONS".equals(request.getMethod())) return true;
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer "))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录管理员账号");
            try {
                var claims = jwt.parseJWT(header.substring(7));
                if (!"admin".equals(claims.get("roles")))
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
                request.setAttribute("adminId", claims.getId());
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
            }
        }
    }
}
