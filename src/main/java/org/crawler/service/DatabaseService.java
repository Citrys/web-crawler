package org.crawler.service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.crawler.model.CrawledPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseService {
    private final BlockingQueue<CrawledPage> queue;
    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    String collectionName = "crawler" + new Date().getTime();

    CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );
    MongoDatabase database = mongoClient.getDatabase("webCrawler");
    MongoCollection<CrawledPage> collection;
    public DatabaseService(BlockingQueue<CrawledPage> queue) {
        this.queue = queue;
        this.database.createCollection(collectionName);
        this.collection = this.database.getCollection(collectionName, CrawledPage.class).withCodecRegistry(this.codecRegistry);
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

        while (true) {
            try {
                CrawledPage data = queue.take();
                // Check if it's the poison pill
                if (data.endOfProcessing()) {
                    break;
                }

                try {
                    this.collection.insertOne(data);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // TODO why the connection is being closed before executing of while loop !!!
       //  this.mongoClient.close();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
