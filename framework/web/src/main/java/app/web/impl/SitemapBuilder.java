package app.web.impl;

import app.web.WebLink;

import java.time.format.DateTimeFormatter;

/**
 * @author chi
 */
public class SitemapBuilder {
    private final String baseURL;
    private final StringBuilder b = new StringBuilder();

    public SitemapBuilder(String baseURL) {
        this.baseURL = baseURL;
        b.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
    }

    public SitemapBuilder add(WebLink webLink) {
        if (webLink.path == null) {
            return this;
        }

        b.append("<url><loc>")
            .append(baseURL)
            .append(normalize(webLink.path))
            .append("</loc>");

        if (webLink.updatedTime != null) {
            b.append("<lastmod>")
                .append(webLink.updatedTime.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append("</lastmod>");
        }

        if (webLink.frequency != null) {
            b.append("<changefreq>")
                .append(webLink.frequency)
                .append("</changefreq>");
        }

        if (webLink.priority != null) {
            b.append("<priority>")
                .append(webLink.priority)
                .append("</priority>");
        }
        b.append("</url>");
        return this;
    }

    private String normalize(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    public String build() {
        b.append("</urlset>");
        return b.toString();
    }
}
