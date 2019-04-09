package server;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamingServer
{
    private int port;

    // Constructor
    public StreamingServer(int port) throws IOException
    {
        this.port = port;
    }

    public void start()
    {
        try (DatagramSocket socket = new DatagramSocket(port))
        {
            ExecutorService threadPool = Executors.newFixedThreadPool(20);
            while(true)
            {
                try
                {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    System.out.println("Waiting for a connection");
                    socket.receive(packet);
                    System.out.println("Spawning new thread for: " + new String(packet.getData(), 0, packet.getLength()));
                    threadPool.execute(new SampleRequestHandler(socket, packet));
                }
                catch (IOException i)
                {
                    i.printStackTrace();
                }
            }
        }
        catch (SocketException s)
        {
            s.printStackTrace();
        }
    }
}
