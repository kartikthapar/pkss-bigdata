package edu.jhu.pkss.clustering;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import nayuki.arithcode.AdaptiveArithmeticImpl;

public class CompressedRecordReader extends org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject>
{
    private long decompressed_size;
    private long bytes_read;
    private int objects_returned;
    private int number_of_objects;
    private VectorizedObject curObj;
    private byte[] byte_data;

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public Long getCurrentKey()
        throws IOException, InterruptedException
    {
        if (!curObj)
            return null;
        else
            return Long.parseLong(curObj.getKey());
    }

    @Override
    public VectorizedObject getCurrentValue()
        throws IOException, InterruptedException
    {
        return cur_obj;
    }

    @Override
    public float getProgress()
        throws IOException, InterruptedException
    {
        return ((float) objects_returned) / number_of_objects;
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        if (objects_returned == number_of_objects)
            return false;
        long end_index;
        for (end_index = bytes_read; end_index < byte_data.length && byte_data[end_index] != '\n'; ++end_index)
        { }
        String string_data = new String(byte_data, bytes_read, end_index - bytes_read);
        curObj = new VectorizedObject(string_data);
        return true;
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
        throws IOException, InterruptedException
    {
        FileSplit fsplit = (FileSplit)split;
        ByteBuffer buf = ByteBuffer.allocate((int)fsplit.getLength());
        
        Configuration conf = context.getConfiguration();
        final Path file = fsplit.getPath();
        FileSystem fs = file.getFileSystem(conf);
        FSDataInputStream strm = fs.open(file);
        strm.seek(fsplit.getStart());
        strm.read(buf);
        strm.close();
        
        decompressed_size = buf.getLong();
        objects_returned = 0;
        number_of_objects = buf.getInt();
        bytes_read = 0;
        
        AdaptiveArithmeticImpl decompressor = new AdaptiveArithmeticImpl();
        byte_data = decompressor.decompress(buf.slice().array());
    }
}
