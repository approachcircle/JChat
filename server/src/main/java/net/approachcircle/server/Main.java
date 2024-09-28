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
        });
        app.get("/message/query", ctx -> {
            ctx.result(messages.getLast());
        });
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}
