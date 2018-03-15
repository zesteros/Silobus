package itl.silobus.util.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Angelo on 08/08/2017.
 */

public class Stop {

    private LatLng mCoordinates;
    private double mDistanceToUser;

    /**
     * @param coordinates
     */
    public Stop(LatLng coordinates){
        this.mCoordinates = coordinates;
    }

    /**
     * @return
     */
    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.mCoordinates = coordinates;
    }

    public double getDistanceToUser() {
        return mDistanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.mDistanceToUser = distanceToUser;
    }
}
