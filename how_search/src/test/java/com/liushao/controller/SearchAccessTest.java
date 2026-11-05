package com.liushao.controller;

import com.liushao.config.SearchSecurityConfig;
import com.liushao.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.util.Base64;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(SearchAccessTest.Config.class) @WebAppConfiguration
class SearchAccessTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtUtil jwt;
    @Test void directSearchReadsWritesAndRebuildRequireAnAdministrator() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        String user = "Bearer " + jwt.createJWT("reader", "reader", "user");
        String admin = "Bearer " + jwt.createJWT("admin", "admin", "admin");
        for (String path : new String[]{"/search/init", "/search/article", "/search/channel", "/search/column",
                "/search/label", "/search/problem", "/search/reply"}) {
            mvc.perform(post(path)).andExpect(status().isUnauthorized());
            mvc.perform(post(path).header("Authorization", user)).andExpect(status().isForbidden());
            mvc.perform(post(path).header("Authorization", admin)).andExpect(status().isOk());
        }
        mvc.perform(get("/search/article/keyword/1/10")).andExpect(status().isUnauthorized());
    }
    @Configuration @EnableWebMvc @Import({SearchSecurityConfig.class, BaseExceptionHandler.class})
    static class Config {
        @Bean @Primary JwtUtil jwt() { var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000); return jwt; }
        @Bean Probe probe() { return new Probe(); }
    }
    @RestController static class Probe { @RequestMapping("/**") String ok() { return "ok"; } }
}
