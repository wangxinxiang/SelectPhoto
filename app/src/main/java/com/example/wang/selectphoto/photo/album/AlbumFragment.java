package com.example.wang.selectphoto.photo.album;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wang.selectphoto.photo.title.TitleView;

/**
 * Created by wang on 2016/7/26.
 * 手机图片选择
 */
public class AlbumFragment extends Fragment{

    private TitleView mTitle;
    private AlbumView view;
    private int titleHeight = 120;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = new AlbumView(getActivity());
        mTitle = new TitleView(getActivity());
        mTitle.setTitle("选择图片");
        view.addView(mTitle, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
