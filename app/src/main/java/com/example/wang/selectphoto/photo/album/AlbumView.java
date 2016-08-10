package com.example.wang.selectphoto.photo.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.title.OnTitleClickListener;
import com.example.wang.selectphoto.photo.title.TitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2016/7/26.
 * 图片选择View
 */
public class AlbumView extends LinearLayout implements Handler.Callback{

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private Context mContext;
    private List<ImageLoaderData> mFileList = new ArrayList<>();     //储存图片路径
    private List<String> mSelected = new ArrayList<>();         //被选中的路径,一般用于选择图片时累加效果
    private int maxCount = 100;     //获取的最大图片数量
    private int maxSelected = 4;    //最多能选择几张图片
    private Handler mHandler;       //用于获取图片后进行界面更新

    public AlbumView(Context context) {
        super(context);
        mContext = context;
        init();
        getImages();
    }

    private void init() {
        this.setOrientation(VERTICAL);

        mRecyclerView = new RecyclerView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        mRecyclerView.setLayoutParams(params);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        addView(mRecyclerView);



        mHandler = new Handler(this);
    }

    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            if(null!=this.mContext)
            {
                Toast.makeText(this.mContext, "暂无外部存储", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Uri mImageUri;
                ContentResolver mContentResolver;
                Cursor mCursor;
                String path;
                File f;
                ImageLoaderData data;
                int selectedCount = 0;
                try
                {
                    if(null!=mContext && null!=mFileList)
                    {
                        mFileList.clear();

                        mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if(null!=mImageUri)
                        {
                            mContentResolver = mContext.getContentResolver();
                            if(null!=mContentResolver)
                            {
                                mCursor = mContentResolver.query(
                                        mImageUri,
                                        null,
                                        //MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                                        MediaStore.Images.Media.MIME_TYPE + "=?",
                                        new String[] { "image/jpeg"},//"image/png"
                                        MediaStore.Images.Media.DATE_MODIFIED +" DESC"
                                );
                                if(null!=mCursor)
                                {
                                    while (mCursor.moveToNext())
                                    {
                                        // 获取图片的路径
                                        path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                        if(null!=path && !"".equals(path))
                                        {
                                            f = new File(path);
                                            if(f.exists())
                                            {
                                                if(f.isFile())
                                                {
                                                    if(maxCount>mFileList.size())
                                                    {
                                                        data = new ImageLoaderData(path,false);
                                                        if(null!=mSelected)
                                                        {
                                                            if(mSelected.contains(path) && selectedCount < maxSelected)
                                                            {
                                                                selectedCount++;
                                                                data.setChecked(true);
                                                            }
                                                        }
                                                        mFileList.add(data);
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                mContentResolver.delete(mImageUri, MediaStore.Images.Media._ID +"="+mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)), null);
                                            }
                                        }
                                    }
                                    mCursor.close();
                                }
                            }
                        }
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                finally
                {
                    if(null!=mHandler)
                    {
                        mHandler.sendEmptyMessage(0x110);
                    }
                }
            }
        }).start();

    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 获取被选择的图片
     */
    public List<String> getSelectedPhoto() {
        List<String> selectList = new ArrayList<>();
        for (ImageLoaderData imageLoaderData : mFileList) {
            if (imageLoaderData.isChecked())
                selectList.add(imageLoaderData.getFilePath());
        }
        return selectList;
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        try
        {
            if(null!=this.mRecyclerView && null!=this.mFileList)
            {
                this.mFileList.add(0, new ImageLoaderData("pictures_camera",false));
                if(null==this.mAdapter)
                {
                    if(null!=this.mContext)
                    {
                        this.mAdapter = new PhotoAdapter(this.mContext);
                    }
                }
                if(null!=this.mAdapter)
                {
                    this.mAdapter.setData(this.mFileList);
                    this.mRecyclerView.setAdapter(this.mAdapter);
                }
            }
//            this.initBtnText();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


}
