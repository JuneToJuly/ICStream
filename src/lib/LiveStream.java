package lib;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 We want a stream view to be able to access a stream's live stream.
 We provide this interface to the viewing client which allows them to register
 as a viewer.
 */
public class LiveStream
{
    // Yeah this is stupid but for now it is fine
    // Only one thread will be updating these values
    AtomicReference<StreamSegment> currentSegment;
    StreamSegment nextSegment;
    StreamSegment nextNextSegment;
    // Register viewers
    CopyOnWriteArrayList<Viewer> viewers = new CopyOnWriteArrayList<Viewer>();

    /**
     Viewers will join the livestream view this method call.
     They should implement the #lib.Viewer interface
     @param viewer
     */
    public void startViewing(Viewer viewer)
    {
        viewers.add(viewer);
    }

    /**
     This is intended only for the streamer...but I'm too lazy to enforce this.
     @param newSegment
     */
    public synchronized void addSegment(StreamSegment newSegment)
    {
        currentSegment.set(nextSegment);
        nextSegment = nextNextSegment;
        nextNextSegment = newSegment;

        notifyViewers();

    }

    private void notifyViewers()
    {
        // For each viewer pass them the new current segment
        for (Viewer viewer: viewers)
        {
            // Immutable object is passed that is fine for concurrent access
            viewer.liveStreamUpdate(currentSegment.get());
        }
    }
}
