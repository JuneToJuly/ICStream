package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SampleRequestHandler implements Runnable
{
    private Socket returnSocket;

    SampleRequestHandler(Socket socket) {
        this.returnSocket = socket;
    }

    @Override
    public void run()
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

        int count = 0;
        for(;;)
        {
            try
            {
                System.out.println("Writing data");
                bw.write(count + " \n");
                bw.flush();
                Thread.sleep(1500);
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
            count++;
        }

    }
}