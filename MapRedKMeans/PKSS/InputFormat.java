package PKSS;
import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class InputFormat extends org.apache.hadoop.mapreduce.lib.input.TextInputFormat
{
    @Override
    public org.apache.hadoop.mapreduce.RecordReader<LongWritable, Text> createRecordReader(
            org.apache.hadoop.mapreduce.InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        return new PKSS.RecordReader<LongWritable, Text>();
    }
}
