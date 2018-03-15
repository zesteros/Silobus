package itl.silobus.util.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import itl.silobus.R;

/**
 * Created by Angelo on 10/08/2017.
 */

public class CustomProgressDialog extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private ProgressDialog mDialog;
    private String mText;
    private LatLng mPosition;

    public CustomProgressDialog(Context context) {
        this.mContext = context;
    }


    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(getText());
        mDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        Address address = null;
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mContext, Locale.getDefault());
        /**
         *
         Geocoder geocoder;
         List<Address> addresses;
         geocoder = new Geocoder(this, Locale.getDefault());

         addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

         String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
         String city = addresses.get(0).getLocality();
         String state = addresses.get(0).getAdminArea();
         String country = addresses.get(0).getCountryName();
         String postalCode = addresses.get(0).getPostalCode();
         String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
         */


        try {
            addresses = geocoder.getFromLocation(getPosition().latitude, getPosition().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null)
            address = addresses.get(0);
        if (address != null)
            result = address.getAddressLine(0);
        if (result != null) return result;

        return mContext.getString(R.string.doesnt_find_stop);
    }

    @Override
    protected void onPostExecute(String s) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog
                .setPositiveButton(R.string.dialog_accept, null)
                .setTitle(R.string.most_close_stop)
                .setMessage(s).show();
        mDialog.dismiss();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public LatLng getPosition() {
        return mPosition;
    }

    public void setPosition(LatLng position) {
        this.mPosition = position;
    }
}
