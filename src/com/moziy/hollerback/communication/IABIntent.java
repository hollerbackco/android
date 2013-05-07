package com.moziy.hollerback.communication;

import android.content.Intent;

public class IABIntent {

	public static final String INTENT_SESSION_REQUEST = "SessionRequest";
	public static final String INTENT_REGISTER_REQUEST = "RegisterRequest";

	public static final String PARAM_SUCCESS = "200";
	public static final String PARAM_FAILURE = "500";
	public static final String PARAM_AUTHENTICATED = "AUTH";

	public static final boolean VALUE_TRUE = true;
	public static final boolean VALUE_FALSE = false;

	public static boolean isIntent(Intent intent, String action) {
		return intent.getAction().equals(action);
	}

}
