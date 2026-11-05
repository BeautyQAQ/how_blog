package com.liushao.security;

import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class AccessPolicyTest {
    private JwtUtil jwt() { var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000); return jwt; }

    @Test void publicAllowlistIsMethodAwareAndUsesCompletePaths() {
        assertEquals(AccessPolicy.Access.PUBLIC, AccessPolicy.gateway("GET", "/article/article/public/123"));
        assertEquals(AccessPolicy.Access.PUBLIC, AccessPolicy.gateway("POST", "/user/admin/login"));
        for (String path : new String[]{"/article/article/public/123", "/article/channel", "/search/search/init",
                "/user/admin", "/article/comment/examine/123", "/article/init/data", "/user/admin/login/extra",
                "/user/admin/article", "/user/admin/login"}) {
            assertEquals(AccessPolicy.Access.ADMIN, AccessPolicy.gateway("DELETE", path), path);
        }
        assertEquals(AccessPolicy.Access.ADMIN, AccessPolicy.gateway("GET", "/article/article/public/123/extra"));
        assertEquals(AccessPolicy.Access.USER, AccessPolicy.gateway("POST", "/article/comment"));
    }

    @Test void rolesAreCheckedAndMalformedAndExpiredTokensAreUnauthorized() {
        var jwt = jwt();
        String user = "Bearer " + jwt.createJWT("user", "reader", "user");
        assertEquals(403, assertThrows(JwtAccess.Rejected.class, () -> JwtAccess.authorize(jwt, AccessPolicy.Access.ADMIN, user)).status());
        assertEquals("user", JwtAccess.authorize(jwt, AccessPolicy.Access.USER, user).getId());
        for (String token : new String[]{"Bearer invalid", "Bearer " + Jwts.builder().id("admin").claim("roles", "admin")
                .expiration(new Date(1)).signWith(Keys.hmacShaKeyFor(new byte[32])).compact(),
                "Bearer " + Jwts.builder().id("admin").claim("roles", "admin")
                        .signWith(Keys.hmacShaKeyFor(new byte[32])).compact()}) {
            assertEquals(401, assertThrows(JwtAccess.Rejected.class,
                    () -> JwtAccess.authorize(jwt, AccessPolicy.Access.ADMIN, token)).status());
        }
    }

    @Test void issuingTokensRequiresExpiryAndCapsItAtOneHour() {
        var jwt = jwt(); jwt.setTtl(0);
        assertThrows(IllegalStateException.class, () -> jwt.createJWT("id", "name", "admin"));
        jwt.setTtl(86_400_000);
        var claims = jwt.parseJWT(jwt.createJWT("id", "name", "admin"));
        assertTrue(claims.getExpiration().getTime() - claims.getIssuedAt().getTime() <= 3_600_000);
    }
}
