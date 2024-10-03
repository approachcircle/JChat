package net.approachcircle.client;

import javafx.util.Duration;
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
    private static final String host = "http://127.0.0.1:1509";
    private static PollingService pollService;
    private static int pollFailCount = 0;
    public static void send(String json) {
        HttpPost post = new HttpPost(host + "/message/post");
        StringEntity entity;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            CentralTextArea.getInstance().putTextLine("unsupported encoding on message");
            e.printStackTrace(System.err);
            return;
        }
        post.setHeader("Content-type", "application/json");
        post.setEntity(entity);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(post);
        } catch (IOException e) {
            CentralTextArea.getInstance().putTextLine("failed to post new message");
            e.printStackTrace(System.err);
        }
    }

    public static String poll() {
        HttpGet get = new HttpGet(host + "/message/query");
        HttpResponse response;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            response = client.execute(get);
        } catch (IOException e) {
            //if (pollFailCount % 20 == 0) {
            CentralTextArea.getInstance().putTextLine(String.format("[#%d] failed to poll (server may be down)", pollFailCount));
            //}
            pollFailCount++;
            e.printStackTrace(System.err);
            return null;
        }
        String responseString;
        try {
            responseString = EntityUtils.toString(response.getEntity());
            // System.out.println("messages from poll: " + responseString);
        } catch (IOException e) {
            CentralTextArea.getInstance().putTextLine("failed to parse response on poll");
            e.printStackTrace(System.err);
            return null;
        }
        try {
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            System.err.println("failed to consume response on poll");
            e.printStackTrace(System.err);
            return null;
        }
        return responseString;
    }

    public static void startPolling() {
        pollService = new PollingService();
        pollService.setOnSucceeded((e) -> {
            String message = pollService.getAndConsumeResult();
            if (message != null && !message.isEmpty()) {
                CentralTextArea.getInstance().putTextLine(message); // only get once (need to consume)
            }
        });
        pollService.setRestartOnFailure(true);
        pollService.setDelay(Duration.seconds(5));
        pollService.start();
    }

    public static void stopPolling() {
        pollService.cancel();
    }
}
