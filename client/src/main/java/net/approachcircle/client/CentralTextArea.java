package net.approachcircle.client;

import javafx.scene.control.TextArea;

public class CentralTextArea {
    private static CentralTextArea instance;
    private final TextArea textArea;

    private CentralTextArea() {
        textArea = new TextArea();
        textArea.setEditable(false);
    }

    public static CentralTextArea getInstance() {
        if (instance == null) {
            instance = new CentralTextArea();
        }
        return instance;
    }

    public void putTextLine(String text) {
        putText(text + "\n");
    }

    public void putText(String text) {
        textArea.appendText(text);
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
