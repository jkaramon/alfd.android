package com.alfd.app.data;

import android.content.Context;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.alfd.app.ImgSize;
import com.alfd.app.utils.FileHelpers;

import net.sourceforge.zbar.Symbol;

import java.io.File;

/**
 * Created by karamon on 10. 4. 2014.
 *
 */
@Table(name = "Products")
public class Product extends BaseServerModel {
    @Column
    public String Name;
    @Column
    public String BarCode;

    @Column
    public String BarType;

    @Column
    public String OverviewPhotos;
    @Column
    public String IngredientsPhotos;
    @Column
    public String Description;




    public Product(){
        super();
    }


    public String getFullBarCodeInfo() {
        return String.format("%s (%s)", BarCode, BarType);
    }
    public static Product findByBarCode(String barCode) {
        return new Select().from(Product.class).where("BarCode=?", barCode).executeSingle();
    }



    public static String mapBarCodeType(int barType) {
        switch (barType){
            case Symbol.EAN8:
                return "EAN8";
            case Symbol.EAN13:
                return "EAN13";
            case Symbol.CODE39:
                return "CODE39";
            case Symbol.CODE93:
                return "CODE93";
            case Symbol.CODE128:
                return "CODE128";
            case Symbol.UPCA:
                return "UPCA";
            case Symbol.UPCE:
                return "UPCE";
            default:
                return null;
        }
    }



    public File getPrimaryPhoto(Context ctx) {
        File[] files = FileHelpers.getProductImageFiles(ctx, BarCode, BarType, ImgSize.LARGE);
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    public File[] getVoiceNotes(Context ctx) {
        return FileHelpers.getProductVoiceFiles(ctx, BarCode, BarType);
    }


    public MoveTempFilesResult moveTempFiles(Context ctx) {
        MoveTempFilesResult result = new MoveTempFilesResult();
        copyTempImagesToSync(ctx);
        copyTempVoicesToSync(ctx);
        result.movedImageFiles = createProductImages(ctx);
        result.movedVoiceNoteFiles = moveTempVoiceNotesToProductDir(ctx);
        clearProductTempDir(ctx);
        return result;

    }


    private void clearProductTempDir(Context ctx) {
        FileHelpers.clearProductTempDir(ctx);
    }

    private File[] moveTempVoiceNotesToProductDir(Context ctx) {
        return FileHelpers.moveTempVoiceNotesToProductDir(ctx, BarCode, BarType);
    }

    private File[] createProductImages(Context ctx) {
        return FileHelpers.createProductImagesFromTempImages(ctx, BarCode, BarType);

    }

    private void copyTempVoicesToSync(Context ctx) {
        FileHelpers.copyTempVoicesToSync(ctx, BarCode, BarType);
    }

    private void copyTempImagesToSync(Context ctx) {
        FileHelpers.copyTempImagesToSync(ctx, BarCode, BarType);
    }


    public class MoveTempFilesResult {
        public File[] movedVoiceNoteFiles;
        public File[] movedImageFiles;

        public boolean onlyVoiceNotesMoved() {
            return movedVoiceNoteFiles.length > 0 && movedImageFiles.length == 0;
        }
    }
}
