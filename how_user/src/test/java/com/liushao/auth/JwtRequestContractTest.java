package com.liushao.auth;

import com.liushao.controller.AdminController;
import com.liushao.interceptor.JwtInterceptor;
import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtRequestContractTest {
    private JwtUtil jwt() {
        JwtUtil jwt = new JwtUtil();
        jwt.setKey(Base64.getEncoder().encodeToString(new byte[32]));
        jwt.setTtl(60000);
        return jwt;
    }

    @Test
    void bearerAdminHeaderWorksForInfoAndProtectedControllerAttributes() throws Exception {
        JwtUtil jwt = jwt();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwt.createJWT("admin-1", "test-admin", "admin"));
        JwtInterceptor interceptor = new JwtInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtUtil", jwt);
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        Claims claims = (Claims) request.getAttribute("admin_claims");
        assertEquals("admin-1", claims.getId());
        assertNull(request.getAttribute("user_claims"));

        AdminController controller = new AdminController();
        ReflectionTestUtils.setField(controller, "jwtUtil", jwt);
        Map<?, ?> info = (Map<?, ?>) controller.adminInfo(request).getData();
        assertEquals("admin", info.get("roles"));
    }

    @Test
    void userClaimsAreAvailableToControllersWithoutAdminPrivileges() throws Exception {
        JwtUtil jwt = jwt();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwt.createJWT("user-1", "test-user", "user"));
        JwtInterceptor interceptor = new JwtInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtUtil", jwt);
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertEquals("user-1", ((Claims) request.getAttribute("user_claims")).getId());
        assertNull(request.getAttribute("admin_claims"));
    }

    @Test
    void invalidBearerTokenCannotPopulateClaims() {
        JwtInterceptor interceptor = new JwtInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtUtil", jwt());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid");
        assertThrows(RuntimeException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertNull(request.getAttribute("admin_claims"));
        assertNull(request.getAttribute("user_claims"));
    }
}
