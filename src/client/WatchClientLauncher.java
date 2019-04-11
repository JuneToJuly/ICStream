package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class WatchClientLauncher extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        WatchClientDisplay display = new WatchClientDisplay(primaryStage);
        display.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
