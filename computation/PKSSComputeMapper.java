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

public class PKSSComputeMapper extends Mapper<LongWritable, Text, LongWritable, Text>
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
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
    {
        // DataPoint
        VectorizedObject obj = new VectorizedObject(value.toString());

        // get location
        SparseDoubleVector data_point = obj.getLocation();

        // get value
        String clusterID = obj.getValue();

        context.write(new LongWritable(clusterID), value);
    }
}
