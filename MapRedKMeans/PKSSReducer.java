import org.apache.hadoop.conf.Configuration;
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
        assignment_dir = new org.apache.hadoop.fs.Path(conf.get ("clusterInput"));
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context) throws java.io.IOException, InterruptedException
    {
        long counter = 0;
        VectorizedObject thisCluster = null;
        for (Text curText : Value)
        {
            VectorizedObject curDataPoint = new VectorizedObject(curText.toString());
            if (thisCluster == null)
            {
                thisCluster = curDataPoint;
            }
            else
            {
                curDataPoint.getLocation().addMyselfToHim(thisCluster.getLocation());
            }

            counter += 1;
        }

        thisCluster.getLocation().multiplyMyselfByHim(1.0 / counter);

        Text outputBuffer = new Text();
        outputBuffer.set(thisCluster.writeOut());
        context.write(key, outputBuffer);
    }
}
