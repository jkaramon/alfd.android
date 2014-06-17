package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alfd.app.R;
import com.alfd.app.SC;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductNameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductNameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener listener;
    private EditText productNameText;

    private String productName;
    private Button createButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ProductNameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductNameFragment newInstance() {
        ProductNameFragment fragment = new ProductNameFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public ProductNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            productName = savedInstanceState.getString(SC.PRODUCT_NAME);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SC.PRODUCT_NAME, getProductName());
    }

    private String getProductName() {
        return productNameText.getText().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_name, container, false);
        productNameText = (EditText) view.findViewById(R.id.product_name_text);
        setProductNameText(productName);
        createButton = (Button)view.findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCreateProduct(getProductName());
            }
        });
        return view;
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
        listener = null;
    }

    public void setProductNameText(String productName) {
        productNameText.setText(productName);
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

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
    public interface OnFragmentInteractionListener {

        void onCreateProduct(String productName);
    }

}
