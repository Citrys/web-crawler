package org.monzo.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPagesContentReader {

    public static Document getPage(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() == 200) {
                return document;
            } else {
                // TODO change to throw proper error with code, implement retries and throttling in service returns too many requests error code
                return null;
            }
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
