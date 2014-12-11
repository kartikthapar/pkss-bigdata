package edu.jhu.pkss.workload;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PKSSComputeMapper extends Mapper<Long, VectorizedObject, LongWritable, Text>
{
    private Map<Long, VectorizedObject> clusters;

    @Override
    protected void setup (Context context) throws IOException, InterruptedException
    {
        return;
    }

    // For now, the key is the number of bytes from the beginning of the file
    // value is the contents of that line of text
    @Override
    public void map(Long key, VectorizedObject value, Context context) throws IOException, InterruptedException
    {
        // get value
        long clusterID = Long.valueOf(value.getValue()).longValue();
        context.write(new LongWritable(clusterID), new Text(value.writeOut()));
    }
}
