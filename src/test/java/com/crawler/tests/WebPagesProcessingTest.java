package com.crawler.tests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.crawler.service.WebPagesContentReader;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class WebPagesProcessingTest {
    @Test
    public void shouldReturnFalseIfUrlHasImage1() {
        String url = "https://images.ctfassets.net/ro61k101ee59/41wN3UPYZEgXOMMYk8U7i7/a8dea8204136eb3bc7c878f967db96f6/contained.jpg";
        Assertions.assertTrue(WebPagesContentReader.reduceImageTypeUrls(url));
    }

    @Test
    public void shouldReturnFalseIfUrlHasImage2() {
        String url = "https://images.ctfassets.net/ro61k101ee59/41wN3UPYZEgXOMMYk8U7i7/a8dea8204136eb3bc7c878f967db96f6/home-hero-contained.png?w=1160&q=75&fm=avif";
        Assertions.assertTrue(WebPagesContentReader.reduceImageTypeUrls(url));
    }

    @Test
    public void shouldProcessValidUrl() {
       ArrayList<String> pageLinks = new ArrayList<>(WebPagesContentReader.processPage(WebPagesContentReader.getPage("https://www.google.com/"), "https://www.google.com/", "https://www.google.com/"));
       Assertions.assertTrue(pageLinks.size() > 0);
    }

    @Test
    public void shouldProcessInvalidUrl() {

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new ArrayList<> (WebPagesContentReader.processPage(WebPagesContentReader.getPage("https://www.google.com/404"), "https://www.google.com/404", "https://www.google.com/404"));
        });

        Assertions.assertTrue("Could not process the requested url".contains(exception.getMessage()));
    }
}
