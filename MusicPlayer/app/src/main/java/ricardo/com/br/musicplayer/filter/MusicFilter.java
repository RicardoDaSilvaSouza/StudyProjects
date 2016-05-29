package ricardo.com.br.musicplayer.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Ricardo on 20/12/2015.
 */
public class MusicFilter implements FileFilter {

    public static final String VALID_EXTENSION = "mp3";

    @Override
    public boolean accept(File pathname) {
        boolean result = true;
        String archiveName = pathname.getName();
        if(archiveName.indexOf(".") > -1){
            String extension = archiveName.substring(archiveName.length() - 3, archiveName.length());
            if(!extension.equalsIgnoreCase(MusicFilter.VALID_EXTENSION)){
                result = false;
            }
        } else if(!pathname.isDirectory()){
            result = false;
        }
        return result;
    }
}
