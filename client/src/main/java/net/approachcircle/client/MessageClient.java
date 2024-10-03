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

public class MessageClient {
    private static final String host = "http://127.0.0.1:1509";
    private static PollingService pollService;
    private static boolean pollingFailed = false;
    private static int pollFailCount = 0;
    private static int pollFailThreshold = 50;
    private static CloseableHttpClient httpClient;
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
        try {
            httpClient.execute(post);
        } catch (IOException e) {
            CentralTextArea.getInstance().putTextLine("failed to post new message");
            e.printStackTrace(System.err);
        }
    }

    public static String poll() {
        HttpGet get = new HttpGet(host + "/message/query");
        HttpResponse response;
        try {
            response = httpClient.execute(get);
        } catch (IOException e) {
            pollingFailed = true;
            pollFailCount++;
            e.printStackTrace(System.err);
            if (pollFailCount >= pollFailThreshold) {
                CentralTextArea.getInstance().putTextLine("failing to poll... (server may be down)");
                CentralTextArea.getInstance().putTextLine("backing off (10s)...");
                pollFailCount = 0;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {
                    throw new RuntimeException("backoff slumber interrupted");
                }
                CentralTextArea.getInstance().putTextLine("resuming...");
            }
            return null;
        }
        if (pollingFailed) {
            CentralTextArea.getInstance().putTextLine("connection re-established");
        }
        pollingFailed = false; // execution will reach here unless an exception is thrown, because we return if so
        String responseString;
        try {
            responseString = EntityUtils.toString(response.getEntity());
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
        httpClient = HttpClients.createDefault();
        pollService = new PollingService();
        pollService.setOnSucceeded((e) -> {
            String message = pollService.getAndConsumeResult();
            if (message != null && !message.isEmpty()) {
                CentralTextArea.getInstance().putTextLine(message, LogSource.Self);
                // will have to change impl drastically to accommodate messages from other clients
                // including changing the message payload structure, logging impl, and probably
                // message distribution on the server side
            }
        });
        pollService.setRestartOnFailure(true);
        pollService.setDelay(Duration.ZERO);
        pollService.start();
    }

    public static void stopPolling() {
        pollService.stopPolling();
        try {
            httpClient.close();
        } catch (IOException e) {
            CentralTextArea.getInstance().putTextLine("failed to close http client");
        }
    }
}
