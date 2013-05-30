package com.moziy.hollerback.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.moziy.hollerback.database.ActiveRecordFields;

@Table(name = ActiveRecordFields.T_VIDEOS)
public class VideoModel extends BaseModel {

	@Column(name = ActiveRecordFields.C_VID_FILENAME)
	private String filename;

	@Column(name = ActiveRecordFields.C_VID_ISREAD)
	private boolean isRead;

	@Column(name = ActiveRecordFields.C_VID_ID)
	private int videoId;

	@Column(name = ActiveRecordFields.C_VID_CONV_ID)
	private String mConvId;

	@Column(name = ActiveRecordFields.C_VID_FILEURL)
	private String fileUrl;

	@Column(name = ActiveRecordFields.C_VID_THUMBURL)
	private String thumbUrl;

	private boolean uploaded;

	public String getConversationId() {
		return mConvId;
	}

	public void setConversationId(String mConvId) {
		this.mConvId = mConvId;
	}

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
		return filename;
	}

	public void setFileName(String fileName) {
		this.filename = fileName;
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

	@Override
	public boolean equals(Object obj) {
		VideoModel video = (VideoModel) obj;
		if (videoId == video.videoId) {
			return true;
		}
		return false;
	}
}
