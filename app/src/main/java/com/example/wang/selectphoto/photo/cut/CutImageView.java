package com.example.wang.selectphoto.photo.cut;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.wang.selectphoto.photo.title.OnTitleClickListener;
import com.example.wang.selectphoto.photo.title.TitleView;

/**
 * Created by wang on 2016/7/22.
 */
public class CutImageView extends LinearLayout implements OnTitleClickListener {
    private TitleView mTitle;
    private Context mContext;
    private ImageCutView imageCutView;
    private int titleHeight = 120;
    private String imagePath;
    private Bitmap image;
    private CutImageListener cutImageListener;

    public CutImageView(Context context) {
        super(context);
        mContext = context;
        init();
        regEvent();
    }

    private void init() {
        this.setOrientation(VERTICAL);
        mTitle = new TitleView(mContext);
        mTitle.setTitle("裁剪图片");
        addView(mTitle, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));

        imageCutView = new ImageCutView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        imageCutView.setLayoutParams(params);
        addView(imageCutView);
    }

    private void regEvent() {
        mTitle.setOnTitleClickListener(this);
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }

    public void setImagePath(String path) {
        imagePath = path;
        imageCutView.setBitmap(path);
    }

    public void setImage(Bitmap image) {
        this.image = image;
        imageCutView.setBitmap(image);
    }


    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void back() {
    }

    @Override
    public void confirm() {
//        Log.d("............", imageCutView.getBitmap().toString());
        cutImageListener.onCutImageFinish(imageCutView.getBitmap());
    }

    public void setCutImageListener(CutImageListener cutImageListener) {
        this.cutImageListener = cutImageListener;
    }

}
