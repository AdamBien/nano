package com.airhacks.nano;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author airhacks.com
 */
public class Server {

    public static void main(String[] args) throws IOException {
        String rootFolder = args.length >= 1 ? args[0] : ".";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 4242;
        Path root = Paths.get(rootFolder);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        List<Path> discovered = Contexts.discoverContexts(root);
        discovered.stream().forEach(p -> Contexts.create(server, p));
        System.out.println("Starting server on port: " + port);
        server.start();
    }
}
