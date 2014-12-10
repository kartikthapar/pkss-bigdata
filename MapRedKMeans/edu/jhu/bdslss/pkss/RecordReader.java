package edu.jhu.bdslss.pkss;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import edu.jhu.bdslss.VectorizedObject;

import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;

public class RecordReader extends org.apache.hadoop.mapreduce.RecordReader<LongWritable, VectorizedObject>
{
    private long totalByteCount;
    private long readByteCount;
    private FSDataInputStream fileInput;

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public LongWritable getCurrentKey()
        throws IOException, InterruptedException
    {
        return null;
    }

    @Override
    public VectorizedObject getCurrentValue()
        throws IOException, InterruptedException
    {
        return null;
    }

    @Override
    public float getProgress()
        throws IOException, InterruptedException
    {
        return ((float)readByteCount) / (float)totalByteCount;
    }

    @Override
    public void initialize(
            org.apache.hadoop.mapreduce.InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
        throws IOException, InterruptedException
    {
        /*Random random = new Random();
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
        strm.writeChars("Split is of type " + split.getClass().toString() + "\n");
        strm.writeChars(split.toString() + "\n");
        strm.writeChars("Split has length " + Long.toString(split.getLength()) + "\n");

        String[] locationNames = split.getLocations();
        strm.writeChars("There are " + Integer.toString(locationNames.length)+ " parts of the split\n");
        for (String n: locationNames)
        {
            strm.writeChars("Part of split is at: " + n + "\n");
        }
        strm.close();*/

        FileSplit fsplit = (FileSplit)split;
        totalByteCount = fsplit.getLength();
        readByteCount = 0;
        
        FileSystem fs = FileSystem.get(context.getConfiguration());
        FSDataInputStream fileInput = fs.open(fsplit.getPath());
        
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        return false;
    }
}
