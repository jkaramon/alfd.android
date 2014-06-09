package com.alfd.app.activities.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.alfd.app.BuildConfig;
import com.alfd.app.ImgSize;
import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.activities.ProductFullScreenActivity;
import com.alfd.app.adapters.ImageAdapter;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.interfaces.OnPhotoInteractionListener;
import com.alfd.app.interfaces.OnPhotoTaken;
import com.alfd.app.utils.FileHelpers;
import com.alfd.app.utils.ImageCache;
import com.alfd.app.utils.ImageResizer;
import com.alfd.app.utils.Utils;

import java.io.File;

public class ProductGalleryPhotoFragment extends Fragment implements AdapterView.OnItemClickListener, OnPhotoTaken {


    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private String imageType;

    private int imageThumbSize;
    private int imageThumbSpacing;
    private ImageAdapter adapter;
    private ImageResizer imageWorker;
    private Button addPhotoButton;

    private OnPhotoInteractionListener listener;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ProductGalleryPhotoFragment() {}

    // TODO: Rename and change types and number of parameters
    public static ProductGalleryPhotoFragment newInstance(String imgPlaceholderType) {
        ProductGalleryPhotoFragment fragment = new ProductGalleryPhotoFragment();
        Bundle args = new Bundle();
        args.putString(SC.IMAGE_TYPE, imgPlaceholderType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        imageType = getArguments().getString(SC.IMAGE_TYPE);

        imageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        imageWorker = new ImageResizer(getActivity(), ImgSize.THUMB);
        imageWorker.setLoadingImage(R.drawable.empty_photo);
        imageWorker.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
        adapter = new ImageAdapter(getActivity(), imageWorker, listener.getImageFiles(imageType));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_product_gallery_photo, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
        addPhotoButton = (Button)v.findViewById(R.id.add_photo);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);
        final Activity activity = this.getActivity();
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                File f = (File)adapter.getItem(position);
                boolean deleted = FileHelpers.deleteFile(f);
                if (deleted) {
                    Toast.makeText(activity, "File deleted", Toast.LENGTH_SHORT).show();
                }
                adapter.setImageFiles(listener.getImageFiles(imageType));
                adapter.notifyDataSetChanged();

                return true;
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentFactory.takePicture(activity, imageType, listener.getTempFileToSave(imageType));
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        imageWorker.setExitTasksEarly(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        imageWorker.setPauseWork(false);
        imageWorker.setExitTasksEarly(true);
        imageWorker.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageWorker.closeCache();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnPhotoInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPhotoInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        File f = (File)adapter.getItem(position);
        ActivityOptions options = null;
        if (Utils.hasJellyBean()) {
            options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
        }
        listener.showFullScreenDetail(f, options);
    }



    @Override
    public void photoTaken(Intent data) {

    }
}
