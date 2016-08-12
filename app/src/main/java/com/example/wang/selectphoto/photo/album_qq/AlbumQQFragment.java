package com.example.wang.selectphoto.photo.album_qq;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wang.selectphoto.photo.title.OnTitleClickListener;
import com.example.wang.selectphoto.photo.title.TitleView;

import java.util.List;

/**
 * Created by wang on 2016/8/10.
 * 仿QQ图片选择
 */
public class AlbumQQFragment extends Fragment implements QQAlbumView.OnAlbumListener{

    private QQAlbumView view;
    private int titleHeight = 120;
    private TitleView mTitle;
    private OnSelectedPhotoListener onSelectedPhotoListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = new QQAlbumView(getActivity());
        mTitle = new TitleView(getActivity());
        mTitle.setTitle("选择图片目录");
        view.addView(mTitle, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        initListener();
        return view;
    }

    private void initListener() {
        mTitle.setOnTitleClickListener(new OnTitleClickListener() {
            @Override
            public void back() {
                if (!"选择图片目录".equals(mTitle.getTitleName())) {
                    view.backPhotoDir();
                    mTitle.getRightBody().setVisibility(View.GONE);
                    mTitle.setTitle("选择图片目录");
                }
            }

            @Override
            public void confirm() {
                if (onSelectedPhotoListener != null) onSelectedPhotoListener.selectPhotoFinish(view.getSelectedPhoto());
            }
        });

        view.setOnAlbumListener(this);
    }

    /**
     * 当点击相册目录时的监听
     * @param dirName 相册名称
     */
    @Override
    public void onEnterPhotoListListener(String dirName) {
        mTitle.setTitle(dirName);
        mTitle.getRightBody().setVisibility(View.VISIBLE);
    }

    public void setOnSelectedPhotoListener(OnSelectedPhotoListener onSelectedPhotoListener) {
        this.onSelectedPhotoListener = onSelectedPhotoListener;
    }

    public interface OnSelectedPhotoListener {
        void selectPhotoFinish(List<String> photos);
    }
}
