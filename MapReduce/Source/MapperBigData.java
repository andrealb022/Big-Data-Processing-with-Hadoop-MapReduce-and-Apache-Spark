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
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

/**
 *
 * @author Andrea Alberti
 * 
 * map: (k1, v1) -> [(k2,v2)]
 * Mapper <k1, v1, k2, v2>
 */
  public class MapperBigData extends Mapper<Text, Text, ApartmentType, FloatWritable>{
      // Minimum number of reviews required  for the b&b
      private final static Integer MinReview = Integer.valueOf(9);
      
    @Override
    public void map(Text key, Text value, Context context) throws IOException, InterruptedException{
        //Remove the header and handles the case where commas appear in the "name" field
        if(!key.toString().contains("id")){
            String parts[] = value.toString().split("\"");
            StringBuilder line = new StringBuilder(parts[0]);
            for (int i = 1; i < parts.length; i += 2) {
                line.append("\"\"").append(parts[i + 1]);
            }
            
            String fields[] = line.toString().split(",");
             if(fields.length>=10){
                    if(!(fields[4].isEmpty() || fields[7].isEmpty() || fields[10].isEmpty())){
                        Integer reviewCount=Integer.valueOf(fields[10]);
                        
                        // Compare the value of reviewCount with the Minimum number of reviews required
                        if (reviewCount.compareTo(MinReview)>0){
                            context.write(new ApartmentType(fields[4],fields[7]),new FloatWritable (Float.parseFloat(fields[8])));
                        }
                    }
             }
            }
        }
    }
  

