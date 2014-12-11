import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.util.ArrayList; 
import java.util.Random;

public class PKSSComputeReducer extends Reducer<LongWritable, Text, LongWritable, Text>
{
    ArrayList<Integer> computeLocations = new ArrayList<Integer>();
    int numberOfComputeLocations;
    int maxLocations;

    public static final String ASSIGNMENT_OUTPUT_DIR_KEY = "assignmentOutput";
    public static final String ASSIGNMENT_OUTPUT_KEY = "writeAssignments";

    public PKSSComputeReducer()
    {
        numberOfComputeLocations = 10;
        maxLocations = 1024;

        Random random = new Random();
        int i = 0;
        while (i > 0)
        {
            // generate a random number in the range 0...1023 and add it to the list of computation indexes
            computeLocations.add(random.nextInt(maxLocations));
            i--;
        }
    }

    private void computeRoutine(SparseDoubleVector location)
    {
        // apply some function to the location here
        
        // 1. Just add duh!
        double sum;
        for (Integer locationIndex : computeLocations)
        {
            double locationValue = location.get(locationIndex);
            sum = sum + locationValue;
        }
    }

    @Override
    protected void setup(Context context)
    {
        return;
    }

    @Override
    public void reduce (Context context) throws java.io.IOException, InterruptedException
    {
        VectorizedObject thisCluster = null;

        for (Text currentText: value)
        {
            VectorizedObject currentDataPoint = new VectorizedObject(currentText.toString());
            computeRoutine(currentDataPoint.getLocation());
        }
    }
}
