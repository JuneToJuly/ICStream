package lib;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 We want a stream view to be able to access a stream's live stream.
 We provide this interface to the viewing client which allows them to register
 as a viewer.
 */
public class LiveStream
{
    // Only one thread will be updating these values
    private Deque<StreamSegment> segments;

    // Register viewers, if we have a ton of viewers might as well just synchronize the array
    // Always have a correct copy when we send out a segment though.
    private CopyOnWriteArrayList<BlockingDeque<StreamSegment>> viewers;

    public LiveStream()
    {
        segments = new ArrayDeque<>();
        viewers = new CopyOnWriteArrayList<>();
    }

    /**
     Viewers will join the livestream view this method call. The simply add their blocking
     queue to this stream. When a segment becomes available, we will put in all queues
     @param viewer
     */
    public void startViewing(BlockingDeque<StreamSegment> viewer)
    {
        viewers.add(viewer);
    }

    /**
     This is intended only for the streamer...but I'm too lazy to enforce this.
     @param newSegment
     */
    public synchronized void addSegment(StreamSegment newSegment)
    {
        segments.add(newSegment);
        notifyViewers();
    }

    private void notifyViewers()
    {
        // For each viewer pass them the new current segment
        for (BlockingDeque<StreamSegment> viewer: viewers)
        {
            if(segments.size() <= 1)
            {
                // We wan't to build up a buffer of about 1
                break;
            }
            else
            {
                // Immutable object is passed and this is fine for concurrent access
                viewer.add(segments.poll());
            }
        }
    }
}
