package org.crawler.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.crawler.model.CrawledPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService {
    private final BlockingQueue<CrawledPage> queue;

    public DatabaseService(BlockingQueue<CrawledPage> queue) {
        this.queue = queue;
    }

    public void runDatabasePool(int parallelFactor) {
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        for (int i = 0; i < parallelFactor; i++) {
            Runnable task = this::saveToDatabase;

            executorService.submit(task);
        }

        executorService.shutdown();
    }

    public void saveToDatabase() {

//      MongoDatabase database = mongoClient.getDatabase("mydb");
//      MongoCollection<Document> collection = database.getCollection("my_collection");
        while (true) {
            try {
                CrawledPage data = queue.take();

                // Check if it's the poison pill
                if (data.endOfProcessing()) {
                    break;
                }
                log.info("Saved to MongoDB: " + data);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
