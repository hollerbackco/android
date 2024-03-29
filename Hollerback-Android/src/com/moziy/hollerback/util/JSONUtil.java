package com.moziy.hollerback.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
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

		Intent intent = new Intent(IABIntent.INTENT_SESSION_REQUEST);
		intent.putExtra(IABIntent.PARAM_AUTHENTICATED, IABIntent.VALUE_TRUE);
		IABroadcastManager.sendLocalBroadcast(intent);

	}

	public static void processSignUp(JSONObject object) {
		try {
			LogUtil.i(object.toString());

			JSONObject user = object.getJSONObject("user");

			String accessToken = user.getString("access_token");
			if (object.has("access_token")) {
				accessToken = user.getString("access_token");
			} else if (user.has("access_token")) {
				accessToken = user.getString("access_token");
			}

			PreferenceManagerUtil.setPreferenceValue(
					HollerbackPreferences.ACCESS_TOKEN,
					user.getString("access_token"));
			Intent intent = new Intent(IABIntent.INTENT_REGISTER_REQUEST);
			if (user != null) {
				intent.putExtra(IABIntent.PARAM_AUTHENTICATED,
						IABIntent.VALUE_TRUE);
			}
			IABroadcastManager.sendLocalBroadcast(intent);

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void processVideoPost(JSONObject object, String customMessage) {

		String conversationId = null;

		try {
			LogUtil.i(object.toString());
			JSONObject videoObject = object.getJSONObject("data");

			VideoModel video = new VideoModel();
			video.setFileName(videoObject.getString("filename"));
			video.setVideoId(videoObject.getInt("id"));
			video.setRead(videoObject.getBoolean("isRead"));

			conversationId = videoObject.getString("conversation_id");

			String hash = HashUtil.generateHashFor(IABIntent.ASYNC_REQ_VIDEOS,
					videoObject.getString("conversation_id"));

			ArrayList<VideoModel> videos = (ArrayList<VideoModel>) QU.getDM()
					.getObjectForToken(hash);

			if (videos == null) {
				videos = new ArrayList<VideoModel>();
			}

			videos.add(video);

			QU.getDM().putIntoHash(hash, videos);

			// videos.add(video);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(IABIntent.INTENT_UPLOAD_VIDEO);
		intent.putExtra(IABIntent.PARAM_AUTHENTICATED, IABIntent.VALUE_TRUE);
		if (customMessage != null && !customMessage.isEmpty()) {
			if (customMessage.equals(IABIntent.MSG_CONVERSATION_ID)) {
				intent.putExtra(IABIntent.PARAM_INTENT_MSG, conversationId);
			} else {
				intent.putExtra(IABIntent.PARAM_INTENT_MSG, customMessage);
			}
		}
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

			String hash = HashUtil.getConvHash();

			QU.getDM().putIntoHash(hash, conversations);

			LogUtil.i("Model Size " + conversations.size());

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_INTENT_DATA, hash);
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

			ActiveAndroid.beginTransaction();

			for (int i = 0; i < dataObject.length(); i++) {
				JSONObject userObject = dataObject.getJSONObject(i);
				UserModel user = new UserModel();
				user.name = userObject.getString("name");
				user.phone = userObject.getString("phone_normalized");
				user.isHollerbackUser = true;

				List<Model> userLocal = (List<Model>) new Select()
						.from(UserModel.class)
						.where(ActiveRecordFields.C_USER_PHONE + " = ?",
								user.phone).execute();

				if (userLocal == null || userLocal.size() < 1) {
					user.save();
				} else {
					((UserModel) userLocal.get(0)).isHollerbackUser = true;
					((UserModel) userLocal.get(0)).save();
				}

				users.add(user);
				if (TempMemoryStore.users.mUserModelHash
						.containsKey(user.phone)) {
					TempMemoryStore.users.mUserModelHash.get(user.phone).isHollerbackUser = true;
				}
			}

			ActiveAndroid.setTransactionSuccessful();
			ActiveAndroid.endTransaction();

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

		try {

			JSONObject conversation = json.getJSONObject("data");
			// JSONArray videosArray = conversation.getJSONArray("videos");
			ConversationModel model = new ConversationModel();
			model.setConversation_id(conversation.getInt("id"));
			model.setConversation_name(conversation.getString("name"));
			model.setConversation_unread_count(conversation
					.getInt("unread_count"));

			int idToReplace = -1;

			ArrayList<ConversationModel> conversations = (ArrayList<ConversationModel>) QU
					.getDM().getObjectForToken(HashUtil.getConvHash());

			if (conversations != null) {
				int i = -1;
				for (ConversationModel convo : conversations) {
					i++;
					if (convo.getConversation_Id() == model
							.getConversation_Id()) {
						idToReplace = i;

					}
				}

				if (idToReplace != -1) {
					conversations.remove(idToReplace);
					conversations.add(0, model);
					LogUtil.i("Removing conversation: " + idToReplace);
				} else {
					conversations.add(0, model);
				}
			} else {
				conversations = new ArrayList<ConversationModel>();
				conversations.add(0, model);
			}

			QU.getDM().putIntoHash(HashUtil.getConvHash(), conversations);

			Intent intent = new Intent(IABIntent.INTENT_POST_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_ID,
					Integer.toString(model.getConversation_Id()));
			LogUtil.i("Sending Broadcast: " + model.getConversation_Id());
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

					VideoModel video = JsonModelUtil
							.createVideo((JSONObject) videosArray.get(j));
					conversationId = video.getConversationId();
					video.save();
					videos.add(video);
				}

				ActiveAndroid.setTransactionSuccessful();

				Collections.reverse(videos);

				String hash = HashUtil.generateHashFor(
						IABIntent.ASYNC_REQ_VIDEOS, conversationId);

				QU.getDM().putIntoHash(hash, videos);

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

	public static void processVideoRead(JSONObject jsonObject) {
		try {
			JSONObject videoJSONObject = jsonObject.getJSONObject("data");

			VideoModel video = JsonModelUtil.createVideo(videoJSONObject);
			QU.updateConversationVideo(video);

			Intent intent = new Intent(IABIntent.INTENT_POST_READ_VIDEO);
			IABroadcastManager.sendLocalBroadcast(intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}