package com.safedjio.internship.task5_httpserver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomHttpServerTest {

    @BeforeAll
    static void startServer() throws InterruptedException {
        Thread serverThread = new Thread(() -> {
            new CustomHttpServer().start();
        });
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(500);
    }

    @Test
    void shouldReturn200ForRootAnd404ForUnknown() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest reqRoot = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/"))
                .GET()
                .build();
        HttpResponse<String> resRoot = client.send(reqRoot, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, resRoot.statusCode());
        assertTrue(resRoot.body().contains("Hello from Custom Server"));

        HttpRequest reqNotFound = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/not-file.txt"))
                .GET()
                .build();
        HttpResponse<String> resNotFound = client.send(reqNotFound, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, resNotFound.statusCode());
        assertTrue(resNotFound.body().contains("Error 404: Not Found"));
    }

    @Test
    void shouldHandle50ConcurrentRequests() throws Exception {
        int requestCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/"))
                .GET()
                .build();

        List<Integer> statusCodes = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    synchronized (statusCodes) {
                        statusCodes.add(response.statusCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);

        assertTrue(completed, "Сервер не успел обработать 50 запросов за 10 секунд!");
        assertEquals(requestCount, statusCodes.size());

        for (int code : statusCodes) {
            assertEquals(200, code, "Один из запросов завершился с ошибкой!");
        }
    }
}