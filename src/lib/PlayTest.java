package lib;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class PlayTest extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        StreamView view = new StreamView();

//        File file = new File("K:/git/ICStream/resources/sample_video/3.mp4");
//        File file1 = new File("K:/git/ICStream/resources/sample_video/4.mp4");
//        File file2 = new File("K:/git/ICStream/resources/sample_video/2.mp4");
        File file = new File("resources/sample_video/3.mp4");
        File file1 = new File("resources/sample_video/2.mp4");
        File file2 = new File("resources/sample_video/1.mp4");

        view.queueMediaPlayer(Stream.playerFromFile(file));
        view.queueMediaPlayer(Stream.playerFromFile(file1));
        view.queueMediaPlayer(Stream.playerFromFile(file2));

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
