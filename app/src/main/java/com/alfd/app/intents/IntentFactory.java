package com.alfd.app.intents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.activities.MainActivity;
import com.alfd.app.activities.NewProductActivity;
import com.alfd.app.activities.ProductDetailActivity;
import com.alfd.app.activities.TakePictureActivity;
import com.alfd.app.data.Product;

import java.io.File;

public class IntentFactory {

    public static Intent createNewProduct(Activity activity, Product p) {
        Intent i = new Intent(activity, NewProductActivity.class);
        i.putExtra(SC.BAR_CODE, p.BarCode);
        i.putExtra(SC.BAR_TYPE, p.BarType);
        return i;
    }

    public static Intent navigateProduct(Context context, Product p) {
        Intent i = new Intent(context, ProductDetailActivity.class);
        i.putExtra(SC.PRODUCT_ID, p.getId());
        return i;
    }





    public static Intent takePicture(Activity activity, String sourceTag, File file) {
        Intent i = new Intent(activity, TakePictureActivity.class);
        i.putExtra("source", sourceTag);
        if (i.resolveActivity(activity.getPackageManager()) != null) {
            if (file != null) {
                i.putExtra(SC.IMAGE_FULL_NAME, file.getAbsolutePath());
            }
            activity.startActivityForResult(i, RequestCodes.IMAGE_CAPTURE);

        }
        return i;

    }


    public static void takeProductPicture(Activity activity, String barCode, String barType, String overview) {
    }
}
