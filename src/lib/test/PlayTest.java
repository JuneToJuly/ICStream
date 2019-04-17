package lib.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lib.Constants;
import lib.Stream;
import lib.StreamView;

import java.io.File;

public class PlayTest extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        StreamView view = new StreamView();

        File file = new File(Constants.PLAYTEST_TEST0);
        File file1 = new File(Constants.PLAYTEST_TEST1);
        File file2 = new File(Constants.PLAYTEST_TEST2);
        File file3 = new File(Constants.PLAYTEST_TEST3);
        File file4 = new File(Constants.PLAYTEST_TEST4);

        view.queueMediaPlayer(Stream.playerFromFile(file));
        view.queueMediaPlayer(Stream.playerFromFile(file1));
        view.queueMediaPlayer(Stream.playerFromFile(file2));
        view.queueMediaPlayer(Stream.playerFromFile(file3));
        view.queueMediaPlayer(Stream.playerFromFile(file4));

        Scene scene = new Scene(new Group(view), view.getMediaPlayer().getMedia().getWidth(), view.getMediaPlayer().getMedia().getHeight());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.show();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
