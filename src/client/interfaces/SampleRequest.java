package client.interfaces;

import javafx.scene.control.TextArea;
import java.io.*;
import java.net.Socket;

public class SampleRequest extends Request
{
    private TextArea dataOutput;

    public SampleRequest(TextArea area)
    {
        super();
        this.dataOutput = area;
    }

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
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        // Converted run() to lambda model
        Runnable receiveLoop = () ->
        {
            dataOutput.clear();
            String newLines = "";
            while(!Thread.interrupted())
            {
                System.out.println("Waiting for data");
                try
                {
                    newLines += br.readLine();
                    System.out.println("Got new lines: " +  newLines);
                    dataOutput.setText(newLines);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                dataOutput.setText("Stream over, thanks for viewing.");
                toSendSocket.close();
                br.close();
                bw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        };

        requestThread = new Thread(receiveLoop);
        requestThread.start();
    }
}