package com.moziy.hollerback.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;

import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.helper.ActiveRecordHelper;
import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerbacky.connection.HBRequestManager;

/**
 * Abstract usage of database, memory store, api calls
 * 
 * @author jianchen
 * 
 */
public class DataModelManager {

	private static HashMap<String, Object> mObjectHash;
	private static TempMemoryStore mTempMemoryStore;

	public DataModelManager() {
		mObjectHash = new HashMap<String, Object>();
	}

	/**
	 * Assumes that receiver is attached to fragment/activity with correct
	 * intentfilter
	 * 
	 * @param populated
	 * @param conversationId
	 */
	public void getVideos(boolean populated, String conversationId) {
		if (!populated) {

			// return in memory or database store solution
			GetVideoAsyncTask task = new GetVideoAsyncTask();
			task.execute(conversationId);
		}
		HBRequestManager.getConversationVideos(conversationId);
	}

	public void getConversations(boolean populated) {
		if (!populated) {

			// return in memory or database store solution
			GetConversationsAsyncTask task = new GetConversationsAsyncTask();
			task.execute();
		}
		HBRequestManager.getConversations();
	}

	private class GetConversationsAsyncTask extends
			AsyncTask<Void, Void, ArrayList<ConversationModel>> {

		@Override
		protected ArrayList<ConversationModel> doInBackground(Void... params) {

			ArrayList<ConversationModel> conversationModel = (ArrayList<ConversationModel>) ActiveRecordHelper
					.getAllConversations();

			return conversationModel;
		}

		@Override
		protected void onPostExecute(ArrayList<ConversationModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			String hash = HashUtil.getConvHash();

			mObjectHash.put(hash, result);

			Intent intent = new Intent(IABIntent.INTENT_GET_CONVERSATIONS);
			intent.putExtra(IABIntent.PARAM_INTENT_DATA, hash);
			IABroadcastManager.sendLocalBroadcast(intent);

		}

	}

	private class GetVideoAsyncTask extends
			AsyncTask<String, Void, HashMap<String, ArrayList<VideoModel>>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(
				HashMap<String, ArrayList<VideoModel>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Iterator it = result.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				System.out.println(pairs.getKey() + " = " + pairs.getValue());
				Intent intent = new Intent(
						IABIntent.INTENT_GET_CONVERSATION_VIDEOS);

				String hash = HashUtil.generateHashFor(
						IABIntent.ASYNC_REQ_VIDEOS, (String) pairs.getKey());

				mObjectHash.put(hash, pairs.getValue());

				intent.putExtra(IABIntent.PARAM_INTENT_DATA, hash);
				IABroadcastManager.sendLocalBroadcast(intent);
				it.remove(); // avoids a ConcurrentModificationException
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected HashMap<String, ArrayList<VideoModel>> doInBackground(
				String... params) {

			if (params.length != 1) {
				return null;
			}

			HashMap<String, ArrayList<VideoModel>> h = new HashMap<String, ArrayList<VideoModel>>();

			ArrayList<VideoModel> videos = (ArrayList<VideoModel>) ActiveRecordHelper
					.getVideosForConversation(params[0]);

			if (videos != null) {
				Collections.reverse(videos);
				h.put(params[0], videos);
			}

			return h;

		}
	}

	public Object getObjectForToken(String token) {
		if (mObjectHash.containsKey(token)) {
			return mObjectHash.get(token);
		}
		return null;
	}

	public void putIntoHash(String key, Object value) {
		mObjectHash.put(key, value);
	}

}
