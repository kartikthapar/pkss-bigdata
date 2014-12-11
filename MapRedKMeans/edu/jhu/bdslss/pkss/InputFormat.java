package edu.jhu.bdslss.pkss;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import edu.jhu.bdslss.VectorizedObject;

public class InputFormat extends org.apache.hadoop.mapreduce.lib.input.FileInputFormat<Long, VectorizedObject>
{
    @Override
    public org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject> createRecordReader(
            org.apache.hadoop.mapreduce.InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        // We'll just assume that the split is a fileSplit
        return new edu.jhu.bdslss.pkss.TextRecordReader();
    }
}
