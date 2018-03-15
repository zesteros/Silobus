package itl.silobus.util.updater;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.List;

import itl.silobus.MainActivity;
import itl.silobus.R;
import itl.silobus.comm.GlobalData;
import itl.silobus.util.calculator.MapCalculator;
import itl.silobus.util.map.MapAdapter;
import itl.silobus.util.map.QueryRoute;
import itl.silobus.util.markeraddons.MarkerMovement;

/**
 * Created by Angelo on 11/06/2017.
 * Update routes according user position
 * shows the near buses and draw the polyline
 * until bus comes to user.
 */


public class MapUpdater extends Thread {

    private final GoogleMap mMap;
    private final List mPoints;
    private final PolylineOptions mPolylineOptions;
    public Context mContext;
    private MarkerMovement mMarkerMovement;
    private MapCalculator mMapCalculator;
    private MapAdapter mMapAdapter;
    private QueryRoute mQueryRoute;
    private boolean keepRunning;
    private Marker mMarker;
    private String mRoute;
    private LatLng mStopPosition;
    private Polyline polyline;
    private boolean showPolyline;

    public MapUpdater(String route, GoogleMap map, Context context, PolylineOptions options, List points, MapAdapter mapAdapter) {
        this.mContext = context;
        this.mMap = map;
        this.mPoints = points;
        this.mPolylineOptions = options;
        this.mMapAdapter = mapAdapter;
        this.keepRunning = true;
        this.mRoute = route;
    }

    @Override
    public void run() {
        mMarkerMovement = new MarkerMovement(mMap);
        mMapCalculator = new MapCalculator();
        mQueryRoute = new QueryRoute(mMap, this);
        final int actualSimulatedPosition = (int) (Math.random() * mPoints.size());
        while (keepRunning) {
            /*Create marker*/
            /*Color of polyline*/
            final int color = Color.rgb((int) (Math.random() * 255),
                    (int) (Math.random() * 255), (int) (Math.random() * 255));
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*
                    * Draw polyline to stop
                    * */
                    if (showPolyline()) {
                        if (getPolyline() != null) getPolyline().remove();
                        setPolyline(mQueryRoute.drawRoute(getMarker(),color, actualSimulatedPosition, mPoints, getStopPosition()));
                    }
                    /*Add marker to map starting at first point*/
                    setMarker(mMap.addMarker(new MarkerOptions()
                            .position((LatLng) mPoints.get(actualSimulatedPosition))
                            .title("Ruta: " + mRoute)
                            .draggable(false)
                            //.infoWindowAnchor(40,40)
                            .snippet("Distancia al usuario: " +
                                    new DecimalFormat("#.##").format(mMapCalculator.getDistanceBetweenTwoCoordinates(
                                            mMapAdapter.getUserLocation(), mMapAdapter.getUserLocation())
                                    ))
                            .icon(BitmapDescriptorFactory.fromResource(getBusIcon(mRoute)))));
                    //mMap.addPolyline(mPolylineOptions.color(Color.BLACK).width(5));
                }
            });
            /*
            * Animate Marker
            * from first point to finish point and start again
            */
            if (actualSimulatedPosition + 1 < mPoints.size())
                for (int i = actualSimulatedPosition + 1; i < mPoints.size(); i++) {
                    final int finalI = i;
                    ((MainActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d("POS_ROT", marker[0].getRotation() + "");
                            if (showPolyline()) {
                                if (getPolyline() != null) getPolyline().remove();
                                setPolyline(mQueryRoute.drawRoute(getMarker(), color, finalI, mPoints, getStopPosition()));
                            }
                            mMarkerMovement.moveMarker(getMarker(), (LatLng) mPoints.get(finalI));
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng) mPoints.get(finalI)));
                            if (finalI + 1 < mPoints.size())
                                mMarkerMovement
                                        .rotateMarker(getMarker(),
                                                mMapCalculator.getAngleBetweenTwoCoordinates
                                                        (((LatLng) mPoints.get(finalI)),
                                                                ((LatLng) mPoints.get(finalI + 1))));
                            if (getMarker().isInfoWindowShown()) getMarker().showInfoWindow();

                            getMarker().setSnippet("distancia al usuario: " +
                                    new DecimalFormat("#.##").format(mMapCalculator.getDistanceBetweenTwoCoordinates(
                                            mMapAdapter.getUserLocation(), (LatLng) mPoints.get(finalI))) + " km" + "\n" +
                                    "Cant. de pasajeros a bordo: " + /*((int) (Math.random() * 45))*/ + (GlobalData.getInstance().getAmountPassengers())
                                    + "\nAsientos disponibles: " + (30-GlobalData.getInstance().getAmountPassengers()<0?0:30-GlobalData.getInstance().getAmountPassengers())/*((int) (Math.random() * 10))*/

                            );
                        /*
                        *
                        * get the route and draw (add the polyline)
                        * for not clear map use polyline.setPoints(list);
                        *
                        * */
                            //mQueryRoute.getRouteBetweenTwoCoordinates(mMapAdapter.getUserLocation(), (LatLng) mPoints.get(finalI));
                        }
                    });
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMarker().remove();

                }
            });

        }
        ((MainActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getPolyline() != null) getPolyline().remove();
            }
        });
    }

    public int getBusIcon(String route) {
        switch (route.charAt(0)) {
            case 'X':
                return R.drawable.bus_ic_aux_96;
            case 'R':
                return R.drawable.bus_ic_conv_96;
            case 'A':
                return R.drawable.bus_ic_alim_96;
        }
        return 0;
    }

    public void stopMovement() {
        this.keepRunning = false;
        getMarker().remove();
        if (getPolyline() != null) getPolyline().remove();
        showPolyline(false);
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker mMarker) {
        this.mMarker = mMarker;
    }

    public LatLng getStopPosition() {
        return mStopPosition;
    }

    public void setStopPosition(LatLng stopPosition) {
        this.mStopPosition = stopPosition;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public boolean showPolyline() {
        return showPolyline;
    }

    public void showPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }
}
