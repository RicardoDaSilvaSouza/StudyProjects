package ricardo.com.br.musicplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import ricardo.com.br.musicplayer.MainActivity;
import ricardo.com.br.musicplayer.fragment.LoadingFragment;
import ricardo.com.br.musicplayer.fragment.MusicLetterFragment;
import ricardo.com.br.musicplayer.fragment.MusicListFragment;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<MusicVO> musics;

    public TabsAdapter(FragmentManager fm, Context context, List<MusicVO> musics) {
        super(fm);
        this.context = context;
        this.musics = musics;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                if(musics == null){
                    fragment = new LoadingFragment();
                } else {
                    fragment = MusicListFragment.newInstance(musics);
                    fragment.onAttach(context);
                }
                break;
            case 1:
                fragment = MusicLetterFragment.newInstance();
                fragment.onAttach(context);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}