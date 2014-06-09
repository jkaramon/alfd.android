package com.alfd.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.alfd.app.utils.ImageResizer;
import com.alfd.app.views.RecyclingImageView;

import java.io.File;

/**
 * The main adapter that backs the GridView. This is fairly standard except the number of
 * columns in the GridView is used to create a fake top row of empty views as we use a
 * transparent ActionBar and don't want the real top row of images to start off covered by it.
 */
public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private ImageResizer imageWorker;
    private GridView.LayoutParams imageViewLayoutParams;
    private File[] imageFiles;



    public ImageAdapter(Context context, ImageResizer imageWorker, File[] imageFiles) {
        super();
        this.imageWorker = imageWorker;
        this.context = context;
        this.setImageFiles(imageFiles);
        imageViewLayoutParams = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    }




    @Override
    public int getCount() {

        return imageFiles.length;
    }

    @Override
    public Object getItem(int position) {
        return imageFiles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        ImageView imageView;
        if (convertView == null) { // if it's not recycled, instantiate and initialize
            imageView = new RecyclingImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(imageViewLayoutParams);
        } else { // Otherwise re-use the converted view
            imageView = (ImageView) convertView;
        }

        imageWorker.loadImage(getItem(position), imageView);
        return imageView;

    }

    public void setImageFiles(File[] imageFiles) {
        this.imageFiles = imageFiles;
    }
}
