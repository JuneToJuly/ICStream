package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class StreamClientLauncher extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        StreamClientDisplay display = new StreamClientDisplay(primaryStage);
        display.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
