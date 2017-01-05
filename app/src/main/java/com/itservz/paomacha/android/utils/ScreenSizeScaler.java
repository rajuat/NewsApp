package com.itservz.paomacha.android.utils;

import android.content.res.Resources;

/**
 * Created by Raju on 12/20/2016.
 */

public class ScreenSizeScaler {

    private final Resources resources;

    public ScreenSizeScaler(Resources resources) {
        this.resources = resources;
    }

    public int getdpAspixel(int dp) {
        float scale = resources.getDisplayMetrics().density;
        int dpAsPixels = (int) (dp * scale + 0.5f);
        return dpAsPixels;
    }
}
