package net.approachcircle.server;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> messages = new ArrayList<>();
        Javalin app = Javalin.create().start(1509);
        app.post("/message/post", ctx -> {
            messages.add(ctx.body());
            System.out.println("posting new message: " + ctx.body());
        });
        app.get("/message/query", ctx -> {
            if (messages.isEmpty()) {
                ctx.result("");
                // System.out.println("no messages, returning empty string");
                return;
            }
            System.out.println("returning last message: " + messages.getLast());
            ctx.result(messages.getLast());
            messages.clear();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}
