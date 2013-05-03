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

}
