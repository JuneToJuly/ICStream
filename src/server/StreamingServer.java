package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StreamingServer
{
    ServerSocket serverSocket;

    public StreamingServer()
    {
        try
        {
            serverSocket = new ServerSocket(7878, 100, InetAddress.getByName("localhost"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void start()
    {
        Socket sock = null;
        BufferedWriter bw  = null;
        try
        {
            System.out.println("Waiting for a connection");
            sock = serverSocket.accept();
            new SampleRequestHandler().handleRequest(sock);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
