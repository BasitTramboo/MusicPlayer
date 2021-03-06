package com.tramboo.basit.olachallenge.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.tramboo.basit.olachallenge.common.Progress_Dialog;
import com.tramboo.basit.olachallenge.R;
import com.tramboo.basit.olachallenge.adapter.SongTrackAdapter;
import com.tramboo.basit.olachallenge.model.Songs;
import com.tramboo.basit.olachallenge.network.NetworkServices;
import com.tramboo.basit.olachallenge.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by basit on 12/19/17.
 */

public class MainActivity extends AppCompatActivity {
    private List<Songs> mListItems;
    private List<Songs> SearchedMusicList;
    private ListView listView;
    private SongTrackAdapter mSongTrackerAdapter;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerControl;
    private TextView mSelectedTrackTitle;
    private TextView mSelectedTrackArtist;
    private SearchView songSearchView;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
            }
        });

        mListItems = new ArrayList<Songs>();
        listView = findViewById(R.id.track_list_view);
        mSongTrackerAdapter = new SongTrackAdapter(this,mListItems);
        listView.setAdapter(mSongTrackerAdapter);
        mSelectedTrackTitle = findViewById(R.id.selected_track_title);
        mSelectedTrackArtist = findViewById(R.id.selected_track_artist);
        mPlayerControl = findViewById(R.id.player_control);
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Songs track = mListItems.get(position);
                mSelectedTrackTitle.setText(track.getSong());
                mSelectedTrackArtist.setText(track.getArtists());
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }

                try {
                    mMediaPlayer.setDataSource(track.getUrl());
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (IllegalStateException exp){

                }
            }
        });


        SearchedMusicList = new ArrayList<>();
        songSearchView = findViewById(R.id.songSearchView);
        songSearchView.setQueryHint("Search...");
        songSearchView.setIconifiedByDefault(false);
        songSearchView.setFocusable(false);
        songSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               UpdateSongAdapter(newText);
                return false;
            }
        });



        getSongs();
    }

    private void UpdateSongAdapter(String newText) {
        SearchedMusicList.clear();
        for (int i =0; i< mListItems.size(); i++){
            Songs music = mListItems.get(i);
            if (music.getSong().toLowerCase().startsWith(newText.toLowerCase())){
                SearchedMusicList.add(music);
            }
        }
        mSongTrackerAdapter = new SongTrackAdapter(this,SearchedMusicList);
        listView.setAdapter(mSongTrackerAdapter);
    }


    private void LoadTracks(List<Songs> songs){
        mListItems.clear();
        mListItems.addAll(songs);
        mSongTrackerAdapter.notifyDataSetChanged();
    }

    private void getSongs(){
        final Progress_Dialog progress_dialog = new Progress_Dialog(MainActivity.this, "Hang Tight!! Getting Songs for you");
        progress_dialog.showProgressDialog();
        NetworkServices networkServices = RetrofitClient.getClient().create(NetworkServices.class);
        Call<List<Songs>> call = networkServices.getSongs();
        call.enqueue(new Callback<List<Songs>>() {
            @Override
            public void onResponse(Call<List<Songs>> call, Response<List<Songs>> response) {
                progress_dialog.hideProgressDialog();
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        List<Songs> songs = response.body();
                        LoadTracks(songs);
                    }else{
                        Toast.makeText(MainActivity.this,"Seems like something went wrong",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this,"Error Code: "+response.code(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Songs>> call, Throwable t) {
                progress_dialog.hideProgressDialog();
                Toast.makeText(MainActivity.this,"Network Error "+t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        } else {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
