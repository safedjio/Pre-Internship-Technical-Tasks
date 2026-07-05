package com.safedjio.internship.task5_httpserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CustomHttpServer {
    private static final Logger logger = Logger.getLogger(CustomHttpServer.class.getName());
    private static final int PORT = 8080;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(50);

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("🌐 Сервер запущен на порту " + PORT + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            logger.severe("Ошибка запуска сервера: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;
            logger.info("Получен запрос: " + requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 3) return;

            String method = parts[0];
            String path = parts[1];

            if (!method.equals("GET")) {
                sendErrorResponse(output, 405, "Method Not Allowed");
                return;
            }

            if (path.equals("/")) {
                sendSuccessResponse(output, "Hello from Custom Server");
            } else {
                serveStaticFile(output, path);
            }

        } catch (Exception e) {
            logger.severe("Внутренняя ошибка сервера: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.severe("Не удалось закрыть сокет: " + e.getMessage());
            }
        }
    }

    private void serveStaticFile(OutputStream output, String path) throws IOException {
        String fileName = path.substring(1);
        URL resource = Thread.currentThread().getContextClassLoader().getResource(fileName);

        if (resource == null) {
            sendErrorResponse(output, 404, "Not Found");
        } else {
            try {
                String decodedPath = java.net.URLDecoder.decode(resource.getFile(), "UTF-8");
                File file = new File(decodedPath);
                byte[] content = Files.readAllBytes(file.toPath());

                PrintWriter writer = new PrintWriter(output, false);
                writer.print("HTTP/1.1 200 OK\r\n");
                writer.print("Content-Length: " + content.length + "\r\n");
                writer.print("Content-Type: text/plain\r\n");
                writer.print("\r\n");
                writer.flush();

                output.write(content);
                output.flush();
            } catch (Exception e) {
                logger.severe("Ошибка при чтении файла: " + e.getMessage());
                sendErrorResponse(output, 500, "Internal Server Error");
            }
        }
    }

    private void sendSuccessResponse(OutputStream output, String message) {
        PrintWriter writer = new PrintWriter(output, true);
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: text/plain; charset=UTF-8\r\n");
        writer.print("Content-Length: " + message.getBytes().length + "\r\n");
        writer.print("\r\n");
        writer.print(message);
        writer.flush();
    }

    private void sendErrorResponse(OutputStream output, int statusCode, String statusMessage) {
        PrintWriter writer = new PrintWriter(output, true);
        writer.print("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
        writer.print("Content-Type: text/plain\r\n");
        writer.print("\r\n");
        writer.print("Error " + statusCode + ": " + statusMessage);
        writer.flush();
    }

    public static void main(String[] args) {
        new CustomHttpServer().start();
    }
}