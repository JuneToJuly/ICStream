package client;

import client.interfaces.Request;

import java.io.*;
import java.net.Socket;

public class CurrentStreamsRequest extends Request
{
    // DataStreams for exchanging preliminary information between C/S
    // Global BufferedReaders + Writers will be used for actual stream
    private DataOutputStream dataOut;
    private DataInputStream dataIn;

    public CurrentStreamsRequest(String clientName)
    {
        this.clientName = clientName;
    }

    @Override
    public void buildRequest(Socket toSendSocket)
    {
        this.toSendSocket = toSendSocket;
        this.requestType = 200;
    }

    @Override
    public void sendRequest()
    {
        try
        {
            // First connect to client
            toSendSocket.connect(streamingServer);

            // Initialize I/O streams
            dataOut = new DataOutputStream(toSendSocket.getOutputStream());
            dataIn = new DataInputStream(toSendSocket.getInputStream());

            // Send requestID and clientName to server
            dataOut.writeInt(requestType);
            dataOut.writeUTF(clientName);
            dataOut.flush();

            // Wait for server to respond
            System.out.println("Waiting for server response...");

            // Blocks until dataIn is populated
            String response = dataIn.readUTF();
            System.out.println("Response from server: " + response);

            // Shut everything down, the request has been satisfied
            toSendSocket.close();
            dataIn.close();
            dataOut.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
