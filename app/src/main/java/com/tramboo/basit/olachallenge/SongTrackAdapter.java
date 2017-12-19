package com.tramboo.basit.olachallenge;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tramboo.basit.olachallenge.model.Songs;
import com.tramboo.basit.olachallenge.network.NetworkServices;
import com.tramboo.basit.olachallenge.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Songdownloader(track.getUrl(),track.getSong());
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

    void Songdownloader(String fileurl, final String filename){
        NetworkServices networkServices = RetrofitClient.getClient().create(NetworkServices.class);
        Call<ResponseBody> call = networkServices.downloadFileWithDynamicUrlSync(fileurl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.d("Call for download", "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(),filename);
                    Log.d("Call for download", "file download was a success? " + writtenToDisk);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Call for download", "failed");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body,String filename) {
        try {
            // todo change the file location/name according to your needs
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC);
            File Olafile = new File( path+ File.separator +filename+".mp3");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(Olafile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("Call for downloadog", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
