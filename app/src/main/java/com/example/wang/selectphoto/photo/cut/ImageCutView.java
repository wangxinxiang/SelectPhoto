package com.example.wang.selectphoto.photo.cut;

import android.content.Context;
import android.graphics.*;
import android.graphics.PorterDuff.Mode;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.wang.selectphoto.photo.util.BasicTool;

public class ImageCutView extends View {
	private float _density = 1;
	private Paint _paint = null;
	private PorterDuffXfermode _mode = null;
	private Bitmap _bitmap = null;
	private Bitmap _mbitmap = null;
	private Canvas _canvas = null;

	private float _px = 0;
	private float _py = 0;
	private float _x = 0;
	private float _y = 0;
	private float _left = 0;
	private float _top = 0;

	private float _radius = 0;
	private float _cx = 0;
	private float _cy = 0;
	private int _color = 0;
	private int _alpha = 100;

	private String _imagepath = null;

	private int _touchmode = 0;
	private float _oldDistance = 0;
	private Matrix _savedMatrix = null;
	private Matrix _matrix = null;
	private float _matrixValue[] = null;

	// 角度
	/*
	 * private final float _MIN_DEGREE = 0f; private final float _MAX_DEGREE =
	 * 180f; private float _curDegree; private double _changeDegree = 0; private
	 * float _saveX; // 当前保存的x private float _saveY; // 当前保存的y
	 */
	private void init() {
		try {
			this._density = BasicTool.dip2px(getContext(), 1);
			this._mode = new PorterDuffXfermode(Mode.DST_OUT);
			this._paint = new Paint();
			if (null != this._paint) {
				this._paint.setAntiAlias(true);
			}
			this._radius = 90 * this._density;
			this._color = Color.GRAY;

			this._savedMatrix = new Matrix();
			this._matrix = new Matrix();
			this._matrixValue = new float[9];

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Bitmap getScaleBitmap(String filePath) {
		BitmapFactory.Options opt = null;
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		try {
			opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opt);
			opt.inSampleSize = 1;
			if (opt.outWidth > opt.outHeight) {
                if (opt.outWidth > wm.getDefaultDisplay().getWidth()) {
                    opt.inSampleSize = opt.outWidth
                            / wm.getDefaultDisplay().getWidth();
                }
            } else {
                if (opt.outHeight > wm.getDefaultDisplay().getHeight()) {
                    opt.inSampleSize = opt.outHeight
                            / wm.getDefaultDisplay().getHeight();
                }
            }
			opt.inJustDecodeBounds = false;

			return BitmapFactory.decodeFile(filePath, opt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void initMeasure() {
		try {
			this._cx = this.getMeasuredWidth() / 2;
			this._cy = this.getMeasuredHeight() / 2;

			this._mbitmap = Bitmap.createBitmap(this.getMeasuredWidth(),
					this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			this._canvas = new Canvas(this._mbitmap);
			this._canvas.drawColor(this._color);

			this._bitmap = this.getScaleBitmap(this._imagepath);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	/**
//	 * 获取两点到第三点的夹角
//	 *
//	 * @param x
//	 * @param y
//	 * @param x1
//	 * @param y1
//	 * @param x2
//	 * @param y2
//	 * @return
//	 */
	/*
	 * private double getActionDegrees(float x, float y, float x1, float y1,
	 * float x2, float y2) {
	 * 
	 * double a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	 * double b = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)); double c
	 * = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)); // 余弦定理 double
	 * cosA = (b * b + c * c - a * a) / (2 * b * c); //
	 * 返回余弦值为指定数字的角度，Math函数为我们提供的方法 double arcA = Math.acos(cosA); double degree
	 * = arcA * 180 / Math.PI;
	 * 
	 * // 接下来我们要讨论正负值的关系了，也就是求出是顺时针还是逆时针。 // 第1、2象限 if (y1 < y && y2 < y) { if
	 * (x1 < x && x2 > x) {// 由2象限向1象限滑动 return degree; } // 由1象限向2象限滑动 else if
	 * (x1 >= x && x2 <= x) { return -degree; } } // 第3、4象限 if (y1 > y && y2 >
	 * y) { // 由3象限向4象限滑动 if (x1 < x && x2 > x) { return -degree; } //
	 * 由4象限向3象限滑动 else if (x1 > x && x2 < x) { return degree; }
	 * 
	 * } // 第2、3象限 if (x1 < x && x2 < x) { // 由2象限向3象限滑动 if (y1 < y && y2 > y) {
	 * return -degree; } // 由3象限向2象限滑动 else if (y1 > y && y2 < y) { return
	 * degree; } } // 第1、4象限 if (x1 > x && x2 > x) { // 由4向1滑动 if (y1 > y && y2
	 * < y) { return -degree; } // 由1向4滑动 else if (y1 < y && y2 > y) { return
	 * degree; } }
	 * 
	 * // 在特定的象限内 float tanB = (y1 - y) / (x1 - x); float tanC = (y2 - y) / (x2
	 * - x); if ((x1 > x && y1 > y && x2 > x && y2 > y && tanB > tanC)// 第一象限 ||
	 * (x1 > x && y1 < y && x2 > x && y2 < y && tanB > tanC)// 第四象限 || (x1 < x
	 * && y1 < y && x2 < x && y2 < y && tanB > tanC)// 第三象限 || (x1 < x && y1 > y
	 * && x2 < x && y2 > y && tanB > tanC))// 第二象限 return -degree; return
	 * degree; }
	 * 
	 * private void optimize(float tempDegree) {
	 * if(tempDegree>this._MAX_DEGREE-1) { this._curDegree=this._MAX_DEGREE; }
	 * else if(tempDegree<this._MIN_DEGREE+1) {
	 * this._curDegree=this._MIN_DEGREE; } else { this._curDegree = tempDegree;
	 * } }
	 */

	public Bitmap getBitmap() {
		Canvas canvas = null;
		Bitmap resultBmp = null;
		try {
			if (null != this._bitmap) {
				resultBmp = Bitmap.createBitmap((int) (this._radius * 2),
						(int) (this._radius * 2), Bitmap.Config.ARGB_8888);
				if (null != resultBmp) {
					canvas = new Canvas(resultBmp);
					if (null != canvas) {

						this._paint.setAlpha(255);
						canvas.drawRect(0, 0, this._radius * 2,
								this._radius * 2, this._paint);
						this._paint.setXfermode(new PorterDuffXfermode(
								Mode.SRC_IN));

						canvas.translate(0 - (this._cx - this._radius)
								+ this._left, 0 - (this._cy - this._radius)
								+ this._top);
						canvas.drawBitmap(this._bitmap, this._matrix,
								this._paint);
						return resultBmp;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			resultBmp = null;
			canvas = null;
		}
		return null;
	}

	public void setBitmap(Bitmap bitmap) {
		this._bitmap = bitmap;
	}

	public void setBitmap(String filepath) {
		this._imagepath = filepath;
		requestLayout();
		invalidate();
	}

	public void setAlpha(int alpha) {
		this._alpha = alpha;
	}

	public void setColor(int color) {
		this._color = color;
	}

	public ImageCutView(Context context) {
		super(context);
		this.init();
	}

	public void OnDestroy() {
		if (null != this._bitmap) {
			this._bitmap.recycle();
			this._bitmap = null;
		}
		if (null != this._mbitmap) {
			this._mbitmap.recycle();
			this._mbitmap = null;
		}
		this._canvas = null;
		this._paint = null;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.initMeasure();
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float newDistance = 0;
		try {

			if (null != event) {

				if (MotionEvent.ACTION_DOWN == (event.getAction() & event.ACTION_MASK)) {
					this._touchmode = 0;
					this._savedMatrix.set(this._matrix);

					this._x = event.getX();
					this._y = event.getY();
				} else if (MotionEvent.ACTION_MOVE == (event.getAction() & event.ACTION_MASK)) {
					if (0 == this._touchmode) {
						this._left = event.getX() - this._x + this._px;
						this._top = event.getY() - this._y + this._py;
						this.invalidate();
					} else {

						newDistance = (float) Math.sqrt((event.getX(0) - event
								.getX(1))
								* (event.getX(0) - event.getX(1))
								+ (event.getY(0) - event.getY(1))
								* (event.getY(0) - event.getY(1)));
						if (newDistance > 10f) {
							if (this._oldDistance > newDistance
									&& 100 < this._oldDistance - newDistance) {
								if (null != this._matrixValue) {
									this._matrix.getValues(this._matrixValue);
									if (0.2 < this._matrixValue[0]) {
										this._oldDistance = newDistance;
										this._matrix.set(this._savedMatrix);
										this._matrix.postScale((float) 0.9,
												(float) 0.9);
										this._savedMatrix.set(this._matrix);
										this.invalidate();
										return true;
									}
								}

							} else if (this._oldDistance < newDistance
									&& 100 < newDistance - this._oldDistance) {
								if (null != this._matrixValue) {
									this._matrix.getValues(this._matrixValue);
									if (1.8 > this._matrixValue[0]) {
										this._oldDistance = newDistance;
										this._matrix.set(this._savedMatrix);
										this._matrix.postScale((float) 1.1,
												(float) 1.1);
										this._savedMatrix.set(this._matrix);
										this.invalidate();
										return true;
									}
								}
							}

						}

						/*
						 * this._changeDegree = this.getActionDegrees(this._cx,
						 * this._cy, this._saveX, this._saveY, event.getX(),
						 * event.getY()); this._saveX = event.getX();
						 * this._saveY = event.getY(); tempDegree =
						 * (float)(this._curDegree + this._changeDegree);
						 * if(tempDegree >= this._MIN_DEGREE && tempDegree <=
						 * this._MAX_DEGREE) { optimize(tempDegree);
						 * this._matrix.setRotate(this._curDegree, this._cx,
						 * this._cy); this.invalidate(); }
						 */
					}

				} else if (MotionEvent.ACTION_UP == (event.getAction() & event.ACTION_MASK)) {
					this._px = this._left;
					this._py = this._top;
					this._touchmode = 0;
				} else if (MotionEvent.ACTION_POINTER_DOWN == (event
						.getAction() & event.ACTION_MASK)) {
					this._oldDistance = (float) Math
							.sqrt((event.getX(0) - event.getX(1))
									* (event.getX(0) - event.getX(1))
									+ (event.getY(0) - event.getY(1))
									* (event.getY(0) - event.getY(1)));
					if (this._oldDistance > 10f) {
						this._savedMatrix.set(this._matrix);
						this._touchmode = 1;
					}

				} else if (MotionEvent.ACTION_POINTER_UP == (event.getAction() & event.ACTION_MASK)) {
					// this._touchmode = 0;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			event = null;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			if (null != canvas) {
				super.onDraw(canvas);
				if (null != this._bitmap) {
					canvas.save();
					canvas.translate(this._left, this._top);
					canvas.drawBitmap(this._bitmap, this._matrix, null);
					canvas.restore();

					this._paint.setXfermode(this._mode);
					this._paint.setAlpha(255);
					this._canvas.drawRect(this._cx - this._radius, this._cy
							- this._radius, this._cx + this._radius, this._cy
							+ this._radius, this._paint);
					this._paint.setAlpha(this._alpha);
					this._paint.setXfermode(null);
					canvas.drawBitmap(this._mbitmap, 0, 0, this._paint);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			canvas = null;
		}
	}
}
