package com.monzo.tests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monzo.service.WebPagesContentReader;
import java.util.ArrayList;


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
       ArrayList<String> pageLinks = new ArrayList<> (WebPagesContentReader.processPage(WebPagesContentReader.getPage("https://monzo.com/"), "https://monzo.com/", "https://monzo.com/"));
       Assertions.assertTrue(pageLinks.size() > 0);
    }

    @Test
    public void shouldProcessInvalidUrl() {
        ArrayList<String> pageLinks = new ArrayList<> (WebPagesContentReader.processPage(WebPagesContentReader.getPage("http://someasjfhklafd.com/asdasf"), "http://someasjfhklafd.com/assdasd", "http://someasjfhklafd.com"));
        Assertions.assertEquals(0, pageLinks.size());
    }
}
