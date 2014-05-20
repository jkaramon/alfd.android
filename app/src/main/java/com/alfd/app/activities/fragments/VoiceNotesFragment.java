package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alfd.app.LogTags;
import com.alfd.app.R;
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

    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private OnFragmentInteractionListener listener;

    private RelativeLayout recordLayout = null;

    private VoiceNotesAdapter adapter;
    private GridView gridView;


    public enum RecordMediaState {
        RECORDING, STOPPED
    }
    public enum PlayMediaState {
        PLAYING, STOPPED
    }
    private RecordMediaState recordingState = RecordMediaState.STOPPED;
    private PlayMediaState playingState = PlayMediaState.STOPPED;

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

    private void updateNotesList() {
        File[] files = listener.getVoiceNoteFiles();
        adapter.setVoiceFiles(files);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateNotesList();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_notes, container, false);
        gridView = (GridView)view.findViewById(R.id.grid_view);
        gridView.setAdapter(adapter);
        recordButton = (ImageButton)view.findViewById(R.id.btn_record);
        recordLayout = (RelativeLayout)view.findViewById(R.id.record_layout);


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


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordingState == RecordMediaState.STOPPED) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });
        return view;
    }

    private void playOrStopItem(VoiceNotesAdapter.VoiceFile vf) {
        if (recordingState == RecordMediaState.RECORDING) {
            return;
        }
        boolean startPlaying = !vf.isPlaying();
        adapter.stopAll();
        stopMediaPlayer(vf);
        if (startPlaying) {
            startMediaPlayer(vf);
        }
    }


    private void deleteNote(VoiceNotesAdapter.VoiceFile vf) {
        recordingState = RecordMediaState.STOPPED;
        playingState = PlayMediaState.STOPPED;
        stopMediaPlayer(vf);
        stopRecording();
        //stopMediaPlayer();
        listener.deleteNote(vf.getFile());
        adapter.notifyDataSetChanged();

    }


    private void startMediaPlayer(final VoiceNotesAdapter.VoiceFile vf) {
        player = new MediaPlayer();
        File f = vf.getFile();
        vf.startPlaying();
        try {

            player.setDataSource(f.getAbsolutePath());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopMediaPlayer(vf);
                    adapter.stopAll();
                }
            });
            playingState = PlayMediaState.PLAYING;
            enteringPlayingState();
        } catch (IOException e) {

            Log.e(LogTags.VOICE_PLAYER, "prepare() failed", e);
        }
    }

    private void stopMediaPlayer(VoiceNotesAdapter.VoiceFile vf) {
        if (player != null) {
            player.release();
            player = null;
        }
        vf.stopPlaying();
        enteringPlayStoppedState();
        playingState = PlayMediaState.STOPPED;

    }

    private void startRecording() {
        recordButton.setEnabled(false);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(listener.createVoiceNoteFile().getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        try {
            recorder.prepare();
            recorder.start();
            recordingState = RecordMediaState.RECORDING;
            enteringRecordingState();
        } catch (IOException e) {
            Log.e(LogTags.VOICE_RECORDER, "prepare() failed");
        }
        finally {
            recordButton.setEnabled(true);
        }


    }

    private void enteringRecordStoppedState() {
        recordButton.setBackgroundResource(R.drawable.record_button);




    }
    private void enteringRecordingState() {
        recordButton.setBackgroundResource(R.drawable.stop_record_button);

    }

    private void enteringPlayingState() {

    }
    private void enteringPlayStoppedState() {
    }



    private void stopRecording() {
        recordButton.setEnabled(false);
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            listener.onVoiceNoteRecorded();

            recorder = null;
        }
        recordButton.setEnabled(true);
        enteringRecordStoppedState();
        updateNotesList();
        recordingState = RecordMediaState.STOPPED;
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

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onVoiceNoteRecorded();
        public File createVoiceNoteFile();
        public File[] getVoiceNoteFiles();
        void deleteNote(File file);
    }

}
