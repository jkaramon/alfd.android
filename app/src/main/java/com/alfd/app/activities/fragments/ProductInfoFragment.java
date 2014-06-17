package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfd.app.ImgSize;
import com.alfd.app.LogTags;
import com.alfd.app.R;
import com.alfd.app.adapters.VoiceNotesAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.utils.ImageResizer;

import java.io.File;
import java.lang.reflect.Field;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductInfoFragment extends Fragment  {


    private OnFragmentInteractionListener listener;
    private ImageView productPhoto;
    private Product product;
    private ImageResizer imageWorker;
    private TextView descriptionText;
    private TextView barCodeText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProductInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductInfoFragment newInstance() {
        ProductInfoFragment fragment = new ProductInfoFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public ProductInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = this.getActivity();
        imageWorker = new ImageResizer(activity, ImgSize.SMALL);

        if (getArguments() != null) {

        }
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag(VoiceNotesFragment.TAG);
        if (f == null) {
            f = VoiceNotesFragment.newInstance();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.voice_notes_layout, f, VoiceNotesFragment.TAG)
                .commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);
        productPhoto = (ImageView)view.findViewById(R.id.default_photo);

        descriptionText = (TextView)view.findViewById(R.id.description_text);
        barCodeText = (TextView)view.findViewById(R.id.bar_code_text);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindView();
    }

    private void bindView() {
        product = listener.getProduct();
        getActivity().setTitle(product.Name);
        descriptionText.setText(product.Description);
        barCodeText.setText(product.getFullBarCodeInfo());
        imageWorker.loadImage(product.getPrimaryPhoto(this.getActivity()), productPhoto);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {

        super.onDetach();
        // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        listener = null;
    }

    public VoiceNotesFragment getVoiceNotesFragment() {
        FragmentManager childFragMan = getChildFragmentManager();
        return (VoiceNotesFragment)childFragMan.findFragmentByTag(VoiceNotesFragment.TAG);
    }

    public interface OnFragmentInteractionListener {

        Product getProduct();

    }

}
