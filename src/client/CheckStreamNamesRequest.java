package client;

import client.interfaces.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CheckStreamNamesRequest extends Request
{
    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private String response;

    public CheckStreamNamesRequest(String clientName)
    {
        this.clientName = clientName;
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
            response = dataIn.readUTF();
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

    // Required String return because Server must return
    // active streams back to the client
    public String getResponse()
    {
        return response;
    }
}
