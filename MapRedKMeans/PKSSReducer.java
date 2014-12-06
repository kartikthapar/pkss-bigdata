import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PKSSReducer extends Reducer<LongWritable, Text, LongWritable, Text>
{
    // This is the directory containing one file per cluster
    // Each file contains the list of IDs of the data points
    // assigned to that cluster
    private org.apache.hadoop.fs.Path assignment_dir;

    @Override
    protected void setup(Context context)
    {
        Configuration conf = context.getConfiguration();
        // FIXME make the string a symbolic constant
        assignment_dir = new org.apache.hadoop.fs.Path(conf.get ("assignmentOutput"));
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context)
        throws java.io.IOException, InterruptedException
    {
        // These are the intermediate results of computing the new cluster center
        long counter = 0;
        VectorizedObject thisCluster = null;

        // These are used for storing the cluster assignments
        // TODO make the write optional depending on configuration
        FileSystem fs = FileSystem.get(context.getConfiguration());
        Path cluster_output = new Path(assignment_dir, key.toString());
        FSDataOutputStream assign_strm = fs.create(cluster_output);

        for (Text curText : Value)
        {
            VectorizedObject curDataPoint = new VectorizedObject(curText.toString());
            if (thisCluster == null) // FIXME Paul doesn't really like doing this check on every iteration
            {
                thisCluster = curDataPoint;
            }
            else
            {
                curDataPoint.getLocation().addMyselfToHim(thisCluster.getLocation());
            }

            counter += 1;

            assign_strm.writeChars(curDataPoint.getKey().toString());
            assign_strm.writeChar('\n');
        }
        assign_strm.close();

        thisCluster.getLocation().multiplyMyselfByHim(1.0 / counter);

        Text outputBuffer = new Text();
        outputBuffer.set(thisCluster.writeOut());
        context.write(key, outputBuffer);
    }
}
