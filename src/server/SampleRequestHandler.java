package server;

import client.interfaces.SampleRequest;
import server.interfaces.RequestHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SampleRequestHandler implements RequestHandler
{
    @Override
    public void handleRequest(Socket returnSocket)
    {
        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new OutputStreamWriter(returnSocket.getOutputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for(;;)
        {
            try
            {
                System.out.println("Writing data");
                bw.write("hello\n");
                bw.flush();
                Thread.sleep(5000);
            }
            catch (IOException e)
            {
                System.out.println("Socket closed, exiting thread.");
                break;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}
