package com.example.wang.selectphoto.photo.album;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.wang.selectphoto.R;
import com.example.wang.selectphoto.photo.util.BasicTool;
import com.example.wang.selectphoto.photo.util.LImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2016/7/26.
 * 图片选择adapter
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>{

    private Context mContext;
    private List<ImageLoaderData> dataList = new ArrayList<>();
    private LImageLoader mImageLoader;

    public PhotoAdapter(Context mContext) {
        this.mContext = mContext;
        this.mImageLoader = LImageLoader.getInstance(mContext, 3, LImageLoader.Type.LIFO);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getItem());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageLoaderData data = dataList.get(position);
        if(null!=this.mImageLoader)
        {
            this.mImageLoader.loadImage(data.getFilePath(), holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private View getItem() {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        Point point = BasicTool.getScreenPoint(mContext);
        relativeLayout.setLayoutParams(new RecyclerView.LayoutParams(point.x/3, point.x/3));
        ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setId(R.id.photo_adapter_image);
        relativeLayout.addView(imageView);

        return relativeLayout;
    }

    public void setData(List<ImageLoaderData> dataList) {
        this.dataList = dataList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo_adapter_image);

        }
    }


}
