package itl.silobus.util.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import itl.silobus.R;
import itl.silobus.comm.GlobalData;
import itl.silobus.util.calculator.MapCalculator;
import itl.silobus.util.updater.MapUpdater;

/**
 * Created by Angelo on 11/06/2017.
 */

public class QueryRoute {


    private final GoogleMap mMap;
    private final MapCalculator mapCalc;
    private MapUpdater mMapUpdater;
    private PolylineOptions mPolylineOptions;

    public QueryRoute(GoogleMap map, MapUpdater mapUpdater) {
        this.mMap = map;
        this.mMapUpdater = mapUpdater;
        this.mapCalc = new MapCalculator();

    }

    /**
     * This method can calculate all distances in bus road with a determined
     * bus stop and select the nearby stop, also draw the polyline between bus and
     * bus stop
     *
     * @param marker
     * @param polylineColor        the oolor of polyline
     * @param busPositionIndex     the start position of lat lng list
     * @param routeCoordinatesList the list
     * @param stopCoordinates      the end lat lng of the list
     * @return the polyline showed
     */
    public Polyline drawRoute(Marker marker, int polylineColor, int busPositionIndex,
                              List routeCoordinatesList, LatLng stopCoordinates) {
        PolylineOptions points = new PolylineOptions();
        /*
        * List of distances
        * */
        ArrayList<Distance> distances = new ArrayList<>();
        /*
        * Configure polyline
        * */
        points.color(polylineColor).clickable(false).width(5);
        /*
        * Start position (index) "b" of lat lng list
        * */
        int pointB = 0;
        /*
        * Add distances and index to list and sort distances
         * between the stop and route in descending form
        * */
        for (int i = 0; i < routeCoordinatesList.size(); i++) {
            /*
            * Calcule distance in all coordinates in route with stop coordinates
            * */
            double distance = mapCalc
                    .getDistanceBetweenTwoCoordinates(((LatLng) routeCoordinatesList.get(i)), stopCoordinates);
            /*
            * If is less than 1 kilometer add to list
            * */
            if (distance < 1d) distances.add(new Distance(distance, i));
        }
        /*
        * Order distances
        */
        Collections.sort(distances, new Comparator<Distance>() {
            @Override
            public int compare(Distance lhs, Distance rhs) {
                return lhs.getDistance() > rhs.getDistance() ? 1 :
                        (lhs.getDistance() < rhs.getDistance()) ? -1 : 0;
            }
        });
        /*Get the nearby stop (index 0 in distances list)*/
        if (distances.size() > 0)
            if (distances.get(0) != null)
                pointB = distances.get(0).getIndex();
        if (pointB != 0) {
            DecimalFormat format = new DecimalFormat("#.##");
            /*
            * Paint the polyline from bus to stop
            * if the point "b" is greater than
            * bus position index then the bus come to stop
            * */
            if (pointB > busPositionIndex) {
                for (int i = busPositionIndex; i < pointB; i++)
                    points.add(((LatLng) routeCoordinatesList.get(i)));
                points.add(stopCoordinates);
                float distance = mapCalc.getDistance(points);
                float speed = mapCalc.getSpeed(distance, false);
                marker.setSnippet(marker.getSnippet() + "\n" +
                        "Distancia a parada: " + format.format(distance) + " km" + "\n" +
                        "Velocidad: " + format.format(speed) + " km/h" + "\n" +
                        "Tiempo estimado a parada:\n" + format.format((distance / speed) * 60) + " minutos");
            } else {
                /*
                * Paint the polyline from stop to bus
                * if point "b" is less than bus position then
                * stop come to bus
                * */
                points.add(stopCoordinates);
                for (int i = pointB + 1; i <= busPositionIndex; i++)
                    points.add(((LatLng) routeCoordinatesList.get(i)));

                float distance = mapCalc.getDistance(points);
                float speed = mapCalc.getSpeed(distance, true);

                marker.setSnippet("Este camión ya no va\npara la parada que seleccionaste.\n" +
                        "Distancia a parada: " + format.format(distance) + " km" + "\n" +
                        "Velocidad: " + format.format(speed) + " km/h" + "\n" +
                        "Tiempo estimado a parada:\n" + format.format((distance / speed) * 60) + " minutos");

            }
        }
        return mMap.addPolyline(points);
    }


    private String getMapsApiDirectionsUrl(LatLng origin, LatLng dest) {
        String key = "key=" + mMapUpdater.mContext.getString(R.string.google_maps_key);
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key + "&" + sensor;
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }


    private class ReadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO Auto-generated method stub
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);


            } catch (Exception e) {
                // TODO: handle exception
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    public class PathJSONParser {

        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);

        }
    }


    public void getRouteBetweenTwoCoordinates(LatLng latlngOne, LatLng latlngTwo) {
        String url = getMapsApiDirectionsUrl(latlngOne, latlngTwo);
        ReadTask downloadTask = new ReadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public PolylineOptions getPolyline() {
        return this.mPolylineOptions;
    }

    public void setPolyline(PolylineOptions polylineOptions) {
        this.mPolylineOptions = polylineOptions;
    }


    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("data", data);
                br.close();


            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }
}
