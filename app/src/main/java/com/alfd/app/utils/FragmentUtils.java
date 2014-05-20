package com.alfd.app.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.alfd.app.FragmentFactory;
import com.alfd.app.R;

/**
 * Created by karamon on 19. 5. 2014.
 */
public class FragmentUtils {
    public static Fragment change(FragmentActivity activity, String tag, FragmentFactory factory)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag(tag);
        if (f == null) {
            f = factory.create();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, f, tag)
                .commit();

        return f;
    }
}
