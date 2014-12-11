package edu.jhu.bdslss.pkss;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import edu.jhu.bdslss.VectorizedObject;

public class TextRecordReader extends org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject>
{
    private org.apache.hadoop.mapreduce.lib.input.LineRecordReader innerReader;
    private VectorizedObject curObj;

    @Override
    public void close()
        throws IOException
    {
        if (innerReader != null)
            innerReader.close();
    }

    @Override
    public Long getCurrentKey()
        throws IOException, InterruptedException
    {
        if (curObj != null)
            return Long.parseLong(curObj.getKey());
        else
            return -1L;
    }

    @Override
    public VectorizedObject getCurrentValue()
        throws IOException, InterruptedException
    {
        return curObj;
    }

    @Override
    public float getProgress()
        throws IOException, InterruptedException
    {
        return innerReader.getProgress();
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        if (innerReader.nextKeyValue())
        {
            curObj = new VectorizedObject(innerReader.getCurrentValue().toString());
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
        throws IOException, InterruptedException
    {
        innerReader = new org.apache.hadoop.mapreduce.lib.input.LineRecordReader();
        innerReader.initialize(split, context);
    }
}
