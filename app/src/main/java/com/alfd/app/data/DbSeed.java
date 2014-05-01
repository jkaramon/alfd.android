package com.alfd.app.data;

import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by karamon on 11. 4. 2014.
 */
public class DbSeed {
    public void seed() {
        List<Product> list =new Select().from(Product.class).orderBy("Name ASC").execute();

    }
}
