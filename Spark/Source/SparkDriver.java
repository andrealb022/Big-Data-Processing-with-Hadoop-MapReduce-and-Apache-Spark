/*Course: High Performance Computing 2023/2024 AH
 *
 * Lecturer: Giuseppe D'Aniello	gidaniello@unisa.it
 *
 * Alberti Andrea   0622702370    a.alberti2@studenti.unisa.it
 *
 * Exercise 2 – Spark
    Identify, for each neighborhood, the three hosts that have the greatest number of apartments.
 */

package it.unisa.diem.hpc.spark;

import java.util.LinkedList;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 *
 * @author Andrea Alberti
 */
public class SparkDriver {

    /**
     * @param args , input e output path
     */
    public static void main(String[] args) {
        //input and output path
        String inputPath = args[0];
        String outputPath = args[1];
        
        // Create a configuration object and set the name of the application 
        SparkConf conf = new SparkConf().setAppName("Project-Big_Data-Spark");
        
        // Create a Spark Context object
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        // Build an RDD of Strings from the input textual file 
        // Each element of the RDD is a line of the input file 
        JavaRDD<String> readingsRDD = sc.textFile(inputPath);
        String header = readingsRDD.first();
        
        //Duplicate elimination
        JavaRDD<String> readingsNoDuplicateRDD = readingsRDD.distinct(); 
        
        //Remove the content of the second field (name), enclosed within quotation marks, as it may contain commas that could pose issues during analyses.
        JavaRDD<String> readingsNoDuplicateNewRDD = readingsNoDuplicateRDD.map(line ->{
            String parts[] = line.split("\"");
            StringBuilder nuovaLinea = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i += 2) {
                nuovaLinea.append("\"\"").append(parts[i + 1]);
            }

            return nuovaLinea.toString();
        });
                
        // Removal of header row and potentially error-causing rows
        JavaRDD<String> dataWithoutHeader = readingsNoDuplicateNewRDD.filter(line -> {
            if (!header.equals(line)){
                String[] fields = line.split(",");
                if(fields.length>=6)
                    if(!(fields[2].isEmpty() || fields[5].isEmpty()))
                            return true;
            }
            return false;
        });
        
        // Create a javaPairRDD mapping each row to a Tuple2<Neighborhood, hostID>
        JavaPairRDD<String, String> hostPerNeighborhood = dataWithoutHeader.mapToPair(line -> {
                    String[] fields = line.split(",");
                    return new Tuple2<>(fields[5],fields[2]);
                });
        
        //  Maps in Tuple2<<Neighborhood, hostID>, 1>
        JavaPairRDD<Tuple2<String, String>, Integer> hostPerNeighborhoodCount = hostPerNeighborhood.mapToPair(
                neighborhoodHost -> new Tuple2<>(neighborhoodHost, 1));

        //  Reduces by key (Tuple2<Neighborhood, hostID>), sum the value for every host per neighborhood
        JavaPairRDD<Tuple2<String, String>, Integer> hostPerNeighborhoodCounted = hostPerNeighborhoodCount.reduceByKey((a,b) -> a+b);

        //  Maps in Tuple2<Neighborhood, Tuple2<hostID, Count>>
        JavaPairRDD<String, Tuple2<String, Integer>> neighbourhoodTupleRDD = hostPerNeighborhoodCounted .mapToPair(
                tuple -> new Tuple2<>(tuple._1()._1(), new Tuple2<>(tuple._1()._2(), tuple._2())));

        // Group by keys (neighborhood), creating a PairRDD < neighborhood, Iterable(Tuple2<hostID,count>)>
        JavaPairRDD<String, Iterable<Tuple2<String, Integer>>> groupedNeighbourhoods = neighbourhoodTupleRDD.groupByKey();
        
        // Map the values of the old RDD to the top 3 hosts for the count value.
        //Use a local LinkedList that contains 3 elements to avoid moving potentially large values locally each time and to utilize removeLast to remove the smallest element after sorting.
        JavaPairRDD<String, Iterable<Tuple2<String, Integer>>> top3HostPerNeighbourhoods = groupedNeighbourhoods.mapValues(values -> {
             LinkedList<Tuple2<String, Integer>> sortedList = new LinkedList<>();
            for ( Tuple2<String, Integer> t : values){
                sortedList.add(t);
                sortedList.sort((t1, t2) -> t2._2().compareTo(t1._2()));
                while(sortedList.size()>3){
                    sortedList.removeLast();
            }}
            return sortedList;   
        });
        
        // Sort elements by key
        JavaPairRDD<String, Iterable<Tuple2<String, Integer>>> orderedTop3HostPerNeighbourhoods = top3HostPerNeighbourhoods.sortByKey();
        
        // Save the results to an output file
        orderedTop3HostPerNeighbourhoods.saveAsTextFile(outputPath);
        
        // Close the Spark context
        sc.close();
    } 
}