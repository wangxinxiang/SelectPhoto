package com.example.wang.selectphoto.photo.album;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wang.selectphoto.R;
import com.example.wang.selectphoto.photo.util.BasicTool;
import com.example.wang.selectphoto.photo.util.LImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2016/7/26.
 * 图片选择adapter
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {

    private Context mContext;
    private List<ImageLoaderData> dataList = new ArrayList<>();
    private LImageLoader mImageLoader;
    private int MAX_PHOTO_NUM = 3;

    public PhotoAdapter(Context mContext) {
        this.mContext = mContext;
        this.mImageLoader = LImageLoader.getInstance(mContext, 3, LImageLoader.Type.LIFO);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getItem());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ImageLoaderData data = dataList.get(position);
        if (null != this.mImageLoader) {
            holder.imageView.setImageResource(R.drawable.pictures_no);
            this.mImageLoader.loadImage(data.getFilePath(), holder.imageView);

            if (data.isChecked())
                holder.check.setImageResource(R.drawable.pictures_selected);
            else holder.check.setImageResource(R.drawable.picture_unselected);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.isChecked()) {
                        holder.check.setImageResource(R.drawable.picture_unselected);
                        data.setChecked(false);
                    } else {
                        if (getCheckCount() < MAX_PHOTO_NUM) {
                            holder.check.setImageResource(R.drawable.pictures_selected);
                            data.setChecked(true);
                        } else
                            Toast.makeText(mContext, "您最多只能选择" + MAX_PHOTO_NUM + "张图片", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 获取被选中的图片数量
     */
    private int getCheckCount() {
        int count = 0;
        try {
            if (null != this.dataList) {
                for (int i = 0; i < this.dataList.size(); i++) {
                    if (null != this.dataList.get(i) && this.dataList.get(i).isChecked()) {
                        count++;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return count;
    }

    private View getItem() {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        Point point = BasicTool.getScreenPoint(mContext);
        relativeLayout.setLayoutParams(new RecyclerView.LayoutParams(point.x / 3, point.x / 3));

        ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setId(R.id.photo_adapter_image);
        relativeLayout.addView(imageView);

        ImageButton imageButton = new ImageButton(mContext);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_TOP);
        imageButton.setLayoutParams(params);
        imageButton.setId(R.id.photo_adapter_check);
        imageButton.setImageResource(R.drawable.picture_unselected);
        imageButton.setClickable(false);
        imageButton.setBackground(null);
        relativeLayout.addView(imageButton);

        return relativeLayout;
    }

    public void setData(List<ImageLoaderData> dataList) {
        this.dataList = dataList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton check;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo_adapter_image);
            check = (ImageButton) itemView.findViewById(R.id.photo_adapter_check);
        }
    }


}
