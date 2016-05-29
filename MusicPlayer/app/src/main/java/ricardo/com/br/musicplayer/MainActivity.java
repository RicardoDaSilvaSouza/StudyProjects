package ricardo.com.br.musicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ricardo.com.br.musicplayer.adapter.TabsAdapter;
import ricardo.com.br.musicplayer.broadcast.HeadsetReceiver;
import ricardo.com.br.musicplayer.fragment.MusicLetterFragment;
import ricardo.com.br.musicplayer.fragment.MusicListFragment;
import ricardo.com.br.musicplayer.service.PlayerService;
import ricardo.com.br.musicplayer.util.MusicFinderUtil;
import ricardo.com.br.musicplayer.vo.MusicVO;

public class MainActivity extends AppCompatActivity
        implements MusicListFragment.OnMusicListInteractionListener,
        MusicLetterFragment.OnMusicLetterInteractionListener, MediaPlayer.OnPreparedListener {

    public static final String PARAM_MUSICS = "musics";
    public static final String PARAM_CURRENT_POSITION = "currentPosition";
    public static final String PARAM_CURRENT_TIME = "currentTime";
    public static final String PARAM_IS_PLAYING = "isPlaying";

    private ViewPager vpPages;
    private Toolbar toolbar;
    private TabLayout tabs;
    private MediaPlayer mediaPlayer;
    private MusicController musicController;
    private Handler handler;
    private MusicListFragment musicListFragment;
    private MusicLetterFragment musicLetterFragment;
    private List<MusicVO> musics;
    private Integer currentPosition = 0;
    private Integer currentTime = 0;
    private Boolean isPlaying = false;
    private HeadsetReceiver headsetReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            musics = savedInstanceState.getParcelableArrayList(PARAM_MUSICS);
            currentPosition = savedInstanceState.getInt(PARAM_CURRENT_POSITION);
            currentTime = savedInstanceState.getInt(PARAM_CURRENT_TIME);
            isPlaying = savedInstanceState.getBoolean(PARAM_IS_PLAYING);
        } else if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(PARAM_MUSICS)){
            loadByExtras(getIntent().getExtras());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = (TabLayout) findViewById(R.id.tlOptions);

        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_list));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_letter));

        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        vpPages = (ViewPager) findViewById(R.id.vpPages);
        vpPages.setAdapter(new TabsAdapter(getSupportFragmentManager(), this, musics));

        vpPages.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPages.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 1:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                                == PackageManager.PERMISSION_GRANTED) {
                            fetchMusicLetter();
                        } else {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.INTERNET}, 1);
                        }

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        handler = new Handler();
        setMediaController();
        initMediaPlayer();
    }

    private void fetchMusicLetter(){
        musicLetterFragment.getCurrentMusic();
        musicLetterFragment.initFetchService();
    }

    private void setMediaController() {
        musicController = new MusicController(this, false);
        musicController.setAnchorView(vpPages);
        musicController.setMediaPlayer(new MusicPlayerController());
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseMediaPlayer();
                updateMusicList();
                currentPosition++;
                if (currentPosition > musics.size() - 1) {
                    currentPosition = musics.size() - 1;
                }
                musicListFragment.getRvMusics().scrollToPosition(currentPosition);
                currentTime = 0;
                initMediaPlayer(musics.get(currentPosition).getPath());
                showController();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseMediaPlayer();
                updateMusicList();
                currentPosition--;
                if (currentPosition < 0) {
                    currentPosition = 0;
                }
                musicListFragment.getRvMusics().scrollToPosition(currentPosition);
                currentTime = 0;
                initMediaPlayer(musics.get(currentPosition).getPath());
                showController();
            }
        });
    }

    private void loadMusics() {
        new FindFilesTask().execute();
    }

    private void initMediaPlayer(String path) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
        mediaPlayer.seekTo(currentTime);
        mediaPlayer.start();
        updateMusicList();
    }

    private void initMediaPlayer() {
        if(musics != null && musics.size() > 0){
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(MainActivity.this, Uri.parse(musics.get(currentPosition).getPath()));
                mediaPlayer.setOnPreparedListener(MainActivity.this);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        currentTime = 0;
    }

    private void callBackgroundService() {
        if (isPlaying) {
            Intent intent = new Intent(this, PlayerService.class);
            intent.putParcelableArrayListExtra(PARAM_MUSICS, (ArrayList<? extends Parcelable>) musics);
            intent.putExtra(PARAM_CURRENT_POSITION, currentPosition.intValue());
            intent.putExtra(PARAM_CURRENT_TIME, mediaPlayer.getCurrentPosition());
            pauseMediaPlayer();
            startService(intent);
        }
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = mediaPlayer.isPlaying();
    }

    private void updateMusicList() {
        RecyclerView.ViewHolder lastViewHolder = musicListFragment.getRvMusics().findViewHolderForLayoutPosition(currentPosition);
        if(lastViewHolder != null){
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                lastViewHolder.itemView.setBackgroundResource(R.color.colorPrimaryDark);
            } else {
                lastViewHolder.itemView.setBackgroundResource(R.color.colorPrimaryLight);
            }
        }
    }

    private void registerHeadsetReceiver() {
        headsetReceiver = new HeadsetReceiver(new MusicPlayerController());
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    private void unregisterHeadsetReiceiver(){
        if(headsetReceiver != null){
            unregisterReceiver(headsetReceiver);
            headsetReceiver = null;
        }
    }

    private void showController(){
        if (musicController.isEnabled()) {
            musicController.show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    musicController.setEnabled(true);
                    musicController.show();
                }
            });
        }
    }

    private void loadByExtras(Bundle extras){
        musics = extras.getParcelableArrayList(PARAM_MUSICS);
        currentPosition = extras.getInt(PARAM_CURRENT_POSITION, 0);
        currentTime = extras.getInt(PARAM_CURRENT_TIME, 0);
        isPlaying = extras.getBoolean(PARAM_IS_PLAYING, false);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if(vpPages != null){
            vpPages.setAdapter(new TabsAdapter(getSupportFragmentManager(), this, musics));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            loadByExtras(intent.getExtras());
            if (isPlaying) {
                initMediaPlayer(musics.get(currentPosition).getPath());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnControle:
                showController();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PARAM_MUSICS, (ArrayList<? extends Parcelable>) musics);
        outState.putInt(PARAM_CURRENT_POSITION, currentPosition);
        outState.putInt(PARAM_CURRENT_TIME, mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0);
        outState.putBoolean(PARAM_IS_PLAYING, mediaPlayer != null ? mediaPlayer.isPlaying() : false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    loadMusics();
                } else {
                    Toast.makeText(MainActivity.this, R.string.message_read_write_permission_deny, Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchMusicLetter();
                } else {
                    Toast.makeText(MainActivity.this, R.string.message_internet_permission_deny, Toast.LENGTH_SHORT).show();
                    musicLetterFragment.getTvMusicLetter().setText(R.string.message_internet_permission_deny);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterHeadsetReiceiver();
        callBackgroundService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        unregisterHeadsetReiceiver();
        stopService(new Intent(this, PlayerService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(this, PlayerService.class));
        registerHeadsetReceiver();
        if(MainActivity.this.musics == null || MainActivity.this.musics.size() <= 0){
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                loadMusics();
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (isPlaying) {
            mp.seekTo(currentTime);
            mp.start();
            updateMusicList();
        }
    }

    @Override
    public void onPlay(Integer position) {
        if (currentPosition.intValue() != position.intValue()) {
            releaseMediaPlayer();
            updateMusicList();
            currentPosition = position;
            initMediaPlayer(musics.get(currentPosition.intValue()).getPath());
        } else {
            if (mediaPlayer == null || !isPlaying) {
                initMediaPlayer(musics.get(currentPosition.intValue()).getPath());
            } else {
                pauseMediaPlayer();
                updateMusicList();
            }
        }
        isPlaying = mediaPlayer.isPlaying();
        showController();
    }



    @Override
    public void setFragment(Fragment fragment) {
        if (fragment instanceof MusicListFragment) {
            musicListFragment = (MusicListFragment) fragment;
        } else {
            musicLetterFragment = (MusicLetterFragment) fragment;
        }
    }

    @Override
    public MusicVO getCurrentMusic() {
        MusicVO vo = null;
        if(musics != null && musics.size() > 0){
            vo = musics.get(currentPosition);
        }
        return vo;
    }

    public class MusicController extends MediaController {

        private ImageButton ibStop;

        public MusicController(Context context) {
            super(context);
        }

        public MusicController(Context context, boolean useFastForward) {
            super(context, useFastForward);
        }

        @Override
        public void setAnchorView(View view) {
            super.setAnchorView(view);

            ibStop = new ImageButton(this.getContext());
            ibStop.setImageResource(R.drawable.ic_action_stop);
            ibStop.setBackground(null);
            ibStop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.this.releaseMediaPlayer();
                    MainActivity.this.updateMusicList();
                    MainActivity.this.showController();
                }
            });
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            addView(ibStop, params);
        }
    }

    public class MusicPlayerController implements MusicController.MediaPlayerControl {

        @Override
        public void start() {
            if (MainActivity.this.mediaPlayer == null) {
                MainActivity.this.isPlaying = true;
                MainActivity.this.initMediaPlayer();
            } else {
                MainActivity.this.mediaPlayer.start();
                MainActivity.this.updateMusicList();
                MainActivity.this.isPlaying = MainActivity.this.mediaPlayer.isPlaying();
            }
            MainActivity.this.showController();
        }

        @Override
        public void pause() {
            if (MainActivity.this.mediaPlayer != null) {
                MainActivity.this.mediaPlayer.pause();
                MainActivity.this.isPlaying = MainActivity.this.mediaPlayer.isPlaying();
            } else {
                MainActivity.this.isPlaying = false;
            }
            MainActivity.this.updateMusicList();
            MainActivity.this.showController();
        }

        @Override
        public int getDuration() {
            int duration = 0;
            if (MainActivity.this.mediaPlayer != null) {
                duration = MainActivity.this.mediaPlayer.getDuration();
            }
            return duration;
        }

        @Override
        public int getCurrentPosition() {
            int currentPosition = 0;
            if (MainActivity.this.mediaPlayer != null) {
                currentPosition = MainActivity.this.mediaPlayer.getCurrentPosition();
            }
            return currentPosition;
        }

        @Override
        public void seekTo(int pos) {
            if (MainActivity.this.mediaPlayer != null) {
                MainActivity.this.mediaPlayer.seekTo(pos);
            }
        }

        @Override
        public boolean isPlaying() {
            boolean isPlaying = false;
            if (MainActivity.this.mediaPlayer != null) {
                isPlaying = MainActivity.this.mediaPlayer.isPlaying();
            }
            return isPlaying;
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            int audioSessionId = 0;
            if (MainActivity.this.mediaPlayer != null) {
                audioSessionId = MainActivity.this.mediaPlayer.getAudioSessionId();
            }
            return audioSessionId;
        }
    }

    public class FindFilesTask extends AsyncTask<Void, Void, List<MusicVO>>{

        @Override
        protected List<MusicVO> doInBackground(Void... params) {
            return MusicFinderUtil.getMusicsFromFiles(MusicFinderUtil.loadMusics());
        }

        @Override
        protected void onPostExecute(List<MusicVO> musicVOs) {
            super.onPostExecute(musicVOs);
            MainActivity.this.musics = musicVOs;
            MainActivity.this.onContentChanged();
        }
    }
}
