package com.liushao.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 *
 * @author Administrator
 * @date 2018/4/11
 */
@ConfigurationProperties("jwt.config")
public class JwtUtil {

    private String key ;

    private long ttl ;//一个小时

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * 生成JWT
     *
     * @param id
     * @param subject
     * @return
     */
    public String createJWT(String id, String subject, String roles) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().id(id)
                .subject(subject)
                .issuedAt(now)
                .signWith(signingKey(), Jwts.SIG.HS256).claim("roles", roles);
        if (ttl > 0) {
            builder.expiration(new Date(nowMillis + ttl));
        }
        return builder.compact();
    }

    /**
     * 解析JWT
     * @param jwtStr
     * @return
     */
    public Claims parseJWT(String jwtStr){
        return  Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(jwtStr)
                .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

}
