package itl.silobus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import itl.silobus.util.calculator.MapCalculator;
import itl.silobus.util.map.Stop;

/**
 * Created by Angelo on 31/05/2017.
 */

public class DatabaseAdapter {

    private final Context mContext;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public DatabaseAdapter(Context context) {
        this.mContext = context;
    }

    public void open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        try {
            mDbHelper.createDatabase();
            mDatabase = mDbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        mDbHelper.close();
    }


    public boolean importDb(String tableName, String[] allColumns) {
        File exportDir = new File(Environment.getExternalStorageDirectory() + "/"/* + mContext.getString(R.string.app_name)*/, "");
        if (!exportDir.exists()) exportDir.mkdirs();

        File file = new File(exportDir.getAbsolutePath(), "rutas.csv");
        FileReader fileReader = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            fileReader = new FileReader(file);
            BufferedReader buffer = new BufferedReader(fileReader);
            String line = "";
            //String tableName = mDbHelper.TABLE_PPM;
            String columns = "";
            for (int i = 0; i < allColumns.length; i++)
                if (i == allColumns.length - 1) columns += allColumns[i];
                else columns += allColumns[i] + ",";

            Log.d("columns", columns);
            String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";

            db.beginTransaction();
            while ((line = buffer.readLine()) != null) {
                StringBuilder sb = new StringBuilder(str1);
                String[] str = line.split(",");
                sb.append(str[0] + ",");//ID
                sb.append("'" + str[1] + "',");//NAME
                sb.append("'" + str[2] + "',");//COORDINATE
                sb.append("'" + str[3] + "');");//TYPE
                try {
                    db.execSQL(sb.toString());
                } catch (Exception e) {
                    db.endTransaction();
                    //e.printStackTrace();
                    return false;
                }
                Log.d("TRANSACTION", sb.toString());
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public PolylineOptions getPolylines(String route) {//X-62-I

        PolylineOptions polylineOptions = new PolylineOptions();
        Cursor cursor = mDatabase.rawQuery(
                "SELECT Ruta FROM rutas WHERE idRuta = " + route, null);
        cursor.moveToFirst();
        String coordinates = cursor.getString(0);
        String[] latlng = coordinates.split(" ");
        Log.d("length", latlng.length + "");
        Log.d("coordinates", coordinates);
        for (int i = 0; i < latlng.length; i++) {
            // Log.d("latlng[i]", latlng[i]);
            String val[] = latlng[i].split(",");
            polylineOptions.add(
                    new LatLng(
                            Double.valueOf(val[0]),
                            Double.valueOf(val[1])
                    )
            );
        }
        return polylineOptions;
    }

    public ArrayList<Stop> getNearbyStops(double radius, LatLng userPosition) {
        MapCalculator mapCalculator = new MapCalculator();
        Cursor cursor = mDatabase.rawQuery("SELECT Lat, Lon FROM paradas", null);
        ArrayList<Stop> stopList = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            LatLng point = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            double distance = mapCalculator.getDistanceBetweenTwoCoordinates(userPosition, point);
            if (distance <= radius){
                Stop stop = new Stop(point);
                stop.setDistanceToUser(distance);
                stopList.add(stop);
            }
        }
        Collections.sort(stopList, new Comparator<Stop>() {
            @Override
            public int compare(Stop o1, Stop o2) {
                return o1.getDistanceToUser() > o2.getDistanceToUser() ? 1 :
                        (o1.getDistanceToUser() < o2.getDistanceToUser() ) ? -1 : 0;
            }

        });
        return stopList;
    }

    public String[] getRoutesFromStop(LatLng position) {
        Cursor cursor = mDatabase.rawQuery("select routes.idRuta" +
                " from (rutas routes join paradas_rutas stops_routes on routes.Id = stops_routes.idRuta)" +
                " join paradas stops on stops.Id = stops_routes.idParada " +
                "where stops.Lat = " + position.latitude + " and stops.Lon = " + position.longitude + ";", null);
        String[] routes = new String[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            routes[i] = cursor.getString(0);
            i++;
        }
        return routes;

    }
}
