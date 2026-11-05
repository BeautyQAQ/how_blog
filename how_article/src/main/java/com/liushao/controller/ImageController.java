package com.liushao.controller;

import com.liushao.dao.ArticleDao;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.ImageStorage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/media")
public class ImageController {
    private final ImageStorage storage;
    private final ArticleDao articles;
    public ImageController(ImageStorage storage, ArticleDao articles) { this.storage = storage; this.articles = articles; }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        return new Result(true, StatusCode.OK, "上传成功", storage.store(file));
    }
    @GetMapping("/{filename}")
    public ResponseEntity<FileSystemResource> image(@PathVariable String filename) {
        var stored = storage.read(filename);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(stored.contentType()))
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .body(new FileSystemResource(stored.path()));
    }
    @DeleteMapping("/{filename}")
    public Result delete(@PathVariable String filename) throws IOException {
        storage.read(filename);
        String url = "/api/article/media/" + filename;
        if (articles.existsByImageOrContentContaining(url, url))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "图片仍被文章使用，不能删除");
        storage.delete(filename);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
