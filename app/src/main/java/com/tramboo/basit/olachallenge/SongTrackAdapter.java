package com.tramboo.basit.olachallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tramboo.basit.olachallenge.model.Songs;

import java.util.List;

/**
 * Created by basit on 12/16/17.
 */

public class SongTrackAdapter extends BaseAdapter {
    private Context mContext;
    private List<Songs> mTracks;

    public SongTrackAdapter(Context mContext, List<Songs> mTracks) {
        this.mContext = mContext;
        this.mTracks = mTracks;
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public Songs getItem(int position) {
        return mTracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Songs track = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.trackImageView = convertView.findViewById(R.id.coverImage);
            viewHolder.titleTextView = convertView.findViewById(R.id.songName);
            viewHolder.artistTextView = convertView.findViewById(R.id.artists);
            viewHolder.downloadBtn = convertView.findViewById(R.id.downloadSongBtn);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.titleTextView.setText(track.getSong());
        viewHolder.artistTextView.setText(track.getArtists());
        Glide.with(mContext).load(track.getCover_image()).thumbnail(0.01f).into(viewHolder.trackImageView);
        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadSong downloadSong = new DownloadSong(mContext);
                downloadSong.startDownload(track.getUrl());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView trackImageView;
        TextView titleTextView;
        TextView artistTextView;
        ImageButton downloadBtn;
    }
}
