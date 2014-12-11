package edu.jhu.pkss.workload;

public class InputFormat extends org.apache.hadoop.mapreduce.lib.input.FileInputFormat<Long, VectorizedObject>
{
    @Override
    public org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject> createRecordReader(
            org.apache.hadoop.mapreduce.InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        // We'll just assume that the split is a fileSplit
        return new edu.jhu.pkss.workload.TextRecordReader();
    }
}
