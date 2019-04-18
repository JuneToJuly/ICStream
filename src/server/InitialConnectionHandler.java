package server;

import lib.LiveStream;
import lib.StreamSegment;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/*
All client connections first spawn this handler to
determine the type of client.
 */
public class InitialConnectionHandler implements Runnable
{
    private Socket returnSocket;
    private ConcurrentHashMap<String,Long> streamingClients;
    private ConcurrentHashMap<String,Long> watchingClients;
    private ConcurrentHashMap<String, LiveStream> liveStreams;
    private BlockingDeque<StreamSegment> currentWatchingStream;

    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    // Constructor, designate return socket and Server HashMaps
    public InitialConnectionHandler(Socket socket,
                                    ConcurrentHashMap<String,Long> streamingClients,
                                    ConcurrentHashMap<String,Long> watchingClients,
                                    ConcurrentHashMap<String, LiveStream> liveStreams)
    {
        this.returnSocket = socket;
        this.streamingClients = streamingClients;
        this.watchingClients = watchingClients;
        this.liveStreams = liveStreams;
        currentWatchingStream = new LinkedBlockingDeque<>();
    }

    @Override
    public void run()
    {
        System.out.println("Connection from Client received.");

        // Vars for type of client and unique identifier
        int clientType;
        String clientName;

        String streamerName;
        String viewerName;

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
                    // Client needs to know who's streaming before they can begin
                    case 100:
                        streamerName = clientName;
                        System.out.println("Checking if streamer name already exists.");
                        System.out.println("Name: " + streamerName);

                        if(streamingClients.containsKey(streamerName))
                        {
                            System.out.println(streamerName + " already viewing.");
                            dataOut.writeUTF("non-unique name");
                        }
                        else
                        {
                            System.out.println(streamerName + " is unique.");
                            streamingClients.put(streamerName, Thread.currentThread().getId());
                            dataOut.writeUTF("valid-name");
                        }
                        dataOut.flush();
                        break;

                    // Client would like to start Streaming
                    case 101:
                        streamerName = clientName;
                        System.out.println("Calling startStream()");
                        startStream(streamerName);
//                        if(!streamingClients.containsKey(streamerName))
//                        {
//                            // Add client name and ID to Server streamingMap
//                            System.out.println("Calling startStream()");
//                            startStream(streamerName);
//                        }
//                        else
//                        {
//                            // Client name already exists
//                            dataOut.writeUTF("Streamer with the same name is already active.");
//                            dataOut.flush();
//                        }
                        break;

                    // Client would like to know who is currently streaming
                    case 200:
                        viewerName = clientName;
                        System.out.println("Handling whoIsStreaming()");
                        System.out.println("Client name: " + viewerName);
                        // Fake viewer for testing purposes TODO comment later

                        // Make sure viewer name is unique
                        if(watchingClients.containsKey(viewerName))
                        {
                            System.out.println("Name already viewing.");
                            dataOut.writeUTF("non-unique name");
                            dataOut.flush();
                            break;
                        }

                        // Fake streamers for testing purposes TODO comment later
//                        streamingClients.put("Chris", Thread.currentThread().getId());
//                        streamingClients.put("Ian", Thread.currentThread().getId() + 1);
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
                        viewerName = clientName;
                        streamerName = dataIn.readUTF();
                        System.out.println("Handling viewStream()");
                        if(streamingClients.containsKey(streamerName))
                        {
                            // Add client name and ID to Server watchingMap
                            watchingClients.put(viewerName, Thread.currentThread().getId());
                            System.out.println("Calling watchStream(toWatch)");
                            watchStream(viewerName, streamerName);
                        }
                        else
                        {
                            // Stream isn't broadcasting anymore by the time client connected
                            dataOut.writeUTF("Streamer " + streamerName + " has ended their stream. Try someone else.");
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startStream(String name)
    {
        // New livestream
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
        catch (IOException e) { e.printStackTrace(); }

        // Stream now ready
        StreamSegment receivedSegment;
        while(!Thread.interrupted())
        {
            try
            {
                if(videoStream != null)
                {
                    System.out.println("Receiving segment...");
                    receivedSegment = (StreamSegment) videoStream.readObject();
                    System.out.println("Segment: " + receivedSegment.toString());
                    myStream.addSegment(receivedSegment);
                }
            }
            catch (IOException | ClassNotFoundException e) { break; }
        }
        liveStreams.remove(name);
    }

    private void watchStream(String viewerName, String streamerName)
    {
        System.out.println("watchStream() called for thread: " + Thread.currentThread().getId());
        // Get the stream
        LiveStream liveStream = liveStreams.get(streamerName);
        System.out.println("Setting up stream: " + viewerName + " watching " + streamerName);
        System.out.println("CurrentWatchingStream: " + currentWatchingStream.toString());
        liveStream.startViewing(viewerName, streamerName, currentWatchingStream);

        ObjectOutputStream videoStream = null;

        try
        {
            // Stream to send back to client
            videoStream = new ObjectOutputStream(returnSocket.getOutputStream());
            System.out.println("Opened socket back to viewer.");
        }
        catch (IOException e) { e.printStackTrace(); }

        while(!Thread.interrupted())
        {
            try
            {
                if(videoStream == null) break;

                // This blocks until their is a segment in the queue
                System.out.println("Blocking until segment is in queue...");
                videoStream.writeObject(currentWatchingStream.take());
                System.out.println("Sent segment to client.");
            }
            // Rethrow the interrupt
            catch (InterruptedException | IOException e) { Thread.currentThread().interrupt(); }
        }
    }
}
