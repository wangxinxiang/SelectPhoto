package com.example.wang.selectphoto.photo.title;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;

/**
 * 小于号,大于号的  角号
 * @author lilw
 *
 */
public class AngleView extends View
{
	private Context _context = null;
	private float _density = 1;
	private float _degrees = 0;
	private float _radiuX = 0;
	private float _radiuY = 0;
	private Paint _paint = null;
	private Path _path = null;
	private kind _kind = kind.LF;

	public void set_kind(kind _kind) {
		this._kind = _kind;
	}

	public enum kind {
		LF, RF
	}
	
	private void init()
	{
		try
		{
			this._paint = new Paint();
			this._paint.setAntiAlias(true);
			this._paint.setStyle(Style.STROKE);
			this._paint.setStrokeWidth(1*this._density);
			this._paint.setColor(Color.GRAY);
			this._path = new Path();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void initPaint()
	{
		try
		{
			this._radiuX = this.getMeasuredWidth()/2;
			this._radiuY = this.getMeasuredHeight()/2;
			
			if(null!=this._path)
			{
				this._path.reset();
				if (kind.LF == _kind) {
					initLT();
				} else if (kind.RF == _kind) {
					initRT();
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 小于号
	 */
	private void initLT() {
		this._path.moveTo(this.getMeasuredWidth(), 0);
		this._path.lineTo(0, this.getMeasuredHeight()/2);
		this._path.lineTo(this.getMeasuredWidth(), this.getMeasuredHeight());
	}

	/**
	 * 大于号
	 */
	private void initRT() {
		this._path.moveTo(0, 0);
		this._path.lineTo(this.getMeasuredWidth()/2, this.getMeasuredHeight()/2);
		this._path.lineTo(0, this.getMeasuredHeight());
	}
	
	public void setDegrees(float degrees)
	{
		this._degrees = degrees;
	}
	
	public void setStrokeWidth(float width)
	{
		if(null!=this._paint)
		{
			this._paint.setStrokeWidth(width * this._density);
		}
	}
	
	public void setColor(int color)
	{
		if(null!=this._paint)
		{
			this._paint.setColor(color);
		}
	}
	
	public AngleView(Context context)
	{
		super(context);
		this._context = context;
		this.init();
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.initPaint();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		try
		{
			super.onDraw(canvas);
			
			if(null!=this._paint)
			{
				if(null!=this._path)
				{
					canvas.rotate(this._degrees, this._radiuX, this._radiuY);
					canvas.drawPath(this._path, this._paint);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
