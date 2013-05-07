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
	public static final String API_VIDEO_READ = "/me/videos/%1$s/read";

	/**
	 * POST
	 */
	public static final String API_REGISTER = "/register";

	public static final String PARAM_EMAIL = "email";

	public static final String PARAM_PASSWORD = "password";

}
