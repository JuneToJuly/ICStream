package client.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Request
{
    protected final InetSocketAddress streamingServer = new InetSocketAddress("localhost", 7878);
    protected Thread requestThread;
    protected Socket toSendSocket;
    protected BufferedReader br;
    protected BufferedWriter bw;
    protected int requestType;
    protected String clientName;

    public abstract void buildRequest(Socket toSendSocket);

    public abstract void sendRequest();

    public void stopRequest()
    {
        requestThread.interrupt();
    }
}