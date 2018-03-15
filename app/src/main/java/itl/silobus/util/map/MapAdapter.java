package itl.silobus.util.map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import itl.silobus.MainActivity;
import itl.silobus.R;
import itl.silobus.adapter.CustomInfoWindowAdapter;
import itl.silobus.comm.ConnectionThread;
import itl.silobus.comm.GlobalData;
import itl.silobus.database.DatabaseAdapter;
import itl.silobus.util.calculator.MapCalculator;
import itl.silobus.util.markeraddons.MarkerMovement;
import itl.silobus.util.ui.UserInteraction;
import itl.silobus.util.updater.MapUpdater;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Angelo on 11/06/2017.
 * configure the map according requirements
 * (search route), gps button and route recommendations
 * also initialize the map camera into user location.
 */

public class MapAdapter implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationChangeListener, View.OnClickListener {

    private final GoogleMap mMap;
    private final Context mContext;
    /*
    * Default Zoom
    * */
    private final static float sDefaultZoomIn = 16.4f;
    private final static float sDefaultZoomOut = 13.5f;
    private LatLng mUserLocation;
    private ArrayList<Stop> mNearbyStops;
    private ArrayList<Marker> mNearbyStopsMarkers;
    private ArrayList<MapUpdater> mActualBuses;
    private ConnectionThread connection;
    private FloatingActionButton locationButton;
    private Location actualLocation;
    private FloatingActionButton mSearchStopButton;
    /*
    * MAIN LAT ANF LNG FOR LEON GTO MX
    * */
    private static final double LEON_GTO_LAT = 21.123619;
    private static final double LEON_GTO_LNG = -101.680496;
    private static int LIMIT_STOPS_SHOWING_IN_MAP = 10;

    private String stopMarkerSelectedId;
    private String busMarkerSelectedId;

    private UserInteraction mUI;
    private MarkerMovement mMarkerMovement;

    /**
     * @param map     the GoogleMap Object
     * @param context the context of main activity
     */
    public MapAdapter(GoogleMap map, Context context) {
        this.mMap = map;
        this.mContext = context;
    }

    /**
     * Main method to call all settings for map
     */
    public void configureMap() {
        /*
        * Register marker Listener
        * */
        mMap.setOnMarkerClickListener(this);
        /*Add UI Elements*/
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        /*
        * Get the user settings
        * */
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        LIMIT_STOPS_SHOWING_IN_MAP = prefs.getInt("amount_buses_showing", 10);
        /*
        * Register Listeners
        * */
        mMap.setOnMyLocationChangeListener(this);
        locationButton.setOnClickListener(this);
        mSearchStopButton.setOnClickListener(this);
        /*
        * Move camera to determined location
        * */
        if (!getUserActualLocation())
            getDefaultLocation();
        /*
        * Set custom info window adapter to markers
        * */
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));
        /*
        * A list of actual buses showing in map
        * */
        mActualBuses = new ArrayList<>();
        /*
        * A list of actual stops showing in map
        * */
        mNearbyStopsMarkers = new ArrayList<>();
        /*The dialogs and toasts object caller*/
        mUI = new UserInteraction(mContext);
        /*Main thread to connect to bus prototype*/

        String ip = prefs.getString("ip_key", "192.168.3.100");
        int port = Integer.parseInt(prefs.getString("port_key", "10001"));

        connection = new ConnectionThread(mContext, ip, port);
        connection.start();

    }


    /**
     * @return if obtained the user location successfully
     */
    public boolean getUserActualLocation() {
        /*Check location permissions*/
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((AppCompatActivity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
        /*Enable my location in maps (point blue)
        * Consider the permissions requested bellow*/
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        /*Call to system location*/
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = null;
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null)
            location = locationManager.getLastKnownLocation(provider);
        else return false;
        /*Move camera to last known location*/
        if (location != null) {
            LatLng latLng = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );
            moveCameraTo(latLng);
            return true;
        } else return false;
    }

    /**
     * @param latLng the position to move the camera
     */
    public void moveCameraTo(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        setUserLocation(latLng);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(sDefaultZoomIn));
    }


    /**
     * @param route the route for simulate
     */
    public void simulateBusMovement(LatLng stopPosition, String route) {
        /*
        * Query to database the polylines of the input route
        * */
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
        databaseAdapter.open();
        final PolylineOptions polylineOptions = databaseAdapter.getPolylines("'" + route + "';");
        /*
        * Create a list of coordinates
        * */
        final List points = polylineOptions.getPoints();

        /*
        * Start a updater thread for bus movement and ploting
        * */
        MapUpdater mapUpdater = new MapUpdater(route, mMap, mContext, polylineOptions, points, this);
        /*
        * Add stop position to updater(polyline purposes)
        * */
        mapUpdater.setStopPosition(stopPosition);
        /*
        * Add the instance of movement thread to actual buses list
        * */
        mActualBuses.add(mapUpdater);
        mapUpdater.start();
        databaseAdapter.close();

    }

    /**
     * @param stops the list of stops
     */
    public void addNearbyStops(final ArrayList<Stop> stops) {
        mNearbyStops = stops;

        /*
        * Clean the stops marker list if is not empty
        * */
        if (!mNearbyStopsMarkers.isEmpty())
            for (int i = 0; i < mNearbyStopsMarkers.size(); i++) {
                mNearbyStopsMarkers.get(i).remove();
            }
        mNearbyStopsMarkers.clear();
        /*
        * Add stops to map and the marker list
        * */
        /*
        * Show only 10 stops, no more
        * */
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        LIMIT_STOPS_SHOWING_IN_MAP = prefs.getInt("amount_buses_showing", 10);
        int amountOfStops =
                stops.size() > LIMIT_STOPS_SHOWING_IN_MAP ?
                        LIMIT_STOPS_SHOWING_IN_MAP : stops.size();
        for (int i = 0; i < amountOfStops; i++) {
            Marker stopMarker = mMap.addMarker(new MarkerOptions()
                    .position(stops.get(i).getCoordinates())
                            /*The snippet of stop marker(change)*/
                    //// TODO: 10/08/2017 Determine the relevant data to show in snippet
                    .title("Parada cercana no. " + i)
                    .draggable(false)
                    .snippet("distancia al usuario: " +
                            new DecimalFormat("#.##").format(stops.get(i).getDistanceToUser())
                            + " km")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop_96)));
            stopMarker.setTag(i);
            mNearbyStopsMarkers.add(stopMarker);
        }
    }

    public LatLng getUserLocation() {
        return mUserLocation;
    }

    public void setUserLocation(LatLng mUserLocation) {
        this.mUserLocation = mUserLocation;
    }

    public void cleanMap() {
        /*
            * If is not empty the list of actual buses, stop them all
            * */
        if (!mActualBuses.isEmpty())
            for (int i = 0; i < mActualBuses.size(); i++) {
                if (mActualBuses.get(i).getPolyline() != null)
                    mActualBuses.get(i).getPolyline().remove();
                mActualBuses.get(i).stopMovement();
            }
    }

    /**
     * @param marker The marker clicked
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        /*
        * If is a stop
        * */
        if (marker.getTitle().startsWith(mContext.getString(R.string.stop))) {
            /*Set the actual marker selected*/
            stopMarkerSelectedId = marker.getId();
            //stopMarkerSelectedId = (int) marker.getTag();
            /*Clean all map of buses and stops*/
            cleanMap();
            /*
            * Query to database the routes than across that stop(marker clicked)
            * */
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
            databaseAdapter.open();
            String[] routes = databaseAdapter.getRoutesFromStop(marker.getPosition());
            /*
            * Starts simulating the new clicked routes in stop
            * */
            if (routes.length != 0) {
                String amountBuses = routes.length == 1 ?
                        mContext.getString(R.string.buses_by_stop_one) :
                        mContext.getString(R.string.buses_by_stop_many);
                mUI.showSnackBarWithAction(amountBuses.replace("%", String.valueOf(routes.length)),
                        Snackbar.LENGTH_LONG,
                        mContext.getString(R.string.more), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mUI.showSnackbar(mContext.getString(R.string.select_bus_for_more_details));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(sDefaultZoomOut));
                            }
                        });
                for (int i = 0; i < routes.length; i++)
                    simulateBusMovement(marker.getPosition(), routes[i]);
                databaseAdapter.close();
            }
        /*Else if the marker clicked is a bus*/
        } else {
            /*Get the ID of marker*/
            busMarkerSelectedId = marker.getId();
            /*
            * Add polylines of selected bus
            * */
            if (!mActualBuses.isEmpty())
                /*
                *   Travel around the actual buses markers list showing actually in map
                *   and trace route between stop and bus only of the selected bus marker.
                *   */
                for (int i = 0; i < mActualBuses.size(); i++)
                    if (mActualBuses.get(i).getMarker().getId().equals(marker.getId()))
                        mActualBuses.get(i).showPolyline(true);
                    else {
                        if (mActualBuses.get(i).getPolyline() != null)
                            mActualBuses.get(i).getPolyline().remove();
                        mActualBuses.get(i).showPolyline(false);
                    }
            /*
            * Delete every stop marker of the map except the stop marker selected.
            * */
            if (!mNearbyStopsMarkers.isEmpty())
                for (int i = 0; i < mNearbyStopsMarkers.size(); i++)
                    /*
                    * The last stop marker selected is determined by their variable
                    * */
                    if (!mNearbyStopsMarkers.get(i).getId().equals(stopMarkerSelectedId))
                        mNearbyStopsMarkers.get(i).remove();

        }
        return false;
    }

    /**
     * @param locationButton the mylocation floating button
     */
    public void setLocationButton(FloatingActionButton locationButton) {
        this.locationButton = locationButton;
    }

    /**
     * @return the actual location
     */
    public Location getActualLocation() {
        return actualLocation;
    }

    /**
     * @param actualLocation
     */
    public void setActualLocation(Location actualLocation) {
        this.actualLocation = actualLocation;
    }

    /**
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
        setActualLocation(location);
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_button:
                getUserActualLocation();
                break;
            case R.id.search_stops_button:
                cleanMap();
                showCloseStops(0.7f, mMap.getCameraPosition().target);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(sDefaultZoomIn));
                break;
        }
    }

    /**
     * @param radius       the radius for show stops
     * @param userLocation the location reference
     */
    public void showCloseStops(float radius, LatLng userLocation) {
        try {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
            databaseAdapter.open();
            addNearbyStops(
                    databaseAdapter.getNearbyStops(radius, userLocation)
            );
            databaseAdapter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSearchStopButton(FloatingActionButton searchStopButton) {
        this.mSearchStopButton = searchStopButton;
    }

    public void getDefaultLocation() {
        moveCameraTo(new LatLng(LEON_GTO_LAT, LEON_GTO_LNG));
    }

    private Marker actualBus;

    private LatLng lastPosition;

    public void addCloseBus() {
        mMarkerMovement = new MarkerMovement(mMap);
        final MapCalculator mMapCalculator = new MapCalculator();

        final LatLng busPosition = new LatLng(
                GlobalData.getInstance().getActualLatitude(),
                GlobalData.getInstance().getActualLongitude()
        );
        actualBus = mMap.addMarker(new MarkerOptions()
                .position(busPosition)
                .title("Ruta: X62 Prototipo")
                .draggable(false)
                //.infoWindowAnchor(40,40)
                .snippet("Distancia al usuario: " +
                        new DecimalFormat("#.##").format(mMapCalculator.getDistanceBetweenTwoCoordinates(
                                MapAdapter.this.getUserLocation(), busPosition)
                        ) + " km" + "\n" +
                        "Cant. de pasajeros a bordo: " + /*((int) (Math.random() * 45))*/ + (GlobalData.getInstance().getAmountPassengers())
                        + "\nAsientos disponibles: " + (30-GlobalData.getInstance().getAmountPassengers()<0?0:30-GlobalData.getInstance().getAmountPassengers())/*((int) (Math.random() * 10))*/)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_ic_aux_96))
        );
        setLastPosition(busPosition);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    final LatLng busPosition = new LatLng(
                            GlobalData.getInstance().getActualLatitude(),
                            GlobalData.getInstance().getActualLongitude()
                    );
                    ((MainActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng) mPoints.get(finalI)));
                            if (actualBus.isInfoWindowShown()) actualBus.showInfoWindow();

                            actualBus.setSnippet("Distancia al usuario: " +
                                    new DecimalFormat("#.##").format(mMapCalculator.getDistanceBetweenTwoCoordinates(
                                            MapAdapter.this.getUserLocation(), busPosition)
                                    ) + " km" + "\n" +
                                    "Cant. de pasajeros a bordo: " + /*((int) (Math.random() * 45))*/ +(GlobalData.getInstance().getAmountPassengers())
                                    + "\nAsientos disponibles: " + (30 - GlobalData.getInstance().getAmountPassengers() < 0 ? 0 : 30 - GlobalData.getInstance().getAmountPassengers())/*((int) (Math.random() * 10))*/);
                            mMarkerMovement
                                    .rotateMarker(actualBus,
                                            mMapCalculator.getAngleBetweenTwoCoordinates
                                                    (getLastPosition(), busPosition));
                            mMarkerMovement.moveMarker(actualBus, busPosition);
                            setLastPosition(busPosition);
                        }
                    });
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public LatLng getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(LatLng lastPosition) {
        this.lastPosition = lastPosition;
    }
}
