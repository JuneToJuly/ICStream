package server;

import lib.LiveStream;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamingServer
{
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String,Long> streamingClients;
    private ConcurrentHashMap<String,Long> watchingClients;
    private ConcurrentHashMap<String, LiveStream> liveStreams;

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
            // TODO Correctly fill out String toWatch
            String toWatch = "temp";
            streamingClients = new ConcurrentHashMap<>();
            watchingClients = new ConcurrentHashMap<>();
            liveStreams = new ConcurrentHashMap<>();
            System.out.println("Waiting for a connection");

            // Arbitrarily 20 threads for now, probably enough for demo
            ExecutorService threadPool = Executors.newFixedThreadPool(20);
            while(true) {
                // As soon as communication is received, send info to Handler thread
                // Include concurrent maps so thread can modify them
                threadPool.execute(new InitialConnectionHandler(serverSocket.accept(), streamingClients, watchingClients, liveStreams));
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}