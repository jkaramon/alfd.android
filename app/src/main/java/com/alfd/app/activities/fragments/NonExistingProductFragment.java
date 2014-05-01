package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alfd.app.R;
import com.alfd.app.SC;
import com.alfd.app.data.Product;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NonExistingProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NonExistingProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NonExistingProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private Product mProduct;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param product Product to add.
     * @return A new instance of fragment NonExistingProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NonExistingProductFragment newInstance(Product product) {
        NonExistingProductFragment fragment = new NonExistingProductFragment();
        Bundle args = new Bundle();
        args.putString(SC.BAR_CODE, product.BarCode);
        args.putString(SC.BAR_TYPE, product.BarType);
        fragment.setArguments(args);
        return fragment;
    }
    public NonExistingProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = new Product();
            mProduct.BarCode = getArguments().getString(SC.BAR_CODE);
            mProduct.BarType = getArguments().getString(SC.BAR_TYPE);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_non_existing_product, container, false);
        TextView tvwBarCode =  (TextView)rootView.findViewById(R.id.tvw_bar_code);
        tvwBarCode.setText(mProduct.getFullBarCodeInfo());
        Button btnCreateProduct = (Button)rootView.findViewById(R.id.btn_insert);
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCreateProduct(mProduct);
                }
            }
        });
        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        public void onCreateProduct(Product p);
    }

}
