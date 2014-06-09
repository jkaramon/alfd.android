package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alfd.app.ImgSize;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.interfaces.OnPhotoInteractionListener;
import com.alfd.app.utils.ImageResizer;

import java.io.File;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.alfd.app.interfaces.OnPhotoInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.alfd.app.activities.fragments.ProductFullPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductFullPhotoFragment extends Fragment implements View.OnClickListener  {


    private ImageView imageView;
    private String imageType;
    private File imageFile;
    private ImageResizer imageWorker;

    private OnPhotoInteractionListener listener;

    // TODO: Rename and change types and number of parameters
    public static ProductFullPhotoFragment newInstance(String imageType, File imageFile) {
        ProductFullPhotoFragment fragment = new ProductFullPhotoFragment();
        Bundle args = new Bundle();
        args.putString(SC.IMAGE_FULL_NAME, imageFile.getAbsolutePath());
        args.putString(SC.IMAGE_TYPE, imageType);
        fragment.setArguments(args);

        return fragment;
    }
    public ProductFullPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageType = getArguments().getString(SC.IMAGE_TYPE);
        imageWorker = new ImageResizer(this.getActivity(), ImgSize.LARGE);
        fillImageFile(savedInstanceState);

    }
    private void fillImageFile(Bundle savedInstanceState) {
        String filename;
        if (savedInstanceState == null) {
            filename = getArguments().getString(SC.IMAGE_FULL_NAME);
        }
        else {
            filename = savedInstanceState.getString(SC.IMAGE_FULL_NAME);
        }
        imageFile = new File(filename);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SC.IMAGE_FULL_NAME, imageFile.getAbsolutePath());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_photo, container, false);
        ImageView imageView =  (ImageView)view.findViewById(R.id.image_view);


        imageWorker.loadImage(imageFile, imageView);
        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        imageWorker.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageWorker.setExitTasksEarly(true);
        imageWorker.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageWorker.closeCache();
    }

    @Override
    public void onClick(View view) {
        takePicture();
    }

    private void takePicture() {
        IntentFactory.takePicture(this.getActivity(), "", listener.getTempFileToSave(imageType));
    }





}

