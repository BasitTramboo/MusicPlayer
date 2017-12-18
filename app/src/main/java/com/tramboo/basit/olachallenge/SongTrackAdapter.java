package com.tramboo.basit.olachallenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
        Songs track = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.trackImageView = convertView.findViewById(R.id.coverImage);
            viewHolder.titleTextView = convertView.findViewById(R.id.songName);
            viewHolder.artistTextView = convertView.findViewById(R.id.artists);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.titleTextView.setText(track.getSong());
        viewHolder.artistTextView.setText(track.getArtists());
        Picasso.with(mContext).load(track.getCover_image()).resize(42,42).placeholder(R.drawable.ic_audiotrack_black_48dp).into(viewHolder.trackImageView);
        Picasso.with(mContext).setLoggingEnabled(true);
        return convertView;
    }

    static class ViewHolder {
        ImageView trackImageView;
        TextView titleTextView;
        TextView artistTextView;
    }
}
