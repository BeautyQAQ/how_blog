package com.liushao.auth;

import com.liushao.dao.AdminDao;
import com.liushao.dao.UserDao;
import com.liushao.pojo.Admin;
import com.liushao.pojo.User;
import com.liushao.service.AdminService;
import com.liushao.service.UserService;
import com.liushao.util.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountPasswordTest {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private <T> T wire(T service, Object dao, String field) {
        ReflectionTestUtils.setField(service, field, dao);
        ReflectionTestUtils.setField(service, "encoder", encoder);
        ReflectionTestUtils.setField(service, "idWorker", new IdWorker(1, 1)); return service;
    }
    @Test void adminPasswordChangesAreHashedAndOmittedPasswordsArePreserved() {
        var dao = mock(AdminDao.class); var service = wire(new AdminService(), dao, "adminDao");
        var stored = new Admin(); stored.setId("1"); stored.setLoginname("admin"); stored.setState("1");
        stored.setPassword(encoder.encode("old-password")); when(dao.findById("1")).thenReturn(Optional.of(stored));
        var edit = new Admin(); edit.setId("1"); service.update(edit);
        assertTrue(encoder.matches("old-password", stored.getPassword()));
        edit.setPassword("new-password"); service.update(edit);
        assertTrue(encoder.matches("new-password", stored.getPassword()));
        assertFalse(encoder.matches("old-password", stored.getPassword()));
        when(dao.findByLoginname("admin")).thenReturn(stored);
        assertNotNull(service.findByLoginnameAndPassword("admin", "new-password"));
        stored.setState("0"); assertNull(service.findByLoginnameAndPassword("admin", "new-password"));
    }
    @Test void firstAdminBootstrapRefusesExistingAccountsAndInvalidPasswords() {
        var dao = mock(AdminDao.class); var service = wire(new AdminService(), dao, "adminDao");
        when(dao.count()).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> service.bootstrap("owner", "new-password"));
        verify(dao, never()).save(any());
        when(dao.count()).thenReturn(0L);
        assertThrows(IllegalArgumentException.class, () -> service.bootstrap("owner", "short"));
        service.bootstrap("owner", "new-password");
        verify(dao).save(argThat(admin -> "owner".equals(admin.getLoginname())
                && "1".equals(admin.getState()) && encoder.matches("new-password", admin.getPassword())));
    }
    @Test void administratorCreatedUsersAcceptPlaintextAndPreservePasswordOnProfileUpdates() {
        var dao = mock(UserDao.class); var service = wire(new UserService(), dao, "userDao");
        var user = new User(); user.setMobile("reader"); user.setNickname("Reader"); user.setPassword("user-password");
        service.add(user); assertTrue(encoder.matches("user-password", user.getPassword()));
        when(dao.findById(user.getId())).thenReturn(Optional.of(user));
        var edit = new User(); edit.setId(user.getId()); edit.setNickname("Updated"); service.update(edit);
        assertTrue(encoder.matches("user-password", edit.getPassword()));
        assertEquals("reader", edit.getMobile());
    }
}
