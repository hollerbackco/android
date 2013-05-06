package com.moziy.hollerback.util;

public class HollerbackAPI {

	public static final String BASE_URL = "http://calm-peak-4397.herokuapp.com";

	public static final String API_ME = "/me"; //get, post
	public static final String API_SESSION = "/session";
	
	/**
	 * GET & POST
	 */
	public static final String API_CONVERSATION = "/me/conversations";
	
	/**
	 * POST
	 * "/me/videos/:id/read"
	 */
	public static final String API_VIDEO_READ = "/me/videos/%1$s/read";
	
	/**
	 * POST
	 */
	public static final String API_REGISTER = "/register";
	
	
}
