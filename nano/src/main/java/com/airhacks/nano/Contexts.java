package com.airhacks.nano;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author airhacks.com
 */
public interface Contexts {

    public static List<Path> discoverContexts(Path root) {
        List<Path> jars = new ArrayList<>();
        SimpleFileVisitor visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (!attributes.isDirectory()) {
                    if (file.toString().endsWith(".js")) {
                        jars.add(file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(root, visitor);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return jars;
    }

    public static HttpHandler instantiate(Path scriptFile) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("javascript");
        try {
            engine.eval(new FileReader(scriptFile.toFile()));
        } catch (ScriptException | FileNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        Invocable invocable = (Invocable) engine;
        NanoRequest request = invocable.getInterface(NanoRequest.class);

        return (HttpExchange he) -> {
            final OutputStream responseBody = he.getResponseBody();
            StringBuilder builder = new StringBuilder();
            ResponseWriter writer = builder::append;
            final InputStream requestBody = he.getRequestBody();
            String requestContent;
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(requestBody))) {
                requestContent = buffer.lines().collect(Collectors.joining("\n"));
            }
            Headers requestHeaders = he.getRequestHeaders();
            Headers responseHeaders = he.getResponseHeaders();
            int statusCode = request.process(he.getRequestMethod(), requestHeaders, responseHeaders, requestContent, writer);
            String content = builder.toString();
            he.sendResponseHeaders(statusCode, content.length());
            responseBody.write(content.getBytes());
            responseBody.flush();
            he.close();
        };
    }

    public static HttpContext create(HttpServer server, Path path) {
        HttpHandler handler = instantiate(path);
        final String extracted = extractContext(path);
        HttpContext context = server.createContext(extracted);
        context.setHandler(handler);
        System.out.println("Context registered: " + context.getPath());
        return context;
    }

    public static String extractContext(Path path) {
        String fileName = "/" + path.normalize().toString();
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndexOf);

    }

}
