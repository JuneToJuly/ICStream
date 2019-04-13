package client;

import client.interfaces.Request;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import lib.FileSplitter;
import lib.Stream;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

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
                    return new FileSplitter.SplitFile(streamFile, player, Duration.seconds(5));
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
                        TextInputDialog dialog = new TextInputDialog();
                        Optional<String> newName = null;
                        while (newName == null || !newName.isPresent())
                        {
                            newName = dialog.showAndWait();
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            FileSplitter.SplitFile splitFile = null;
            try
            {
                splitFile = splitFileTask.get(); // Get our split file from earlier future
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }

            // Currently stops here, handshake is working
            if(splitFile != null)
            {
                return;
            }

            String prefix = splitFile.getSplitPrefix();
            for (int i = 0; i < splitFile.getSplitCount(); i++)
            {
                // send each split
                File segment = new File(prefix + i + ".mp4");

                // wait to simulate stream fetch
            }

            try
            {
                toSendSocket.close();
                br.close();
                bw.close();
                dataIn.close();
                dataOut.close();
                videoStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        };
        new Thread(request).start();
    }


}
