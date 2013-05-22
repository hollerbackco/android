package com.moziy.hollerback.util;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.database.ActiveRecordFields;
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
			video.setVideoId(videoObject.getInt("id"));
			video.setRead(videoObject.getBoolean("isRead"));

			TempMemoryStore.videos
					.get(videoObject.getString("conversation_id")).add(video);

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
			ActiveAndroid.beginTransaction();
			new Delete().from(ConversationModel.class).execute();

			JSONObject dataObject = object.getJSONObject("data");
			JSONArray conversationArray = dataObject
					.getJSONArray("conversations");
			if (conversationArray != null && conversationArray.length() > 0) {

				for (int i = 0; i < conversationArray.length(); i++) {

					LogUtil.i("Processing JSON " + i);
					JSONObject conversation = (JSONObject) conversationArray
							.get(i);

					ConversationModel model = new ConversationModel();
					model.setConversation_id(conversation.getInt("id"));
					model.setConversation_name(conversation.getString("name"));
					model.setConversation_unread_count(conversation
							.getInt("unread_count"));
					model.save();
					conversations.add(model);

				}
				ActiveAndroid.setTransactionSuccessful();

			}

			TempMemoryStore.conversations = conversations;
			LogUtil.i("Model Size " + conversations.size());

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_INTENT_DATA, HashUtil
					.generateHashFor(IABIntent.INTENT_GET_CONVERSATIONS,
							IABIntent.VALUE_CONV_HASH));
			IABroadcastManager.sendLocalBroadcast(intent);

		} catch (Exception e) {
			LogUtil.e(e.getMessage());
		} finally {
			ActiveAndroid.endTransaction();
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

			// ArrayList<VideoModel> videos = new ArrayList<VideoModel>();
			// for (int j = 0; j < videosArray.length(); j++) {
			//
			// JSONObject videoItem = (JSONObject) videosArray.get(j);
			//
			// VideoModel video = new VideoModel();
			// video.setFileName(videoItem.getString("filename"));
			// video.setId(videoItem.getInt("id"));
			// video.setRead(videoItem.getBoolean("isRead"));
			// videos.add(video);
			// }
			//
			// model.setVideos(videos);
			// LogUtil.i("Video Size " + videos.size());

			TempMemoryStore.conversations.add(conversationModel);

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_ID, model.getConversation_Id());
			IABroadcastManager.sendLocalBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void processConversationVideos(String convoId, JSONObject json) {
		try {

			ActiveAndroid.beginTransaction();

			String conversationId = "";
			JSONArray videosArray = json.getJSONArray("data");
			ArrayList<VideoModel> videos = new ArrayList<VideoModel>();

			if (videosArray != null) {

				try {
					new Delete()
							.from(VideoModel.class)
							.where(ActiveRecordFields.C_VID_CONV_ID + " = ?",
									convoId).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int j = 0; j < videosArray.length(); j++) {

					JSONObject videoItem = (JSONObject) videosArray.get(j);
					conversationId = videoItem.getString("conversation_id");
					VideoModel video = new VideoModel();
					video.setFileName(videoItem.getString("filename"));
					video.setVideoId(videoItem.getInt("id"));
					video.setConversationId(videoItem
							.getString("conversation_id"));

					video.setRead(videoItem.getBoolean("isRead"));
					video.save();
					videos.add(video);
				}

				ActiveAndroid.setTransactionSuccessful();

				Collections.reverse(videos);

				String hash = HashUtil.generateHashFor(
						IABIntent.ASYNC_REQ_VIDEOS, conversationId);

				HollerbackApplication.getInstance().getDM()
						.putIntoHash(hash, videos);

				TempMemoryStore.videos.put(conversationId, videos);
				Intent intent = new Intent(
						IABIntent.INTENT_GET_CONVERSATION_VIDEOS);
				intent.putExtra(IABIntent.PARAM_INTENT_DATA, hash);
				intent.putExtra(IABIntent.PARAM_ID, conversationId);

				IABroadcastManager.sendLocalBroadcast(intent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ActiveAndroid.endTransaction();
		}
	}
}