package lib;


import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;

/**
 This class is ugly. I had it as a simple static call but for some reason, the task api doesn't let you
 invoke static methods?...Didn't look into it too much, it works.
 */
public class FileSplitter
{
    public static String FFMPEG_PATH = "K:\\Downloads\\ffmpeg-20190410-4d2f621-win64-static\\ffmpeg-20190410-4d2f621-win64-static\\bin\\ffmpeg.exe";

    public static SplitFile splitFile(File toSplit, MediaPlayer player, Duration timePerSplit)
    {
        return new SplitFile(toSplit, player, timePerSplit);
    }

    public static Duration calculateTimePerSplit(double duration, double splitsWanted)
    {
        return new Duration(Math.ceil(duration / splitsWanted));
    }

    public static class SplitFile
    {

        public SplitFile(File toSplit, MediaPlayer player, Duration timePerSplit)
        {
            this.setSplitPrefix(toSplit.getName().substring(0, toSplit.getName().indexOf(".")));
            player.seek(Duration.millis(0));

            this.setDuration(player.getMedia().getDuration().toSeconds());

            // Need to take ceil for odd splits
            this.setSplitCount((int) Math.ceil(this.getDuration() / timePerSplit.toSeconds()));

            for(int i = 0; i < this.getSplitCount(); i++)
            {
                // Start split
                String seekTo = String.format("%02d:%02d:%02d", (int) (i * timePerSplit.toSeconds()) / 3600,
                        (int) (((i * timePerSplit.toSeconds()) % 3600) / 60),
                        (int) (i * timePerSplit.toSeconds()) % 60);

                // Finish split at
                String finish = String.format("%02d:%02d:%02d", (int) ((i+1) * timePerSplit.toSeconds()) / 3600,
                        (int) ((((i+1) * timePerSplit.toSeconds()) % 3600) / 60),
                        (int) ((i+1) * timePerSplit.toSeconds()) % 60);


                // Cmd for splitting
                String[] cmd = {
                        FFMPEG_PATH,
                        "-y",
                        "-i", toSplit.getAbsolutePath(),
                        "-ss", seekTo,
                        "-to", finish,
                        this.getSplitPrefix() + i + ".mp4"
                };

                // On the final split, we need to only split the last part off
                if(i+1 == this.getSplitCount())
                {
                    cmd = new String[]{
                            FFMPEG_PATH,
                            "-y",
                            "-i", toSplit.getAbsolutePath(),
                            "-ss", seekTo,
                            this.getSplitPrefix() + i + ".mp4"
                    };
                }

                Process p = null;
                try
                {
                    p = Runtime.getRuntime().exec(cmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    // Need to read line for some reason or it blocks...
                    String beep = "";
                    while((beep = reader.readLine()) != null) System.out.println(beep);
                    p.waitFor();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println("split done");
            }
        }

        private File originalFile;
        private double duration;
        private int splitCount;
        private String splitPrefix;

        public double getDuration()
        {
            return duration;
        }

        public void setDuration(double duration)
        {
            this.duration = duration;
        }

        public File getOriginalFile()
        {
            return originalFile;
        }

        public void setOriginalFile(File originalFile)
        {
            this.originalFile = originalFile;
        }

        public int getSplitCount()
        {
            return splitCount;
        }

        public void setSplitCount(int splitCount)
        {
            this.splitCount = splitCount;
        }

        public String getSplitPrefix()
        {
            return splitPrefix;
        }

        public void setSplitPrefix(String splitPrefix)
        {
            this.splitPrefix = splitPrefix;
        }
    }
}
