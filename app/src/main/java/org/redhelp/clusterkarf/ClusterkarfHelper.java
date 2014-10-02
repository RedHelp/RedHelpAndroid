package org.redhelp.clusterkarf;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.LatLng;
import com.twotoasters.clusterkraf.InputPoint;
import com.twotoasters.clusterkraf.Options;

import org.redhelp.common.BloodProfileSearchResponse;
import org.redhelp.common.BloodRequestSearchResponse;
import org.redhelp.common.EventSearchResponse;
import org.redhelp.common.types.SearchItemTypes;
import org.redhelp.interfaces.ISearchResult;
import org.redhelp.util.UiHelper;

/**
 * Created by harshis on 7/9/14.
 */
public class ClusterkarfHelper {

    public static InputPoint getInputPointFromSearchObject(ISearchResult searchResult) {
        InputPoint inputPoint = null;
        try{
            if(searchResult != null && searchResult.getLocation() != null);
            LatLng latLng = new LatLng(searchResult.getLocation().latitude, searchResult.getLocation().longitude);
            String title = searchResult.getTitle();
            Long id = searchResult.getId();
            SearchItemTypes itemType = null;
            if(searchResult instanceof BloodProfileSearchResponse)
                itemType = SearchItemTypes.BLOOD_PROFILE;
            else if(searchResult instanceof BloodRequestSearchResponse)
                itemType = SearchItemTypes.BLOOD_REQUEST;
            else if(searchResult instanceof EventSearchResponse)
                itemType = SearchItemTypes.EVENTS;

            MarkerData markerData = new MarkerData(latLng, title, id, itemType);

            inputPoint = new InputPoint(latLng, markerData);
        }catch(Exception e) {
            return null;
        }
        return inputPoint;
    }

    public static Options getOptionsObject(Fragment fragment, InputPoint inputPoint) {
        Context ctx = null;
        if(fragment != null)
            ctx = fragment.getActivity();
        else
            return null;
        Options options = new Options();
        options.setTransitionDuration(500);
        options.setTransitionInterpolator(new LinearInterpolator());
        options.setPixelDistanceToJoinCluster(UiHelper.convertDpToPixel(ctx, 50));
        options.setZoomToBoundsAnimationDuration(500);
        options.setShowInfoWindowAnimationDuration(500);

        options.setExpandBoundsFactor(0.5d);
        options.setSinglePointClickBehavior(Options.SinglePointClickBehavior.SHOW_INFO_WINDOW);
        options.setClusterClickBehavior(Options.ClusterClickBehavior.ZOOM_TO_BOUNDS);
        options.setClusterInfoWindowClickBehavior(Options.ClusterInfoWindowClickBehavior.ZOOM_TO_BOUNDS);

        //options.setZoomToBoundsPadding();

        options.setMarkerOptionsChooser(new ToastedMarkerOptionsChooser(ctx, inputPoint));
        options.setOnInfoWindowClickDownstreamListener(new ToastedOnMarkerClickDownstreamListener(ctx, fragment));
        return options;
    }
}
