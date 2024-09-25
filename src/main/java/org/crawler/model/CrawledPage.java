package org.crawler.model;

public record CrawledPage(
        String id,
        String url,

        // TODO add correct mongo codec for list of strings
      //  String[] urls,
        boolean endOfProcessing
) {}
