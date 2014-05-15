package com.alfd.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alfd.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The main adapter that backs the GridView. This is fairly standard except the number of
 * columns in the GridView is used to create a fake top row of empty views as we use a
 * transparent ActionBar and don't want the real top row of images to start off covered by it.
 */
public class VoiceNotesAdapter extends BaseAdapter {

    private final Context context;
    private List<VoiceFile> voiceFiles;

    public VoiceNotesAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {

        return voiceFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return voiceFiles.get(position);
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_voice_note, container, false);
        }
        updateView(position, view);
        return view;
    }

    private void updateView(int position, View view) {
        ImageView playButton  = (ImageView)view.findViewById(R.id.play_button);
        VoiceFile vf = voiceFiles.get(position);
        if (vf.isPlaying()) {
            playButton.setBackgroundResource(R.drawable.stop_button);
        }
        else {
            playButton.setBackgroundResource(R.drawable.play_button);
        }
    }


    public void setVoiceFiles(File[] files) {
        this.voiceFiles = new ArrayList<VoiceFile>();
        for (File f : files) {
            VoiceFile vf = new VoiceFile(f);
            this.voiceFiles.add(vf);
        }
    }

    public void stopAll() {
        for (VoiceFile vf : voiceFiles) {
           vf.stopPlaying();
        }
        notifyDataSetChanged();
    }

    public class VoiceFile {

        private File file;
        private boolean isPlaying;

        public VoiceFile(File f) {
            file = f;
            isPlaying = false;
        }

        public void updatePlayState(File currentlyPlayingFile) {
            isPlaying = getFile().equals(currentlyPlayingFile);
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public File getFile() {
            return file;
        }

        public void stopPlaying() {
            isPlaying = false;
        }
        public void startPlaying() {
            isPlaying = true;
        }
    }
}
