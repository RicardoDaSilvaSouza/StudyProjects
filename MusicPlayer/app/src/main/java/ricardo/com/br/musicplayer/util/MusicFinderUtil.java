package ricardo.com.br.musicplayer.util;

import android.os.Environment;
import android.util.Log;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ricardo.com.br.musicplayer.filter.MusicFilter;
import ricardo.com.br.musicplayer.vo.MusicVO;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class MusicFinderUtil {

    public static final String NAME_MUSIC_DIRECTORY = "/musicas";

    public static List<File> loadMusics(){
        return loadMusics(null);
    }

    public static List<File> loadMusics(List<File> files){
        List<File> musics = new ArrayList<>();
        if(files != null){
            for(File file: files){
                if(file.isDirectory()){
                    List<File> subDirs = Arrays.asList(file.listFiles(new MusicFilter()));
                    musics.addAll(loadMusics(subDirs));
                } else if(file.isFile()) {
                    musics.add(file);
                }
            }
        } else {
            File dirMusic = new File(Environment.getExternalStorageDirectory().getPath().concat(NAME_MUSIC_DIRECTORY));
            if(dirMusic.exists()){
                musics.addAll(loadMusics(createListFiles(dirMusic.listFiles(new MusicFilter()))));
            }
        }
        return musics;
    }

    private static List<File> createListFiles(File[] files){
        List<File> result = null;
        if(files != null){
            result = Arrays.asList(files);
        }
        return result;
    }

    public static List<MusicVO> getMusicsFromFiles(List<File> files){
        List<MusicVO> musics = new ArrayList<>();
        for(File file : files){
            try {
                MP3File mp3File = new MP3File(file);
                MusicVO music = new MusicVO();
                String musicName = null;
                String artistName = null;
                if(mp3File.hasID3v2Tag()){
                    musicName = mp3File.getID3v2Tag().getSongTitle();
                    artistName = mp3File.getID3v2Tag().getLeadArtist();
                } else if(mp3File.hasID3v1Tag()){
                    musicName = mp3File.getID3v1Tag().getSongTitle();
                    artistName = mp3File.getID3v1Tag().getLeadArtist();
                }
                if(musicName == null || musicName.isEmpty()){
                    String path = file.getPath();
                    musicName = path.substring(path.lastIndexOf(File.separator)+1,path.length() -4);
                }
                if(artistName == null || artistName.isEmpty()){
                    artistName = "";
                }
                music.setName(musicName);
                music.setArtist(artistName);
                music.setPath(file.getAbsolutePath());
                musics.add(music);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            }
        }
        return musics;
    }
}
