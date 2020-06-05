import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Parser {
    public static void main() throws Exception {
        Parser parser = new Parser();
        String filename = "test.json";
        parser.run(filename);
    }

    public void run(String filename) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(filename));
        JSONObject jo = (JSONObject) obj;
    }
}