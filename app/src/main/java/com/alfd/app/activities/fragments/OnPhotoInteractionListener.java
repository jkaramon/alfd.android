package com.alfd.app.activities.fragments;

import android.graphics.Bitmap;

import java.io.File;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnPhotoInteractionListener {
    // TODO: Update argument type and name
    public void onPhotoSelected(Bitmap imageBitmap, String imageType);
    File[] getImageFiles(String imageType);
}
