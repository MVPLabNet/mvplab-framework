package app.web.impl;

import app.resource.Resource;
import app.resource.StringResource;
import app.web.Sitemap;
import app.web.WebCache;
import app.web.WebLink;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chi
 */
public class SitemapService {
    private int maxEntry = 50000;
    private final String baseURL;
    private final WebCache cache;
    private final List<Sitemap> sitemaps = Lists.newArrayList();

    public SitemapService(String baseURL, WebCache cache) {
        this.baseURL = baseURL;
        this.cache = cache;
    }

    public void setMaxEntry(int maxEntry) {
        this.maxEntry = maxEntry;
    }

    public void addSitemap(Sitemap sitemap) {
        sitemaps.add(sitemap);
    }

    public Optional<Resource> sitemap(String path) {
        return cache.get(path);
    }

    public void build() {
        long totalLinks = totalLinks();
        Iterator<WebLink> iterator = Iterators.concat(sitemaps.stream().map(Iterable::iterator).collect(Collectors.toList()).iterator());
        if (totalLinks > maxEntry) {
            List<String> resourcePaths = createIndexResource("sitemap.xml", totalLinks);
            for (String resourcePath : resourcePaths) {
                createResource(resourcePath, iterator);
            }
        } else {
            createResource("sitemap.xml", iterator);
        }
    }

    private List<String> createIndexResource(String path, long totalLinks) {
        SitemapIndexBuilder b = new SitemapIndexBuilder(baseURL);
        List<String> resourcePaths = Lists.newArrayList();
        int count = (int) (totalLinks % maxEntry == 0 ? totalLinks / maxEntry : totalLinks / maxEntry + 1);
        for (int i = 0; i < count; i++) {
            String resourcePath = "sitemap/sitemap-" + i + ".xml";
            resourcePaths.add(resourcePath);
            b.add(resourcePath);
        }
        StringResource resource = new StringResource(path, b.build());
        cache.create(resource);
        return resourcePaths;
    }

    private void createResource(String path, Iterator<WebLink> iterator) {
        SitemapBuilder b = new SitemapBuilder(baseURL);
        int index = 0;
        while (iterator.hasNext() && index < maxEntry) {
            b.add(iterator.next());
            index++;
        }
        StringResource resource = new StringResource(path, b.build());
        cache.create(resource);
    }

    private long totalLinks() {
        int totalLinks = 0;
        for (Sitemap sitemap : sitemaps) {
            totalLinks += sitemap.totalLinks();
        }
        return totalLinks;
    }
}
