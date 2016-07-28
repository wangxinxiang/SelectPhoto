package com.example.wang.selectphoto.photo.album;

public class ImageLoaderData
{
	private String mFilepath = null;
	private boolean mIsCheck = false;
	
	public void setFilePath(String filepath)
	{
		this.mFilepath = filepath;
	}
	public String getFilePath()
	{
		return this.mFilepath;
	}
	
	public void setChecked(boolean ischeck)
	{
		this.mIsCheck = ischeck;
	}
	public boolean isChecked()
	{
		return this.mIsCheck;
	}
	public ImageLoaderData()
	{
		
	}
	
	public ImageLoaderData(String filepath, boolean ischecked)
	{
		this.mFilepath = filepath;
		this.mIsCheck = ischecked;
	}
}
