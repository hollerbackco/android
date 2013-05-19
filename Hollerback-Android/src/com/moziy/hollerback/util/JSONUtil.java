package com.moziy.hollerback.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.SortedArray;
import com.moziy.hollerback.model.UserModel;
import com.moziy.hollerback.model.VideoModel;

public class JSONUtil {

	public static void processSignIn(JSONObject object) {
		try {
			LogUtil.i("HB", object.toString());

			PreferenceManagerUtil.setPreferenceValue(
					HollerbackPreferences.ACCESS_TOKEN,
					object.getString("access_token"));

			JSONObject user = object.getJSONObject("user");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(HollerbackApplication.getInstance(), "DONE",
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(IABIntent.INTENT_SESSION_REQUEST);
		intent.putExtra(IABIntent.PARAM_AUTHENTICATED, IABIntent.VALUE_TRUE);
		IABroadcastManager.sendLocalBroadcast(intent);

	}

	public static void processVideoPost(JSONObject object) {
		try {
			LogUtil.i(object.toString());
			JSONObject videoObject = object.getJSONObject("data");

			VideoModel video = new VideoModel();
			video.setFileName(videoObject.getString("filename"));
			video.setId(videoObject.getInt("id"));
			video.setRead(videoObject.getBoolean("isRead"));

			TempMemoryStore.conversations
					.get(TempMemoryStore.getIndexForConversationId(videoObject
							.getInt("conversation_id"))).getVideos()
					.add(0, video);

			// videos.add(video);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(HollerbackApplication.getInstance(), "DONE",
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(IABIntent.INTENT_UPLOAD_VIDEO);
		intent.putExtra(IABIntent.PARAM_AUTHENTICATED, IABIntent.VALUE_TRUE);
		IABroadcastManager.sendLocalBroadcast(intent);

	}

	public static void processGetConversations(JSONObject object) {
		ArrayList<ConversationModel> conversations = new ArrayList<ConversationModel>();
		try {

			JSONObject dataObject = object.getJSONObject("data");
			JSONArray conversationArray = dataObject
					.getJSONArray("conversations");
			if (conversationArray != null && conversationArray.length() > 0) {

				for (int i = 0; i < conversationArray.length(); i++) {

					LogUtil.i("Processing JSON " + i);
					JSONObject conversation = (JSONObject) conversationArray
							.get(i);
					JSONArray videosArray = conversation.getJSONArray("videos");
					ConversationModel model = new ConversationModel();
					model.setConversation_id(conversation.getInt("id"));
					model.setConversation_name(conversation.getString("name"));
					model.setConversation_unread_count(conversation
							.getInt("unread_count"));
					conversations.add(model);

					ArrayList<VideoModel> videos = new ArrayList<VideoModel>();
					for (int j = 0; j < videosArray.length(); j++) {

						JSONObject videoItem = (JSONObject) videosArray.get(j);

						VideoModel video = new VideoModel();
						video.setFileName(videoItem.getString("filename"));
						video.setId(videoItem.getInt("id"));
						video.setRead(videoItem.getBoolean("isRead"));
						videos.add(video);
					}

					model.setVideos(videos);
					LogUtil.i("Video Size " + videos.size());

				}

			}
			TempMemoryStore.conversations = conversations;
			LogUtil.i("Model Size " + conversations.size());

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			IABroadcastManager.sendLocalBroadcast(intent);
		} catch (Exception e) {
			LogUtil.e(e.getMessage());
		}
	}

	public static void processGetContacts(JSONObject json) {
		try {
			ArrayList<UserModel> users = new ArrayList<UserModel>();
			JSONArray dataObject = json.getJSONArray("data");
			for (int i = 0; i < dataObject.length(); i++) {
				JSONObject userObject = dataObject.getJSONObject(i);
				UserModel user = new UserModel();
				user.name = userObject.getString("name");
				user.mPhone = userObject.getString("phone_normalized");
				users.add(user);
				if (TempMemoryStore.users.mUserModelHash
						.containsKey(user.mPhone)) {
					TempMemoryStore.users.mUserModelHash.get(user.mPhone).isHollerbackUser = true;
				}
			}

			ArrayList<UserModel> valuesList = new ArrayList<UserModel>(
					TempMemoryStore.users.mUserModelHash.values());

			SortedArray array = CollectionOpUtils.sortContacts(valuesList);

			TempMemoryStore.users = array;

			// for (UserModel user : TempMemoryStore.users) {
			// LogUtil.i(user.mDisplayName + " hb: "
			// + Boolean.toString(user.isHollerbackUser));
			// }

			// TempMemoryStore.users = users;
			Intent intent = new Intent(IABIntent.INTENT_GET_CONTACTS);
			IABroadcastManager.sendLocalBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processPostConversations(JSONObject json) {

		ConversationModel conversationModel = new ConversationModel();

		try {

			JSONObject conversation = json.getJSONObject("data");
			JSONArray videosArray = conversation.getJSONArray("videos");
			ConversationModel model = new ConversationModel();
			model.setConversation_id(conversation.getInt("id"));
			model.setConversation_name(conversation.getString("name"));
			model.setConversation_unread_count(conversation
					.getInt("unread_count"));

			ArrayList<VideoModel> videos = new ArrayList<VideoModel>();
			for (int j = 0; j < videosArray.length(); j++) {

				JSONObject videoItem = (JSONObject) videosArray.get(j);

				VideoModel video = new VideoModel();
				video.setFileName(videoItem.getString("filename"));
				video.setId(videoItem.getInt("id"));
				video.setRead(videoItem.getBoolean("isRead"));
				videos.add(video);
			}

			model.setVideos(videos);
			LogUtil.i("Video Size " + videos.size());

			TempMemoryStore.conversations.add(conversationModel);

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_ID, model.getConversation_id());
			IABroadcastManager.sendLocalBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}