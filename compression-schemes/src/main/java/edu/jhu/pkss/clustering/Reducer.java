package edu.jhu.pkss.clustering;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.nio.ByteBuffer;

import edu.jhu.pkss.compression.AdaptiveArithmeticImpl;
import edu.jhu.pkss.compression.BitBuffer;
import edu.jhu.pkss.compression.CompressionScheme;
import edu.jhu.pkss.compression.Compressor;

public class Reducer extends org.apache.hadoop.mapreduce.Reducer<LongWritable, Text, LongWritable, Text>
{
    // This is the directory containing one file per cluster
    // Each file contains the list of IDs of the data points
    // assigned to that cluster
    private org.apache.hadoop.fs.Path assignment_dir;
    private Configuration conf;
    private long currentBlockAmount;
    private int currentNumElements;
    private long blockSize;
    private BitBuffer bitBuffer;
    private CompressionScheme scheme;
    private Compressor compressor;
    private boolean writeAssignments;

    public static final String ASSIGNMENT_OUTPUT_DIR_KEY = "assignmentOutput";
    public static final String ASSIGNMENT_OUTPUT_KEY = "writeAssignments";
    public static final short REPLICATION_FACTOR = 1;

    @Override
    protected void setup(Context context)
    {
        conf = context.getConfiguration();
        blockSize = TextInputFormat.getMaxSplitSize(context);

        assignment_dir = new org.apache.hadoop.fs.Path(conf.get(ASSIGNMENT_OUTPUT_DIR_KEY));

        writeAssignments = context.getConfiguration().getBoolean(ASSIGNMENT_OUTPUT_KEY, true);

        if (writeAssignments)
        {
            bitBuffer = new BitBuffer(ByteBuffer.allocate((int)blockSize));

            switch(conf.get("compression")) {
                case "arith":
                    scheme = new AdaptiveArithmeticImpl();
                    break;
                case "lz4":
                    // TODO bring LZ4 into the new compressionscheme
                    //OurLz4Impl lz = new OurLz4Impl();
                    //compressed = lz.compress(currentData.toString().getBytes("UTF-8"));
                    break;
                case "bzip2":
                    //TODO complete me
                    break;
            }
            compressor = scheme.newCompressor(bitBuffer);
        }
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context)
        throws java.io.IOException, InterruptedException
    {
        long counter = 0;
        VectorizedObject thisCluster = null;

        FSDataOutputStream assign_strm;
        if (writeAssignments)
        {
            FileSystem fs = FileSystem.get(context.getConfiguration());
            Path cluster_output = new Path(assignment_dir, "Cluster"+key.toString());
            assign_strm = fs.create(cluster_output, REPLICATION_FACTOR);
        }

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

                // Try to write this data point into the block.
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
    private void writeCompressedBytes(FSDataOutputStream stream)
        throws IOException
    {
        byte[] compressed = null;// = new byte[(int)blockSize];
        //may need to be slightly refactored
        stream.writeInt(compressed.length);
        stream.writeLong(currentBlockAmount);
        stream.writeInt(currentNumElements);
        stream.write(compressed, 0, compressed.length);
        // FIXME this number is the length of the header
        for (int i = compressed.length + 16; i < blockSize; ++i)
            stream.write(0);
	}
}
