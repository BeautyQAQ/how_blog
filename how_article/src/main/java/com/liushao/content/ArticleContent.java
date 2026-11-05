package com.liushao.content;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import java.util.List;

/** The same renderer serves the editor preview, public detail and generated excerpts. */
@Component
public class ArticleContent {
    public static final int MAX_CONTENT = 200_000;
    private final Parser parser;
    private final HtmlRenderer renderer;
    private final Safelist safelist = new Safelist()
            .addTags("p", "br", "h1", "h2", "h3", "h4", "h5", "h6", "strong", "em", "del", "blockquote",
                    "ul", "ol", "li", "hr", "pre", "code", "a", "img", "table", "thead", "tbody", "tr", "th", "td")
            .addAttributes("a", "href", "title").addAttributes("img", "src", "alt", "title")
            .addAttributes("ol", "start").addAttributes("code", "class")
            .addAttributes("th", "align").addAttributes("td", "align")
            .addProtocols("a", "href", "http", "https", "mailto").addProtocols("img", "src", "http", "https")
            .preserveRelativeLinks(true);

    public ArticleContent() {
        var extensions = List.of(TablesExtension.create(), StrikethroughExtension.create());
        parser = Parser.builder().extensions(extensions).build();
        renderer = HtmlRenderer.builder().extensions(extensions).escapeHtml(true).sanitizeUrls(true)
                .softbreak("<br>\n").build();
    }

    public String format(String format) {
        if (format == null || format.isBlank()) return "text";
        if (!"text".equals(format) && !"markdown".equals(format))
            throw new IllegalArgumentException("正文格式只能是 text 或 markdown");
        return format;
    }

    public String render(String content, String format) {
        content = content == null ? "" : content;
        if (content.length() > MAX_CONTENT) throw new IllegalArgumentException("正文不能超过20万字符");
        if ("text".equals(format(format)))
            return "<p>" + Entities.escape(content).replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br>\n") + "</p>";
        String safe = Jsoup.clean(renderer.render(parser.parse(content)), "https://how-blog.invalid/", safelist,
                new Document.OutputSettings().prettyPrint(false));
        var document = Jsoup.parseBodyFragment(safe);
        document.outputSettings().prettyPrint(false);
        document.select("a[href]").forEach(link -> link.attr("rel", "nofollow noopener noreferrer"));
        document.select("img").forEach(image -> image.attr("loading", "lazy").attr("decoding", "async"));
        return document.body().html();
    }

    public String excerpt(String content, String format) {
        String text = "markdown".equals(format(format)) ? Jsoup.parseBodyFragment(render(content, format)).text() : content;
        return truncate(normalize(text), 180);
    }

    public String summary(String custom, String excerpt) {
        return custom == null || custom.isBlank() ? normalize(excerpt) : normalize(custom);
    }

    public static String normalize(String text) { return text == null ? "" : text.replaceAll("\\s+", " ").trim(); }
    public static String truncate(String text, int max) {
        return text.codePointCount(0, text.length()) <= max ? text : text.substring(0, text.offsetByCodePoints(0, max)) + "…";
    }
}
