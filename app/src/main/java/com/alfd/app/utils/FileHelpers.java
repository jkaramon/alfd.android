package com.alfd.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.app.Application;
import com.alfd.app.activities.NewProductActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class FileHelpers {
    public static File createTempProductImageFile(Context ctx, String imageType, String barCode) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getTempImageName(imageType, barCode);
        imageFileName += "_" + timeStamp;
        File storageDir = getTempImageDir(ctx);

        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }



    public static Boolean saveImageFile(File file, Bitmap content) {
        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            content.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static File[] getProductImageTempFiles(Context ctx, final String imageType, final String barCode) {
        File dir = getTempImageDir(ctx);
        FilenameFilter filter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(name.startsWith(getTempImageName(imageType, barCode))) {
                    return true;
                }
                return false;
            }
        };
        return dir.listFiles(filter);
    }
    private static File getTempImageDir(Context ctx) {
        return ctx.getExternalFilesDir("TempImages");
    }

    private static String getTempImageName(String imageType, String barCode) {
        return String.format("temp_%s_%s", imageType, barCode);
    }
}
