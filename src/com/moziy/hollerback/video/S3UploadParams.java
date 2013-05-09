package com.moziy.hollerback.video;

import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerbacky.connection.RequestCallbacks.OnS3UploadListener;

import android.content.Context;

public class S3UploadParams {

	private Context mContext;
	private OnS3UploadListener mOnS3UploadListener;
	private String mFilePath;
	private String mFileName;
	private String mFileType;
	public VideoModel mVideo;

	public String getFileType() {
		return mFileType;
	}

	public void setFileType(String mFileType) {
		this.mFileType = mFileType;
	}

	public String VID_MP4 = "mp4";
	public String IMG_PNG = "-thumb.png";

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

	public String getVideoName() {
		return mFileName;

	}

	public String getJPEGName() {
		return mFileName.split("\\.")[0] + IMG_PNG;
	}
}