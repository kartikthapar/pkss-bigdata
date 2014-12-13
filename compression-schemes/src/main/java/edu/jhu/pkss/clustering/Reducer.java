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
    private long currentUncompressedBytes;
    private int currentNumElements;
    private long blockSize;
    private BitBuffer bitBuffer;
    private CompressionScheme scheme;
    private Compressor compressor;
    private boolean writeAssignments;
    private FSDataOutputStream assign_strm;

    public static final String ASSIGNMENT_OUTPUT_DIR_KEY = "assignmentOutput";
    public static final String ASSIGNMENT_OUTPUT_KEY = "writeAssignments";
    public static final short REPLICATION_FACTOR = 1;
    public static final int HEADER_SIZE = 16;

    @Override
    protected void setup(Context context)
    {
        conf = context.getConfiguration();
        blockSize = TextInputFormat.getMaxSplitSize(context);

        assignment_dir = new org.apache.hadoop.fs.Path(conf.get(ASSIGNMENT_OUTPUT_DIR_KEY));

        writeAssignments = context.getConfiguration().getBoolean(ASSIGNMENT_OUTPUT_KEY, true);

        if (writeAssignments)
        {
            bitBuffer = new BitBuffer(ByteBuffer.allocate((int)blockSize - HEADER_SIZE));

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

    private static byte[] bytesForData(VectorizedObject obj)
        throws IOException
    {
        return obj.writeOut().getBytes("UTF-8");
    }

    @Override
    public void reduce(LongWritable key, Iterable<Text> Value, Context context)
        throws java.io.IOException, InterruptedException
    {
        long counter = 0;
        VectorizedObject thisCluster = null;

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
                byte[] currentBytes = bytesForData(curDataPoint);
                BitBuffer.Marker cleanMark = bitBuffer.mark();
                Compressor cleanCompressor = compressor.clone();
                try {
                    compressor.compress(currentBytes);

                    BitBuffer.Marker nextMark = bitBuffer.mark();
                    Compressor nextCompressor = compressor.clone();

                    // Make sure that we have enough room to close the stream
                    // within the block before committing to putting this
                    // data point in the stream
                    compressor.finish();

                    // If that succeeded, rewind to before the end of stream marker
                    bitBuffer.rewind(nextMark);
                    compressor = nextCompressor;
                }
                catch (java.nio.BufferOverflowException e)
                {
                    // Ran out of space in this block, so flush it out and try again
                    bitBuffer.rewind(cleanMark);
                    cleanCompressor.finish();
                    ByteBuffer byteBuffer = bitBuffer.getBuffer();
                    for (int idx = byteBuffer.position(); idx < byteBuffer.limit(); ++idx)
                    {
                        byteBuffer.put((byte)0);
                    }
                    writeBuffer();

                    bitBuffer.rewind();
                    compressor = scheme.newCompressor(bitBuffer);
                    resetVariables();
                    // Trying again
                    compressor.compress(currentBytes);

                    // If this one fails, then that means that the compressed
                    // data is larger than a block, and that's never going to
                    // work.  Get bigger blocks.
                }
            }
        }


        if (writeAssignments)
        {
            if (!bitBuffer.empty()) {
                compressor.finish();
                writeBuffer();
            }

            assign_strm.close();
        }

        thisCluster.getLocation().multiplyMyselfByHim(1.0 / counter);
        Text outputBuffer = new Text();
        outputBuffer.set(thisCluster.writeOut());
        context.write(key, outputBuffer);
    }

    private void writeBuffer() throws IOException
    {
        assign_strm.writeInt(bitBuffer.getBuffer().position());
        assign_strm.writeLong(currentUncompressedBytes);
        assign_strm.writeInt(currentNumElements);
    }

    private void updateData(String datapoint) {
         currentUncompressedBytes += datapoint.length() + 1;
         currentNumElements++;
    }
 
 
    private void resetVariables() {
         currentUncompressedBytes = 0;
         currentNumElements = 0;
     }

}
