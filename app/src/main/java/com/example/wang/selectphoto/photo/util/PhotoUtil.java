package com.example.wang.selectphoto.photo.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wang on 2016/8/12.
 */
public class PhotoUtil {

    public static List<String> getPhotosFromDir(String dir) {
        File file = new File(dir);
        List<String> list = Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File file = new File(dir.getAbsolutePath() + "/" + filename);
                return file.length() > 100 && (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"));
            }

        }));
        return list;
    }

    public static List<String> fileOrderByTime(List<String> list) {
        return null;
    }
}
