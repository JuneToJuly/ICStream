package client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import lib.StreamView;

import javax.swing.plaf.basic.BasicViewportUI;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class WatchClientDisplay
{
    private Stage stage;
    private Scene scene;
    private ViewStreamRequest viewStreamReq;
    private String[] currentStreamers;
    private String username;

    public WatchClientDisplay(Stage stage, String[] currentStreamers, String username)
    {
        this.stage = stage;
        this.currentStreamers = currentStreamers;
        this.username = username;
        buildDisplay();
    }

    private void buildDisplay()
    {
        stage.setTitle("ICStream - Viewer Dashboard");

        // List all streamers as clickable, plus Watch button
        ToggleGroup radioGroup = new ToggleGroup();
        RadioButton radioArray[] = new RadioButton[currentStreamers.length];
        for(int i = 0; i < currentStreamers.length; i++) {
            RadioButton rButton = new RadioButton(currentStreamers[i]);
            rButton.setToggleGroup(radioGroup);
            radioArray[i] = rButton;
        }

        Label sideBarLabel = new Label("Streamers");
        sideBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        HBox hBox1 = new HBox(sideBarLabel);
        VBox vBox2 = new VBox(10, radioArray);
        Button viewButton = new Button("View");
        Button streamButton = new Button("Stream");

        VBox sideBar = new VBox(20, hBox1, vBox2, viewButton, streamButton);
        sideBar.setAlignment(Pos.TOP_LEFT);

        StreamView streamView = new StreamView();
        VBox viewArea = new VBox(streamView);

        Label ws1 = new Label("   ");
        Label ws2 = new Label("        ");

        HBox hBox = new HBox(ws1, sideBar, ws2, viewArea);

        addListeners(viewButton, streamButton, radioGroup, streamView);
        scene = new Scene(hBox, 850, 400);
        stage.setScene(scene);
        stage.show();
//        FlowPane pane = new FlowPane();
//        TextField streamText = new TextField("Streamer to Watch");
//        TextField clientNameText = new TextField("Client Name");
//        Button b_startStream = new Button("Start Stream");
//        Button b_stopStream = new Button("Stop Stream");
//

//        pane.getChildren().addAll(clientNameText, b_startStream, streamText, b_stopStream, streamView);
//

//        scene = new Scene(pane, 500, 240);
//        stage.setScene(scene);
//        stage.setOnCloseRequest(event -> Platform.exit());

    }

    private void addListeners(Button viewButton, Button streamButton, ToggleGroup radioGroup, StreamView streamView)
    {
        viewButton.setOnAction(e ->
        {
            RadioButton button = (RadioButton) radioGroup.getSelectedToggle();


            Socket socket = new Socket();
            viewStreamReq = new ViewStreamRequest(username, button.getText(), streamView);
            viewStreamReq.buildRequest(socket);
            System.out.println("Sending view request to server.");
            viewStreamReq.sendRequest();
        });

//        streamButton.setOnAction(e ->
//        {
//            viewStreamReq.stopRequest();
//        });
    }
    public void show()
    {
        stage.show();
    }
}