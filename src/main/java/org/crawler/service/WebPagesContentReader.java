package org.crawler.service;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.SECONDS;


@Slf4j
public class WebPagesContentReader {

    static RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.of(2, SECONDS))
            .build();
    static RetryRegistry registry = RetryRegistry.of(config);
    static Retry retry = registry.retry("WebContentReader", config);

    public static Document getPage(String url) {
        Supplier<Document> supplier = () -> getWebPageFromUrl(url);
        Supplier<Document> getPageWithRetry = Retry.decorateSupplier(retry, supplier);
        getPageWithRetry.get();
        return getPageWithRetry.get();
    }

    private static Document getWebPageFromUrl(String url) throws RuntimeException {
        try {
            Connection connection = Jsoup.connect(url);
            return connection.get();
        } catch (IOException e) {
            log.error(String.valueOf(e));
            throw new RuntimeException("Could not process the requested url");
        }

    }

    public static Set<String> processPage(Document document, String url, String baseDomainUrl) {
        Set<String> targetLinks = new HashSet<>();
        if (document != null) {
            for (Element link : document.select("a[href]")) {
                String absoluteUrl = link.absUrl("href");
                if (absoluteUrl.startsWith(baseDomainUrl) && !WebPagesContentReader.reduceImageTypeUrls(absoluteUrl)) {
                    targetLinks.add(absoluteUrl);
                }
            }
        }
        return targetLinks;
    }

    public static boolean reduceImageTypeUrls(String s) {
        String regex = "http(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./]*)+\\.(?:[gG][iI][fF]|[jJ][pP][gG]|[jJ][pP][eE][gG]|[pP][nN][gG]|[bB][mM][pP])";
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find();
    }
}
