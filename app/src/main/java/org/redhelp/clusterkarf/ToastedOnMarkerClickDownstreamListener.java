package org.redhelp.clusterkarf;

/**
 * Created by harshis on 7/9/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.Marker;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.OnMarkerClickDownstreamListener;

import java.lang.ref.WeakReference;

public class ToastedOnMarkerClickDownstreamListener implements OnMarkerClickDownstreamListener {

    public interface IToastedOnMarkerClickDownstreamListener{
        public void onClickHandler(MarkerData data);
    }
    private final WeakReference<Context> contextRef;
    private final Fragment containerFragment;


    public ToastedOnMarkerClickDownstreamListener(Context context, Fragment fragment) {
        this.contextRef = new WeakReference<Context>(context);
        this.containerFragment = fragment;
    }

    @Override
    public boolean onMarkerClick(Marker marker, ClusterPoint clusterPoint) {
        Context context = contextRef.get();
        MarkerData data = null;
        if(clusterPoint != null && clusterPoint.size() == 1)
             data = (MarkerData)clusterPoint.getPointAtOffset(0).getTag();
        if (context != null && marker != null  && data != null) {
            if(containerFragment instanceof IToastedOnMarkerClickDownstreamListener){
                ((IToastedOnMarkerClickDownstreamListener)containerFragment).onClickHandler(data);
            }
            /**/
            return true;
        }
        return false;
    }

}