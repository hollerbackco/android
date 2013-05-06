package com.moziy.hollerback.util;

import com.moziy.hollerback.model.UserModel;

public class HollerbackAppState {

	private static HollerbackAppState sInstance;

	private HollerbackAppState() {
	}

	public static HollerbackAppState getInstance() {
		if (sInstance == null) {
			sInstance = new HollerbackAppState();
		}
		return sInstance;
	}

	public static UserModel isUserLoggedIn() {
		return null;
	}

	public static boolean isValidSession() {
		if (PreferenceManagerUtil.getPreferenceValue(
				HollerbackPreferences.ACCESS_TOKEN, null) != null) {
			return true;
		}
		return false;
	}

	public static void logOut() {
		PreferenceManagerUtil.setPreferenceValue(
				HollerbackPreferences.ACCESS_TOKEN, null);
	}

}
