package com.example.wang.selectphoto.photo.album_qq;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wang.selectphoto.R;
import com.example.wang.selectphoto.photo.title.AngleView;
import com.example.wang.selectphoto.photo.util.BasicTool;
import com.example.wang.selectphoto.photo.util.LImageLoader;

import java.util.List;

/**
 * Created by wang on 2016/8/10.
 * 图片文件夹adapter
 */
public class PhotoDirAdapter extends RecyclerView.Adapter<PhotoDirAdapter.MyViewHolder> {

    private List<ImageFolder> mImageFolders;
    private Context mContext;
    private int itemHeight = 70;
    private LImageLoader mImageLoader;
    private OnItemClickListener mOnItemClickLictener;

    public PhotoDirAdapter(List<ImageFolder> mImageFolders, Context mContext) {
        this.mImageFolders = mImageFolders;
        this.mContext = mContext;
        this.mImageLoader = LImageLoader.getInstance(mContext, 3, LImageLoader.Type.LIFO);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(getItem());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageFolder imageFolder = mImageFolders.get(position);
        mImageLoader.loadImage(imageFolder.getFirstImagePath(), holder.imageView);
        holder.dir.setText(imageFolder.getName() + " (" + imageFolder.getCount() + ")");
    }

    @Override
    public int getItemCount() {
        return mImageFolders.size();
    }

    private View getItem() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, BasicTool.dip2px(mContext, itemHeight)));

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(BasicTool.dip2px(mContext, itemHeight), BasicTool.dip2px(mContext, itemHeight)));
        imageView.setId(R.id.photo_adapter_image);
        linearLayout.addView(imageView);

        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        textView.setPadding(BasicTool.dip2px(mContext, itemHeight / 3), 0, 0, 0);
        textView.setTextSize(16);
        textView.setId(R.id.photo_adapter_dir);
        linearLayout.addView(textView);

        AngleView enter = new AngleView(mContext);
        enter.set_kind(AngleView.kind.RF);
        enter.setStrokeWidth(3);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemHeight / 2, itemHeight / 2);
        layoutParams.setMargins(0, 0, BasicTool.dip2px(mContext, itemHeight / 4), 0);
        enter.setLayoutParams(layoutParams);
        linearLayout.addView(enter);

        return linearLayout;
    }

    public void setmImageFolders(List<ImageFolder> mImageFolders) {
        this.mImageFolders = mImageFolders;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickLictener = mOnItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView dir;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo_adapter_image);
            dir = (TextView) itemView.findViewById(R.id.photo_adapter_dir);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickLictener != null) {
                        mOnItemClickLictener.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
