package com.moziy.hollerback.util;

public class HollerbackAPI {

	public static final String BASE_URL = "https://calm-peak-4397.herokuapp.com";

	public static final String API_SUFFIX = "/api";

	public static final String API_ME = "/me"; // get, post
	public static final String API_SESSION = "/session";

	/**
	 * GET & POST
	 */
	public static final String API_CONVERSATION = "/me/conversations";

	/**
	 * POST "/me/videos/:id/read"
	 */
	public static final String API_VIDEO_READ_FORMAT = "/me/videos/%1$s/read";

	/**
	 * POST new video '/me/conversations/:id/videos'
	 */
	public static final String API_VIDEO_POST_FORMAT = "/me/conversations/%1$s/videos";

	/**
	 * POST
	 */
	public static final String API_REGISTER = "/register";

	public static final String API_CONTACTS = "/contacts/check";

	// /////////////

	public static final String PARAM_EMAIL = "email";

	public static final String PARAM_PASSWORD = "password";

	public static final String PARAM_ACCESS_TOKEN = "access_token";

	public static final String PARAM_FILENAME = "filename";

	public static final String PARAM_NUMBERS = "numbers[]";

}