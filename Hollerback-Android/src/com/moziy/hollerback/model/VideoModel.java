package com.moziy.hollerback.model;

public class VideoModel extends BaseModel {

	private String fileName;
	private boolean isRead;
	private int videoId;
	private String fileUrl;
	private String thumbUrl;

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int id) {
		videoId = id;
	}

	public static String getURLPath() {
		return null;
	}
}
