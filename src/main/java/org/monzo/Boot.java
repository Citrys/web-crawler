package org.monzo;
import org.monzo.service.Crawler;

public class Boot {
    public static void main(String[] args)  {
        Crawler crawler = new Crawler("https://monzo.com/", "https://monzo.com/");
        int parallelFactor = 1;
        if (args.length > 0) {
            try {
                parallelFactor = Integer.parseInt(args[0]);
                if (parallelFactor > 20) {
                    System.err.println("Argument" + args[0] + " must be lower than 20.");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        crawler.runCrawler(parallelFactor);
    }
}