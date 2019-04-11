package client;

import client.interfaces.SampleRequest;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.net.Socket;

public class WatchClientDisplay
{
    private Stage stage;
    private Scene scene;
    private SampleRequest sampleRequest;

    public WatchClientDisplay(Stage stage)
    {
        this.stage = stage;
        buildDisplay();
    }

    private void buildDisplay()
    {
        FlowPane pane = new FlowPane();
        TextArea textStream = new TextArea("The stream data will appear here.");
        Button b_startStream = new Button("Start Stream");
        Button b_stopStream = new Button("Stop Stream");
        pane.getChildren().addAll(b_startStream, b_stopStream, textStream);

        addListeners(b_startStream, b_stopStream, textStream);
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    private void addListeners(Button b_startStream, Button b_stopStream, TextArea dataOutput)
    {
        b_startStream.setOnAction(e ->
        {
            Socket socket = new Socket();
            sampleRequest = new SampleRequest(dataOutput);
            sampleRequest.buildRequest(socket);
            sampleRequest.sendRequest();
        });

        b_stopStream.setOnAction(e ->
        {
            sampleRequest.stopRequest();
        });

    }

    public void show()
    {
        stage.show();
    }
}