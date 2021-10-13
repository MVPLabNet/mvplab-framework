package app.web.impl;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author chi
 */
public class SitemapIndexBuilder {
    private final StringBuilder b = new StringBuilder();
    private final String baseURL;

    public SitemapIndexBuilder(String baseURL) {
        this.baseURL = baseURL;
        b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
    }

    public SitemapIndexBuilder add(String path) {
        b.append("<sitemap><loc>").append(baseURL).append(path).append("</loc><lastmod>");
        b.append(OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("</lastmod></sitemap>");
        return this;
    }

    public String build() {
        b.append("</sitemapindex>");
        return b.toString();
    }
}
