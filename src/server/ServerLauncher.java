package server;

import java.io.IOException;

public class ServerLauncher
{
    public static void main(String args[]) throws IOException
    {
        StreamingServer server = new StreamingServer();
        server.start();
    }
}
