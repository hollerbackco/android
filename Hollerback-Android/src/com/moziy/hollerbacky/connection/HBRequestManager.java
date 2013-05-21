package com.moziy.hollerbacky.connection;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.UserModel;
import com.moziy.hollerback.util.HBRequestUtil;
import com.moziy.hollerback.util.HollerbackAPI;
import com.moziy.hollerback.util.HollerbackAppState;
import com.moziy.hollerback.util.JSONUtil;

/**
 * Manage all Requests here so other classes can make request agnostically
 * 
 * @author jianchen
 * 
 */
public class HBRequestManager {

	static boolean isS3Upload;

	// public static boolean postVideo(JSONObject jsonObject, String filePath,
	// String fileName) {
	// if (isS3Upload) {
	//
	// }
	// return true;
	// }

	public static void postVideo(String conversation_id, String filename) {
		RequestParams params = new RequestParams();

		LogUtil.i("PostVideo", conversation_id);

		params.put(HollerbackAPI.PARAM_ACCESS_TOKEN,
				HollerbackAppState.getValidToken());
		params.put(HollerbackAPI.PARAM_FILENAME, filename);

		HollerbackAsyncClient.getInstance().post(
				String.format(HollerbackAPI.API_VIDEO_POST_FORMAT,
						conversation_id), params,
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onSuccess(int arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1);
						JSONUtil.processVideoPost(arg1);
					}

					@Override
					protected Object parseResponse(String arg0)
							throws JSONException {
						// TODO Auto-generated method stub
						LogUtil.i(arg0);
						return super.parseResponse(arg0);
					}
				});

	}

	public static void postLogin(String email, String password) {

		RequestParams params = null;

		if (!email.isEmpty() && !password.isEmpty()) {
			params = new RequestParams();
			params.put(HollerbackAPI.PARAM_EMAIL, email);
			params.put(HollerbackAPI.PARAM_PASSWORD, password);
		}

		HollerbackAsyncClient.getInstance().post(HollerbackAPI.API_SESSION,
				params, new JsonHttpResponseHandler() {

					@Override
					protected Object parseResponse(String arg0)
							throws JSONException {
						LogUtil.i(arg0);
						return super.parseResponse(arg0);

					}

					@Override
					public void onFailure(Throwable arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						LogUtil.i("LOGIN FAILURE");
					}

					@Override
					public void onSuccess(int arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1);
						JSONUtil.processSignIn(arg1);
					}

				});

	}

	public static void getConversations() {
		if (HollerbackAppState.isValidSession()) {
			RequestParams params = new RequestParams();
			params.put(HollerbackAPI.PARAM_ACCESS_TOKEN,
					HollerbackAppState.getValidToken());

			HollerbackAsyncClient.getInstance().get(
					HollerbackAPI.API_CONVERSATION, params,
					new JsonHttpResponseHandler() {

						@Override
						protected Object parseResponse(String arg0)
								throws JSONException {
							LogUtil.i(arg0);
							return super.parseResponse(arg0);

						}

						@Override
						public void onFailure(Throwable arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							LogUtil.e(HollerbackAPI.API_CONVERSATION
									+ "FAILURE");
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1);
							LogUtil.i("ON SUCCESS API CONVO");
							JSONUtil.processGetConversations(arg1);
						}

					});

		}

	}

	public static void postConversations(ArrayList<UserModel> contacts) {
		if (HollerbackAppState.isValidSession()) {
			RequestParams params = new RequestParams();
			params.put(HollerbackAPI.PARAM_ACCESS_TOKEN,
					HollerbackAppState.getValidToken());

			params.put(HollerbackAPI.PARAM_INVITES,
					HBRequestUtil.generateStringArray(contacts));

			HollerbackAsyncClient.getInstance().post(
					HollerbackAPI.API_CONVERSATION, params,
					new JsonHttpResponseHandler() {

						@Override
						protected Object parseResponse(String arg0)
								throws JSONException {
							LogUtil.i(arg0);
							return super.parseResponse(arg0);

						}

						@Override
						public void onFailure(Throwable arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							LogUtil.e(HollerbackAPI.API_CONVERSATION
									+ "FAILURE");
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1);
							LogUtil.i("ON SUCCESS API CONVO");
							JSONUtil.processPostConversations(arg1);
						}

					});

		}
	}

	public static void getContacts(ArrayList<UserModel> contacts) {

		if (HollerbackAppState.isValidSession()) {
			RequestParams params = new RequestParams();

			LogUtil.i("Token: " + HollerbackAppState.getValidToken());

			params.put(HollerbackAPI.PARAM_ACCESS_TOKEN,
					HollerbackAppState.getValidToken());

			params.put(HollerbackAPI.PARAM_NUMBERS,
					HBRequestUtil.generateStringArray(contacts));

			HollerbackAsyncClient.getInstance().get(HollerbackAPI.API_CONTACTS,
					params, new JsonHttpResponseHandler() {

						@Override
						protected Object parseResponse(String arg0)
								throws JSONException {
							LogUtil.i("RESPONSE: " + arg0);
							return super.parseResponse(arg0);

						}

						@Override
						public void onFailure(Throwable arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1);
							LogUtil.e(HollerbackAPI.API_CONTACTS + "FAILURE");
						}

						@Override
						public void onSuccess(int arg0, JSONObject arg1) {
							// TODO Auto-generated method stub
							super.onSuccess(arg0, arg1);
							LogUtil.i("ON SUCCESS API CONTACTS");
							JSONUtil.processGetContacts(arg1);
						}

					});

		}
	}

	public static void getConversationVideos(final String conversationId) {
		if (HollerbackAppState.isValidSession()) {
			RequestParams params = new RequestParams();

			params.put(HollerbackAPI.PARAM_ACCESS_TOKEN,
					HollerbackAppState.getValidToken());

			HollerbackAsyncClient
					.getInstance()
					.get(String.format(
							HollerbackAPI.API_CONVERSATION_DETAILS_VIDEOS_FORMAT,
							conversationId), params,
							new JsonHttpResponseHandler() {

								@Override
								protected Object parseResponse(String arg0)
										throws JSONException {
									LogUtil.i("RESPONSE: " + arg0);
									return super.parseResponse(arg0);

								}

								@Override
								public void onFailure(Throwable arg0,
										JSONObject arg1) {
									// TODO Auto-generated method stub
									super.onFailure(arg0, arg1);
									LogUtil.e(HollerbackAPI.API_CONTACTS
											+ "FAILURE");
								}

								@Override
								public void onSuccess(int arg0, JSONObject arg1) {
									// TODO Auto-generated method stub
									super.onSuccess(arg0, arg1);
									LogUtil.i("ON SUCCESS API CONTACTS");
									JSONUtil.processConversationVideos(conversationId, arg1);
								}

							});
		}
	}
}