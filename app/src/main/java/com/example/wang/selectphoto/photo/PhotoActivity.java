package com.example.wang.selectphoto.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.cut.CutImageListener;
import com.example.wang.selectphoto.photo.cut.CutImageView;
import com.example.wang.selectphoto.photo.util.BasicTool;

/**
 * Created by wang on 2016/7/22.
 */
public class PhotoActivity extends Activity implements CutImageListener {

    CutImageView cutImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cutImageView = new CutImageView(this);
        cutImageView.setImagePath("/storage/emulated/0/DCIM/Camera/IMG_20160710_195357_HDR.jpg");
        setContentView(cutImageView);
        cutImageView.setCutImageListener(this);
//        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
//        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intent1, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(".............", data.getData().getPath());
        cutImageView.setImagePath(data.getData().getPath());
    }

    @Override
    public void onCutImageFinish(Bitmap result) {
        Log.d(".............", result.toString());
        Toast.makeText(PhotoActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
        cutImageView.setImagePath(BasicTool.setPicToView(result));
    }
}
