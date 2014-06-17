package com.alfd.app.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alfd.app.ImgSize;
import com.alfd.app.LogTags;
import com.alfd.app.SC;
import com.alfd.app.Services;
import com.alfd.app.activities.fragments.AddVoiceNoteFragment;
import com.alfd.app.activities.fragments.ProductInfoFragment;
import com.alfd.app.activities.fragments.ProductNameFragment;
import com.alfd.app.activities.fragments.VoiceNotesFragment;
import com.alfd.app.adapters.VoiceNotesAdapter;
import com.alfd.app.intents.IntentFactory;
import com.alfd.app.interfaces.OnPhotoInteractionListener;
import com.alfd.app.data.Product;
import com.alfd.app.services.BaseServiceReceiver;
import com.alfd.app.services.MoveTempProductFilesService;
import com.alfd.app.utils.FileHelpers;

import java.io.File;
import java.io.IOException;

/**
 * Created by karamon on 2. 5. 2014.
 */
public class BaseProductActivity extends BaseActionBarActivity implements AddVoiceNoteFragment.OnFragmentInteractionListener, OnPhotoInteractionListener, VoiceNotesFragment.OnFragmentInteractionListener, ProductNameFragment.OnFragmentInteractionListener {
    protected Product product;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;


    public enum RecordMediaState {
        RECORDING, STOPPED
    }
    public enum PlayMediaState {
        PLAYING, STOPPED
    }
    private RecordMediaState recordState = RecordMediaState.STOPPED;
    private PlayMediaState playState = PlayMediaState.STOPPED;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillProduct(savedInstanceState);


    }

    private void fillProduct(Bundle savedInstanceState) {

        product = new Product();
        if (savedInstanceState == null) {
            Intent i = getIntent();
            long productId = i.getLongExtra(SC.PRODUCT_ID, -1);
            if (productId != -1) {
                product = Product.load(Product.class, productId);
            }
            else {
                product.BarCode = i.getStringExtra(SC.BAR_CODE);
                product.BarType = i.getStringExtra(SC.BAR_TYPE);
            }
        }
        else {
            long productId = savedInstanceState.getLong(SC.PRODUCT_ID, -1);
            if (productId != -1) {
                product = Product.load(Product.class, productId);
            }
            else {
                product.BarCode = savedInstanceState.getString(SC.BAR_CODE);
                product.BarType = savedInstanceState.getString(SC.BAR_TYPE);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (product.isNew()) {
            savedInstanceState.putString(SC.BAR_CODE, product.BarCode);
            savedInstanceState.putString(SC.BAR_TYPE, product.BarType);
        }
        else {
            savedInstanceState.putLong(SC.PRODUCT_ID, product.getId());
        }
    }





    @Override
    public File getTempFileToSave(String imageType) {
        return FileHelpers.createTempProductImageFile(this, imageType, product.BarCode, product.BarType);
    }

    @Override
    public File[] getImageFiles(String imageType) {
        if (product.isNew()) {
            return FileHelpers.getProductImageTempFiles(this, imageType, product.BarCode, product.BarType);
        }
        else {

            return FileHelpers.getProductImageFiles(this, product.BarCode, product.BarType, ImgSize.LARGE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void showFullScreenDetail(File currentImage, ActivityOptions options) {
        final Intent i = new Intent(this, ProductFullScreenActivity.class);
        i.putExtra(SC.IMAGE_FULL_NAME, currentImage.getAbsolutePath());
        if (product.isNew()) {
            i.putExtra(SC.BAR_CODE, product.BarCode);
            i.putExtra(SC.BAR_TYPE, product.BarType);
        }

        if (options != null) {
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onStartRecording() {
        startRecording();
    }

    @Override
    public void onStopRecording() {
        stopRecording();
    }

    public void onVoiceNoteRecorded() {
        refreshVoiceNotes();
    }

    protected void refreshVoiceNotes() {
        getVoiceNotesFragment().refreshNotes();
    }

    public File createVoiceNoteFile() {
        if (product.isNew()) {
            return FileHelpers.createTempProductVoiceFile(this, product.BarCode, product.BarType);
        }
        else {
            return FileHelpers.getProductVoiceFile(this, product.BarCode, product.BarType);
        }
    }

    @Override
    public File[] getVoiceNoteFiles() {
        if (product.isNew()) {
            return FileHelpers.getProductVoiceTempFiles(this, product.BarCode, product.BarType);
        }
        else {
            return product.getVoiceNotes(this);
        }
    }


    public PlayMediaState playOrStopItem(VoiceNotesAdapter.VoiceFile vf) {
        if (recordState == RecordMediaState.RECORDING) {
            return PlayMediaState.STOPPED;
        }
        boolean startPlaying = !vf.isPlaying();
        stopMediaPlayer(vf);
        if (startPlaying) {
            startMediaPlayer(vf);
            return PlayMediaState.PLAYING;
        }
        return PlayMediaState.STOPPED;
    }

    @Override
    public void deleteNote(final VoiceNotesAdapter.VoiceFile vf) {
        if (vf.getFile().exists()) {
            vf.getFile().delete();
            recordState = RecordMediaState.STOPPED;
            playState = PlayMediaState.STOPPED;
            stopMediaPlayer(vf);
            stopRecording();
            refreshVoiceNotes();
        }
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
                    stopAllVoiceNotes();
                }
            });
            playState = PlayMediaState.PLAYING;
        } catch (Exception e) {

            Log.e(LogTags.VOICE_PLAYER, "prepare() failed", e);
        }
    }

    private void stopAllVoiceNotes() {
        getVoiceNotesFragment().stopAll();
    }

    protected VoiceNotesFragment getVoiceNotesFragment() {
        ProductInfoFragment f = getProductInfoFragment();
        if (f != null) {
            return f.getVoiceNotesFragment();
        }
        AddVoiceNoteFragment f2 = getAddVoiceNoteFragment();
        if (f2 == null) {
            return null;
        }
        return f2.getVoiceNotesFragment();
    }

    protected AddVoiceNoteFragment getAddVoiceNoteFragment() {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof AddVoiceNoteFragment) {
                return (AddVoiceNoteFragment)f;
            }
        }
        return null;
    }

    protected ProductInfoFragment getProductInfoFragment() {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof ProductInfoFragment) {
                return (ProductInfoFragment)f;
            }
        }
        return null;
    }


    private void stopMediaPlayer(VoiceNotesAdapter.VoiceFile vf) {
        if (player != null) {
            player.release();
            player = null;
        }
        vf.stopPlaying();
        playState = PlayMediaState.STOPPED;

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(createVoiceNoteFile().getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        try {
            recorder.prepare();
            recorder.start();
            recordState = RecordMediaState.RECORDING;
        } catch (IOException e) {
            Log.e(LogTags.VOICE_RECORDER, "prepare() failed");
        }
        finally {
        }


    }



    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                onVoiceNoteRecorded();

                recorder = null;
            }
        }
        catch (Exception e) {

        }
        finally {
            recordState = RecordMediaState.STOPPED;
        }

    }


    @Override
    public void onCreateProduct(String productName) {
        product.Name = productName;

        startMoveProductFilesService();




    }

    protected void startMoveProductFilesService() {
        Intent svcIntent = new Intent(this, MoveTempProductFilesService.class);
        svcIntent.putExtra(SC.BAR_CODE, product.BarCode);
        svcIntent.putExtra(SC.BAR_TYPE, product.BarType);
        this.startService(svcIntent);
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


}
