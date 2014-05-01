package com.alfd.app.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import net.sourceforge.zbar.Symbol;

/**
 * Created by karamon on 10. 4. 2014.
 */
@Table(name = "Products")
public class Product extends BaseModel {
    @Column
    public String Name;
    @Column
    public String BarCode;

    @Column
    public String BarType;

    @Column
    public String OverviewPhotos;
    @Column
    public String IngredientsPhoto;
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
}
