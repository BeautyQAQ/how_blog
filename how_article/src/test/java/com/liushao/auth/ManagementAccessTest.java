package com.liushao.auth;

import com.liushao.config.ArticleSecurityConfig;
import com.liushao.controller.BaseExceptionHandler;
import com.liushao.util.JwtUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(ManagementAccessTest.Config.class) @WebAppConfiguration
class ManagementAccessTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtUtil jwt;
    @Test void allManagementResourcesAreProtectedWhileReadingStaysPublic() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        String user = "Bearer " + jwt.createJWT("reader", "reader", "user");
        String admin = "Bearer " + jwt.createJWT("admin", "admin", "admin");
        for (String path : new String[]{"/channel/1", "/city/1", "/column/1", "/label/1", "/comment/1",
                "/spit/1", "/problem/1", "/reply/1", "/article/1"}) {
            mvc.perform(delete(path)).andExpect(status().isUnauthorized());
            mvc.perform(delete(path).header("Authorization", user)).andExpect(status().isForbidden());
            mvc.perform(delete(path).header("Authorization", admin)).andExpect(status().isOk());
        }
        mvc.perform(get("/init/data")).andExpect(status().isUnauthorized());
        mvc.perform(get("/channel")).andExpect(status().isOk());
        mvc.perform(get("/article/public")).andExpect(status().isOk());
        mvc.perform(post("/comment").header("Authorization", user)).andExpect(status().isOk());
        mvc.perform(post("/comment")).andExpect(status().isUnauthorized());
    }

    @Test void feignForwardsTheCallingIdentityWithoutInventingAServiceCredential() {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer caller");
        try {
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
            var template = new RequestTemplate(); context.getBean(RequestInterceptor.class).apply(template);
            assertTrue(template.headers().get("Authorization").contains("Bearer caller"));
        } finally { RequestContextHolder.resetRequestAttributes(); }
        var background = new RequestTemplate(); context.getBean(RequestInterceptor.class).apply(background);
        assertFalse(background.headers().containsKey("Authorization"));
    }

    @Configuration @EnableWebMvc @Import({ArticleSecurityConfig.class, BaseExceptionHandler.class})
    static class Config {
        @Bean @Primary JwtUtil jwt() { var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000); return jwt; }
        @Bean Probe probe() { return new Probe(); }
    }
    @RestController static class Probe {
        @RequestMapping("/**") String ok() { return "ok"; }
    }
}
