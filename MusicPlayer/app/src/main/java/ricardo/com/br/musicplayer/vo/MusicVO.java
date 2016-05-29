package ricardo.com.br.musicplayer.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class MusicVO implements Parcelable{

    private String name;
    private String artist;
    private String path;

    public MusicVO(){}

    public MusicVO(String name, String artist, String path){
        this.name = name;
        this.artist = artist;
        this.path = path;
    }

    public MusicVO(Parcel source){
        this(source.readString(),
                source.readString(),
                source.readString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(artist);
        dest.writeString(path);
    }

    public static final Creator<MusicVO> CREATOR= new Creator<MusicVO>(){

        @Override
        public MusicVO createFromParcel(Parcel source) {
            return new MusicVO(source);
        }

        @Override
        public MusicVO[] newArray(int size) {
            return new MusicVO[size];
        }
    };
}
