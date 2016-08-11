package com.example.wang.selectphoto.photo.album_qq;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wang on 2016/8/10.
 */
public class AlbumQQFragment extends Fragment{

    private QQAlbumView qqAlbumView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new QQAlbumView(getActivity());
    }
}
