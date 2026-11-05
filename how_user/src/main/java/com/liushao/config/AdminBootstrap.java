package com.liushao.config;

import com.liushao.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Opt-in local startup operation; never an anonymous HTTP endpoint. */
@Component
@ConditionalOnProperty(name = "security.bootstrap.enabled", havingValue = "true")
public class AdminBootstrap implements ApplicationRunner {
    private final AdminService admins;
    private final String loginname;
    private final String password;
    public AdminBootstrap(AdminService admins, @Value("${HOW_BOOTSTRAP_ADMIN_LOGIN:}") String loginname,
                          @Value("${HOW_BOOTSTRAP_ADMIN_PASSWORD:}") String password) {
        this.admins = admins; this.loginname = loginname; this.password = password;
    }
    @Override public void run(ApplicationArguments args) { admins.bootstrap(loginname, password); }
}
