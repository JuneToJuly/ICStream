package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamingServer
{
    private ServerSocket serverSocket;

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
        try
        {
            System.out.println("Waiting for a connection");
            // Arbitrarily 20 threads for now, probably enough for demo
            ExecutorService threadPool = Executors.newFixedThreadPool(20);
            while(true) {
                threadPool.execute(new SampleRequestHandler(serverSocket.accept()));
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}