package com.moziy.hollerbacky.connection;

public class RequestCallbacks {

	public interface OnS3UploadListener {
		
		public void onS3Upload(boolean success);
		
		public void onS3Url(String url, boolean success);
	}
}
