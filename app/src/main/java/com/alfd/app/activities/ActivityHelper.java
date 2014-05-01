package com.alfd.app.activities;

import android.app.Activity;
import android.view.ViewGroup;

import com.alfd.app.fonts.FontUtils;

/**
 * Created by karamon on 25. 4. 2014.
 */
public class ActivityHelper {
    public static void setFonts(Activity activity)
    {
        FontUtils.setRobotoFont(activity, (ViewGroup) activity.getWindow().getDecorView());

    }
}
