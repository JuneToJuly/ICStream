package client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.Socket;
import java.util.Optional;

public class StreamClientDisplay
{
    private Stage stage;
    private Scene scene;
    private StartStreamRequest startStreamRequest;

    public StreamClientDisplay(Stage stage)
    {
        this.stage = stage;
        buildDisplay();
    }

    private void buildDisplay()
    {
        FlowPane pane = new FlowPane();

        Button b_startStream = new Button("Start Streaming");
        FileChooser chooser = new FileChooser();
        Button b_stopStream = new Button("Stop Stream");

        pane.getChildren().addAll(b_startStream, b_stopStream);

        addListeners(b_startStream, b_stopStream, chooser);
        scene = new Scene(pane);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    private void addListeners(Button b_startStream, Button b_stopStream, FileChooser chooser)
    {
        b_startStream.setOnAction(e ->
        {
            // Choose a file for stream
            File chosenFile = chooser.showOpenDialog(stage);
            TextInputDialog name = new TextInputDialog("Streamer Name");
            Optional<String> streamerName = name.showAndWait();
            TextInputDialog title = new TextInputDialog("Stream Title");
            Optional<String> streamTitle = title.showAndWait();

            Socket socket = new Socket();
            startStreamRequest = new StartStreamRequest(streamerName.get(), streamTitle.get(), chosenFile);
            startStreamRequest.buildRequest(socket);
            startStreamRequest.sendRequest();
        });

        b_stopStream.setOnAction(e ->
        {
            startStreamRequest.stopRequest();
        });
    }

    public void show()
    {
        stage.show();
    }
}
