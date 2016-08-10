package com.example.wang.selectphoto.photo.album;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.title.OnTitleClickListener;
import com.example.wang.selectphoto.photo.title.TitleView;

import java.util.List;

/**
 * Created by wang on 2016/7/26.
 * 手机图片选择
 */
public class AlbumFragment extends Fragment{

    private TitleView mTitle;
    private AlbumView view;
    private int titleHeight = 120;
    private OnTitleClickListener onTitleClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = new AlbumView(getActivity());
        mTitle = new TitleView(getActivity());
        mTitle.setTitle("选择图片");
        view.addView(mTitle, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        mTitle.setOnTitleClickListener(onTitleClickListener);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }



    public List<String> getCheckedImage() {
        return view.getSelectedPhoto();
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleCLickListener) {
        this.onTitleClickListener = onTitleCLickListener;
    }
}
