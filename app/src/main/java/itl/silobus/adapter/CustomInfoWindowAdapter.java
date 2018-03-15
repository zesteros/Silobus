package itl.silobus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import itl.silobus.R;

/**
 * Created by Angelo on 03/08/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context mContext;

    public CustomInfoWindowAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LinearLayout info = new LinearLayout(mContext);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(mContext);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());


        TextView snippet = new TextView(mContext);
        snippet.setGravity(Gravity.CENTER);
        snippet.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        snippet.setText(marker.getSnippet());
        info.addView(title);
        info.addView(snippet);

        return info;
    }
}
