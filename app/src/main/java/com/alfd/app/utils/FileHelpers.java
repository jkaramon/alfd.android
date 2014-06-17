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
import java.util.ArrayList;
import java.util.List;
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

    public static File createTempProductImageFile(Context ctx, String imageType, String barCode, String barType) {
        String fileName = getTempImageName(imageType, barCode, barType);
        File storageDir = getTempImageDir(ctx);
        return createTempFile(fileName, IMG_EXT, storageDir);
    }

    public static File createTempProductVoiceFile(Context ctx, String barCode, String barType) {
        String fileName = getTempVoiceName(barCode, barType);
        File storageDir = getTempVoiceDir(ctx);
        return createTempFile(fileName, AUDIO_EXT, storageDir);
    }

    public static File getProductVoiceFile(Context ctx, String barCode, String barType) {


        return new File(getProductVoicesDir(ctx, barCode, barType), getProductVoiceName(generateUUID()));
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static File[] getProductImageTempFiles(Context ctx, final String imageType, final String barCode, final String barType) {
        File dir = getTempImageDir(ctx);
        FilenameFilter filter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(name.startsWith(getTempImageName(imageType, barCode, barType))) {
                    return true;
                }
                return false;
            }
        };
        return listFiles(dir, filter);
    }
    public static File[] getProductVoiceTempFiles(Context ctx, final String barCode, final String barType) {
        File dir = getTempVoiceDir(ctx);
        FilenameFilter filter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(name.startsWith(getTempVoiceName(barCode, barType))) {
                    return true;
                }
                return false;
            }
        };
        return listFiles(dir, filter);
    }

    public static File[] getProductImageFiles(Context ctx, String barCode, String barType, ImgSize size) {
        File dir = getProductImageDir(ctx, barCode, barType, size);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return true;
            }
        };
        return listFiles(dir, filter);
    }

    public static File[] getProductVoiceFiles(Context ctx, String barCode, String barType) {
        File dir = getProductVoicesDir(ctx, barCode, barType);
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


    private static File getProductBaseDir(Context ctx) {
        return getBaseFolder(ctx, "products");
    }
    private static File getProductVoicesDir(Context ctx, String barCode, String barType) {
        File baseDir = getProductBaseDir(ctx);
        File productDir = new File(baseDir, String.format("%s.%s", barCode, barType));
        return new File(productDir, "voices");
    }

    private static File getProductImageDir(Context ctx, String barCode, String barType, ImgSize size) {
        File baseDir = getProductBaseDir(ctx);
        File productDir = new File(baseDir, String.format("%s.%s", barCode, barType));

        return new File(new File(productDir, "images"), getImageSizeName(size));
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
        // DEBUG-ONLY: return ctx.getExternalFilesDir(subFolder);
        return new File(new File(Environment.getExternalStorageDirectory(), "ALFD"), subFolder);
    }

    private static String getTempImageName(String imageType, String barCode, String barType) {
        return String.format("temp.%s.%s.%s", imageType, barCode, barType);
    }
    private static String getTempVoiceName(String barCode, String barType) {
        return String.format("temp.%s.%s", barCode, barType);
    }
    private static String getSyncImageName(String sequenceId, String barCode, String barType, String imageType) {
        return String.format("%s.%s.%s.%s%s", barCode, barType, imageType, sequenceId, IMG_EXT);
    }
    private static String getProductImageName(String sequenceId, String imageType) {
        return String.format("%s.%s%s", imageType, sequenceId, IMG_EXT);
    }

    private static String getSyncVoiceName(  String barCode, String barType, String sequenceId) {
        return String.format("%s.%s.%s%s", barCode, barType, sequenceId, AUDIO_EXT);
    }
    private static String getProductVoiceName(String sequenceId) {
        return String.format("%s%s", sequenceId, AUDIO_EXT);
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




    public static boolean copyTempImagesToSync(Context ctx, String barCode, String barType) {
        File[] fOverview = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode, barType);
        File[] fIngredients = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode, barType);

        for (int i = 0; i<fOverview.length; i++) {
            boolean result = copyFile(fOverview[i], new File(getSyncImageDir(ctx), getSyncImageName(generateUUID(), barCode, barType, ProductImageTypes.OVERVIEW)));
            if (result == false) {
                return false;
            }
        }
        for (int i = 0; i<fIngredients.length; i++) {
            boolean result = copyFile(fIngredients[i], new File(getSyncImageDir(ctx), getSyncImageName(generateUUID(), barCode, barType, ProductImageTypes.INGREDIENTS)));
            if (result == false) {
                return false;
            }
        }
        return true;
    }
    public static boolean copyTempVoicesToSync(Context ctx, String barCode, String barType) {
        File[] files = getProductVoiceTempFiles(ctx,barCode, barType);
        for (int i = 0; i<files.length; i++) {
            boolean result = copyFile(files[i], new File(getSyncVoiceDir(ctx), getSyncVoiceName(barCode, barType, generateUUID())));
            if (result == false) {
                return false;
            }
        }
        return true;

    }
    public static File[] moveTempVoiceNotesToProductDir(Context ctx, String barCode, String barType) {
        File[] files = getProductVoiceTempFiles(ctx,barCode, barType);
        File voicesDir = getProductVoicesDir(ctx, barCode, barType);
        if (voicesDir.exists() == false) {
            voicesDir.mkdirs();
        }
        List<File> movedFiles = new ArrayList<File>();
        for (File file : files) {
            boolean result = moveFile(file, new File(voicesDir, getProductVoiceName(generateUUID())));
            if (result == false) {
                return listToArray(movedFiles);
            }
            movedFiles.add(file);
        }
        return listToArray(movedFiles);
    }

    private static File[] listToArray(List<File> list) {
        return list.toArray(new File[list.size()]);
    }
    public static File[] createProductImagesFromTempImages(Context ctx, String barCode, String barType) {
        File[] fOverview = getProductImageTempFiles(ctx, ProductImageTypes.OVERVIEW, barCode, barType);
        File[] fIngredients = getProductImageTempFiles(ctx, ProductImageTypes.INGREDIENTS, barCode, barType);
        List<File> movedFiles = new ArrayList<File>();
        for (File f : fOverview) {
            if (createProductImagesFromTempImage(ctx, ProductImageTypes.OVERVIEW, barCode, barType, f, generateUUID()) == false) {
                return listToArray(movedFiles);
            }
            movedFiles.add(f);
        }
        for (File f : fIngredients) {
            if (createProductImagesFromTempImage(ctx, ProductImageTypes.INGREDIENTS, barCode, barType, f, generateUUID()) == false) {
                return listToArray(movedFiles);
            }
            movedFiles.add(f);
        }
        return listToArray(movedFiles);
    }

    private static boolean createProductImagesFromTempImage(Context ctx, String imageType, String barCode, String barType, File source, String imgIndex ) {
        resizeAndCopyImage(ctx, source, ImgSize.LARGE, imageType, barCode, barType, imgIndex);
        resizeAndCopyImage(ctx, source, ImgSize.SMALL, imageType, barCode, barType, imgIndex);
        return deleteFile(source);

    }

    private static void resizeAndCopyImage(Context ctx, File source, ImgSize imgSize, String imageType, String barCode, String barType, String imgIndex) {
        File productImageDir = getProductImageDir(ctx, barCode, barType, imgSize);
        if (productImageDir.exists() == false) {
            productImageDir.mkdirs();
        }
        File target = new File(productImageDir, getProductImageName(imgIndex, imageType));
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
        InputStream in;
        OutputStream out;
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
