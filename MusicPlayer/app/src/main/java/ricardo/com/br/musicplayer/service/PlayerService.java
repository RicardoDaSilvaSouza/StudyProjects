package ricardo.com.br.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import ricardo.com.br.musicplayer.MainActivity;
import ricardo.com.br.musicplayer.R;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * Created by Ricardo on 30/12/2015.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener{

    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = 1;
    private List<MusicVO> musics;
    private Integer currentPosition = 0;
    private Integer currentTime = 0;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            Bundle extras = intent.getExtras();
            musics = extras.getParcelableArrayList(MainActivity.PARAM_MUSICS);
            currentPosition = extras.getInt(MainActivity.PARAM_CURRENT_POSITION);
            currentTime = extras.getInt(MainActivity.PARAM_CURRENT_TIME);
            startMediaPlayer(musics.get(currentPosition).getPath());

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(musics.get(currentPosition).getArtist());
            builder.setContentText(musics.get(currentPosition).getName());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentIntent(createPendingIntent());
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(notificationManager != null){
            notificationManager.cancel(NOTIFICATION_ID);
        }
        releaseMediaPlayer();
        stopSelf();
        super.onDestroy();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startMediaPlayer(String path) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.seekTo(currentTime);
        mediaPlayer.start();
    }

    private PendingIntent createPendingIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra(MainActivity.PARAM_MUSICS, (ArrayList<? extends Parcelable>) musics);
        intent.putExtra(MainActivity.PARAM_CURRENT_POSITION, currentPosition.intValue());
        intent.putExtra(MainActivity.PARAM_CURRENT_TIME, mediaPlayer.getCurrentPosition());
        intent.putExtra(MainActivity.PARAM_IS_PLAYING, mediaPlayer.isPlaying());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notificationManager.cancel(NOTIFICATION_ID);
        stopSelf();
    }
}
