package com.liushao.content;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArticleContentTest {
    private final ArticleContent content = new ArticleContent();
    @Test void markdownRendersHeadingsTablesCodeAndUploadedImages() {
        String html = content.render("## 标题\n\n**加粗**和~~删除~~\n\n```java\nSystem.out.println(1);\n```\n\n|列|值|\n|---|---|\n|甲|乙|\n\n![图片](/api/article/media/0123456789abcdef0123456789abcdef.png)", "markdown");
        var document = Jsoup.parseBodyFragment(html);
        assertEquals("标题", document.selectFirst("h2").text());
        assertEquals("加粗", document.selectFirst("strong").text());
        assertEquals("删除", document.selectFirst("del").text());
        assertNotNull(document.selectFirst("table")); assertNotNull(document.selectFirst("pre code"));
        assertEquals("/api/article/media/0123456789abcdef0123456789abcdef.png", document.selectFirst("img").attr("src"));
    }
    @Test void rawHtmlAndUnsafeLinksNeverBecomeExecutableMarkup() {
        String html = content.render("<script>alert(1)</script>\n\n<img src=x onerror=alert(1)>\n\n[x](javascript:alert%281%29)\n\n![x](data:image/svg+xml,bad)\n\n[安全](https://example.com)", "markdown");
        var document = Jsoup.parseBodyFragment(html);
        assertTrue(document.select("script,iframe,svg,[onerror],[onclick]").isEmpty());
        document.select("a[href],img[src]").forEach(node -> {
            String url = node.hasAttr("href") ? node.attr("href") : node.attr("src");
            assertFalse(url.startsWith("javascript:")); assertFalse(url.startsWith("data:"));
        });
        assertTrue(document.selectFirst("a[href=https://example.com]").attr("rel").contains("noopener"));
    }
    @Test void legacyTextStaysLiteralAndExcerptsArePlainUnicodeText() {
        String html = content.render("## 仍是纯文本\n<b>不能变成标签</b>", null);
        assertFalse(html.contains("<h2>")); assertFalse(html.contains("<b>")); assertTrue(html.contains("<br>"));
        assertEquals("标题 加粗 正文", content.excerpt("# 标题\n\n**加粗** 正文", "markdown"));
        String excerpt = content.excerpt("🙂".repeat(200), "text");
        assertEquals(181, excerpt.codePointCount(0, excerpt.length()));
        assertEquals("自定义 摘要", content.summary(" 自定义\n摘要 ", "自动"));
        assertThrows(IllegalArgumentException.class, () -> content.render("test", "html"));
    }
}
