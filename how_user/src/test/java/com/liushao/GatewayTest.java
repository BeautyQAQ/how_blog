package com.liushao;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GatewayTest {

    @Test
    public void urlTest(){
        String url = "/admin/login";
        System.out.println(url.indexOf("/admin/login"));
    }
}
