package edu.jhu.pkss.clustering;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class InputFormat extends org.apache.hadoop.mapreduce.lib.input.FileInputFormat<Long, VectorizedObject>
{
    @Override
    public org.apache.hadoop.mapreduce.RecordReader<Long, VectorizedObject> createRecordReader(
            InputSplit split,
            org.apache.hadoop.mapreduce.TaskAttemptContext context)
    {
        // We'll just assume that the split is a fileSplit
        return new edu.jhu.pkss.clustering.TextRecordReader();
    }

    private String[] getHostsForSplit(FileSystem fs, FileStatus file, long offset, long size)
        throws IOException
    {
        java.util.Set<String> hostSet = new java.util.HashSet();
        BlockLocation[] block_locations = fs.getFileBlockLocations(file, offset, size);
        for (BlockLocation bl : block_locations)
        {
            String[] block_hosts = bl.getHosts();
            for (String host : block_hosts)
            {
                hostSet.add(host);
            }
        }
        String[] result = new String[hostSet.size()];
        int index = 0;
        for (String host : hostSet)
        {
            result[index] = host;
            index += 1;
        }
        return result;
    }

    @Override
    public List<InputSplit> getSplits(org.apache.hadoop.mapreduce.JobContext context)
        throws IOException
    {
        long blockSize = 16*1024*1024; // derive this the same way as Steve & Simar do
        List<InputSplit> splits = new java.util.ArrayList();

        List<FileStatus> files = listStatus(context);
        for (FileStatus file : files)
        {
            Path path = file.getPath();
            FileSystem fs = path.getFileSystem(context.getConfiguration());
            long bytesRemaining = file.getLen();
            long bytesAllocated = 0;

            while (bytesRemaining > 0)
            {
                // FileSplit(path, start, length, hosts, in memory hosts);
                long splitSize = Math.min(bytesRemaining, blockSize);
                String[] hosts = getHostsForSplit(fs, file, bytesAllocated, splitSize);
                FileSplit cur_split = new FileSplit(path, bytesAllocated, splitSize, hosts);
            }
        }
        return splits;
    }
}
