package com.moziy.hollerback.util;


public class AppEnvironment {

	// NEED NOT RESET //

	public static final String APP_PREF = "HollerbackAppPrefs";

	// DEV
	// http://lit-sea-1934.herokuapp.com/

	// NEED NOT RESET //

	public static final String ACCESS_KEY_ID = "AKIAJX65IZWDWNJQVNIA";
	public static final String SECRET_KEY = "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";

	// public static final String PICTURE_NAME = null;

	public static final String PICTURE_BUCKET = "hollerback-jian";

	public static final int ENV_PRODUCTION = 0x9999;
	public static final int ENV_DEVELOPMENT = 0x1234;

	public static final int ENV = ENV_PRODUCTION;

	public static String getPictureBucket() {
		return PICTURE_BUCKET;
	}

	public void setEnvironment() {
		switch (ENV) {
		case ENV_DEVELOPMENT:
			break;
		case ENV_PRODUCTION:
			break;
		}
	}

}
