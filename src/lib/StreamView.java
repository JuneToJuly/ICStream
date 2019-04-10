package lib;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;

public class StreamView extends MediaView
{
    private Deque<MediaPlayer> mediaPlayers;

    public StreamView()
    {
        mediaPlayers = new ArrayDeque<>();
    }
    public void queueMediaPlayer(MediaPlayer player)
    {
        // When a player has finished, it needs to play the next one.
        player.setOnEndOfMedia(this::playNext);

        // Prep the player, reading the source from disk.
        player.seek(Duration.millis(0));
        mediaPlayers.add(player);

        // If we are the first media player, we need to start ourself
        if(mediaPlayers.size() == 1)
        {
            this.setMediaPlayer(player);
            player.play();
        }

    }
    public void playNext()
    {
        // Dequeue finished media player
        mediaPlayers.poll();
        MediaPlayer nextPlayer = mediaPlayers.peek();

        // We have no more players
        if(nextPlayer != null)
        {
            this.setMediaPlayer(nextPlayer);
            nextPlayer.play();
        }

    }

}
