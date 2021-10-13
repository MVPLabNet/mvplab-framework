package app.web;

/**
 * @author chi
 */
public interface Sitemap extends Iterable<WebLink> {
    long totalLinks();
}
