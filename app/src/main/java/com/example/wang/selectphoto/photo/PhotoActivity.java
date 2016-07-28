package com.example.wang.selectphoto.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.album.AlbumView;
import com.example.wang.selectphoto.photo.cut.CutImageListener;
import com.example.wang.selectphoto.photo.cut.CutImageView;
import com.example.wang.selectphoto.photo.util.BasicTool;

/**
 * Created by wang on 2016/7/22.
 */
public class PhotoActivity extends Activity {

    CutImageView cutImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new AlbumView(this));
    }


}
