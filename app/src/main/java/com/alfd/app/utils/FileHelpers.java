package com.alfd.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.alfd.app.LogTags;
import com.alfd.app.ProductImageTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class FileHelpers {

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static void ensureDirectoryStructureExists(Context ctx) {
        ensureDir(getTempImageDir(ctx));
        ensureDir(getTempVoiceDir(ctx));
        ensureDir(getSyncImageDir(ctx));
        ensureDir(getSyncVoiceDir(ctx));
        ensureDir(getProductImageDir(ctx));
        ensureDir(getProductVoiceDir(ctx));

    }
    public static boolean ensureDir(File folder) {
        boolean success = true;
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return true;
    }

    public static File createTempProductImageFile(Context ctx, String imageType, String barCode) {
        String fileName = getTempImageName(imageType, barCode);
        File storageDir = getTempImageDir(ctx);
        return createTempFile(fileName, ".jpg", storageDir);
    }

    public static File createTempProductVoiceFile(Context ctx, String barCode) {
        String fileName = getTempVoiceName(barCode);
        File storageDir = getTempVoiceDir(ctx);
        return createTempFile(fileName, ".mp4", storageDir);
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
    public static File[] getProductVoiceTempFiles(Context ctx, final String barCode) {
        File dir = getTempVoiceDir(ctx);
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


    private static File getProductImageDir(Context ctx) { return getBaseFolder(ctx, "ProductImages"); }
    private static File getProductVoiceDir(Context ctx) { return getBaseFolder(ctx, "ProductVoices"); }
    private static File getSyncVoiceDir(Context ctx) { return getBaseFolder(ctx, "Upload/Voices"); }
    private static File getSyncImageDir(Context ctx) {
        return getBaseFolder(ctx, "Upload/Images");
    }
    private static File getTempImageDir(Context ctx) {
        return getBaseFolder(ctx, "TempImages");
    }
    private static File getTempVoiceDir(Context ctx) {
        return getBaseFolder(ctx, "TempVoices");
    }


    private static File getBaseFolder(Context ctx, String subFolder) {
        // return ctx.getExternalFilesDir(subFolder);
        return new File(new File(Environment.getExternalStorageDirectory(), "ALFD"), subFolder);
    }

    private static String getTempImageName(String imageType, String barCode) {
        return String.format("temp.%s.%s", imageType, barCode);
    }
    private static String getTempVoiceName(String barCode) {
        return String.format("temp.%s", barCode);
    }
    private static String getSyncImageName(int id, UUID productUUID, String imageType) {
        return String.format("%s.%s.%d.jpg", productUUID.toString(), imageType, id);
    }
    private static String getSyncVoiceName(int id, UUID productUUID) {
        return String.format("%s.%d.mp4", productUUID.toString(), id);
    }
    private static String getProductImageName(int id, UUID productUUID, String imageType) {
        return String.format("%s.%s.%d.jpg", productUUID.toString(), imageType, id);
    }
    private static String getProductThumbImageName(int id, UUID productUUID, String imageType) {
        return String.format("%s.%s.%.thumb.%d.jpg", imageType, productUUID.toString(), id);
    }
    private static String getProductVoiceName(int id, UUID productUUID) {
        return String.format("%s.%d.mp4", productUUID.toString(), id);
    }

    public static boolean copyTempImagesToSync(Context ctx, UUID productUUID, String barCode) {
        File[] fOverview = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);
        File[] fIngredients = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);
        int idx = 1;
        for (File f : fOverview) {
            boolean result = copyFile(f, new File(getSyncImageDir(ctx), getSyncImageName(idx, productUUID, ProductImageTypes.OVERVIEW)));
            idx++;
            if (result == false) {
                return false;
            }
        }
        idx = 1;
        for (File f : fIngredients) {
            boolean result = copyFile(f, new File(getSyncImageDir(ctx), getSyncImageName(idx, productUUID, ProductImageTypes.INGREDIENTS)));
            idx++;
            if (result == false) {
                return false;
            }
        }
        return true;
    }
    public static boolean copyTempVoicesToSync(Context ctx, UUID productUUID, String barCode) {
        File[] files = getProductVoiceTempFiles(ctx,barCode);
        int idx = 1;
        for (File f : files) {
            boolean result = copyFile(f, new File(getSyncVoiceDir(ctx), getSyncVoiceName(idx, productUUID)));
            if (result == false) {
                return false;
            }
        }
        return true;

    }
    public static boolean moveTempVoiceNotesToProductDir(Context ctx, UUID productUUID, String barCode) {
        File[] files = getProductVoiceTempFiles(ctx,barCode);
        int idx = 1;
        for (File f : files) {
            boolean result = copyFile(f, new File(getSyncVoiceDir(ctx), getSyncVoiceName(idx, productUUID)));
            if (result == false) {
                return false;
            }
        }
        return true;
    }


    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static boolean moveFile(File inputFile, File outputFile) {
        if (copyFile(inputFile, outputFile)) {
            return deleteFile(inputFile);
        }
        return false;
    }

    public static boolean copyFile(File inputFile, File outputFile) {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputFile.getParent());
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;
            return true;
        }

        catch (FileNotFoundException exc) {
            Log.e(LogTags.FILE_STORAGE, exc.getMessage());

        }
        catch (Exception e) {
            Log.e(LogTags.FILE_STORAGE, e.getMessage());
        }
        return false;

    }


}
