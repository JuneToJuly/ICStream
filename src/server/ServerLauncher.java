package server;

public class ServerLauncher
{
    public static void main(String args[])
    {
        StreamingServer server = new StreamingServer();
        server.start();
    }
}
