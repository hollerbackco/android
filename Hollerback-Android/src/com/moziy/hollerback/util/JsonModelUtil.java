package com.moziy.hollerback.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.VideoModel;

public class JsonModelUtil {

	public static VideoModel createVideo(JSONObject videoItem) {
		try {
			VideoModel video = new VideoModel();
			video.setFileName(videoItem.getString("filename"));
			video.setVideoId(videoItem.getInt("id"));
			video.setConversationId(videoItem.getString("conversation_id"));
			video.setRead(videoItem.getBoolean("isRead"));
			video.setFileUrl(videoItem.getString("url"));
			video.setThumbUrl(videoItem.getString("thumb_url"));
			return video;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static ConversationModel createConverstion(JSONObject conversation) {
		try {
			ConversationModel model = new ConversationModel();
			model.setConversation_id(conversation.getInt("id"));
			model.setConversation_name(conversation.getString("name"));
			model.setConversation_unread_count(conversation
					.getInt("unread_count"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
