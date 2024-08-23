package org.crawler;

import org.crawler.service.Crawler;

public class Boot {
    public static void main(String[] args) {
        //Number of threads = Number of Available Cores * Target CPU utilization * (1 + Wait time / Service time)
        int availableCores = Runtime.getRuntime().availableProcessors();
        int parallelFactor = Math.max(availableCores - 1, 1);
        String startAndBaseUrl = "https://www.bbc.co.uk/";
        if (args.length > 0) {
            try {
                parallelFactor = Integer.parseInt(args[0]);
                if (parallelFactor > 20) {
                    System.err.println("Argument" + args[0] + " must be lower than 20.");
                    System.exit(1);
                }
                startAndBaseUrl = args[1];
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        Crawler crawler = new Crawler(startAndBaseUrl, startAndBaseUrl);

        crawler.runCrawler(parallelFactor);
    }
}