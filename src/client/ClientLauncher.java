package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class ClientLauncher extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ClientDisplay display = new ClientDisplay(primaryStage);
        display.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
