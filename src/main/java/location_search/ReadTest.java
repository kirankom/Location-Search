package lsr_ws;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.io.PolyshapeReader;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.impl.BufferedLineString;

/**
 * 
 * @author kirank
 *
 */

public class ReadTest {

	public static void main(String[] args) throws InvalidShapeException, IOException, ParseException {
		SpatialContextFactory factory = new SpatialContextFactory();
		SpatialContext s = SpatialContext.GEO;
		// TODO Auto-generated method stub
		PolyshapeReader reader = new PolyshapeReader(s , factory);
		

		BufferedLineString shape = (BufferedLineString) reader.read("1~wj{Uezb_G") ;
		
		System.out.println(shape.getClass());
		
		shape.getPoints().forEach(point -> System.out.println("Latitude::" + point.getY() + "::Longitude" + point.getX()));
		
		
	}

}
