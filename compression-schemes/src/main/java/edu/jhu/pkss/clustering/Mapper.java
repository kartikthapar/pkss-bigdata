package edu.jhu.pkss.clustering;

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

public class Mapper extends org.apache.hadoop.mapreduce.Mapper<Long, VectorizedObject, LongWritable, Text>
{
    private Map<Long, VectorizedObject> oldClusters;

    // This method is taken from the starter code we downloaded
    // this is called to set up the mapper... it basically just reads the clusters file into memory
    @Override
    protected void setup (Context context) throws IOException, InterruptedException
    {
        // first we open up the clusters file
        Configuration conf = context.getConfiguration();
        FileSystem dfs = FileSystem.get(conf);

        // if we can't find it in the configuration, then die
        if (conf.get("clusterInput") == null)
        {
            throw new RuntimeException("no cluster file!");
        }

        // create a BufferedReader to open up the cluster file
        oldClusters = KMeans.ReadClusterCenters(dfs, conf.get("clusterInput"));
    }

    // For now, the key is the number of bytes from the beginning of the file
    // value is the contents of that line of text
    @Override
    public void map(Long key, VectorizedObject obj, Context context)
        throws IOException, InterruptedException
    {
        SparseDoubleVector data_point = obj.getLocation();

        double minDist = -1;
        long closestIndex = -1;
        for (long i = 0; i < oldClusters.size(); ++i)
        {
            SparseDoubleVector clusterCenter = oldClusters.get(i).getLocation();
            double distance = clusterCenter.distance(data_point);
            if (minDist < 0 || distance < minDist)
            {
                minDist = distance;
                closestIndex = i;
            }
        }

        context.write(new LongWritable(closestIndex), new Text(obj.writeOut()));
    }
}
