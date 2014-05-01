package com.alfd.app.intents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.activities.NewProductActivity;
import com.alfd.app.data.Product;

public class IntentFactory {

    public static Intent createNewProduct(Activity activity, Product p) {
        Intent i = new Intent(activity, NewProductActivity.class);
        i.putExtra(SC.BAR_CODE, p.BarCode);
        i.putExtra(SC.BAR_TYPE, p.BarType);
        return i;
    }

    public static Intent createTakePictureIntent(Activity activity) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    }

}
