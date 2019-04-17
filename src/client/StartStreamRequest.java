package client;

import client.interfaces.Request;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lib.FileSplitter;
import lib.Stream;
import lib.StreamSegment;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.*;

public class StartStreamRequest extends Request
{
    private String clientName;
    private String streamTitle;
    private int requestType;
    private File streamFile;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;


    public StartStreamRequest(String clientName, String streamTitle, File streamFile)
    {
        this.clientName = clientName;
        this.streamTitle = streamTitle;
        this.streamFile = streamFile;
        this.requestType = 100;
    }

    @Override
    public void buildRequest(Socket toSendSocket)
    {
        this.toSendSocket = toSendSocket;
    }

    @Override
    public void sendRequest()
    {
        try
        {
            System.out.println("Connecting to streaming server");
            toSendSocket.connect(streamingServer);
            System.out.println("Connection success");
            br  = new BufferedReader(new InputStreamReader(toSendSocket.getInputStream()));
            bw  = new BufferedWriter(new OutputStreamWriter(toSendSocket.getOutputStream()));

            dataOut = new DataOutputStream(toSendSocket.getOutputStream());
            dataIn = new DataInputStream(toSendSocket.getInputStream());
        }
        catch (IOException ex) { ex.printStackTrace(); }

        Runnable request = () ->
        {
            // JavaFx does a lot of stuff lazily, can't get media duration without using
            // the object
            MediaPlayer player = Stream.playerFromFile(streamFile);
            player.seek(Duration.millis(0));
            new MediaView(player);

            // Can't lambda it's not a functional interface
            Task<FileSplitter.SplitFile> splitFileTask = new Task<FileSplitter.SplitFile>()
            {
                @Override
                protected FileSplitter.SplitFile call() throws Exception
                {
                    return new FileSplitter.SplitFile(streamFile, player, Duration.seconds(2));
                }
            };

            // Split file done in background task
            // When player is ready, the media is considered ready
            player.setOnReady(() -> Executors.newSingleThreadExecutor().submit(splitFileTask));

            // Send over initial information and get confirmation
            String confirmation = "";
            ObjectOutputStream videoStream  = null;
            try
            {
                dataOut.writeInt(requestType);
                dataOut.writeUTF(clientName);
                dataOut.flush();
                // The object stream actually blocks on the other side
                videoStream = new ObjectOutputStream(toSendSocket.getOutputStream());

                while (!(confirmation.compareTo("ready") == 0) && !Thread.interrupted())
                {
                    confirmation = dataIn.readUTF();
                    if (confirmation.equals("ready"))
                    {
                        break;
                    }
                    else // Name not unique, need another name
                    {
                        String name = "";
                        ArrayBlockingQueue<String> nameQ = new ArrayBlockingQueue<String>(1);
                        Platform.runLater(() ->
                        {
                            try { nameQ.put(new TextInputDialog("Enter New Name").showAndWait().get()); }
                            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        });

                        // Blocks
                        try { nameQ.take(); }
                        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                        dataOut.writeInt(requestType);
                        dataOut.writeUTF(name);
                        dataOut.flush();
                    }
                }
            }
            catch (IOException e) { e.printStackTrace(); }

            FileSplitter.SplitFile splitFile = null;
            try
            {
                splitFile = splitFileTask.get(); // Get our split file from earlier future
            }
            catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }


            String prefix = splitFile.getSplitPrefix();
            for (int i = 0; i < splitFile.getSplitCount(); i++)
            {
                // send each split
                try
                {
                    videoStream.writeObject(new StreamSegment(new File("tmp_local/" + prefix + i + ".mp4"), i));
                    System.out.println("Successfully sent a segment");
                    Thread.sleep(1000);
                }
                catch (IOException | InterruptedException e) { e.printStackTrace(); Thread.currentThread().interrupt(); break; }
            }

        };
        new Thread(request).start();
    }


}
