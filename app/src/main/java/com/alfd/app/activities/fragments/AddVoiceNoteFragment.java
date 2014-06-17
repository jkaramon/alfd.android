package com.alfd.app.activities.fragments;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alfd.app.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class AddVoiceNoteFragment extends Fragment {

    public static final String TAG = "AddVoiceNoteFragment";

    private AddVoiceNoteFragment.OnFragmentInteractionListener listener;
    private ImageButton startRecordingButton;
    private ImageButton stopRecordingButton;

    public AddVoiceNoteFragment() {
        // Required empty public constructor
    }

    public VoiceNotesFragment getVoiceNotesFragment() {
        FragmentManager childFragMan = getChildFragmentManager();
        return (VoiceNotesFragment)childFragMan.findFragmentByTag(VoiceNotesFragment.TAG);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_voice_note, container, false);
        startRecordingButton = (ImageButton)view.findViewById(R.id.start_recording_button);
        stopRecordingButton = (ImageButton)view.findViewById(R.id.stop_recording_button);
        stopRecordingButton.setVisibility(View.GONE);

        FragmentManager childFragMan = getChildFragmentManager();

        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        childFragTrans.add(R.id.voice_notes_layout, VoiceNotesFragment.newInstance(), VoiceNotesFragment.TAG);
        childFragTrans.commit();


        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onStartRecording();
                setRecordingStarted();
            }
        });
        stopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onStopRecording();
                setRecordingStopped();
            }
        });

        return view;
    }

    public void setRecordingStarted() {
        startRecordingButton.setVisibility(View.GONE);
        stopRecordingButton.setVisibility(View.VISIBLE);
    }
    public void setRecordingStopped() {
        startRecordingButton.setVisibility(View.VISIBLE);
        stopRecordingButton.setVisibility(View.GONE);
    }




    public static AddVoiceNoteFragment newInstance() {
        AddVoiceNoteFragment fragment = new AddVoiceNoteFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
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
        public void  onStartRecording();
        public void  onStopRecording();
    }

}
