package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alfd.app.LogTags;
import com.alfd.app.R;
import com.alfd.app.activities.BaseProductActivity;
import com.alfd.app.adapters.VoiceNotesAdapter;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoiceNotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoiceNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class VoiceNotesFragment extends Fragment {

    public static final String TAG = "VoiceNotesFragment";
    private OnFragmentInteractionListener listener;


    private VoiceNotesAdapter adapter;
    private GridView gridView;




    public static VoiceNotesFragment newInstance() {
        VoiceNotesFragment fragment = new VoiceNotesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public VoiceNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        adapter = new VoiceNotesAdapter(getActivity());

    }

    public void refreshNotes() {
        File[] files = listener.getVoiceNoteFiles();
        adapter.setVoiceFiles(files);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        refreshNotes();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_notes, container, false);
        gridView = (GridView)view.findViewById(R.id.grid_view);
        gridView.setAdapter(adapter);



        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                VoiceNotesAdapter.VoiceFile vf = (VoiceNotesAdapter.VoiceFile)adapter.getItem(i);
                deleteNote(vf);
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VoiceNotesAdapter.VoiceFile vf = (VoiceNotesAdapter.VoiceFile)adapter.getItem(i);
                playOrStopItem(vf);
            }
        });



        return view;
    }

    private void playOrStopItem(VoiceNotesAdapter.VoiceFile vf) {
        stopAll();
        if (listener.playOrStopItem(vf) == BaseProductActivity.PlayMediaState.PLAYING) {
            vf.startPlaying();
            adapter.notifyDataSetChanged();
        }
    }

    public void stopAll() {
        adapter.stopAll();
    }


    private void deleteNote(VoiceNotesAdapter.VoiceFile vf) {
        listener.deleteNote(vf);
        adapter.notifyDataSetChanged();

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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public File[] getVoiceNoteFiles();
        public BaseProductActivity.PlayMediaState playOrStopItem(VoiceNotesAdapter.VoiceFile vf);
        void deleteNote(VoiceNotesAdapter.VoiceFile vf);
    }

}
