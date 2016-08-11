package com.example.wang.selectphoto.photo.album_qq;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wang.selectphoto.photo.album.DividerItemDecoration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by wang on 2016/8/10.
 * 仿qq图片选择器
 */
public class QQAlbumView extends LinearLayout{

    private RecyclerView mRecyclerView;
    private PhotoDirAdapter mPhotoDirAdapter;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private LinkedHashSet<String> mDirPaths = new LinkedHashSet<>();
    int totalCount = 0;
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFolders = new ArrayList<ImageFolder>();
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    private File mImgDir;


    public QQAlbumView(Context context) {
        super(context);
        mContext = context;
        init();
        getImages();
    }

    private void init() {
        mRecyclerView = new RecyclerView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        mRecyclerView.setLayoutParams(params);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));
        addView(mRecyclerView);
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(mContext, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(mContext, null, "正在加载...");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);

                if (mCursor != null) {
                    while (mCursor.moveToNext())
                    {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));

                        Log.e("TAG", path);
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        String dirPath = parentFile.getAbsolutePath();
                        ImageFolder imageFolder = null;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath))
                        {
                            continue;
                        } else
                        {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            imageFolder = new ImageFolder();
                            imageFolder.setDir(dirPath);
                            imageFolder.setFirstImagePath(path);
                        }

                        int picSize = parentFile.list(new FilenameFilter()
                        {
                            @Override
                            public boolean accept(File dir, String filename)
                            {
                                return filename.endsWith(".jpg")
                                        || filename.endsWith(".png")
                                        || filename.endsWith(".jpeg");
                            }
                        }).length;
                        totalCount += picSize;

                        imageFolder.setCount(picSize);

                        //按从大到小进行排序插入
                        if (mImageFolders.size() > 0) {
                            for (int i = 0; i < mImageFolders.size(); i++) {
                                if (mImageFolders.get(i).getCount() < picSize) {
                                    mImageFolders.add(i, imageFolder);
                                    break;
                                }

                            }
                        } else  mImageFolders.add(imageFolder);

                        if (picSize > mPicsSize)
                        {
                            mPicsSize = picSize;
                            mImgDir = parentFile;
                        }
                    }
                    mCursor.close();
                }

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            mProgressDialog.dismiss();
            if (mImageFolders != null && mImageFolders.size() > 0) {
                mPhotoDirAdapter = new PhotoDirAdapter(mImageFolders, mContext);
                mRecyclerView.setAdapter(mPhotoDirAdapter);
            } else {
                Toast.makeText(mContext, "没有扫描到图片", Toast.LENGTH_SHORT).show();
            }

        }
    };


}
