package client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lib.StreamView;

import java.net.Socket;

public class WatchClientDisplay
{
    private Stage stage;
    private Scene scene;
    private ViewStreamRequest viewStreamReq;

    public WatchClientDisplay(Stage stage)
    {
        this.stage = stage;
        buildDisplay();
    }

    private void buildDisplay()
    {
        FlowPane pane = new FlowPane();
        TextField streamText = new TextField();
        Button b_startStream = new Button("Start Stream");
        Button b_stopStream = new Button("Stop Stream");

        StreamView streamView = new StreamView();
        pane.getChildren().addAll(b_startStream, streamText, b_stopStream, streamView);

        addListeners(b_startStream, b_stopStream, streamView, streamText);
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    private void addListeners(Button b_startStream, Button b_stopStream, StreamView streamView,
                              TextField streamText)
    {
        b_startStream.setOnAction(e ->
        {
            Socket socket = new Socket();
            viewStreamReq = new ViewStreamRequest(streamText.getText(), streamView);
            viewStreamReq.buildRequest(socket);
            viewStreamReq.sendRequest();
        });

        b_stopStream.setOnAction(e ->
        {
            viewStreamReq.stopRequest();
        });
    }
    public void show()
    {
        stage.show();
    }
}