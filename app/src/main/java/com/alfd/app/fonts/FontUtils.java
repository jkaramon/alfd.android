/**
 * @author Anton Averin
 * mailto:
 */
package com.alfd.app.fonts;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
public class FontUtils {
    public static interface FontTypes {
        public static String LIGHT = "Light";
        public static String BOLD = "Bold";
    }
    /**
     * map of font types to font paths in assets
     */
    private static HashMap<String, String> fontMap = new HashMap();
    static {
        fontMap.put(FontTypes.LIGHT, "fonts/roboto/Roboto-Light.ttf");
        fontMap.put(FontTypes.BOLD, "fonts/roboto/Roboto-Bold.ttf");
    }
    /* cache for loaded Roboto typefaces*/
    private static HashMap<String, Typeface> typefaceCache = new HashMap();
    /**
     * Creates Roboto typeface and puts it into cache
     * @param context
     * @param fontType
     * @return
     */
    private static Typeface getRobotoTypeface(Context context, String fontType) {
        String fontPath = fontMap.get(fontType);
        if (!typefaceCache.containsKey(fontType))
        {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }
        return typefaceCache.get(fontType);
    }
    /**
     * Gets roboto typeface according to passed typeface style settings.
     * Will get Roboto-Bold for Typeface.BOLD etc
     * @param context
     * @param originalTypeface
     * @return
     */
    private static Typeface getRobotoTypeface(Context context, Typeface originalTypeface) {
        String robotoFontType = FontTypes.LIGHT; //default Light Roboto font
        if (originalTypeface != null) {
            int style = originalTypeface.getStyle();
            switch (style) {
                case Typeface.BOLD:
                    robotoFontType = FontTypes.BOLD;
            }
        }
        return getRobotoTypeface(context, robotoFontType);
    }
    /**
     * Walks ViewGroups, finds TextViews and applies Typefaces taking styling in consideration
     * @param context - to reach assets
     * @param view - root view to apply typeface to
     */
    public static void setRobotoFont(Activity context, View view)
    {
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
            {
                setRobotoFont(context, ((ViewGroup)view).getChildAt(i));
            }
        }
        else if (view instanceof TextView)
        {
            TextView tv = (TextView) view;
            Typeface currentTypeface = tv.getTypeface();
            Typeface tf = getRobotoTypeface(context, currentTypeface);
            Log.v("SET_FONTS", String.format("Setting typeface font %s on TextView %s", tf.toString(), tv.getText()));
            tv.setTypeface(tf);
        }
        else if (view instanceof Button)
        {
            Button btn = (Button) view;
            Typeface currentTypeface = btn.getTypeface();
            Typeface tf = getRobotoTypeface(context, currentTypeface);
            Log.v("SET_FONTS", String.format("Setting typeface font %s on Button %s", tf.toString(), btn.getText()));
            btn.setTypeface(getRobotoTypeface(context, currentTypeface));
        }
    }
}