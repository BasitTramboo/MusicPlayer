package com.tramboo.basit.olachallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tramboo.basit.olachallenge.model.Songs;

import java.util.List;

/**
 * Created by basit on 12/18/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongsViewHolder> {

    private Context mContext;
    private List<Songs> mTracks;

    public SongAdapter() {
    }

    public SongAdapter(Context mContext, List<Songs> mTracks) {
        this.mContext = mContext;
        this.mTracks = mTracks;
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new SongsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {
        final Songs song = mTracks.get(position);
        Glide.with(mContext).load(song.getCover_image()).thumbnail(0.01f).into(holder.trackImageView);
        holder.titleTextView.setText(song.getSong());
        holder.artistTextView.setText(song.getArtists());

    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder{

        ImageView trackImageView;
        TextView titleTextView;
        TextView artistTextView;

        public SongsViewHolder(View itemView) {
            super(itemView);

         trackImageView = itemView.findViewById(R.id.coverImage);
         titleTextView = itemView.findViewById(R.id.songName);
         artistTextView = itemView.findViewById(R.id.artists);
        }
    }
}
