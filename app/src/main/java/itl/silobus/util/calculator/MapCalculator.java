package itl.silobus.util.calculator;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import itl.silobus.comm.GlobalData;

/**
 * Created by Angelo on 11/06/2017.
 */

public class MapCalculator {

    private long actualTime;
    private float actualDistance;

    public MapCalculator() {
        actualTime = System.currentTimeMillis();
    }

    /*Calculates the distance between two locations in KM */
    public double getDistanceBetweenTwoCoordinates(LatLng latLng1, LatLng latLng2) {
        float[] results = new float[1];
        Location.distanceBetween(
                latLng1.latitude,
                latLng1.longitude,
                latLng2.latitude,
                latLng2.longitude,
                results
        );
        return results[0] / 1000;
    }

    public float getAngleBetweenTwoCoordinates(LatLng latLng1, LatLng latLng2) {

        double lat1 = latLng1.latitude * Math.PI / 180;
        double lng1 = latLng1.longitude * Math.PI / 180;
        double lat2 = latLng2.latitude * Math.PI / 180;
        double lng2 = latLng2.longitude * Math.PI / 180;

        double distanceBetweenLng = (lng2 - lng1);

        double y = Math.sin(distanceBetweenLng) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(distanceBetweenLng);

        double angle = Math.atan2(y, x);

        angle = Math.toDegrees(angle);
        angle = (angle + 360) % 360;

        return (float) angle;
    }

    public float getDistance(PolylineOptions polylines) {
        float totalDistance = 0;
        for (int i = 1; i < polylines.getPoints().size(); i++) {
            Location currLocation = new Location("this");
            currLocation.setLatitude(polylines.getPoints().get(i).latitude);
            currLocation.setLongitude(polylines.getPoints().get(i).longitude);

            Location lastLocation = new Location("this");
            currLocation.setLatitude(polylines.getPoints().get(i - 1).latitude);
            currLocation.setLongitude(polylines.getPoints().get(i - 1).longitude);
            totalDistance += lastLocation.distanceTo(currLocation) / 1E8;
        }
        return totalDistance;
    }

    public float getSpeed(float distance, boolean inverse) {
        float speed = 0;
        actualTime = System.currentTimeMillis() - actualTime;
        if (actualDistance != 0)
            if (!inverse)
                speed = (float) (((actualDistance - distance) / actualTime) * 1E6);
            else
                speed = (int) (((distance - actualDistance) / actualTime) * 1E6);
        Log.d("time", "distance=" + distance + "actualTime=" + actualTime + ",actualdistance=" + actualDistance + ",speed=" + speed);
        actualTime = System.currentTimeMillis();
        actualDistance = distance;
        speed *= 3.6;
        float speedRandom = (float) ((Math.random() * 50) + 5);
        speed = speed - speedRandom > 0 ? speed - speedRandom : speed;
        return speed;
    }


}
