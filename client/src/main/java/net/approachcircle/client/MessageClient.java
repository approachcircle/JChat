package net.approachcircle.client;

import javafx.scene.control.TextArea;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MessageClient {
    private static String host = "http://127.0.0.1:1509";
    private static List<String> messages = new ArrayList<>();
    private static volatile boolean isPolling = false;
    public static void send(String json) {
        HttpPost post = new HttpPost(host + "/message/post");
        StringEntity entity;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            System.err.println("unsupported encoding on message");
            e.printStackTrace();
            return;
        }
        post.setHeader("Content-type", "application/json");
        post.setEntity(entity);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(post);
        } catch (IOException e) {
            System.err.println("failed to post new message");
            e.printStackTrace();
        }
    }

    public static String poll() {
        HttpGet get = new HttpGet(host + "/message/query");
        HttpResponse response = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            response = client.execute(get);
        } catch (IOException e) {
            System.err.println("failed to poll");
            e.printStackTrace();
            return null;
        }
        String responseString = null;
        try {
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            System.err.println("failed to parse response on poll");
            e.printStackTrace();
            return null;
        }
        try {
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            System.err.println("failed to consume response on poll");
            e.printStackTrace();
            return null;
        }
        return responseString;
    }

    public static void startPolling(TextArea textArea) {
        isPolling = true;
        new Thread(() -> {
            while (isPolling) {
                String message = poll();
                if (message != null) {
                    textArea.appendText(message); //TODO: not allowed unfortunately, either use a 'Service' or a 'Task'
                }
            }
        }).start();
    }

    public static void stopPolling() {
        isPolling = false;
    }

    public static List<String> getMessages() {
        return messages;
    }
}
