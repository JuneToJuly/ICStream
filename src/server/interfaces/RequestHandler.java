package server.interfaces;

import java.net.Socket;

public interface RequestHandler
{
    public void handleRequest(Socket returnSocket);
}
