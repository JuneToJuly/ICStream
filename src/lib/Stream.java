package lib;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Stream
{
    /**
     Convenience method for getting a player from a file.
     @param sourceFile file to get
     @return MediaPlayer with file as source
     */
    public static MediaPlayer playerFromFile(File sourceFile)
    {
        return new MediaPlayer(new Media(sourceFile.toURI().toString()));
    }
}
