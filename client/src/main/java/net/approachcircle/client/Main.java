package net.approachcircle.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();
        BorderPane borderPane = new BorderPane();
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        MessageClient.startPolling(textArea);
        TextField messageField = new TextField();
        messageField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                // textArea.appendText(messageField.getText() + "\n");
                MessageClient.send(String.format("{\"content\": \"%s\"", messageField.getText()));
                messageField.setText("");
            }
        });
        borderPane.setBottom(messageField);
        borderPane.setCenter(textArea);
        root.getChildren().add(borderPane);
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(MessageClient::stopPolling));
        launch();
    }
}