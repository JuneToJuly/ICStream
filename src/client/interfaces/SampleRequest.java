package client.interfaces;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.*;

public class SampleRequest extends Request
{
    private TextArea dataOutput;

    // Constructor
    public SampleRequest(TextArea area) throws UnknownHostException
    {
        super();
        this.dataOutput = area;
        this.serverAddress = InetAddress.getByName("localhost");
        this.sendBuffer = new byte[256];
        this.recBuffer = new byte[65508];
        this.isConnected = false;
    }

    // Set up connection vars
    public void buildRequest(DatagramSocket serverSocket)
    {
        this.serverSocket = serverSocket;
        sendBuffer = "May I connect?".getBytes();
        this.sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, 4445);
    }

    // Attempt to send request to Server
    @Override
    public void sendRequest()
    {
        try
        {
            dataOutput.clear();
            System.out.println("Connecting to streaming server");
            serverSocket.send(sendPacket);
            System.out.println("Connection success");
//            isConnected = true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        Runnable receiveLoop = new Runnable()
        {
            @Override
            public void run()
            {
                DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                // While connection exists, listen for packets from server
                while(!Thread.interrupted()) {
                    System.out.println("Waiting for data.");
                    try
                    {
                        serverSocket.receive(recPacket);
                    }
                    catch (IOException i)
                    {
                        System.out.println("Client stopped. Send END packet.");
                        break;
                    }

                    // Extract data from sent packet, add to text field
                    String recData = new String(recPacket.getData(), 0, recPacket.getLength());
                    dataOutput.appendText(recData);
                }

                // TODO Send END-packet so server knows to stop sending count to this client
                // Connection broken, close down
                dataOutput.setText("Stream has ended.");
                serverSocket.close();
            }
        };

        receiveThread = new Thread(receiveLoop);
        receiveThread.start();
    }
}
