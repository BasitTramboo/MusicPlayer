package com.tramboo.basit.olachallenge.adapter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tramboo.basit.olachallenge.R;
import com.tramboo.basit.olachallenge.activity.MainActivity;
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

import static android.app.Notification.DEFAULT_LIGHTS;
import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by basit on 12/19/17.
 */

public class SongTrackAdapter extends BaseAdapter {
    private static final String TAG = "SongAdapter";
    private Context mContext;
    private List<Songs> mTracks;
    private NotificationCompat.Builder mBuilder;
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
                SongDownloader(track.getUrl(),track.getSong());
                mBuilder = new NotificationCompat.Builder(mContext);
                showNotification("Downloading Song...",track.getSong());
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

   private void SongDownloader(String fileurl, final String filename){
        NetworkServices networkServices = RetrofitClient.getClient().create(NetworkServices.class);
        Call<ResponseBody> call = networkServices.downloadFileWithDynamicUrlSync(fileurl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(),filename);
                    if (writtenToDisk){
                        showNotification("Download Complete",filename);
                    }
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showNotification("Download Failed",filename);
                Log.d(TAG, "failed");
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

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
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

    private void showNotification(String message,String songName){
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(songName)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE | DEFAULT_LIGHTS);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, (int)
                System.currentTimeMillis(), intent, 0);
        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
