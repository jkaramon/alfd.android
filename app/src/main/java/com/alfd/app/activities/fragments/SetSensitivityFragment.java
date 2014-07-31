package com.alfd.app.activities.fragments;



import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;

import com.alfd.app.R;
import com.alfd.app.adapters.EditSensitivityListCardAdapter;
import com.alfd.app.data.Product;
import com.alfd.app.data.Sensitivity;
import com.alfd.app.data.User;
import com.alfd.app.interfaces.ProductDetailListener;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SetSensitivityFragment extends Fragment {

    private EditSensitivityListCardAdapter adapter;
    private ProductDetailListener listener;
    private Product product;
    private List<User> users;
    private CardListView sensitivityList;
    private List<Sensitivity> sensitivities;

    public SetSensitivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_sensitivity, container, false);
        sensitivityList = (CardListView)view.findViewById(R.id.set_sensitivity_list);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ProductDetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProductDetailListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindView();
    }

    private void bindView() {
        product = listener.getProduct();

        users = User.getMembers();
        sensitivities = Sensitivity.getByProduct(product.getId());
        List<Card> cards = EditSensitivityListCardAdapter.mapCards(this.getActivity(), product, users, sensitivities);
        adapter = new EditSensitivityListCardAdapter(this.getActivity(), cards);
        sensitivityList.setAdapter(adapter);
        sensitivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (view.getTag() == null) {
                    return;
                }
                String level = (String) view.getTag();
                //updateSensitivityLevel(level);
                //bindLevel();
            }
        });
    }




}
