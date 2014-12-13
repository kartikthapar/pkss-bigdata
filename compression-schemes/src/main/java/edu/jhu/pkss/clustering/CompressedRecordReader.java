package edu.jhu.pkss.clustering;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import edu.jhu.pkss.compression.AdaptiveArithmeticImpl;

public class CompressedRecordReader extends org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject>
{
    private long decompressed_size;
    private int bytes_read;
    private int objects_returned;
    private int number_of_objects;
    private VectorizedObject curObj;
    private byte[] byte_data;
    private String string_data;

    private Writer output;

    @Override
    public void close()
        throws IOException
    {
        if (output != null)
            output.close();
    }

    @Override
    public Long getCurrentKey()
        throws IOException, InterruptedException
    {
        if (curObj == null)
            return null;
        else
            return Long.parseLong(curObj.getKey());
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
        return ((float) objects_returned) / number_of_objects;
    }

    @Override
    public boolean nextKeyValue()
        throws IOException, InterruptedException
    {
        if (objects_returned == number_of_objects)
            return false;
        output.write("Starting scan for newline at index " + Integer.toString(bytes_read) + "\n");
        int end_index;
        //for (end_index = bytes_read; end_index < string_data.length() && string_data.charAt(end_index) != '\n'; ++end_index)
        for (end_index = bytes_read; end_index < byte_data.length && byte_data[end_index] != '\n'; ++end_index)
        { }
        
        //String string_vector = string_data.substring(bytes_read, end_index - bytes_read);
        String string_vector = new String(byte_data, bytes_read, end_index - bytes_read);
        output.write("Found end of line at " + Integer.toString(end_index) + ", contents = " + string_vector + "\n");
        curObj = new VectorizedObject(string_vector);
        bytes_read = end_index + 1;
        objects_returned += 1;
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
        buf.rewind();
        
        Path log_path = new Path(fsplit.getPath().toString() + "_log");
        FSDataOutputStream outputFile;
        if (fs.exists(log_path))
            outputFile = fs.append(log_path);
        else
            outputFile = fs.create(log_path, (short)1);
        output = new OutputStreamWriter(outputFile);

        int compressed_size = buf.getInt();
        decompressed_size = buf.getLong();
        objects_returned = 0;
        number_of_objects = buf.getInt();
        bytes_read = 0;
        
        output.write("Header - decompressed size: " + Long.toString(decompressed_size) + ", number_of_objects: " + Integer.toString(number_of_objects) + "\n");
        
        AdaptiveArithmeticImpl decompressor = new AdaptiveArithmeticImpl();
        byte[] compressed_data = new byte[compressed_size];
        buf.get(compressed_data);
        byte_data = decompressor.decompress(compressed_data);
        string_data = new String(byte_data, Charset.forName("UTF-8"));
    }
}
