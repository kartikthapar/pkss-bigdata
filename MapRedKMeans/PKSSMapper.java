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

public class PKSSMapper extends Mapper<LongWritable, Text, LongWritable, Text>
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
        Path src = new Path(conf.get("clusterInput"));
        FSDataInputStream fs = dfs.open(src);
        BufferedReader myReader = new BufferedReader(new InputStreamReader(fs));

        oldClusters = new java.util.HashMap<Long, VectorizedObject>();
        // and now we read it in, just like in the code that runs on a single machine
        String cur = myReader.readLine();
        for (long counter = 0; cur != null; counter += 1)
        {
            VectorizedObject temp = new VectorizedObject(cur);
            oldClusters.put(counter, temp);
            VectorizedObject newCluster = temp.copy();
            cur = myReader.readLine();
        }
    }

    // For now, the key is the number of bytes from the beginning of the file
    // value is the contents of that line of text
    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException
    {
        VectorizedObject obj = new VectorizedObject(value.toString());
        SparseDoubleVector data_point = obj.getLocation();

        double minDist = -1;
        int closestIndex = -1;
        for (int i = 0; i < oldClusters.size(); ++i)
        {
            SparseDoubleVector clusterCenter = oldClusters.get(i).getLocation();
            double distance = clusterCenter.distance(data_point);
            if (minDist < 0 || distance < minDist)
            {
                minDist = distance;
                closestIndex = i;
            }
        }

        context.write(new LongWritable(closestIndex), value);
    }
}
