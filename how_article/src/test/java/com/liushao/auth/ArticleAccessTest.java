package com.liushao.auth;

import com.liushao.security.ApiAuthorizationInterceptor;
import com.liushao.security.AccessPolicy;
import com.liushao.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class ArticleAccessTest {
    private JwtUtil jwt() { var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000); return jwt; }
    @Test void missingOrInvalidTokenIsUnauthorized() {
        var interceptor = new ApiAuthorizationInterceptor(jwt(), AccessPolicy.Service.ARTICLE); var request = new MockHttpServletRequest();
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object())).getStatusCode().value());
        request.addHeader("Authorization", "Bearer invalid");
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object())).getStatusCode().value());
    }
    @Test void ordinaryUserCannotManageArticles() {
        var jwt = jwt(); var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer " + jwt.createJWT("user", "user", "user"));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> new ApiAuthorizationInterceptor(jwt, AccessPolicy.Service.ARTICLE).preHandle(request, new MockHttpServletResponse(), new Object())).getStatusCode().value());
    }
    @Test void administratorCanManageArticles() {
        var jwt = jwt(); var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer " + jwt.createJWT("admin", "admin", "admin"));
        assertTrue(new ApiAuthorizationInterceptor(jwt, AccessPolicy.Service.ARTICLE).preHandle(request, new MockHttpServletResponse(), new Object()));
        assertEquals("admin", request.getAttribute("adminId"));
    }
}
