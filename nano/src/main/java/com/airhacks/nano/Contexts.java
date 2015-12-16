package com.airhacks.nano;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
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
        return invocable.getInterface(HttpHandler.class);
    }

    public static HttpContext create(HttpServer server, Path path) {
        HttpHandler handler = instantiate(path);
        HttpContext context = server.createContext(extractContext(path));
        context.setHandler(handler);
        System.out.println("Registering context with path: " + context.getPath());
        return context;
    }

    public static String extractContext(Path path) {
        String fileName = path.toString();
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(0, lastIndexOf);

    }

}
