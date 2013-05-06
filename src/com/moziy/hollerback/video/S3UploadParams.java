package com.moziy.hollerback.video;

import com.moziy.hollerbacky.connection.RequestCallbacks.OnS3UploadListener;

import android.content.Context;

public class S3UploadParams {

	private Context mContext;
	private OnS3UploadListener mOnS3UploadListener;
	private String mFilePath;
	private String mFileName;
	public Context getmContext() {
		return mContext;
	}
	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
	public OnS3UploadListener getOnS3UploadListener() {
		return mOnS3UploadListener;
	}
	public void setOnS3UploadListener(OnS3UploadListener mOnS3UploadListener) {
		this.mOnS3UploadListener = mOnS3UploadListener;
	}
	public String getFilePath() {
		return mFilePath;
	}
	public void setFilePath(String mFilePath) {
		this.mFilePath = mFilePath;
	}
	public String getFileName() {
		return mFileName;
	}
	public void setFileName(String mFileName) {
		this.mFileName = mFileName;
	}
}