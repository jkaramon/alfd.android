package com.alfd.app.activities.fragments;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfd.app.LogTags;
import com.alfd.app.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoiceNoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoiceNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class VoiceNoteFragment extends Fragment {

    private ImageButton stopButton = null;
    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;

    private ImageButton deleteButton = null;
    private ImageButton playButton = null;
    private MediaPlayer player = null;
    private OnFragmentInteractionListener listener;
    private Timer timer;
    private long timeElapsed = 0;
    private TextView stopWatchText = null;

    private RelativeLayout playLayout = null;
    private RelativeLayout recordLayout = null;
    private int stopWatchOriginalColor;


    public enum RecordMediaState {
        RECORDING, STOPPED
    }
    public enum PlayMediaState {
        PLAYING, STOPPED
    }
    private RecordMediaState recordingState = RecordMediaState.STOPPED;
    private PlayMediaState playingState = PlayMediaState.STOPPED.STOPPED;


    final Handler timerHandler = new Handler();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VoiceNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceNoteFragment newInstance() {
        VoiceNoteFragment fragment = new VoiceNoteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public VoiceNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_note, container, false);
        recordButton = (ImageButton)view.findViewById(R.id.btn_record);
        playButton = (ImageButton)view.findViewById(R.id.btn_play);
        deleteButton = (ImageButton)view.findViewById(R.id.btn_delete);

        stopWatchText = (TextView)view.findViewById(R.id.stop_watch);
        stopWatchOriginalColor = stopWatchText.getDrawingCacheBackgroundColor();
        playLayout = (RelativeLayout)view.findViewById(R.id.play_layout);
        recordLayout = (RelativeLayout)view.findViewById(R.id.record_layout);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordingState == RecordMediaState.STOPPED) {
                    startRecording();
                }
                else {
                    stopRecording();
                }
            }
        });

        deleteButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playingState == PlayMediaState.STOPPED) {
                    startPlaying();
                }
                else {
                    stopPlaying();
                }
            }
        });
        determinePlayRecordState();
        return view;
    }

    private void determinePlayRecordState() {
        File noteFile = listener.onVoiceNoteFileRequested();
        if (noteFile.exists()) {
            recordLayout.setVisibility(View.GONE);
            playLayout.setVisibility(View.VISIBLE);
        }
        else {
            recordLayout.setVisibility(View.VISIBLE);
            playLayout.setVisibility(View.GONE);
        }
    }

    private void deleteNote() {
        recordingState = RecordMediaState.STOPPED;
        stopPlaying();
        listener.deleteNote();
        determinePlayRecordState();

    }


    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(listener.onVoiceNoteFileRequested().getAbsolutePath());
            player.prepare();
            player.start();
            playingState = PlayMediaState.PLAYING;
            enteringPlayingState();
        } catch (IOException e) {

            Log.e(LogTags.VOICE_PLAYER, "prepare() failed", e);
        }
    }

    private void stopPlaying() {
        playButton.setEnabled(false);
        player.release();
        player = null;
        enteringPlayStoppedState();
        playingState = PlayMediaState.STOPPED;
        playButton.setEnabled(true);
    }

    private void startRecording() {
        recordButton.setEnabled(false);
        timeElapsed = 0;
        updateStopWatchText();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(listener.onVoiceNoteFileRequested().getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        try {
            recorder.prepare();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                        timerHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                timeElapsed += 50;
                                updateStopWatchText();
                            }
                        });
                }
            }, 0, 50);
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
        recordLayout.setVisibility(View.GONE);
        playLayout.setVisibility(View.VISIBLE);
        stopWatchText.setTextColor(stopWatchOriginalColor);


    }
    private void enteringRecordingState() {
        recordButton.setBackgroundResource(R.drawable.stop_record_button);
        stopWatchText.setTextColor(getResources().getColor(R.color.red));

    }

    private void enteringPlayingState() {
        playButton.setBackgroundResource(R.drawable.stop_button);

    }
    private void enteringPlayStoppedState() {
        playButton.setBackgroundResource(R.drawable.play_button);
    }

    private void updateStopWatchText() {
        Date date = new Date(timeElapsed);
        DateFormat formatter = new SimpleDateFormat("mm:ss:SS");
        String dateFormatted = formatter.format(date);
        stopWatchText.setText(dateFormatted);
    }

    private void stopRecording() {
        recordButton.setEnabled(false);
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            listener.onVoiceNoteRecorded();
            timer.cancel();
            timer = null;
            recorder = null;
        }
        recordButton.setEnabled(true);
        enteringRecordStoppedState();
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
        public File onVoiceNoteFileRequested();

        void deleteNote();
    }

}
