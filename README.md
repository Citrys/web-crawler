# Task
We'd like you to write a simple web crawler in a programming language you're familiar with.
Given a starting URL, the crawler should visit each URL it finds on the same domain.
It should print each URL visited, and a list of links found on that page.
The crawler should be limited to one subdomain - so when you start with *https://monzo.com/*, 
it would crawl all pages on the monzo.com website, but not follow external links, 
for example to facebook.com or community.monzo.com.
We would like to see your own implementation of a web crawler. 
Please do not use frameworks like scrapy or go-colly which handle all the crawling behind the scenes or someone else's code.
You are welcome to use libraries to handle things like HTML parsing.

# Run details:
- Java > 21 version required
- Gradle 8.5 version
- Build: `./gradlew shadowJar`
- Run: java -jar build/libs/monzo-web-crawler-1.0-SNAPSHOT-all.jar n, where n is the thread factor (up to 10)

# Description:
- The start entry point is fixed to 'https://monzo.com/'
- The system preloads all URLs beginning with 'https://monzo.com/' into a concurrent queue at the start and uses only those URLs that begin with that prefix.
- Parallelism is controlled via the Java Executor service to simplify multithreading execution.
- The service outputs the target URL and a list of links found on that page as stated in the task (console print).
- Output example:
```
Web Crawler created
- Current url https://monzo.com/, amount of links 63, page urls [https://monzo.com/savingwithmonzo, ...]
- Current url https://monzo.com/accessibility, amount of links 62, page urls  [https://monzo.com/savingwithmonzo, https://monzo.com/legal/browser-support-policy/...]
```
- The tested timeframe to execute a full crawl of 'https://monzo.com/' is around 30 minutes with a parallel factor of 5.

# Implementation details:
- Thread-safe synchronized queue to poll and add to the job queue of URLs that should be processed.
- The service checks if a URL is already in the queue or has been processed and skips such URLs.
- The service ignores URLs that point to resources such as images (jpeg, png, gif).
- The service lacks retries and throttling mechanisms for remote communication.