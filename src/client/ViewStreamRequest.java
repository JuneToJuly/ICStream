package client;

import client.interfaces.Request;
import javafx.application.Platform;
import lib.StreamSegment;
import lib.StreamView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ViewStreamRequest extends Request
{
    private String streamName;
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private int reqestType;
    private ObjectInputStream videoStream;
    private StreamView streamView;

    public ViewStreamRequest(String streamName, StreamView view)
    {
        this.streamName = streamName;
        this.streamView = view;
        reqestType = 201;
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
                toSendSocket.connect(streamingServer);

                dataOut = new DataOutputStream(toSendSocket.getOutputStream());

                dataOut.writeInt(reqestType);
                dataOut.writeUTF(streamName);

                videoStream = new ObjectInputStream(toSendSocket.getInputStream());

                while(!Thread.interrupted())
                {

                    StreamSegment segment = (StreamSegment) videoStream.readObject();
                    System.out.println("Got a segement!");

                    Platform.runLater(() ->
                    {
                        streamView.queueMediaPlayer(segment.getSegment());
                    });
                }
            }
            catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
        };

        requestThread = new Thread(requestRunnable);
        requestThread.start();
    }
}
