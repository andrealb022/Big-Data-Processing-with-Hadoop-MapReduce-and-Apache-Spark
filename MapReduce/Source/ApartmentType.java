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
/**
 *
 * @author  Andrea Alberti
 * WritableComparables can be compared to each other, typically via Comparators. Any type which is to be used as a key in the Hadoop Map-Reduce framework should implement this interface.
 */
import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

public class ApartmentType implements WritableComparable<ApartmentType> {
    private final Text neighborhood;
    private final Text roomType;
    
    public ApartmentType() {
        this.neighborhood = new Text();
        this.roomType = new Text();
    }
    
    public ApartmentType(String neighborhood, String roomType) {
        this.neighborhood = new Text(neighborhood);
        this.roomType = new Text(roomType);
    }

    public Text getNeighborhood() {
        return neighborhood;
    }

    public Text getRoomType() {
        return roomType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        neighborhood.write(out);
        roomType.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        neighborhood.readFields(in);
        roomType.readFields(in);
    }

    //compare the neighborhood, if equal, compare the room_type
    @Override
    public int compareTo(ApartmentType other) {
        int neighborhoodComparison = neighborhood.toString().compareToIgnoreCase(other.neighborhood.toString());
        if (neighborhoodComparison != 0) {
            return neighborhoodComparison;
        }
        return roomType.toString().compareToIgnoreCase(other.roomType.toString());
    }

    //two ApartmentType object are equals if the neighborhood and the room_type are both equals ignoring case
    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        ApartmentType a = (ApartmentType) o;
        return (a.getNeighborhood().toString().equalsIgnoreCase(neighborhood.toString()) && a.getRoomType().toString().equalsIgnoreCase(roomType.toString()));
    }

    //Note that hashCode() is frequently used in Hadoop to partition keys. It's important that your implementation of hashCode() returns the same result across different instances of the JVM.
    //Note also that the default hashCode() implementation in Object does not satisfy this property.
    @Override
    public int hashCode() {
        final int prime = 31;
         int result = 1;
         result = prime * result + neighborhood.hashCode();
         result = prime * result + roomType.hashCode();
         return result;
    }

    @Override
    public String toString() {
        return '(' + neighborhood.toString() + ", " + roomType.toString() + ')';
    }
    
    
}
