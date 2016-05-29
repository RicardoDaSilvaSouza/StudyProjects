package ricardo.com.br.musicplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import ricardo.com.br.musicplayer.MainActivity;

/**
 * Created by Ricardo on 03/01/2016.
 */
public class HeadsetReceiver extends BroadcastReceiver{

    private final MainActivity.MusicPlayerController musicPlayerController;

    public HeadsetReceiver(MainActivity.MusicPlayerController musicPlayerController){
        this.musicPlayerController = musicPlayerController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("state", -1);
        //Outros parâmetros recebidos
        //int microphone = intent.getIntExtra("microphone", -1);
        //String name = intent.getStringExtra("name");
        switch (state){
            case 0:
                if(musicPlayerController != null && musicPlayerController.isPlaying()){
                    musicPlayerController.pause();
                }
                break;
            case 1:
                break;
            default:
                Toast.makeText(context, "Não foi possível identificar o estado do headset", Toast.LENGTH_SHORT).show();
        }
    }
}