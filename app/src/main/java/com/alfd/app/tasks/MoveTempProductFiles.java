package com.alfd.app.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.alfd.app.data.Product;

/**
 * Moves temporary Product files (images, voice notes) to final place.
 */
public class MoveTempProductFiles extends AsyncTask<Product, Integer, Long> {

    Context context;
    public MoveTempProductFiles(Context context) {
        this.context = context;
    }
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected Long doInBackground(Product... products) {
        Product product = products[0];
        product.moveTempFiles(context);

        return 1L;
    }

    protected void onPostExecute(Long result) {

    }
}