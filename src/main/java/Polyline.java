//import com.google.maps.android.PolyUtil;
import org.locationtech.spatial4j.io.PolyshapeWriter.Encoder;
import org.locationtech.spatial4j.io.PolyshapeWriter;

import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;


public class Polyline {

    public static void main(String[] args) {
        Polyline p = new Polyline();
        int num = 0b11111110111011010101111000001111;
        // // System.out.println(num);
        // System.out.println(Integer.toBinaryString(num));
        // System.out.println(Integer.toBinaryString(num<<1));
        // print(num<<1);
        double[] arr = new double[] {13};
        
        StringWriter writer = new StringWriter();
        for (int i = 0; i < arr.length; i++) {	
        	p.storeLocation(-179.9832104, arr[i], writer);
        }
        System.out.println(writer);
    }
    
    public void storeLocation(double lat, double lon, StringWriter writer) {
//    	StringWriter writer = new StringWriter();
    	Encoder encoder = new Encoder(writer);
    	try {
    		encoder.write(lat, lon);
    	} catch (IOException e) {
    		System.exit(1);
    	}
    	
    		
    }

    public String encode(double num) {
        
        // Step 2: Multiply by 1e5 and round
        int numE5 = (int) (num * 100000);
        // int latE5 = latitude * 100000;

        // Step 3: Take two's complement if necessary
        if (numE5 < 0) {
            numE5 = twosComplement(numE5);
        }
        // if (latE5 < 0) {
        //     latE5 = twosComplement(latE5);
        // }

        // Step 4: Left shift by one digit
        numE5 = numE5 << 1;

        // Step 5: Invert if num is negative
        if (num < 0) {
            numE5 = ~numE5;
        }

        // Step 6: 
        int one = numE5 & 0b11111; 
        int two = numE5 & 0b1111100000; 
        int three = numE5 & 0b111110000000000; 
        int four = numE5 & 0b11111000000000000000; 
        int five = numE5 & 0b1111100000000000000000000;

        int newNum = one << 25 + two << 20 + three << 15 + four << 10 + five << 5;

        return "";


        // if less than 0, take twos complement
    }

    public int twosComplement(int num) {
        return ~num + 1;
    }
    
}