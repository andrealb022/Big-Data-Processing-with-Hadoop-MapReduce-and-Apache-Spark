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

import java.io.IOException;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 *
 * @author Andrea Alberti
 * 
 * reduce: (k2, [v2]) -> [(k3,v3)]
 * Reducer<k2,v2,k3,v3>
 * The reducer is called for each key, iterates over the prices in the list of values, and calculates the average. 
 */
  public class ReducerBigData extends Reducer<ApartmentType,FloatWritable,ApartmentType,Text> {

    @Override
    public void reduce(ApartmentType key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
        
      float sum = (float) 0.0;
      int n = 0;
      for (FloatWritable val : values) {
        sum += val.get();
        n += 1;
      }
      float average = sum/n;
      context.write(key, new Text("(" + average + ", " + n + ")"));
    }
  }
