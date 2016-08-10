package com.example.wang.selectphoto.photo;

import android.app.Activity;
import android.app.FragmentController;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.wang.selectphoto.R;
import com.example.wang.selectphoto.photo.album.AlbumFragment;
import com.example.wang.selectphoto.photo.album.AlbumView;
import com.example.wang.selectphoto.photo.cut.CutImageListener;
import com.example.wang.selectphoto.photo.cut.CutImageView;
import com.example.wang.selectphoto.photo.title.OnTitleClickListener;
import com.example.wang.selectphoto.photo.util.BasicTool;


public class PhotoActivity extends Activity implements OnTitleClickListener {

    private AlbumFragment albumFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.photo_album_frameLayout);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        albumFragment = new AlbumFragment();
        fragmentTransaction.add(R.id.photo_album_frameLayout, albumFragment, "1");
        fragmentTransaction.commit();
        albumFragment.setOnTitleClickListener(this);
    }


    @Override
    public void back() {
        Toast.makeText(PhotoActivity.this, "back", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void confirm() {
        Toast.makeText(PhotoActivity.this, "confirm" + albumFragment.getCheckedImage().size(), Toast.LENGTH_SHORT).show();
    }
}
