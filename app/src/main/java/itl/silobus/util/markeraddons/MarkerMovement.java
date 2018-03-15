package itl.silobus.util.markeraddons;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Angelo on 02/06/2017.
 */

public class MarkerMovement {

    private boolean mIsMarkerRotating;
    private GoogleMap mMap;

    public MarkerMovement(GoogleMap map) {
        this.mMap = map;
    }


    public void moveMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection projection = mMap.getProjection();
        Point startPoint = projection.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = projection.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float interpolation = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = interpolation * toPosition.longitude + (1 - interpolation)
                        * startLatLng.longitude;
                double lat = interpolation * toPosition.latitude + (1 - interpolation)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (interpolation < 1.0)
                    // Post again 16ms later.
                    handler.postDelayed(this,16);
            }
        });
    }

    public void rotateMarker(final Marker marker, final float toRotation) {
        if (!mIsMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    mIsMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float interpolation = interpolator.getInterpolation((float) elapsed / duration);

                    float rotation = interpolation * toRotation + (1 - interpolation) * startRotation;

                    marker.setRotation(-rotation > 180 ? rotation / 2 : rotation);
                    if (interpolation < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        mIsMarkerRotating = false;
                    }
                }
            });
        }
    }
}
