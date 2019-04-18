package client;

import client.interfaces.Request;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import lib.StreamSegment;
import lib.StreamView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ViewStreamRequest extends Request
{
    private String viewerName;
    private String streamerName;
    private DataOutputStream dataOut;
    private int requestType;
    private ObjectInputStream videoStream;
    private StreamView streamView;

    public ViewStreamRequest(String viewerName, String streamerName, StreamView view)
    {
        this.viewerName = viewerName;
        this.streamerName = streamerName;
        this.streamView = view;
        requestType = 201;
    }

    @Override
    public void buildRequest(Socket toSendSocket)
    {
        this.toSendSocket = toSendSocket;
    }

    @Override
    public void sendRequest()
    {

        Runnable requestRunnable = () ->
        {
            try
            {
                // Connect to server, open communication stream
                toSendSocket.connect(streamingServer);
                dataOut = new DataOutputStream(toSendSocket.getOutputStream());

                // Send the server your clientType (WATCH), name, and who you want to watch
                System.out.println("View Request - requestType: " + requestType);
                dataOut.writeInt(requestType);
                System.out.println("View Request - viewerName: " + viewerName);
                dataOut.writeUTF(viewerName);
                System.out.println("View Request - streamerName: " + streamerName);
                dataOut.writeUTF(streamerName);
                dataOut.flush();

                videoStream = new ObjectInputStream(toSendSocket.getInputStream());

                while(!Thread.interrupted())
                {
                    // Read segment from stream
                    StreamSegment segment = (StreamSegment) videoStream.readObject();
                    // Pass viewer's name so correct directory is made
                    segment.setViewerName(viewerName);
                    Platform.runLater(() ->
                    {
                        // Write new segment file
                        MediaPlayer player = segment.getSegment();
                        streamView.queueMediaPlayer(player);
                    });
                }
            }
            catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        };

        requestThread = new Thread(requestRunnable);
        requestThread.start();
    }
}
