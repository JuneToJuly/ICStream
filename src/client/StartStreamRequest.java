package client;

import client.interfaces.Request;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lib.FileSplitter;
import lib.Stream;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

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
            // Send over initial information and get confirmation
            char confirmation = 'n';
            System.out.println("Waiting for confirmation.");
            try
            {
                while (confirmation != 'c' && !Thread.interrupted())
                {
                    confirmation = (char) br.read();
                    if (confirmation == 'c')
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

            // Confirmation has been given
            // Split file, could have done this earlier for performance
            MediaPlayer player = Stream.playerFromFile(streamFile);
            FileSplitter.SplitFile splitFile = new FileSplitter.SplitFile(streamFile, player, Duration.seconds(2));

            for (int i = 0; i < splitFile.getSplitCount(); i++)
            {
                // send each split
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
