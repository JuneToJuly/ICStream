package lib.test;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lib.Constants;
import lib.FileSplitter;

import java.io.File;

public class SplitTest extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Button butt = new Button("SplitFile");
        File file = new File(Constants.SPLITTEST_FILE);
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);

        Scene scene = new Scene(new StackPane(new MediaView(player), butt), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        // So, according to javadoc media is asynchronous which means media objects aren't ready until their status
        // is ready. We need them to be ready because we need to know how long their are for splitting purposes
        Task<FileSplitter.SplitFile> task = new Task<FileSplitter.SplitFile>()
        {
            @Override
            protected FileSplitter.SplitFile call() throws Exception
            {
                return new FileSplitter.SplitFile(file, player, Duration.seconds(2));
            }
        };

        butt.setOnAction(e -> new Thread(task).start());
        // Really could do this instead to automatically split
        player.setOnReady(() -> System.out.println("Duration should be ready"));
        task.setOnSucceeded(e -> System.out.println("Finished"));
    }
}
