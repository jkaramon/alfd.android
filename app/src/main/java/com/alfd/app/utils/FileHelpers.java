package com.alfd.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import com.alfd.app.ImgSize;
import com.alfd.app.LogTags;
import com.alfd.app.ProductImageTypes;
import com.alfd.app.activities.BaseProductActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by karamon on 28. 4. 2014.
 */
public class FileHelpers {

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMG_EXT = ".jpg";
    public static final String AUDIO_EXT = ".m4a";


    public static void ensureDirectoryStructureExists(Context ctx) {
        ensureDir(getTempImageDir(ctx));
        ensureDir(getTempVoiceDir(ctx));
        ensureDir(getSyncImageDir(ctx));
        ensureDir(getSyncVoiceDir(ctx));
        ensureDir(getProductBaseDir(ctx));

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
        return createTempFile(fileName, IMG_EXT, storageDir);
    }

    public static File createTempProductVoiceFile(Context ctx, String barCode) {
        String fileName = getTempVoiceName(barCode);
        File storageDir = getTempVoiceDir(ctx);
        return createTempFile(fileName, AUDIO_EXT, storageDir);
    }

    public static File createProductVoiceFile(Context ctx, UUID uniqueId) {
        File[] files = getProductVoiceFiles(ctx, uniqueId);
        int idx = files.length + 1;
        return new File(getProductVoicesDir(ctx, uniqueId), getProductVoiceName(idx));
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

    public static File[] getProductImageFiles(Context ctx, UUID productUUID, ImgSize size) {
        File dir = getProductImageDir(ctx, productUUID, size);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return true;
            }
        };
        return listFiles(dir, filter);
    }

    public static File[] getProductVoiceFiles(Context ctx, UUID productUUID) {
        File dir = getProductVoicesDir(ctx, productUUID);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return true;
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


    private static File getProductBaseDir(Context ctx) { return getBaseFolder(ctx, "products"); }
    private static File getProductVoicesDir(Context ctx, UUID productUUID) {
        File productBaseDir = getProductBaseDir(ctx);
        File productVoiceDir = new File(productBaseDir, productUUID.toString());
        return new File(productVoiceDir, "voices");
    }

    private static File getProductImageDir(Context ctx, UUID productUUID, ImgSize size) {
        File productBaseDir = getProductBaseDir(ctx);
        File productImageDir = new File(productBaseDir, productUUID.toString());

        return new File(new File(productImageDir, "images"), getImageSizeName(size));
    }

    private static File getSyncVoiceDir(Context ctx) { return getBaseFolder(ctx, "upload/voices"); }
    private static File getSyncImageDir(Context ctx) {
        return getBaseFolder(ctx, "upload/images");
    }
    private static File getTempImageDir(Context ctx) {
        return getBaseFolder(ctx, "temp/images");
    }
    private static File getTempVoiceDir(Context ctx) {
        return getBaseFolder(ctx, "temp/voices");
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
        return String.format("%s.%s.%04d%s", productUUID.toString(), imageType, id, IMG_EXT);
    }
    private static String getProductImageName(int id, String imageType) {
        return String.format("%s.%04d%s", imageType, id, IMG_EXT);
    }

    private static String getSyncVoiceName(int id,  UUID productUUID) {
        return String.format("%04d.%s%s", id, productUUID.toString(), AUDIO_EXT);
    }
    private static String getProductVoiceName(int id) {
        return String.format("%04d%s", id, AUDIO_EXT);
    }



    private static String getImageSizeName(ImgSize size) {
        String imgSize;
        switch (size) {
            case THUMB:
                imgSize = "thumb";
                break;
            default:
                imgSize = "large";
                break;
        }
        return imgSize;
    }




    public static boolean copyTempImagesToSync(Context ctx, UUID productUUID, String barCode) {
        File[] fOverview = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);
        File[] fIngredients = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);

        for (int i = 0; i<fOverview.length; i++) {
            boolean result = copyFile(fOverview[i], new File(getSyncImageDir(ctx), getSyncImageName(i+1, productUUID, ProductImageTypes.OVERVIEW)));
            if (result == false) {
                return false;
            }
        }
        for (int i = 0; i<fIngredients.length; i++) {
            boolean result = copyFile(fIngredients[i], new File(getSyncImageDir(ctx), getSyncImageName(i+1, productUUID, ProductImageTypes.INGREDIENTS)));
            if (result == false) {
                return false;
            }
        }
        return true;
    }
    public static boolean copyTempVoicesToSync(Context ctx, UUID productUUID, String barCode) {
        File[] files = getProductVoiceTempFiles(ctx,barCode);
        for (int i = 0; i<files.length; i++) {
            boolean result = copyFile(files[i], new File(getSyncVoiceDir(ctx), getSyncVoiceName(i+1, productUUID)));
            if (result == false) {
                return false;
            }
        }
        return true;

    }
    public static boolean moveTempVoiceNotesToProductDir(Context ctx, UUID productUUID, String barCode) {
        File[] files = getProductVoiceTempFiles(ctx,barCode);
        File voicesDir = getProductVoicesDir(ctx, productUUID);
        if (voicesDir.exists() == false) {
            voicesDir.mkdirs();
        }
        for (int i = 0; i<files.length; i++) {
            boolean result = moveFile(files[i], new File(voicesDir, getProductVoiceName(i+1)));
            if (result == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean createProductImagesFromTempImages(Context ctx, UUID productUUID, String barCode) {
        File[] fOverview = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);
        File[] fIngredients = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode);

        for (int i = 0; i<fOverview.length; i++) {
            if (createProductImagesFromTempImage(ctx, ProductImageTypes.OVERVIEW, productUUID, fOverview[i], i+1) == false) {
                return false;
            }
        }
        for (int i = 0; i<fIngredients.length; i++) {
            if (createProductImagesFromTempImage(ctx, ProductImageTypes.INGREDIENTS, productUUID, fIngredients[i], i+1) == false) {
                return false;
            }
        }
        return true;
    }

    private static boolean createProductImagesFromTempImage(Context ctx, String imageType, UUID productUUID, File source, int imgIndex ) {
        File productImageDir = getProductImageDir(ctx, productUUID, ImgSize.LARGE);
        if (productImageDir.exists() == false) {
            productImageDir.mkdirs();
        }
        File target = new File(productImageDir, getProductImageName(imgIndex, imageType));
        resizeAndCopyImage(ctx, source, target, ImgSize.LARGE);

        productImageDir = getProductImageDir(ctx, productUUID, ImgSize.THUMB);
        if (productImageDir.exists() == false) {
            productImageDir.mkdirs();
        }
        target = new File(productImageDir, getProductImageName(imgIndex, imageType));
        resizeAndCopyImage(ctx, source, target, ImgSize.THUMB);
        return deleteFile(source);

    }

    private static void resizeAndCopyImage(Context ctx, File source, File target, ImgSize imgSize) {
        Point p = ImageResizer.getImageSize(ctx, imgSize);
        Bitmap bitmap = ImageResizer.decodeSampledBitmapFromFile(source.getAbsolutePath(), p.x, p.y, null);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {}
        }
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


    public static void clearProductTempDir(Context ctx) {
        for (File f : getTempImageDir(ctx).listFiles()) {
            f.delete();
        }
        for (File f : getTempVoiceDir(ctx).listFiles()) {
            f.delete();
        }
    }



}
