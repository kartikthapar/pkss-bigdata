package edu.jhu.pkss.clustering;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import nayuki.arithcode.AdaptiveArithmeticImpl;
import edu.jhu.pkss.compression.OurLz4Impl;
import org.apache.tools.bzip2.*;

public class PKSSReducer extends Reducer<LongWritable, Text, LongWritable, Text>
{
    // This is the directory containing one file per cluster
    // Each file contains the list of IDs of the data points
    // assigned to that cluster
    private org.apache.hadoop.fs.Path assignment_dir;
    private Configuration conf;
    private long currentBlockAmount;
    private int currentNumElements;
    private long blockSize;   
    private StringBuilder currentData;
 
    public static final String ASSIGNMENT_OUTPUT_DIR_KEY = "assignmentOutput";
    public static final String ASSIGNMENT_OUTPUT_KEY = "writeAssignments";

    @Override
    protected void setup(Context context)
    {
        conf = context.getConfiguration();
        blockSize = TextInputFormat.getMaxSplitSize(context);

        //TODO set an initial capacity
        currentData = new StringBuilder();
        assignment_dir = new org.apache.hadoop.fs.Path(conf.get(ASSIGNMENT_OUTPUT_DIR_KEY));
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context)
        throws java.io.IOException, InterruptedException
    {
        // These are the intermediate results of computing the new cluster center

        boolean writeAssignments = context.getConfiguration().getBoolean(ASSIGNMENT_OUTPUT_KEY, true);

        FSDataOutputStream assign_strm = null;
        if (writeAssignments)
        {
            // These are used for storing the cluster assignments
            // TODO make the write optional depending on configuration
            FileSystem fs = FileSystem.get(context.getConfiguration());
            Path cluster_output = new Path(assignment_dir, "Cluster"+key.toString());
            assign_strm = fs.create(cluster_output);
        }

        processInputData(assign_strm, key, Value, context, writeAssignments); 
    }


    private void processInputData(FSDataOutputStream assign_strm, LongWritable key, Iterable<Text> Value, Context context, boolean writeAssignments) throws IOException, InterruptedException{
        long counter = 0;
        VectorizedObject thisCluster = null;       
 
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

                if (writeAssignments)
                {
                    curDataPoint.setValue(key.toString());

                    if (needToCreateNewBlock(curDataPoint.writeOut())) {
                        writeCompressedBytes(assign_strm);
                        resetVariables();
                    }  
                        updateData(curDataPoint.writeOut()); 
                }
            }


        if (writeAssignments)
        {
            if (currentData.length() > 0) {
                writeCompressedBytes(assign_strm);
            }
            
            assign_strm.close();
        }

        thisCluster.getLocation().multiplyMyselfByHim(1.0 / counter);
        Text outputBuffer = new Text();
        outputBuffer.set(thisCluster.writeOut());
        context.write(key, outputBuffer);
    }

   private void updateData(String datapoint) {
        currentData.append(datapoint + "\n");
        currentBlockAmount += datapoint.length() + 1;
        currentNumElements++;        
   }


   private void resetVariables() {
        currentBlockAmount = 0;
        currentNumElements = 0;
        currentData = new StringBuilder();
    }

    private boolean needToCreateNewBlock(String data) {
        return currentBlockAmount + data.length() + 1 > blockSize;
    }

    //Delimiter is newline (\n)
    private void writeCompressedBytes(FSDataOutputStream stream) { 
    		byte[] compressed = new byte[(int)blockSize];

            try {
                stream.writeLong(currentBlockAmount);
                stream.writeInt(currentNumElements); 

                //may need to be slightly refactored
                switch(conf.get("compression")) {
                    case "arith":
                        AdaptiveArithmeticImpl arith = new AdaptiveArithmeticImpl();
                        compressed = arith.compress(currentData.toString().getBytes("UTF-8"));
                        stream.write(compressed, 0, compressed.length);
			break;
                    case "lz4":
                        OurLz4Impl lz = new OurLz4Impl();
                        compressed = lz.compress(currentData.toString().getBytes("UTF-8"));
                        stream.write(compressed, 0, compressed.length); 
                        break;

                    case "bzip2":
                        //TODO complete me
                        break;        
                }
            } catch(UnsupportedEncodingException e) {
    			e.printStackTrace();
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
	}
}
