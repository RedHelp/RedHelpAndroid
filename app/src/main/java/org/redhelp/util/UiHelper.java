package org.redhelp.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by harshis on 7/6/14.
 */
public class UiHelper {
    public static int convertDpToPixel(Context ctx, float dp_value) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp_value;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }


}
