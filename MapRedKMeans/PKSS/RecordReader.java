package PKSS;

import java.io.IOException;
import org.apache.hadoop.mapred.SplitLocationInfo;

import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;

public class RecordReader<Key, Value> extends org.apache.hadoop.mapreduce.RecordReader<Key, Value>
{
    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public Key getCurrentKey()
        throws IOException, InterruptedException
    {
        return null;
    }

    @Override
    public Value getCurrentValue()
        throws IOException, InterruptedException
    {
        return null;
    }

    @Override
    public float getProgress()
        throws IOException, InterruptedException
    {
        return -1.0f;
    }

    @Override
    public void initialize(
            org.apache.hadoop.mapreduce.InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
        throws IOException, InterruptedException
    {
        Random random = new Random();
        Configuration conf = context.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        Path logDir = new Path("/pauls_log");
        if (!fs.exists(logDir))
            fs.mkdirs(logDir);
        FSDataOutputStream strm;
        int random_int = -1;
        while (random_int < 0)
            random_int = random.nextInt();
        Path logPath = new Path(logDir, Integer.toString(random_int));
        if (fs.exists(logPath))
            strm = fs.append(logPath);
        else
            strm = fs.create(logPath);
        strm.writeChars("*****\n");
        strm.writeChars("Split has length " + Long.toString(split.getLength()) + "\n");

        String[] locationNames = split.getLocations();
        strm.writeChars("There are " + Integer.toString(locationNames.length)+ " parts of the split\n");
        for (String n: locationNames)
        {
            strm.writeChars("Part of split is at: " + n + "\n");
        }
        strm.close();
        //SplitLocationInfo[] locations = split.getLocationInfo();
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        return false;
    }
}
