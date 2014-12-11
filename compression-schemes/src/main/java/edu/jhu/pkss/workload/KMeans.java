package edu.jhu.pkss.workload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;

public class KMeans
{
    static void printUsage()
    {
        System.out.println ("KMeans <input> <clusterFileDirectory> <assignmentFileDirectory> <numIters> <numItersWriteIntResults>");
        System.exit(-1);
    }

    public static Map<Long, VectorizedObject> ReadClusterCenters(FileSystem fs, String dirName) throws java.io.IOException
    {
        Path path = new Path(dirName);
        FileStatus fstatus[] = fs.listStatus(path);
        Map<Long, VectorizedObject> clusters = new java.util.HashMap<Long, VectorizedObject>();
        for (FileStatus f: fstatus)
        {
            // ignore files that start with an underscore, since they just describe Hadoop output
            if (f.getPath().toUri().getPath().contains ("/_"))
            continue;

            // Count the number of clusters, so we know how many reducers to use
            Path clusterPath = new Path(f.getPath().toUri().getPath());
            BufferedReader strm = new BufferedReader(new InputStreamReader(fs.open(clusterPath)));

            for (String curLine = strm.readLine(); curLine != null; curLine = strm.readLine())
            {
                int separatorIndex = curLine.indexOf('\t');
                long clusterId = Long.parseLong(curLine.substring(0, separatorIndex));
                VectorizedObject obj = new VectorizedObject(curLine.substring(separatorIndex));
                clusters.put(clusterId, obj);
            }
        }
        return clusters;
    }

    public static int main (String [] args) throws Exception 
    {
        // if we have the wrong number of args, then exit
        if (args.length != 2) 
        {
            printUsage ();
            return 1;
        }

        Configuration conf = new Configuration();
        // get the new job
        Job job = Job.getInstance(conf);
        job.setJobName ("K-Means clustering");

        // all of the inputs and outputs are text
        job.setMapOutputKeyClass (LongWritable.class);
        job.setMapOutputValueClass (Text.class);
        job.setOutputKeyClass (LongWritable.class);
        job.setOutputValueClass (Text.class);

        // tell Hadoop what mapper and reducer to use
        job.setMapperClass (PKSSComputeMapper.class);
        job.setReducerClass (PKSSComputeReducer.class);

        // set the input and output format class... these tell Haoop how to read/write to HDFS
        job.setInputFormatClass(edu.jhu.pkss.workload.InputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        // set the input and output files
        edu.jhu.pkss.workload.InputFormat.setInputPaths (job, args[0]);
        TextOutputFormat.setOutputPath (job, new Path (args[1]));

        // force the split size to 8 megs (this is small!)
        TextInputFormat.setMinInputSplitSize (job, 1 * 1024 * 1024);
        TextInputFormat.setMaxInputSplitSize (job, 1 * 1024 * 1024);

        // set the jar file to run
        job.setJarByClass (KMeans.class);

        // submit the job
        int exitCode = job.waitForCompletion(true) ? 0 : 1;
        if (exitCode != 0)
        {
            System.out.println("Job Failed!!!");
            return exitCode;
        }
        
        return 0;
    }
}
