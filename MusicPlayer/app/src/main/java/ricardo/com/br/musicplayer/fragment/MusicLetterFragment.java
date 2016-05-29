package ricardo.com.br.musicplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import ricardo.com.br.musicplayer.R;
import ricardo.com.br.musicplayer.service.FetchMusicLetterIntentService;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicLetterFragment.OnMusicLetterInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicLetterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicLetterFragment extends Fragment {

    public static final String TAG = "MusicLetterFragment";

    private MusicVO music;
    private OnMusicLetterInteractionListener mListener;
    private TextView tvMusicLetter;
    private ImageView ivVagalumeLogo;
    private FetchMusicLetterResultReceiver receiver;

    public MusicLetterFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicLetterFragment.
     */
    public static MusicLetterFragment newInstance() {
        return new MusicLetterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_letter, container, false);
        ivVagalumeLogo = (ImageView) view.findViewById(R.id.ivVagalumeLogo);
        try {
            ivVagalumeLogo.setImageDrawable(Drawable.createFromStream(view.getContext().getAssets().open("vagalumelogo.jpg"), null));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        tvMusicLetter = (TextView) view.findViewById(R.id.tvMusicLetter);
        receiver = new FetchMusicLetterResultReceiver(new Handler());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMusicLetterInteractionListener) {
            mListener = (OnMusicLetterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mListener.setFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public TextView getTvMusicLetter(){
        return this.tvMusicLetter;
    }

    public void getCurrentMusic() {
        if (mListener != null) {
            music = mListener.getCurrentMusic();
        }
    }

    public void initFetchService(){
        if(music != null){
            Intent intent = new Intent(getActivity(), FetchMusicLetterIntentService.class);
            StringBuilder url = new StringBuilder("http://api.vagalume.com.br/search.php?");
            url.append("art=")
                    .append(music.getArtist())
                    .append("&mus=")
                    .append(music.getName())
                    .append("&apikey=f3a5957ff56168724495df91cd50dafc");
            intent.putExtra(FetchMusicLetterIntentService.URL, url.toString());
            intent.putExtra(FetchMusicLetterIntentService.RESULT_RECEIVER, receiver);
            getActivity().startService(intent);
        } else {
            tvMusicLetter.setText(R.string.message_no_current_music);
        }
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
    public interface OnMusicLetterInteractionListener {
        MusicVO getCurrentMusic();
        void setFragment(Fragment fragment);
    }

    private class FetchMusicLetterResultReceiver extends ResultReceiver{

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public FetchMusicLetterResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            switch (resultCode) {
                case FetchMusicLetterIntentService.FETCH_SUCCESS:
                    tvMusicLetter.setText(resultData.getString(FetchMusicLetterIntentService.MUSIC_LETTER));
                    break;
                case FetchMusicLetterIntentService.FETCH_ERROR:
                    tvMusicLetter.setText(R.string.message_error_fetch_letter);
                    break;
            }
        }
    }
}