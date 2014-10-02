package org.redhelp.util;

import android.graphics.Bitmap;

/**
 * Created by harshis on 9/26/14.
 */
public class ImageHelper {
    public static int getSizeInBytes(Bitmap bitmap) {
        if(AndroidVersion.isHoneycombMr2OrHigher()) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
}
