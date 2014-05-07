package com.alfd.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.alfd.app.LogTags;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class FileHelpers {

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static void ensureDirectoryStructureExists(Context ctx) {
        ensureDir(getTempImageDir(ctx));
        ensureDir(getTempVoiceDir(ctx));

    }
    public static boolean ensureDir(File folder) {
        boolean success = true;
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return true;
    }

    public static File createTempProductImageFile(Context ctx, String imageType, String barCode) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
        String fileName = getTempImageName(imageType, barCode);
        fileName += "_" + timeStamp;
        File storageDir = getTempImageDir(ctx);
        return createTempFile(fileName, ".jpg", storageDir);
    }

    public static File getTempProductVoiceFile(Context ctx, String barCode) {
        String fileName = getTempVoiceName(barCode) + ".mp4";
        File storageDir = getTempVoiceDir(ctx);
        return new File(storageDir, fileName);
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
    public static Boolean saveVoiceFile(File file, Bitmap content) {
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


    public static File[] getProductVoiceTempFiles(Context ctx, final String barCode) {
        File dir = getTempImageDir(ctx);
        FilenameFilter filter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(name.startsWith(getTempVoiceName(barCode))) {
                    return true;
                }
                return false;
            }
        };
        return listFiles(dir, filter);
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
        return listFiles(dir, filter);
    }


    private static File[] listFiles(File dir, FilenameFilter filter) {
        if (dir.isDirectory() == false) {
            Log.w(LogTags.FILE_STORAGE, String.format("Parameter dir is not valid directory. (%s)", dir.getAbsolutePath()));
        }
        return dir.listFiles(filter);
    }

    private static File createTempFile(String fileName, String extension, File storageDir) {
        File file = null;
        try {
            file = File.createTempFile(fileName, extension, storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }




    private static File getTempImageDir(Context ctx) {
        return getBaseFolder(ctx, "TempImages");
    }
    private static File getTempVoiceDir(Context ctx) {
        return getBaseFolder(ctx, "TempVoices");
    }
    private static File getBaseFolder(Context ctx, String subFolder) {
        // return ctx.getExternalFilesDir(subFolder);
        return new File(Environment.getExternalStorageDirectory(), subFolder);
    }

    private static String getTempImageName(String imageType, String barCode) {
        return String.format("temp_%s_%s", imageType, barCode);
    }
    private static String getTempVoiceName(String barCode) {
        return String.format("temp_%s", barCode);
    }
}
