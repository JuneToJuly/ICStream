package server;

import client.interfaces.SampleRequest;
import javafx.scene.chart.XYChart;
import server.interfaces.RequestHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class SampleRequestHandler implements Runnable
{
    private DatagramSocket socket;
    private DatagramPacket packet;

    // Constructor
    SampleRequestHandler(DatagramSocket socket, DatagramPacket packet) throws IOException
    {
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    public void run()
    {
        System.out.println("Executing run().");
        int count = 0;
        try
        {
            while(true) {
                // Extract data from request
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                // Create data to send to client
                String str = Integer.toString(count) + " ";
                byte[] payload = str.getBytes();

                // Send packet to client
                DatagramPacket sendPacket = new DatagramPacket(payload, payload.length, address, port);
                socket.send(sendPacket);
                count++;
                Thread.sleep(2000);
            }
        }
        catch (IOException e)
        {
            System.out.println("Socket closed, exiting thread.");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
