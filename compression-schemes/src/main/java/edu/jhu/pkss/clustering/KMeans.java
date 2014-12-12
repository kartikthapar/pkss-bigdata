package edu.jhu.pkss.clustering;

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

public class KMeans {

  static void printUsage() {
    System.out.println ("KMeans <input> <clusterFileDirectory> <assignmentFileDirectory> <numIters> <numItersWriteIntResults> <compressionScheme {arith, lz4, bzip2}>");
    System.exit(-1);
  }

  public static Map<Long, VectorizedObject> ReadClusterCenters(FileSystem fs, String dirName)
      throws java.io.IOException
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

  public static int main (String [] args) throws Exception {

    // if we have the wrong number of args, then exit
    if (args.length != 6) {
      printUsage ();
      return 1;
    }
    
    // Not using the compressed input for the first iteration
    boolean use_compressed_input = false;
    int iteration_to_read = 0;
    // repeate the whole thing the correct number of times
    for (int i = 0; i < Integer.parseInt (args[3]); i++) {

      // Get the default configuration object
      Configuration conf = new Configuration ();

      // look at all of the files in the cluster file directory... start by getting the directory name
      String dirName;

      // if this is the very first iter, use the directory name supplied by the user
      if (i == 0)
        dirName = args[1];
      // otherwise, use the last one that we wrote to
      else
        dirName = args[1] + i;

      // now, list the files in that directory
      FileSystem fs = FileSystem.get (conf);
      conf.set("clusterInput", dirName);
      
      // Set the compression scheme to be used
      conf.set("compression", args[5]);


      conf.set(PKSSReducer.ASSIGNMENT_OUTPUT_DIR_KEY, args[2] + (i + 1));
      Map<Long, VectorizedObject> clusters = ReadClusterCenters(fs, dirName);
      int cluster_count = clusters.size();
      if (cluster_count <= 0)
      {
        throw new RuntimeException ("Could not find any clusters in the directory " + dirName);
      }

      // Set whether the input is compressed *before* we decide that the output is compressed
      // and that future inputs are comopressed
      conf.setBoolean(edu.jhu.pkss.clustering.InputFormat.COMPRESSED_INPUT, use_compressed_input);
      
      // Need to decide when to write assignemnts to do reshuffling
      if (i % Integer.parseInt(args[4]) == 0) {
        conf.setBoolean(PKSSReducer.ASSIGNMENT_OUTPUT_KEY, true);
        use_compressed_input = true;
	    iteration_to_read = i;
      }
      else {
        conf.setBoolean(PKSSReducer.ASSIGNMENT_OUTPUT_KEY, false);
      }
      
      /*
      conf.setBoolean(PKSSReducer.ASSIGNMENT_OUTPUT_KEY, true);
      if (i != 0) {
	  use_compressed_input = true;
      }
      */
      
      // conf.setBoolean(edu.jhu.pkss.clustering.InputFormat.COMPRESSED_INPUT, false);
      
      // get the new job
      Job job = Job.getInstance(conf);
      job.setJobName ("K-Means clustering");

      // all of the inputs and outputs are text
      job.setMapOutputKeyClass (LongWritable.class);
      job.setMapOutputValueClass (Text.class);
      job.setOutputKeyClass (LongWritable.class);
      job.setOutputValueClass (Text.class);

      // tell Hadoop what mapper and reducer to use
      job.setMapperClass (PKSSMapper.class);
      job.setReducerClass (PKSSReducer.class);

      // set the input and output format class... these tell Haoop how to read/write to HDFS
      job.setInputFormatClass(edu.jhu.pkss.clustering.InputFormat.class);
      job.setOutputFormatClass(TextOutputFormat.class);

      // set the input and output files
      // TODO Simar thinks this is needs to be done, any thoughts?
      // We need to change the Input files path to be the 
      // new assignned directories depending on the iteration
      // instead of the original Infut files directory
      if(i == 0)
	  edu.jhu.pkss.clustering.InputFormat.setInputPaths (job, args[0]);
      else
	  edu.jhu.pkss.clustering.InputFormat.setInputPaths (job, args[2] + iteration_to_read);
      
      Path outputPath = new Path (args[1] + (i + 1));
      if (fs.exists(outputPath))
        fs.delete(outputPath, true);
      TextOutputFormat.setOutputPath (job, outputPath);

      // force the split size to 8 megs (this is small!)
      TextInputFormat.setMinInputSplitSize (job, 1 * 1024 * 1024);
      TextInputFormat.setMaxInputSplitSize (job, 1 * 1024 * 1024);

      // set the jar file to run
      job.setJarByClass (KMeans.class);

      //set the number of reducers to the number of clusters
      job.setNumReduceTasks(cluster_count);

      // submit the job
      System.out.println ("Starting iteration " + i);
      int exitCode = job.waitForCompletion(true) ? 0 : 1;
      if (exitCode != 0) {
        System.out.println("Job Failed!!!");
        return exitCode;
      }
    }
    return 0;
  }
}
