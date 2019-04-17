package lib;

import javafx.scene.media.MediaPlayer;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Immutable
/**
 The keyword just says that we meet the specification here: http://jcip.net.s3-website-us-east-1.amazonaws.com/annotations/doc/index.html

 The streaming client should create a segment, send it to the server, to which it is synchronized for the live stream.
 The server will eventually send it to all clients.
 */
public final class StreamSegment implements Serializable
{
    private final byte[] segment;
    private final File file;
    private int segCount;
    private String viewerName;

    /**
        We simply read the whole file into our buffer.
     */
    public StreamSegment(File file, int count)
    {
        this.file = file;
        long size = file.length();
        InputStream source;
        this.segment = new byte[(int)size];
        this.segCount = count;

        int nread;
        int next;

        if (size > Integer.MAX_VALUE) {
            System.out.println("File size too large");
        }

        try
        {
            source = new FileInputStream(file);
            for (next = 0; next < segment.length; next += nread) {
                nread = source.read(segment, next, segment.length - next);
                if (nread < 0) {
                    System.out.println("Failed to read");
                }
            }
            if (source.read() != -1) {
                System.out.println("Failed to read");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     Call this for getting a file as a stream segment.
     @return MediaPlayer representing that segment
     */
    public MediaPlayer getSegment()
    {
        try
        {
            // Create a new directory for segments this client/thread receives
            File outDir = new File(Constants.VIEWFILES_PATH + viewerName);
            if(!outDir.exists()) outDir.mkdirs();

            // Write new file for each segment to this directory
            File outFile = new File(outDir + "\\out_" + segCount + ".mp4");
            Files.write(Paths.get(outFile.getPath()), segment);
            System.out.println("Writing new file: " + outDir.toString() + "\\out_" + segCount + ".mp4");
        }
        catch (IOException e) { e.printStackTrace(); }

        return Stream.playerFromFile(file);
    }

    // Simple setter so the segment knows where it's going
    public void setViewerName(String viewerName) {
        this.viewerName = viewerName;
    }
}
