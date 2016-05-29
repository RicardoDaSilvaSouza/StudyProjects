package ricardo.com.br.musicplayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ricardo.com.br.musicplayer.R;

/**
 * Created by Ricardo on 10/01/2016.
 */
public class FetchMusicLetterIntentService extends IntentService{

    public static final String TAG = FetchMusicLetterIntentService.class.getName();
    public static final int FETCH_ERROR = 0;
    public static final int FETCH_SUCCESS = 1;
    public static final String MUSIC_LETTER = "musicLetter";
    public static final String URL = "url";
    public static final String RESULT_RECEIVER = "resultReceiver";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchMusicLetterIntentService() {
        super(FetchMusicLetterIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(FetchMusicLetterIntentService.URL).replaceAll(" ", "%20");
        ResultReceiver resultReceiver = intent.getParcelableExtra(FetchMusicLetterIntentService.RESULT_RECEIVER);
        HttpURLConnection connection =  null;
        BufferedReader reader = null;
        URL fetchUrl;
        try {
            fetchUrl= new URL(url);
            connection = (HttpURLConnection) fetchUrl.openConnection();
            if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder builder = new StringBuilder();
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                Bundle bundle = new Bundle();
                bundle.putString(FetchMusicLetterIntentService.MUSIC_LETTER, getMusicaLetterFromJson(builder.toString()));
                resultReceiver.send(FETCH_SUCCESS, bundle);
            } else {
                throw new Exception(getString(R.string.message_fetch_music_letter_error));
            }
        } catch (Exception e) {
            Log.e(FetchMusicLetterIntentService.TAG, e.getMessage());
            resultReceiver.send(FETCH_ERROR, Bundle.EMPTY);
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(FetchMusicLetterIntentService.TAG, e.getMessage());
                }
            }
        }
    }

    private String getMusicaLetterFromJson(String json) throws Exception {
        String musicLetter = null;
        try {
            if(json != null){
                JSONObject jsonObject = new JSONObject(json);
                String type = jsonObject.getString("type");
                if(type.equalsIgnoreCase("exact")){
                    JSONArray jsonArray =jsonObject.getJSONArray("mus");
                    musicLetter = ((JSONObject)jsonArray.get(0)).getString("text");
                } else {
                    throw new Exception(getResources().getText(R.string.message_fetch_music_letter_error).toString());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        }
        return musicLetter;
    }
}
