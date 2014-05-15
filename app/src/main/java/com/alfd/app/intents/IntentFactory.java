package com.alfd.app.intents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.activities.NewProductActivity;
import com.alfd.app.data.Product;

import java.io.File;

public class IntentFactory {

    public static Intent createNewProduct(Activity activity, Product p) {
        Intent i = new Intent(activity, NewProductActivity.class);
        i.putExtra(SC.BAR_CODE, p.BarCode);
        i.putExtra(SC.BAR_TYPE, p.BarType);
        return i;
    }

    public static Intent takePicture(Activity activity, File file) {
        return takePicture(activity, null, file);
    }

    public static Intent takePicture(Fragment fragment, File file) {
        return takePicture(fragment.getActivity(), fragment, file);

    }

    private static Intent takePicture(Activity activity, Fragment fragment, File file) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(activity.getPackageManager()) != null) {
            if (file != null) {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }
            if (fragment == null) {
                activity.startActivityForResult(i, RequestCodes.IMAGE_CAPTURE);
            }
            else {
                fragment.startActivityForResult(i, RequestCodes.IMAGE_CAPTURE);
            }
        }
        return i;

    }

}
