package net.approachcircle.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();
        MessageClient.startPolling();
        TextField messageField = new TextField();
        messageField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // textArea.appendText(messageField.getText() + "\n");
                ObjectMapper mapper = new ObjectMapper();
                Message message = new Message();
                message.content = messageField.getText();
                try {
                    MessageClient.send(mapper.writeValueAsString(message));
                } catch (JsonProcessingException e) {
                    CentralTextArea.getInstance().putTextLine("error parsing outgoing message as json");
                    e.printStackTrace(System.err);
                }
                messageField.setText("");
            }
        });
        borderPane.setBottom(messageField);
        borderPane.setCenter(CentralTextArea.getInstance().getTextArea());
        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("JChat");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(MessageClient::stopPolling));
        launch();
    }
}