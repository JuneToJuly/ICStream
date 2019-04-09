package client.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.*;

public abstract class Request
{
    protected InetAddress serverAddress;
    protected DatagramSocket serverSocket;
    protected DatagramPacket sendPacket;
    protected Thread receiveThread;
    protected byte[] sendBuffer, recBuffer;
    protected boolean isConnected;

    public abstract void buildRequest(DatagramSocket serverSocket);

    public abstract void sendRequest();

    public void stopRequest()
    {
        serverSocket.close();
    }
}
