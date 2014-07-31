package com.alfd.app.data;

import android.content.Context;
import android.text.TextUtils;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.alfd.app.ImgSize;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.Utils;

import net.sourceforge.zbar.Symbol;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public String Description;

    @Column
    public String SearchName;


    @Override
    protected void beforeSave() {
        List<String> parts = new ArrayList<String>();
        appendSearchNamePart(parts, Name);
        appendSearchNamePart(parts, Description);
        parts.add(BarCode);
        SearchName = TextUtils.join("|", parts);
        super.beforeSave();
    }

    private void appendSearchNamePart(List<String> parts, String val) {
        if (Utils.isBlank(val)) {
            return;
        }
        parts.add(val.toLowerCase());
        parts.add(Utils.transliterate(val));
    }


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
        File[] files = FileHelpers.getProductImageFiles(ctx, BarCode, BarType);
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
        result.movedImageFiles = moveTempProductImages(ctx);
        result.movedVoiceNoteFiles = moveTempVoiceNotes(ctx);
        clearProductTempDir(ctx);
        return result;

    }


    private void clearProductTempDir(Context ctx) {
        FileHelpers.clearProductTempDir(ctx);
    }

    private File[] moveTempVoiceNotes(Context ctx) {
        return FileHelpers.moveTempVoiceNotes(ctx, BarCode, BarType);
    }

    private File[] moveTempProductImages(Context ctx) {
        return FileHelpers.moveTempImages(ctx, BarCode, BarType);

    }





    public static Product getByBarCode(String barCode) {
        return new Select()
                .from(Product.class)
                .where("BarCode = ?", barCode)
                .executeSingle();
    }

    public static Product fromREST(com.alfd.app.rest.Product restProduct) {
        Product p = new Product();
        p.fillFromREST(restProduct);
        return p;
    }
    public void fillFromREST(com.alfd.app.rest.Product restProduct) {
        this.BarCode = restProduct.barCode;
        this.BarType = restProduct.barType;
        this.Name = restProduct.name;
        this.Description = restProduct.description;
    }

    public com.alfd.app.rest.Product toREST() {
        com.alfd.app.rest.Product restProduct = new com.alfd.app.rest.Product();
        restProduct.barCode = BarCode;
        restProduct.barType = BarType;
        restProduct.description = Description;
        restProduct.name = Name;
        return restProduct;
    }




    public class MoveTempFilesResult {
        public File[] movedVoiceNoteFiles;
        public File[] movedImageFiles;

        public boolean onlyVoiceNotesMoved() {
            return movedVoiceNoteFiles.length > 0 && movedImageFiles.length == 0;
        }
    }
}
