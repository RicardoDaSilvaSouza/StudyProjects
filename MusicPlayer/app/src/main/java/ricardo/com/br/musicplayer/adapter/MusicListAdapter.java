package ricardo.com.br.musicplayer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.Objects;

import ricardo.com.br.musicplayer.MainActivity;
import ricardo.com.br.musicplayer.R;
import ricardo.com.br.musicplayer.fragment.MusicListFragment;
import ricardo.com.br.musicplayer.holder.MusicListHolder;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListHolder>{

    private final List<MusicVO> musics;
    private final Context context;
    private MusicListFragment.OnMusicListInteractionListener listener;

    public MusicListAdapter(Context context, List<MusicVO> musics, MusicListFragment.OnMusicListInteractionListener listener){
        this.context = context;
        this.musics = musics;
        this.listener = listener;
    }

    @Override
    public MusicListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_row, parent, false);
        return new MusicListHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicListHolder holder, final int position) {
        final MusicVO music = musics.get(position);

        holder.getTvMusicName().setText(music.getName());
        holder.getTvArtist().setText(music.getArtist());
        if(listener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlay(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musics != null?musics.size():0;
    }
}
