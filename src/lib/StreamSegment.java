package lib;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.io.File;
import java.io.Serializable;

@Immutable
/**
 The keyword just says that we meet the specification here: http://jcip.net.s3-website-us-east-1.amazonaws.com/annotations/doc/index.html

 The streaming client should create a segment, send it to the server, to which it is synchronized for the live stream.
 The server will eventually send it to all clients.
 */
public final class StreamSegment implements Serializable
{
    private final File segment;

    public StreamSegment(File segment)
    {
        this.segment = segment;
    }

    public File getSegment()
    {
        return segment;
    }
}
