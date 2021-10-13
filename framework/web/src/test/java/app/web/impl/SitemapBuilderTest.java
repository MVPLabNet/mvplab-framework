package app.web.impl;

import app.web.WebLink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
class SitemapBuilderTest {
    @Test
    void build() {
        SitemapBuilder b = new SitemapBuilder("http://localhost/");
        WebLink webLink = new WebLink();
        webLink.path = "test1";
        webLink.frequency = "Weekly";
        b.add(webLink);

        assertEquals("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><urlset xmlns=\\\"http://www.sitemaps.org/schemas/sitemap/0.9\\\"><url><loc>http://localhost/test1</loc><changefreq>Weekly</changefreq></url></urlset>", b.build());
    }
}