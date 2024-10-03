package net.approachcircle.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class PollingService extends ScheduledService<String> {
    private final StringProperty result = new SimpleStringProperty();
    private final BooleanProperty polling = new SimpleBooleanProperty(true);

    public final String getResult() {
        return result.get();
    }

    public final boolean isPolling() {
        return polling.get();
    }

    public final void stopPolling() {
        polling.set(false);
    }

    public final void consumeResult() {
        result.set(null);
    }

    public final String getAndConsumeResult() {
        String returnValue = getResult();
        consumeResult();
        return returnValue;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<>() {
            @Override
            protected String call() {
                if (!polling.get()) {
                    this.cancel();
                }
                String message = MessageClient.poll();
                if (message != null && !message.isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        // CentralTextArea.getInstance().putTextLine(mapper.readValue(message, Message.class).content);
                        System.out.println("polled successfully");
                        result.set(mapper.readValue(message, Message.class).content);
                        return "";
                        // return mapper.readValue(message, Message.class).content;
                    } catch (JsonProcessingException e) {
                        CentralTextArea.getInstance().putTextLine("error parsing incoming message as json");
                        e.printStackTrace(System.err);
                    }
                }
                return "";
            }
        };
    }
}
