package PKSS;

import java.io.IOException;

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
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        return false;
    }
}
