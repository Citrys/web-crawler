package org.monzo.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPagesContentReader {
    static ExponentialBackoffStrategy backoff = new ExponentialBackoffStrategy();

    public static Document getPage(String url) {
        try {
            Connection connection = Jsoup.connect(url);

            return backoff.attempt(
                    () -> {
                        try {
                            return connection.get();
                        } catch (IOException e) {
                            throw new RuntimeException(String.valueOf(connection.response().statusCode()),e);
                        }
                    },
                    r -> {
                        final int statusCode = connection.response().statusCode();
                        return statusCode == 200;
                    }
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<String> processPage(Document document, String url, String baseDomainUrl) {
        Set<String> targetLinks = new HashSet<>();
        if (document != null) {
            for (Element link: document.select("a[href]")) {
                String absoluteUrl = link.absUrl("href");
                if (absoluteUrl.startsWith(baseDomainUrl) && !WebPagesContentReader.reduceImageTypeUrls(absoluteUrl)) {
                    targetLinks.add(absoluteUrl);
                }
            }
            System.out.printf("- Current url %s, amount of links %s, page urls %s", url, targetLinks.size(), targetLinks);
            System.out.println();
        }
        return targetLinks;
    }

    public static boolean reduceImageTypeUrls(String s) {
        String regex = "http(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./]*)+\\.(?:[gG][iI][fF]|[jJ][pP][gG]|[jJ][pP][eE][gG]|[pP][nN][gG]|[bB][mM][pP])";
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find();
    }
}
