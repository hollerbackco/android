package com.moziy.hollerback.util;

import java.util.Locale;

public class AppEnvironment {

	// NEED NOT RESET //

	public static final String APP_PREF = "HollerbackAppPrefs";

	// NEED NOT RESET //

	public static final String ACCESS_KEY_ID = "AKIAIXP4MGYJKF2XZTIA";
	public static final String SECRET_KEY = "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";

	// public static final String PICTURE_NAME = null;

	public static final String PICTURE_BUCKET = "media.jianchen.com";

	public static final int ENV_PRODUCTION = 0x9999;
	public static final int ENV_DEVELOPMENT = 0x1234;

	public static final String BASE_URL = "https://www.realtechcompany.com/wow";

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
