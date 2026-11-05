package com.liushao.auth;

import com.liushao.controller.ImageController;
import com.liushao.controller.ArticlePreviewController;
import com.liushao.controller.BaseExceptionHandler;
import com.liushao.content.ArticleContent;
import com.liushao.dao.ArticleDao;
import com.liushao.security.AccessPolicy;
import com.liushao.security.ApiAuthorizationInterceptor;
import com.liushao.service.ImageStorage;
import com.liushao.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Base64;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImageAccessTest {
    @TempDir Path directory;
    @Test void uploadPreviewAndDeletionRequireAdminWhileImagesArePublic() throws Exception {
        var dao = mock(ArticleDao.class); var jwt = new JwtUtil(); jwt.setKey(Base64.getEncoder().encodeToString(new byte[32])); jwt.setTtl(60000);
        var mvc = MockMvcBuilders.standaloneSetup(new ImageController(new ImageStorage(directory.toString()), dao), new ArticlePreviewController(new ArticleContent()))
                .setControllerAdvice(new BaseExceptionHandler()).addInterceptors(new ApiAuthorizationInterceptor(jwt, AccessPolicy.Service.ARTICLE)).build();
        var bytes = new ByteArrayOutputStream(); ImageIO.write(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), "png", bytes);
        var file = new MockMultipartFile("file", "test.png", "image/png", bytes.toByteArray());
        String admin = "Bearer " + jwt.createJWT("admin", "admin", "admin"), user = "Bearer " + jwt.createJWT("user", "user", "user");
        mvc.perform(multipart("/media").file(file)).andExpect(status().isUnauthorized());
        mvc.perform(multipart("/media").file(file).header("Authorization", user)).andExpect(status().isForbidden());
        var response = mvc.perform(multipart("/media").file(file).header("Authorization", admin)).andExpect(status().isOk()).andReturn();
        String filename = new ObjectMapper().readTree(response.getResponse().getContentAsString()).path("data").path("filename").asText();
        mvc.perform(get("/media/" + filename)).andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        when(dao.existsByImageOrContentContaining(anyString(), anyString())).thenReturn(true);
        mvc.perform(delete("/media/" + filename).header("Authorization", admin)).andExpect(status().isConflict());
        when(dao.existsByImageOrContentContaining(anyString(), anyString())).thenReturn(false);
        mvc.perform(delete("/media/" + filename)).andExpect(status().isUnauthorized());
        mvc.perform(delete("/media/" + filename).header("Authorization", admin)).andExpect(status().isOk());
        mvc.perform(get("/media/" + filename)).andExpect(status().isNotFound());
        mvc.perform(post("/article/preview").contentType("application/json").content("{}")) .andExpect(status().isUnauthorized());
        mvc.perform(post("/article/preview").header("Authorization", admin).contentType("application/json")
                        .content("{\"content\":\"## 标题\",\"contentformat\":\"markdown\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.html").value("<h2>标题</h2>\n"));
    }
}
