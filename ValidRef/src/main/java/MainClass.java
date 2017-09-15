import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.output.Bibliography;
import org.json.simple.JSONArray;

import java.sql.Timestamp;

import static spark.Spark.post;
import static spark.Spark.threadPool;

/**
 * Created by Uni on 16.06.2017.
 */
public class MainClass {

    public static void main(String[] args) {
        int maxThreads = 8;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);
        post("/checkAll", (request, response) -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            JSONArray jsonReferences = new JSONArray();
            org.json.JSONObject params = new org.json.JSONObject(request.body());
            System.out.println("request is: " + request.body());
            String references = params.getString("references");
            String style = params.getString("style").toLowerCase();
            response.type("json");
            ReferenceCheckHandler handler = new ReferenceCheckHandler();
            JSONArray toReturn = handler.checkAll(style, references);
            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
            System.out.println("DAUER " + (timestamp2.getTime()-timestamp.getTime()));
            if (toReturn != null) {
                response.status(200);
                return toReturn;
            } else {
                response.status(400);
                return "wrong call";
            }
        });
    }
}
