package com.GeoJson.servlet;

import com.GeoJson.Point;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.PrintWriter;

public class GeoJsonServlet extends HttpServlet {
    // Default GeoJSON value when no GeoJSON is sent in the request
    private static final String GEO_JSON_STR = "{\"crs\": {\"type\": \"link\", \"properties\": {\"href\": \"http://spatialreference.org/ref/epsg/4326/\", \"type\": \"proj4\"}}," +
                                                "\"type\": \"FeatureCollection\", " +
                                                "\"features\": [{\"geometry\": {\"type\": \"Point\", \"coordinates\": [3.3781779999999886, 6.5056559999999966]}, \"type\": \"Feature\"," +
                                                                    "\"properties\": {\"distance\": 662.0, \"name\": [\"POLICE\"], \"picture\":" +
                                                                    "\"/media/piclist/2016/07/26/police22.png\", \"did\": 38, \"picture2\":" +
                                                                    "\"/media/piclist/2016/07/26/police_station.png\", \"address\": \"Oyadiran St, Lagos, Nigeria\"," +
                                                                    "\"id\": \"POLICE\"}}," +
                                                                "{\"geometry\": {\"type\": \"Point\", \"coordinates\": [3.364241999999998," +
                                                                    "6.504802999999988]}, \"type\": \"Feature\", \"properties\": {\"distance\": 1663.0, \"name\": [\"POLICE\"], \"picture\": \"/media/piclist/2016/07/26/police22.png\"," +
                                                                    "\"did\": 39, \"picture2\": \"/media/piclist/2016/07/26/police_station.png\", \"address\": \"Aiyeleto St, Lagos, Nigeria\"," +
                                                                    "\"id\": \"POLICE\"}}," +
                                                                "{\"geometry\": {\"type\": \"Point\", \"coordinates\": [3.353865999999988," +
                                                                    "6.517038000000005]}, \"type\": \"Feature\", \"properties\": {\"distance\": 2708.0, \"name\": " +
                                                                    "[\"POLICE\"], \"picture\": \"/media/piclist/2016/07/26/police22.png\", \"did\": 33, \"picture2\": " +
                                                                    "\"/media/piclist/2016/07/26/police_station.png\", \"address\": \"Main Gate Rd, LUTH, Lagos, Nigeria\", \"id\": \"POLICE\"}}" +
                                                "]}";

    // Get the default GeoJSON string object
    private String getGeoJsonStr() {
        return GEO_JSON_STR;
    }

   // Calculates the distance between 2 geometry points using pythagoras' theorem
    private double getDistance(Point p1, Point p2) {
        double x = p2.getLongitude() - p1.getLongitude();
        double y = p2.getLatitude() - p1.getLatitude();
        return Math.sqrt(Math.pow(x,2.0)+Math.pow(y,2.0));
    }

    // Find the closest feature to a point
    private JSONObject findNearestFeature(JSONObject geoJsonObj, Point a) {
        JSONObject closestFeatureObj = null;
        try {
            // Retrieve coordinates from JSON Object
            JSONArray features = geoJsonObj.getJSONArray("features");
            JSONObject closestFeature = features.getJSONObject(0);
            JSONArray coordinates = closestFeature.getJSONObject("geometry").getJSONArray("coordinates");

            // Find closest distance by computing the distance of each feature to the point
            double closestDistance = getDistance(new Point(coordinates),a);
            for (int i=1; i<features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
                double distance = getDistance(new Point(coordinates),a);
                if (distance < closestDistance) {
                    closestFeature = feature;
                    closestDistance = distance;
                }
            }

            // Construct JSON Output from closest feature and distance
            Map<String,String> closestFeatureMap = new HashMap<String,String>();
            closestFeatureMap.put("feature", closestFeature.toString());
            closestFeatureMap.put("distance", String.valueOf(closestDistance));
            closestFeatureObj = new JSONObject(closestFeatureMap);
        } catch(JSONException je) {
            je.printStackTrace();
        }
        return closestFeatureObj;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Entered GeoJsonServlet:: doPost()");

        // Initialize default values for request parameters
        double longitude = 3.3775515;
        double latitude = 6.5115311;
        int srid = 4326;
        String geoJsonStr = getGeoJsonStr();
        
        try {
            // Retrieve request parameter values (IFF available)
            if ((request.getParameter("longitude") != null) && (!request.getParameter("longitude").equals(""))) {
                longitude = Double.parseDouble(request.getParameter("longitude"));
            }
            if ((request.getParameter("latitude") != null) && (!request.getParameter("latitude").equals(""))) {
                latitude = Double.parseDouble(request.getParameter("latitude"));
            }
            if ((request.getParameter("srid") != null) && (!request.getParameter("srid").equals(""))) {
                srid = Integer.parseInt(request.getParameter("srid"));
            }
            if ((request.getParameter("geojson") != null) && (!request.getParameter("geojson").equals(""))) {
                geoJsonStr = request.getParameter("geojson");
            }

            // Create a Point object from given parameters (Longitude, Latitude, and Srid) and
            Point a = new Point(longitude,latitude,srid);
            // Create a JSON object from given GeoJSON string
            JSONObject geoJsonObj = new JSONObject(geoJsonStr);
            // Find the nearest feature in the GeoJSON to the given point
            JSONObject nearestFeature = findNearestFeature(geoJsonObj,a);
            System.out.println("closestFeatureObj: " + nearestFeature);

            // Return JSON Object of nearest feature
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(nearestFeature);
            out.flush();

            /* OUTPUT for default input values: The distance and feature is returned as JSON
                 {"distance":"0.005908409452638361","feature":"{"properties"
                  :{"id":"POLICE","picture":"/media/piclist/2016/07/26/police22.png","distance":662,"address":"Oyadiran St, Lagos, Nigeria","picture2":"/media/piclist/2016/07/26/police_station.png","name":["POLICE"],"did":38},
                  "type":"Feature","geometry":{"type":"Point","coordinates":[3.3781779999999886,6.5056559999999966]}}"}
            */           

        } catch(JSONException je) {
            je.printStackTrace();
        }
    }
}
