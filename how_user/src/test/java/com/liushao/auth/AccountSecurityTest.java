package com.liushao.auth;

import com.liushao.config.WebSecurityConfig;
import com.liushao.controller.AdminController;
import com.liushao.controller.BaseExceptionHandler;
import com.liushao.pojo.Admin;
import com.liushao.pojo.User;
import com.liushao.service.AdminService;
import com.liushao.util.JwtUtil;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(AccountSecurityTest.Config.class) @WebAppConfiguration
class AccountSecurityTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtUtil jwt;
    @org.springframework.test.context.bean.override.mockito.MockitoBean AdminService admins;

    @Test void actualSecurityFilterChainRejectsAnonymousAndUserAdminAccess() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class)).build();
        mvc.perform(get("/admin")).andExpect(status().isUnauthorized());
        mvc.perform(post("/admin").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/admin").header("Authorization", "Bearer invalid")).andExpect(status().isUnauthorized());
        mvc.perform(get("/admin").header("Authorization", "Bearer " + jwt.createJWT("user", "reader", "user")))
                .andExpect(status().isForbidden());
        Admin admin = new Admin(); admin.setId("1"); admin.setLoginname("test"); admin.setPassword("secret-hash");
        when(admins.findAll()).thenReturn(List.of(admin));
        mvc.perform(get("/admin").header("Authorization", "Bearer " + jwt.createJWT("admin", "test", "admin")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].loginname").value("test"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
        mvc.perform(get("/admin/info").header("Authorization", "Bearer " + jwt.createJWT("admin", "test", "admin")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.roles").value("admin"));
    }

    @Test void passwordsCanBeSubmittedButNeverSerializedByEitherJacksonVersion() throws Exception {
        String input = "{\"password\":\"test-password\"}";
        var jackson3 = new ObjectMapper();
        var jackson2 = new com.fasterxml.jackson.databind.ObjectMapper();
        for (Class<?> type : List.of(Admin.class, User.class)) {
            Object account = jackson3.readValue(input, type);
            assertFalse(jackson3.writeValueAsString(account).contains("password"));
            assertFalse(jackson2.writeValueAsString(account).contains("password"));
        }
        assertEquals("test-password", jackson3.readValue(input, Admin.class).getPassword());
        assertEquals("test-password", jackson2.readValue(input, User.class).getPassword());
    }

    @Configuration @EnableWebMvc @Import({WebSecurityConfig.class, AdminController.class, BaseExceptionHandler.class})
    static class Config {
        @Bean JwtUtil jwt() { var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000); return jwt; }
        @Bean ObjectMapper mapper() { return new ObjectMapper(); }
    }
}
