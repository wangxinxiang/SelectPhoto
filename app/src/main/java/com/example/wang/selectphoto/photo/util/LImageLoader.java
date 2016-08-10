package com.example.wang.selectphoto.photo.util;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.example.wang.selectphoto.R;

public class LImageLoader
{
	private Context _context = null;
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 线程池的线程数量，默认为1
	 */
	private int mThreadCount = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * 轮询的线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHander;

	/**
	 * 运行在UI线程的handler，用于给ImageView设置图片
	 */
	private Handler mHandler;

	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
	 */
	private volatile Semaphore mSemaphore = new Semaphore(1);

	/**
	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
	 */
	private volatile Semaphore mPoolSemaphore;

	private static LImageLoader mInstance;

	/**
	 * 队列的调度方式
	 * 
	 * @author zhy
	 * 
	 */
	public enum Type
	{
		FIFO, LIFO
	}


	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static LImageLoader getInstance(Context c)
	{

		if (mInstance == null)
		{
			synchronized (LImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new LImageLoader(c,1, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	private LImageLoader(Context c, int threadCount, Type type)
	{
		this._context = c;
		init(threadCount, type);
	}

	private void init(int threadCount, Type type)
	{
		// loop thread
		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					// 请求一个信号量
					mSemaphore.acquire();
				} catch (InterruptedException e)
				{
				}
				Looper.prepare();

				mPoolThreadHander = new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						mThreadPool.execute(getTask());
						try
						{
							mPoolSemaphore.acquire();
						} catch (InterruptedException e)
						{
						}
					}
				};
				// 释放一个信号量
				mSemaphore.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			};
			
			/*@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) 
			{
				try
				{
					if(null!=oldValue && !oldValue.isRecycled())
					{	
						oldValue.recycle();	
						oldValue = null;
						System.gc();
					}
				}
				catch(Exception ex){}
				finally
				{
					
				}
				super.entryRemoved(evicted, key, oldValue, newValue);
			}*/
		};

		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemaphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;

	}

	

	public void OnDestroy()
	{
		//Bitmap bm = null;
		try
		{
			if(null!=this.mLruCache && 0<this.mLruCache.size())
			{
				this.mLruCache.evictAll();
			}
		}
		catch(Exception ex){}
		finally
		{
			//bm = null;
		}
	}
	/**
	 * 加载图片
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView)
	{
		// set tag
		imageView.setTag(path);
		// UI线程
		if (mHandler == null)
		{
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path))
					{
						if("pictures_camera".equals(path))
						{
							imageView.setBackgroundColor(Color.parseColor("#333333"));
							imageView.setScaleType(ScaleType.CENTER_INSIDE);
						}
						else
						{
							imageView.setScaleType(ScaleType.FIT_XY);
							imageView.setBackgroundColor(0);
						}
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null)
		{
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			mHandler.sendMessage(message);
		} 
		else
		{
			addTask(new Runnable()
			{
				@Override
				public void run()
				{
					ImageSize imageSize = null;
					Bitmap bm = null;
					Message message = null;
					ImgBeanHolder holder = null;
					int reqWidth = 0;
					int reqHeight = 0;
					try
					{
						imageSize = getImageViewWidth(imageView);
						if(null!=imageSize)
						{
							reqWidth = imageSize.width;
							reqHeight = imageSize.height;
						}
						if("pictures_camera".equals(path))
						{
							if(null!=_context)
							{
								bm = BitmapFactory.decodeResource(_context.getResources(), R.drawable.longrise_pictures_camera);
							}
						}
						else
						{
							bm = decodeSampledBitmapFromResource(path, reqWidth,	reqHeight);
						}
						if(null!=bm)
						{
							addBitmapToLruCache(path, bm);
							
							holder = new ImgBeanHolder();
							if(null!=holder)
							{
								holder.bitmap = getBitmapFromLruCache(path);
								holder.imageView = imageView;
								holder.path = path;
								message = Message.obtain();
								if(null!=message)
								{
									message.obj = holder;
									mHandler.sendMessage(message);
								}
							}
						} else {
							Log.d("......", "图片获取错误 ：" + mTasks.size());
						}
					}
					catch(Exception ex){
						Log.e("mPoolSemaphore ---->", ex.getMessage());
					}
					finally
					{
						mPoolSemaphore.release();
						imageSize = null;
						bm = null;
						holder = null;
						message = null;
					}
					
				}
			});
		}

	}
	
	/**
	 * 添加一个任务
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable)
	{
		try
		{
			// 请求信号量，防止mPoolThreadHander为null
			if (mPoolThreadHander == null)
				mSemaphore.acquire();
		} catch (InterruptedException e)
		{
		}
		mTasks.add(runnable);
		mPoolThreadHander.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一个任务
	 * 
	 * @return
	 */
	private synchronized Runnable getTask()
	{
		if (mType == Type.FIFO)
		{
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO)
		{
			return mTasks.removeLast();
		}
		return null;
	}
	
	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static LImageLoader getInstance(Context c, int threadCount, Type type)
	{

		if (mInstance == null)
		{
			synchronized (LImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new LImageLoader(c, threadCount, type);
				}
			}
		}
		return mInstance;
	}


	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		return mLruCache.get(key);
	}

	/**
	 * 往LruCache中添加一张图片
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromLruCache(key) == null)
		{
			if (bitmap != null)
				mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		int width = 0;
		int height = 0;
		int widthRatio = 0;
		int heightRatio = 0;
		try
		{
			if(null!=options)
			{
				width = options.outWidth;
				height = options.outHeight;
				if (width > reqWidth && height > reqHeight)
				{
					widthRatio = Math.round((float) width / (float) reqWidth);
					heightRatio = Math.round((float) width / (float) reqWidth);
					return Math.max(widthRatio, heightRatio);
				}
			}
		}
		catch(Exception ex){}
		finally
		{
			
		}
		return 1;		
	}

	

	
	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName,	int reqWidth, int reqHeight)
	{
		BitmapFactory.Options options = null;
		FileInputStream fs = null;
		Bitmap bitmap = null;
		try
		{
			if(null!=pathName && !"".equals(pathName))
			{
				options = new BitmapFactory.Options();
				if(null!=options)
				{
					
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(pathName, options);
					options.inSampleSize = calculateInSampleSize(options, reqWidth,	reqHeight);
					
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					options.inPurgeable = true;//设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收					
					options.inInputShareable = true;  
					//options.inTempStorage = new byte[16 * 1024];
					options.inJustDecodeBounds = false;
					options.inScaled = true;
					fs = new FileInputStream(pathName);
					if(null!=fs)
					{
						//bitmap = BitmapFactory.decodeFile(pathName, options);
						bitmap = BitmapFactory.decodeStream(fs, null, options);
						fs.close();
					}
					
				}
			}
			return bitmap;
		}
		catch(Exception ex){}
		finally
		{
			fs = null;
			options = null;
		}
		return null;
		
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	private class ImageSize
	{
		int width;
		int height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;

				Log.e("TAG", value + "");
			}
		} catch (Exception e)
		{
		}
		return value;
	}

}
