package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginLauncher extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        LoginDisplay display = new LoginDisplay(primaryStage);
        display.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
