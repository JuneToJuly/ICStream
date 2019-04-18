package client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.Socket;

public class LoginDisplay
{
    private Stage stage;
    private CurrentStreamsRequest curStreamReq;
    private CheckStreamNamesRequest checkStreamNamesRequest;
    private StartStreamRequest startStreamReq;

    public LoginDisplay(Stage stage)
    {
        this.stage = stage;
        buildDisplay();
    }

    private void buildDisplay()
    {
        stage.setTitle("ICStream - Login");

        Label label = new Label("Username: ");
        TextField textField = new TextField();
        HBox hBox1 = new HBox(10, label, textField);
        hBox1.setAlignment(Pos.CENTER);

//        Label label1 = new Label("Stream Name: ");
//        TextField textField1 = new TextField();
//        HBox hBox2 = new HBox(10, label1, textField1);
//        hBox2.setAlignment(Pos.CENTER);

        Button b_view = new Button("Watch");
        Button b_stream = new Button("Stream");
        HBox hBox3 = new HBox(20, b_view, b_stream);
        hBox3.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20, hBox1, hBox3);
        vBox.setAlignment(Pos.CENTER);

        addListeners(b_view, b_stream, textField);

        Scene scene = new Scene(vBox, 375, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void addListeners(Button b_view, Button b_stream, TextField t_name)
    {
        b_view.setOnAction(event ->
        {
            // Extract username from textfield
            String username = t_name.getText();

            // Configure alert if name was not entered
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Input Validation");
            alert1.setHeaderText(null);
            alert1.setContentText("Username is required to continue.");

            // Configure alert if name is not unique
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Non-Unique Name");
            alert2.setHeaderText(null);
            alert2.setContentText("The username you provided is already taken " +
                    "by a current user. Please try a different name.");

            // Make sure a name was entered.
            if(!username.isEmpty())
            {
                // Call request to get active streams
                Socket socket = new Socket();
                curStreamReq = new CurrentStreamsRequest(username);
                curStreamReq.buildRequest(socket);
                curStreamReq.sendRequest();
                String streamsStr = curStreamReq.getResponse();

                // Convert String into list of strings
                if(!streamsStr.isEmpty())
                {
                    // Check for non-unique name
                    if(streamsStr.equals("non-unique name"))
                    {
                        // They need to provide a different name and try again
                        alert2.showAndWait();
                    }
                    else
                    {
                        // Clean string provided by request "[name, name, name]" -> ["name", "name", ...]
                        String[] activeStreams = streamsStr
                                .substring(1, streamsStr.length() - 1)
                                .trim()
                                .split(",");

                        System.out.println("Currently active streams: ");
                        for(String s : activeStreams)
                        {
                            System.out.println(s);
                        }

                        // We just pass the current stage. The watch client will then
                        // just remove the current scene and add its own scene
                        WatchClientDisplay display = new WatchClientDisplay(stage, activeStreams, username);
                        display.show();
                    }
                }
                else
                {
                    System.out.println("Return String is empty... no active streamers.");
                }
            }
            else
            {
                // They need to provide a name to continue
                alert1.showAndWait();
            }
        });

        b_stream.setOnAction(event ->
        {
            // Get username from textfield
            String username = t_name.getText();

            // Configure alert if name was not entered
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Input Validation");
            alert1.setHeaderText(null);
            alert1.setContentText("Username is required to continue.");

            // Configure alert if name is not unique
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Non-Unique Name");
            alert2.setHeaderText(null);
            alert2.setContentText("The username you provided is already taken " +
                    "by a current user. Please try a different name.");

            // Make sure name was entered
            if(!username.isEmpty())
            {
                // Call request to get active streams
                Socket socket = new Socket();
                checkStreamNamesRequest= new CheckStreamNamesRequest(username);
                checkStreamNamesRequest.buildRequest(socket);
                checkStreamNamesRequest.sendRequest();
                String ans = checkStreamNamesRequest.getResponse();

                // Response will be unique or not
                if(!ans.isEmpty())
                {
                    if(ans.equals("non-unique name"))
                    {
                        // Name already streaming, try again
                        alert2.showAndWait();
                    } else if(ans.equals("valid-name"))
                    {
                        StreamClientDisplay display = new StreamClientDisplay(stage, username);
                        display.show();
                    }
                }
            }
            else
            {
                System.out.println("Return String is empty... no active streamers.");
            }
        });

//        b_stream.setOnAction(event ->
//        {
//            // Extract username from textfield
//            String username = t_name.getText();
//            String streamName = t_stream.getText();
//            FileChooser chooser = new FileChooser();
//            File streamFile = chooser.showOpenDialog(stage);
//
//            // Configure alert if name was not entered
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Input Validation");
//            alert.setHeaderText(null);
//            alert.setContentText("Username required to begin streaming.");
//
//            // Make sure a name was entered.
//            if(!username.isEmpty() && !streamName.isEmpty())
//            {
//                // Call request to start streaming
//                Socket socket = new Socket();
//                startStreamReq = new StartStreamRequest(username, streamName, streamFile);
//                startStreamReq.buildRequest(socket);
//                startStreamReq.sendRequest();
//            }
//            else
//            {
//                // They need to provide names to continue
//                alert.showAndWait();
//            }
//        });

    }

    public void show()
    {
        stage.show();
    }
}
