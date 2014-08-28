package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.Cache;
import com.alfd.app.R;
import com.alfd.app.RequestCodes;
import com.alfd.app.activities.ScanActivity;
import com.alfd.app.adapters.ProductListCardAdapter;
import com.alfd.app.utils.Utils;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardGridView;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScanOrSearchFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ProductListCardAdapter adapter;
    private CardGridView productList;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ScanOrSearchFragment newInstance() {
        ScanOrSearchFragment fragment = new ScanOrSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScanOrSearchFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        adapter = new ProductListCardAdapter(this.getActivity());

        View rootView = inflater.inflate(R.layout.fragment_scan_or_search, container, false);
        final Button scanButton = (Button)rootView.findViewById((R.id.scan_button));
        EditText searchText = (EditText)rootView.findViewById(R.id.search_text);
        final Activity activity = this.getActivity();
        productList = (CardGridView) rootView.findViewById(R.id.product_list);
        productList.setAdapter(adapter);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(activity, ScanActivity.class);
                activity.startActivityForResult(intent, RequestCodes.SCAN);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (Utils.isBlank(text)) {
                    scanButton.setVisibility(View.VISIBLE);
                    Cursor c = Cache.openDatabase().rawQuery("SELECT Id _id, * FROM products WHERE 1=0", null);
                    adapter.swapCursor(c);
                    return;
                }
                scanButton.setVisibility(View.GONE);
                loadCards(text);
            }
        });



        return rootView;
    }

    private void loadCards(String searchText) {
        String sql = "SELECT Id _id, * FROM products WHERE SearchName LIKE '%' || ? || '%' ORDER BY Name";
        String[] args = new String[] {
                searchText
        };
        Cursor c = Cache.openDatabase().rawQuery(sql, args);
        adapter.swapCursor(c);

    }


}
