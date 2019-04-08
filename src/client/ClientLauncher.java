package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ClientLauncher extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setScene(new Scene(new ScrollPane(), 500, 500));
        primaryStage.show();
    }
}
