package com.crawler.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.crawler.service.Crawler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CrawlerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    WireMockServer service;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        service = new WireMockServer(options().port(8888).httpsPort(4433));
        service.start();
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test()
    public void crawlPages() {
        service.stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html> <body> <a href=\"http://localhost:8888/page3\">Page 3</a> <a href=\"http://localhost:8888/page4\">Page 4</a> </body> </html>")));

        service.stubFor(get(urlEqualTo("/page2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html> <body> <a href=\"http://localhost:8888/page3\">Page 3</a> <a href=\"http://localhost:8888/page4\">Page 4</a> </body> </html>")));

        service.stubFor(get(urlEqualTo("/page3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html> <body> <a href=\"http://localhost:8888/page2\">Page 3</a> <a href=\"http://localhost:8888/page2\">Page 4</a> </body> </html>")));


        service.stubFor(get(urlEqualTo("/page4"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html> <body> <a href=\"http://localhost:8888/page3\">Page 3</a> <a href=\"http://localhost:8888/page4\">Page 4</a> </body> </html>")));

        Crawler crawler = new Crawler("http://localhost:8888/", "http://localhost:8888/");
        crawler.runCrawler(1);
    }
}
