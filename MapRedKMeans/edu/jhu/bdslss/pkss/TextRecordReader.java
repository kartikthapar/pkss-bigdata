package edu.jhu.bdslss.pkss;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import edu.jhu.bdslss.VectorizedObject;

public class RecordReader extends org.apache.hadoop.mapreduce.RecordReader<LongWritable, VectorizedObject>
{
    private long startOffset;
    private long totalByteCount;
    private long readByteCount;
    private FSDataInputStream fileInput;
    private FileSystem fs;
    private ByteBuffer buf;
    private VectorizedObject current_value;

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public LongWritable getCurrentKey()
        throws IOException, InterruptedException
    {
        if (current_value == null)
            return null;
        else
            return current_value.getKey();
    }

    @Override
    public VectorizedObject getCurrentValue()
        throws IOException, InterruptedException
    {
        return current_value;
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
        FileSplit fsplit = (FileSplit)split;
        startOffset = fsplit.getStart();
        totalByteCount = fsplit.getLength();
        readByteCount = 0;
        
        fs = FileSystem.get(context.getConfiguration());
        fileInput = fs.open(fsplit.getPath());
        buf = ByteBuffer.allocate(totalByteCount);
        fileInput.read(startOffset, buf, 0, totalByteCount);
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        if (readByteCount >= totalByteCount)
            return false;
        // Do something intelligent or buffering or something
        // read a line from the file
        boolean found_newline = false;
        String line = "";
        do {
            long starting_point = readByteCount;
            long end_index;
            for (end_index = starting_point; buf.get(end_index) != '\n' && end_index < totalByteCount; ++end_index)
            { }
            line += buf.slice(starting_point, end_index).asCharBuffer().toString();
            
            if (buf.get(end_index) != '\n') 
        } while (!found_newline);
        
        // if we hit the end of the buffer and there was no newline, then we
        // need to keep reading in order to find the end of the record
        // FIXME we should probably not read an entire new buffer...
        fileInput.read(startOffset + totalByteCount, buf, 0, totalByteCount);

        current_value = new VectorizedObject(line);

        return true;
    }
}
