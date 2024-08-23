package org.crawler.service;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Crawler {
    private final String startUrl;
    private final String baseDomainUrl;
    private final ConcurrentHashMap<String, String> visitedUrls = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> toBeVisited = new ConcurrentLinkedQueue<>();

    public Crawler(String startUrl, String baseDomainUrl) {
        System.out.println("Web Crawler created");
        this.startUrl = startUrl;
        this.baseDomainUrl = baseDomainUrl;
        initCrawler();
    }

    public void runCrawler(int parallelFactor) {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        for (int i = 0; i < parallelFactor; i++) {
            Runnable task = this::crawlUrls;
            executorService.submit(task);
        }
        executorService.shutdown();
    }

    private synchronized void crawlUrls() {
        while (!toBeVisited.isEmpty()) {
            String nextUrl = toBeVisited.poll();
            if (!visitedUrls.contains(nextUrl)) {
                ArrayList<String> nextSetOfUrls = new ArrayList<>(WebPagesContentReader
                        .processPage(WebPagesContentReader.getPage(nextUrl), nextUrl, this.baseDomainUrl));
                for (String url : nextSetOfUrls) {
                    /*
                     To be sure each url from next Set is not in visited and not in the queued,
                     if it's already in the queued, there's no need to add it to the queue
                     */
                    if (!visitedUrls.contains(url) || !toBeVisited.contains(url)) {
                        toBeVisited.offer(url);
                    }
                }
                visitedUrls.put(nextUrl, nextUrl);
            }
        }
    }

    private void initCrawler() {
        if (this.startUrl != null) {
            WebPagesContentReader
                    .processPage(WebPagesContentReader
                            .getPage(this.startUrl), this.startUrl, this.baseDomainUrl)
                    .forEach(this.toBeVisited::offer);
        }
    }
}
