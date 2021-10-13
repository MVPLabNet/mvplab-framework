package app.web.impl;

import app.web.Sitemap;
import app.web.WebCache;
import app.web.WebLink;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import app.resource.Resource;
import app.test.TempDirectory;
import app.test.TempDirectoryExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author chi
 */
@ExtendWith(TempDirectoryExtension.class)
class SitemapServiceTest {
    SitemapService sitemapService;


    @BeforeEach
    void setup(@TempDirectory Path root) {
        WebCache cache = new WebCacheImpl(root);
        sitemapService = new SitemapService("http://localhost:8080/", cache);
        sitemapService.addSitemap(sitemap());
    }

    @Test
    void buildIndex() {
        sitemapService.setMaxEntry(1);
        sitemapService.build();

        Optional<Resource> sitemap = sitemapService.sitemap("sitemap.xml");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><sitemap><loc>http://localhost:8080/sitemap/sitemap-0.xml</loc><lastmod>2018-08-15</lastmod></sitemap><sitemap><loc>http://localhost:8080/sitemap/sitemap-1.xml</loc><lastmod>2018-08-15</lastmod></sitemap></sitemapindex>", sitemap.get().toText(Charsets.UTF_8));
    }

    @Test
    void build() {
        sitemapService.setMaxEntry(2);
        sitemapService.build();

        Optional<Resource> sitemap = sitemapService.sitemap("sitemap.xml");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url><loc>http://localhost:8080/1.html</loc></url><url><loc>http://localhost:8080/2.html</loc></url></urlset>", sitemap.get().toText(Charsets.UTF_8));
    }

    private Sitemap sitemap() {
        return new Sitemap() {
            @Override
            public long totalLinks() {
                return 2;
            }

            @Override
            public Iterator<WebLink> iterator() {
                return Lists.newArrayList(webLink("1.html"), webLink("2.html")).iterator();
            }
        };
    }

    private WebLink webLink(String path) {
        WebLink webLink = new WebLink();
        webLink.path = path;
        return webLink;
    }
}