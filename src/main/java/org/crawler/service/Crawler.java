package org.crawler.service;

import org.crawler.model.CrawledPage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Crawler {
    private final String startUrl;
    private final String baseDomainUrl;
    private final ConcurrentHashMap<String, String> visitedUrls = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> toBeVisited = new ConcurrentLinkedQueue<>();
    // Queue to hold data to be saved to the database
    private final BlockingQueue<CrawledPage> dataQueue = new LinkedBlockingQueue<>();
    private final DatabaseService databaseService;

    public Crawler(String startUrl, String baseDomainUrl) {
        System.out.println("Web Crawler created");
        this.startUrl = startUrl;
        this.baseDomainUrl = baseDomainUrl;
        this.databaseService = new DatabaseService(dataQueue);
        initCrawler();
    }

    public void runCrawler(int parallelFactor) {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (int i = 0; i < parallelFactor; i++) {
            Runnable task = this::crawlUrls;
            executorService.submit(task);
        }
        // TODO make poison pill better structured
        dataQueue.add(new CrawledPage("", "", new String[]{""}, true ));
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
                dataQueue.add(this.convertToCrawlerData(nextUrl, nextSetOfUrls));
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
        this.databaseService.runDatabasePool(4);
    }

    private String calculateHashFromURL(String url) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply the hash function to the URL
            byte[] hashBytes = digest.digest(url.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private CrawledPage convertToCrawlerData(String primaryUrl, ArrayList<String> containingUrls) {
        return new CrawledPage(
                this.calculateHashFromURL(primaryUrl),
                primaryUrl,
                containingUrls.toArray(new String[0]),
                false
        );
    }

}
