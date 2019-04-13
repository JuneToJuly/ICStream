package client;

import client.interfaces.Request;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lib.FileSplitter;
import lib.Stream;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StartStreamRequest extends Request
{
    private String name;
    private String streamTitle;
    private File streamFile;

    public StartStreamRequest(String name, String streamTitle, File streamFile)
    {
        this.name = name;
        this.streamTitle = streamTitle;
        this.streamFile = streamFile;
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
            bw.write( name + ":" + streamTitle);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }




        Runnable receiveLoop = () ->
        {


            MediaPlayer player = Stream.playerFromFile(streamFile);

            // Can't lambda it's not a functional interface
            Task<FileSplitter.SplitFile> splitFileTask = new Task<FileSplitter.SplitFile>()
            {
                @Override
                protected FileSplitter.SplitFile call() throws Exception
                {
                    return new FileSplitter.SplitFile(streamFile, player, Duration.seconds(5));
                }
            };

            Executors.newSingleThreadExecutor().submit(splitFileTask);

            // Send over initial information and get confirmation
            String confirmation = "";
            System.out.println("Waiting for confirmation.");
            try
            {
                while (!confirmation.equals("ready") && !Thread.interrupted())
                {
                    confirmation = br.readLine();
                    if (confirmation.equals("ready"))
                    {
                        break;
                    }
                    // Get a new name
                    else
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

            // Split the file, a task is just a Future pretty much
            FileSplitter.SplitFile splitFile = null;
            try
            {
                splitFile = splitFileTask.get();
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }

            try
            {
                ObjectOutputStream videoSteam = new ObjectOutputStream(toSendSocket.getOutputStream());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String filename = splitFile.getSplitPrefix();
            for (int i = 0; i < splitFile.getSplitCount(); i++)
            {
                // send each split
                File segment = new File(filename + i + ".mp4");

                // wait to simulate stream fetch
            }

            try
            {
                toSendSocket.close();
                br.close();
                bw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        };
    }
}
