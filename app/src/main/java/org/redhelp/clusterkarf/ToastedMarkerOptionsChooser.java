package org.redhelp.clusterkarf;

/**
 * Created by harshis on 7/9/14.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;

import org.redhelp.app.R;
import org.redhelp.common.types.SearchItemTypes;

import java.lang.ref.WeakReference;

public class ToastedMarkerOptionsChooser extends MarkerOptionsChooser {

    private final WeakReference<Context> contextRef;
    private final InputPoint currentLocation;
    private final Paint clusterPaintLarge;
    private final Paint clusterPaintMedium;
    private final Paint clusterPaintSmall;

    public ToastedMarkerOptionsChooser(Context context, InputPoint currentLocation) {
        this.contextRef = new WeakReference<Context>(context);
        this.currentLocation = currentLocation;

        Resources res = context.getResources();

        clusterPaintMedium = new Paint();
        clusterPaintMedium.setColor(Color.WHITE);
        clusterPaintMedium.setAlpha(255);
        clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
        clusterPaintMedium.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
        clusterPaintMedium.setTextSize(res.getDimension(R.dimen.cluster_text_size_medium));

        clusterPaintSmall = new Paint(clusterPaintMedium);
        clusterPaintSmall.setTextSize(res.getDimension(R.dimen.cluster_text_size_small));

        clusterPaintLarge = new Paint(clusterPaintMedium);
        clusterPaintLarge.setTextSize(res.getDimension(R.dimen.cluster_text_size_large));
    }

    @Override
    public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
        Context context = contextRef.get();
        if (context != null) {
            Resources res = context.getResources();
            boolean isCluster = clusterPoint.size() > 1;
            boolean hasCurrentLocation = clusterPoint.containsInputPoint(currentLocation);
            BitmapDescriptor icon;
            String title;
            if (isCluster) {
                title = res.getQuantityString(R.plurals.count_points, clusterPoint.size(), clusterPoint.size());
                int clusterSize = clusterPoint.size();
                if (hasCurrentLocation) {
                    icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_map_pin_cluster_toaster, clusterSize));
                    title = res.getString(R.string.including_two_toasters, title);
                } else {
                    icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_map_pin_cluster, clusterSize));
                    title = res.getQuantityString(R.plurals.count_points, clusterSize, clusterSize);
                }
            } else {
                MarkerData data = (MarkerData)clusterPoint.getPointAtOffset(0).getTag();
                if (hasCurrentLocation) {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_toaster);
                    title = data.getLabel();
                } else {
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin);
                    String baseTitle = "";

                    if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.BLOOD_PROFILE))
                        baseTitle = "Donor: ";
                    else if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.BLOOD_REQUEST))
                         baseTitle = "Blood Request: ";
                    else if(data.getItemType() != null && data.getItemType().equals(SearchItemTypes.EVENTS))
                        baseTitle = "Event: ";
                    title = baseTitle + data.getLabel();
                }
            }
            markerOptions.icon(icon);
            markerOptions.title(title);
            markerOptions.anchor(0.5f, 1.0f);
        }
    }

    @SuppressLint("NewApi")
    private Bitmap getClusterBitmap(Resources res, int resourceId, int clusterSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
        if (bitmap.isMutable() == false) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        Canvas canvas = new Canvas(bitmap);

        Paint paint = null;
        float originY;
        if (clusterSize < 100) {
            paint = clusterPaintLarge;
            originY = bitmap.getHeight() * 0.64f;
        } else if (clusterSize < 1000) {
            paint = clusterPaintMedium;
            originY = bitmap.getHeight() * 0.6f;
        } else {
            paint = clusterPaintSmall;
            originY = bitmap.getHeight() * 0.56f;
        }

        canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);

        return bitmap;
    }
}