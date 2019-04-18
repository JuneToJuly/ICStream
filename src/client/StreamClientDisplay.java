package client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.Socket;
import java.util.Optional;

public class StreamClientDisplay
{
    private Stage stage;
    private String username;
    private StartStreamRequest startStreamRequest;

    public StreamClientDisplay(Stage stage, String username)
    {
        this.stage = stage;
        this.username = username;
        buildDisplay();
    }

    private void buildDisplay()
    {
        stage.setTitle("ICStream - Streamer Dashboard");

        Label streamerName = new Label(username);
        streamerName.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        HBox hBox0 = new HBox(streamerName);
        hBox0.setAlignment(Pos.CENTER);

        Label title = new Label("Stream Title:");
        TextField titleInput = new TextField();
        HBox hBox1 = new HBox(10, title, titleInput);
        hBox1.setAlignment(Pos.CENTER);

        Button b_start = new Button("Start");
        Button b_stop = new Button("Stop");
        HBox hBox2 = new HBox(10, b_start, b_stop);
        hBox2.setAlignment(Pos.CENTER);

        Label status = new Label("--");

        VBox vBox = new VBox(20, hBox0, hBox1, hBox2, status);
        vBox.setAlignment(Pos.CENTER);

        FileChooser chooser = new FileChooser();

        addListeners(b_start, b_stop, titleInput, chooser, status);

        Scene scene = new Scene(vBox, 350, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void addListeners(Button b_startStream,
                              Button b_stopStream,
                              TextField textField,
                              FileChooser chooser,
                              Label status)
    {
        b_startStream.setOnAction(e ->
        {
            // Set status label
            status.setText("Streaming...");

            // Choose a file for stream
            File chosenFile = chooser.showOpenDialog(stage);
//            TextInputDialog name = new TextInputDialog("Streamer Name");
//            Optional<String> streamerName = name.showAndWait();
//            TextInputDialog title = new TextInputDialog("Stream Title");
            String titleName = textField.getText();

            Socket socket = new Socket();
            startStreamRequest = new StartStreamRequest(username, titleName, chosenFile);
            startStreamRequest.buildRequest(socket);
            startStreamRequest.sendRequest();
        });

        b_stopStream.setOnAction(e ->
        {
            status.setText("Stream stopped.");
            startStreamRequest.stopRequest();
        });
    }

    public void show()
    {
        stage.show();
    }
}
