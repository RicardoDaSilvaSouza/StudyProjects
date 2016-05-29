package ricardo.com.br.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ricardo.com.br.musicplayer.MainActivity;
import ricardo.com.br.musicplayer.R;
import ricardo.com.br.musicplayer.adapter.MusicListAdapter;
import ricardo.com.br.musicplayer.itemdecoration.VerticalSpaceItemDecoration;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicListFragment.OnMusicListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicListFragment extends Fragment {

    public static final String TAG = "MusicListFragment";

    private RecyclerView rvMusics;
    private LinearLayoutManager linearLayoutManager;
    private List<MusicVO> musics;
    private OnMusicListInteractionListener mListener;

    public MusicListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param musics List<MusicVO>
     * @return A new instance of fragment MusicListFragment.
     */
    public static MusicListFragment newInstance(List<MusicVO> musics) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.PARAM_MUSICS, (ArrayList<? extends Parcelable>) musics);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musics = getArguments().getParcelableArrayList(MainActivity.PARAM_MUSICS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        rvMusics = (RecyclerView) view.findViewById(R.id.rvMusics);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvMusics.setLayoutManager(linearLayoutManager);
        rvMusics.setHasFixedSize(true);
        rvMusics.addItemDecoration(new VerticalSpaceItemDecoration(VerticalSpaceItemDecoration.VERTICAL_ITEM_SPACE));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvMusics.setAdapter(new MusicListAdapter(getContext(), musics, mListener));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMusicListInteractionListener) {
            mListener = (OnMusicListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMusicListInteractionListener");
        }
        mListener.setFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public RecyclerView getRvMusics(){
        return rvMusics;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMusicListInteractionListener {
        void onPlay(Integer position);
        void setFragment(Fragment fragment);
    }
}
