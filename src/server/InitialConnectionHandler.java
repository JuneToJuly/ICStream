package server;

import lib.LiveStream;
import lib.StreamSegment;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/*
All client connections first spawn this handler to
determine the type of client.
 */
public class InitialConnectionHandler implements Runnable
{
    private Socket returnSocket;
    private String toWatch;
    private String streamTitle;
    private ConcurrentHashMap<String,Long> streamingClients;
    private ConcurrentHashMap<String,Long> watchingClients;

    // Pair is streamer name, and stream title
    private ConcurrentHashMap<String, LiveStream> liveStreams;

    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    // Constructor, designate return socket and Server HashMaps
    public InitialConnectionHandler(Socket socket,
                                    ConcurrentHashMap<String,Long> streamingClients,
                                    ConcurrentHashMap<String,Long> watchingClients,
                                    ConcurrentHashMap<String, LiveStream> liveStreams,
                                    String toWatch)
    {
        this.returnSocket = socket;
        this.toWatch = toWatch;
        this.streamingClients = streamingClients;
        this.watchingClients = watchingClients;
        this.liveStreams = liveStreams;
    }

    @Override
    public void run()
    {
        System.out.println("Connection from Client received.");

        // Vars for type of client and unique identifier
        int clientType;
        String clientName;

        try
        {
            // Receive client type and name
            dataIn = new DataInputStream(returnSocket.getInputStream());
            clientType = dataIn.readInt();
            clientName = dataIn.readUTF();

            System.out.println("Client type: " + clientType);
            System.out.println("Message: " + clientName);

            // Execute based on Client type
            // Modify server maps accordingly
            if(!clientName.isEmpty())
            {
                dataOut = new DataOutputStream(returnSocket.getOutputStream());
                switch(clientType)
                {
                    // Client would like to start Streaming
                    case 100:
                        System.out.println("Handling streamStart()");
                        if(!streamingClients.containsKey(clientName))
                        {
                            // Add client name and ID to Server streamingMap
                            streamingClients.put(clientName, Thread.currentThread().getId());
                            System.out.println("Calling startStream()");
                            startStream(clientName);
                        }
                        else
                        {
                            // Client name already exists
                            dataOut.writeUTF("Streamer with the same name is already active.");
                            dataOut.flush();
                        }
                        break;

                    // Client would like to know who is currently streaming
                    case 200:
                        System.out.println("Handling whoIsStreaming()");
                        System.out.println("Client name: " + clientName);
                        // Fake viewer for testing purposes TODO comment later
                        watchingClients.put("Alexis", Thread.currentThread().getId());

                        // Make sure viewer name is unique
                        if(watchingClients.containsKey(clientName))
                        {
                            System.out.println("Name already viewing.");
                            dataOut.writeUTF("non-unique name");
                            dataOut.flush();
                            break;
                        }

                        // Fake streamers for testing purposes TODO comment later
                        streamingClients.put("Chris", Thread.currentThread().getId());
                        streamingClients.put("Ian", Thread.currentThread().getId() + 1);
                        if(!streamingClients.isEmpty())
                        {
                            System.out.println("The following streamers are active: " +
                                    streamingClients.keySet().toString());

                            // Sends list of streamers to client in form:
                            // "[<streamer1>, <streamer2>, ..., <streamerN>]"
                            dataOut.writeUTF(streamingClients.keySet().toString());
                        }
                        else
                        {
                            System.out.println("No streamers currently - writing to buffer.");
                            dataOut.writeUTF("No streamers currently active.");
                        }
                        dataOut.flush();
                        break;

                    // Client would like to view a specific stream
                    case 201:
                        System.out.println("Handling viewStream()");
                        if(!streamingClients.containsKey(toWatch))
                        {
                            // Add client name and ID to Server watchingMap
                            watchingClients.put(clientName, Thread.currentThread().getId());
                            System.out.println("Calling watchStream(toWatch)");
                            // TODO - write watchStream(toWatch)
                            // watchStream(toWatch);
                        }
                        else
                        {
                            // Stream isn't broadcasting anymore by the time client connected
                            dataOut.writeUTF("Streamer " + toWatch + " has ended their stream. Try someone else.");
                            dataOut.flush();
                        }
                        break;

                    default:
                        System.out.println("Incorrect client code: " +
                                clientType +
                                ". Should be 100, 200, or 201.");
                        dataOut.writeUTF("Incorrect client code: " +
                                clientType +
                                ". Should be 100, 200, or 201.");
                        break;
                }
            }
            else
            {
                // Error with client name, it's empty
                System.out.println("Error in extracting ClientName. Try again.");
                dataOut.writeUTF("Error in extracting ClientName. Try again.");
            }

            dataOut.close();
            dataIn.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startStream(String name)
    {
        LiveStream myStream = new LiveStream();

        // Concurrent HashMap guarantees happens-before relationship for safe-publication
        liveStreams.put(name, myStream);

        ObjectInputStream videoStream = null;
        try
        {
            // Need to create stream for serialization, this blocks waiting for header
            videoStream = new ObjectInputStream(returnSocket.getInputStream());
            dataOut.writeUTF("ready");
            dataOut.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Stream now ready
        StreamSegment receivedSegment;
        while(!Thread.interrupted())
        {
            try
            {
                if(videoStream != null)
                {
                    receivedSegment = (StreamSegment) videoStream.readObject();
                    myStream.addSegment(receivedSegment);
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void watchStream(String streamName)
    {

    }
}
