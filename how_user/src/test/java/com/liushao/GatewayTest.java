package com.liushao;

import com.liushao.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void urlTest(){
        String url = "/admin/login";
        System.out.println(url.indexOf("/admin/login"));
    }

    @Test
    public void tokenTest(){
        String str = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzI2OTAwNTI5MjUwNTcwMjQwIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE2MDU0MzEyODQsInJvbGVzIjoiYWRtaW4iLCJleHAiOjE2MDU0MzE2NDR9.YbXq12fprVHbRN2PmiTbWphO1T_9HDUPZQ4FYJ3fSPs";
        String token = str.substring(7);
        System.out.println(token);
        Claims claims = jwtUtil.parseJWT(token);
        System.out.println(claims);
    }
}
