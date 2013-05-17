package com.moziy.hollerback.communication;

import android.content.Intent;

public class IABIntent {

	public static final String INTENT_SESSION_REQUEST = "SessionRequest";
	public static final String INTENT_REGISTER_REQUEST = "RegisterRequest";
	public static final String INTENT_GET_CONVERSATIONS = "GetConvo";
	public static final String INTENT_REQUEST_VIDEO = "VideoRequest";
	public static final String INTENT_UPLOAD_VIDEO = "UploadVideo";
	public static final String INTENT_GET_CONTACTS = "GetContacts";
	public static final String INTENT_POST_CONVERSATIONS = "PostConversation";

	public static final String PARAM_SUCCESS = "200";
	public static final String PARAM_FAILURE = "500";
	public static final String PARAM_AUTHENTICATED = "AUTH";
	public static final String PARAM_URI = "URI";
	public static final String PARAM_ID = "ID";

	public static final boolean VALUE_TRUE = true;
	public static final boolean VALUE_FALSE = false;

	public static boolean isIntent(Intent intent, String action) {
		return intent.getAction().equals(action);
	}

}
