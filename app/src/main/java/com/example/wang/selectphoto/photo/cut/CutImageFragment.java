package com.example.wang.selectphoto.photo.cut;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.util.BasicTool;

/**
 * Created by wang on 2016/7/22.
 */
public class CutImageFragment extends Fragment  implements CutImageListener{
    CutImageView cutImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        cutImageView = new CutImageView(getActivity());
        cutImageView.setImagePath("/storage/emulated/0/DCIM/Camera/IMG_20160710_195357_HDR.jpg");
        cutImageView.setCutImageListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCutImageFinish(Bitmap result) {
        Log.d(".............", result.toString());
        Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_SHORT).show();
        cutImageView.setImagePath(BasicTool.setPicToView(result));
    }
}
