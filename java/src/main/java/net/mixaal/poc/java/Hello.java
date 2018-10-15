package net.mixaal.poc.java;

import fi.iki.elonen.NanoHTTPD.Response.Status;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Hello extends fi.iki.elonen.NanoHTTPD {
    private static final Executor EXEC = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Hello(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("/hello".equals(session.getUri())) {
            String m = "Hello World!";
            ByteArrayInputStream is = new ByteArrayInputStream(m.getBytes(StandardCharsets.UTF_8));
            return new Response(Status.OK, "text/plain", is, m.length()) {
            };
        }
        return super.serve(session);
    }

    public static void main(String[] args) throws IOException {
        Hello h = new Hello(4567);
        h.start();
        System.in.read();
    }
}
