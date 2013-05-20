package com.moziy.hollerback.model;

import java.util.ArrayList;

public class ConversationModel extends BaseModel {

	private int conversation_id;
	private String conversation_name;
	private int conversation_unread_count;
	private String recentThumbUrl;
	private String recentVideoUrl;

	private ArrayList<VideoModel> mVideos;

	//public ArrayList<VideoModel> getVideos() {
	//	return mVideos;
	//}

	//public void setVideos(ArrayList<VideoModel> mVideos) {
	//	this.mVideos = mVideos;
	//}

	public ConversationModel() {

	}

	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

	public String getConversationName() {
		return conversation_name;
	}

	public void setConversation_name(String conversation_name) {
		this.conversation_name = conversation_name;
	}

	public int getConversationUnreadCount() {
		return conversation_unread_count;
	}

	public void setConversation_unread_count(int conversation_unread_count) {
		this.conversation_unread_count = conversation_unread_count;
	}

}
