package net.approachcircle.client;

import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CentralTextArea {
    private static CentralTextArea instance;
    private final TextArea textArea;
    private final SimpleDateFormat timeFormat;

    private CentralTextArea() {
        timeFormat = new SimpleDateFormat("HH:mm:ss");
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
        putTextLine(text, LogSource.System);
    }

    public void putTextLine(String text, LogSource logSource) {
        putText(text + "\n", logSource);
    }

    public void putText(String text) {
        putText(text, LogSource.System);
    }

    public void putText(String text, LogSource logSource) {
        textArea.appendText(
                String.format("[%s %s] %s", timeFormat.format(new Date()), logSource.name().toUpperCase(), text)
        );
    }


    public TextArea getTextArea() {
        return textArea;
    }
}
