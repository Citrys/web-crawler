package org.crawler.model;

public record CrawledPage(
        String id,
        String url,
        String[] urls,
        boolean endOfProcessing
) {}
