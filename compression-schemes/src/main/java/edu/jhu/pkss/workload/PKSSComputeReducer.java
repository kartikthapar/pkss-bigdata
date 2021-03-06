package edu.jhu.pkss.workload;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.util.ArrayList; 
import java.util.Random;

import edu.jhu.pkss.clustering.VectorizedObject;
import edu.jhu.pkss.clustering.SparseDoubleVector;

public class PKSSComputeReducer extends Reducer<LongWritable, Text, LongWritable, Text>
{
    ArrayList<Integer> computeLocations = new ArrayList<Integer>();

    public PKSSComputeReducer()
    {
        Random random = new Random();
        int i = 0;
        while (i > 0)
        {
            // generate a random number in the range 0...1023 and add it to the list of computation indexes
            i--;
        }
    }

    private double computeRoutine(SparseDoubleVector location)
    {
        // apply some function to the location here
        ArrayList<Integer> computeLocations = new ArrayList<Integer>();
        computeLocations.add(1);
        computeLocations.add(2);
        
        double sum = 0.0;
        for (Integer locationIndex: computeLocations)
        {
            sum = sum + location.getItem(locationIndex);
        }
        return sum;
        //return location.getItem(1) + location.getItem(2);

        /*       
        // 1. Just add duh!
        double sum = 0.0;
        for (Integer locationIndex : computeLocations)
        {
            double locationValue = location.getItem(locationIndex);
            sum = sum + locationValue;
        }
        return sum;
        */
    }

    @Override
    protected void setup(Context context)
    {
        return;
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context) throws java.io.IOException, InterruptedException
    {
        String result = "key: " + key + " value: ";
        for (Text currentText: Value)
        {
            VectorizedObject currentDataPoint = new VectorizedObject(currentText.toString());
            result = result + " " + computeRoutine(currentDataPoint.getLocation());
        }

        Text outputBuffer = new Text();
        outputBuffer.set(result);
        context.write(key, outputBuffer);
    }
}
