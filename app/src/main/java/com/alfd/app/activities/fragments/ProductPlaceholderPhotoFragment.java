package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alfd.app.ProductImageTypes;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.SC;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.interfaces.OnPhotoInteractionListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.alfd.app.interfaces.OnPhotoInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductPlaceholderPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductPlaceholderPhotoFragment extends Fragment implements View.OnClickListener  {


    private ImageView photo;
    private String imageType;


    private OnPhotoInteractionListener listener;

    // TODO: Rename and change types and number of parameters
    public static ProductPlaceholderPhotoFragment newInstance(String imgPlaceholderType) {
        ProductPlaceholderPhotoFragment fragment = new ProductPlaceholderPhotoFragment();
        Bundle args = new Bundle();
        args.putString(SC.IMAGE_TYPE, imgPlaceholderType);
        fragment.setArguments(args);
        return fragment;
    }
    public ProductPlaceholderPhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageType = getArguments().getString(SC.IMAGE_TYPE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_placeholder_product_photo, container, false);
        ImageView placeholder =  (ImageView)view.findViewById(R.id.placeholder_photo);
        if (imageType == ProductImageTypes.OVERVIEW) {
            placeholder.setImageResource(R.drawable.product_placeholder);
        }
        else if (imageType == ProductImageTypes.INGREDIENTS) {
            placeholder.setImageResource(R.drawable.ingredients_placeholder);
        }
        setupListeners(view);
        return view;
    }


    private void setupListeners(View view) {
        photo  =  (ImageView)view.findViewById(R.id.placeholder_photo);
        photo.setOnClickListener(this);
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
    public void onClick(View view) {
        takePicture();
    }

    private void takePicture() {
        IntentFactory.takePicture(this, listener.getFileToSave(imageType));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            
        }
    }



}

