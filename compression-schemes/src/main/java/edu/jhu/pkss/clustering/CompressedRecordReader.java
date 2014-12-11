package edu.jhu.pkss.clustering;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CompressedRecordReader extends org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject>
{
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
        FileSplit fsplit = (FileSplit)split;
        ByteBuffer buf = ByteBuffer.allocate(blockSize);
        
        Configuration conf = context.getConfiguration();
        final Path file = split.getFile();
        FileSystem fs = file.getFileSystem(conf);
        FSDataInputStream strm = fs.open(file);
        strm.seek(split.getStart());
        strm.read(buf);
        
        
    }
}
