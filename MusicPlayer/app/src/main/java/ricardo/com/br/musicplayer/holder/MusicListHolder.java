package ricardo.com.br.musicplayer.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ricardo.com.br.musicplayer.R;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class MusicListHolder extends RecyclerView.ViewHolder {

    private TextView tvMusicName;
    private TextView tvArtist;

    public MusicListHolder(View itemView) {
        super(itemView);

        tvMusicName = (TextView) itemView.findViewById(R.id.tvMusicName);
        tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
    }

    public TextView getTvMusicName() {
        return tvMusicName;
    }

    public TextView getTvArtist() {
        return tvArtist;
    }
}
