/*Course: High Performance Computing 2023/2024 AH
 *
 * Lecturer: Giuseppe D'Aniello	gidaniello@unisa.it
 *
 * Alberti Andrea   0622702370    a.alberti2@studenti.unisa.it
 *
 * Exercise 1 – MapReduce
   Find the average prices of b&bs with a number greater than 10 reviews, grouped by neighborhood and room_type.
 */
package it.unisa.hpc.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 *
 * @author Andrea Alberti
 */
public class DriverBigData {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Path inputPath;  //input file
        Path outputPath;  //output file
        
        // Parse the parameters
        inputPath = new Path(args[0]);
        outputPath = new Path(args[1]);
        
        Configuration conf = new Configuration();
        
        //set new separator forKeyValueTextInputFormat 
        conf.set("key.value.separator.in.input.line", ","); 
        
        // Define a new job
        Job job = Job.getInstance(conf, "Project_BigData_MapReduce");
        
        /// Set path of the input file/folder (if it is a folder, the job reads all the files in the specified folder) for this job
        FileInputFormat.addInputPath(job, inputPath);

        // Set path of the output folder for this job
        FileOutputFormat.setOutputPath(job, outputPath);

        // Specify the class of the Driver for this job
        job.setJarByClass(DriverBigData.class);

        // Set job input format
        job.setInputFormatClass(KeyValueTextInputFormat.class);

        // Set job output format
        job.setOutputFormatClass(TextOutputFormat.class);

        // Set map class
        job.setMapperClass(MapperBigData.class);

        // Set map output key and value classes
        job.setMapOutputKeyClass(ApartmentType.class);
        job.setMapOutputValueClass(FloatWritable.class);

        // Set reduce class
        job.setReducerClass(ReducerBigData.class);

        // Set reduce output key and value classes
        job.setOutputKeyClass(ApartmentType.class);
        job.setOutputValueClass(Text.class);

        // Set number of reducers
        job.setNumReduceTasks(1);
        
        // Execute the job and wait for completion
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
    
}
