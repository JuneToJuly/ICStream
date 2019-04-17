package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class WatchClientLauncher extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        String[] arr = {"Chris", "Jack", "Tony", "Carl"};
        WatchClientDisplay display = new WatchClientDisplay(primaryStage, arr, "username");
        display.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
