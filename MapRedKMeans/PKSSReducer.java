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

    public static final String ASSIGNMENT_OUTPUT_DIR_KEY = "assignmentOutput";
    public static final String ASSIGNMENT_OUTPUT_KEY = "writeAssignments";

    @Override
    protected void setup(Context context)
    {
        Configuration conf = context.getConfiguration();
        assignment_dir = new org.apache.hadoop.fs.Path(conf.get(ASSIGNMENT_OUTPUT_DIR_KEY));
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context)
        throws java.io.IOException, InterruptedException
    {
        // These are the intermediate results of computing the new cluster center
        long counter = 0;

        VectorizedObject thisCluster = null;

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

        //TODO should probably set a capacity here!
        List<String> data = new ArrayList<>();


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
                data.add(curDataPoint.writeOut());
            }
        }


        if (writeAssignments)
        {
        	writeCompressedBytes(assign_strm, data, context);
            assign_strm.close();
        }

        thisCluster.getLocation().multiplyMyselfByHim(1.0 / counter);

        Text outputBuffer = new Text();
        outputBuffer.set(thisCluster.writeOut());
        context.write(key, outputBuffer);
    }

    //Delimiter is newline (\n)
    private void writeCompressedBytes(FSDataOutputStream stream, List<String> data, Context context) {
    	 //maximum number of bytes per split (we're making this the max num)
        long blockSize = TextInputFormat.getMaxInputSplitSize(context);

    	while (!data.isEmpty()) {
    		long bytesBeforeCompression = 0;
    		int numberOfElements = 0;

    		//TODO should probably set a capacity here, too
    		StringBuilder sb = new StringBuilder();

    		while (bytesBeforeCompression < blockSize) {
    			String current = data.get(0);

    			if (bytesBeforeCompression + current.length() <= blockSize) {
    				sb.append(data.remove(0) + "\n");
    				//+1 for newline character
    				bytesBeforeCompression += current.length() + 1;
    				numberOfElements++;
    			}
    		}

    		String block = Long.toString(bytesBeforeCompression) + "\n" + Integer.toString(numberOfElements) + "\n" + sb.toString();  
    		byte[] temp = block.getBytes("UTF-8");

    		//TODO ACTUALLY COMPRESS
    		stream.write(temp, 0, temp.length);
    	}
    }
}
