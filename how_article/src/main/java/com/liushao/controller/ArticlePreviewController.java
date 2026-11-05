package com.liushao.controller;

import com.liushao.content.ArticleContent;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/article/preview")
public class ArticlePreviewController {
    private final ArticleContent content;
    public ArticlePreviewController(ArticleContent content) { this.content = content; }
    public record Preview(String content, String contentformat) {}
    @PostMapping public Result preview(@RequestBody Preview input) {
        return new Result(true, StatusCode.OK, "预览成功", Map.of("html", content.render(input.content(), input.contentformat()),
                "summary", content.excerpt(input.content(), input.contentformat())));
    }
}
