package com.moziy.hollerback.database;

public class ActiveRecordFields {

	// Conversation
	public static final String C_CONV_ID = "ConvId";
	public static final String C_CONV_NAME = "ConvName";
	public static final String C_CONV_UNREAD = "ConvUnread";
	public static final String C_CONV_MOST_RECENT_VIDEO = "ConvRecentVideo";
	public static final String C_CONV_MOST_RECENT_THUMB = "ConvRecentUrl";

	// Videos
	public static final String C_VID_ID = "VidId";
	public static final String C_VID_CONV_ID = "VidConvId";

	// Users
	public static final String C_USER_ID = "UserId";
	public static final String C_USER_NAME = "";
	public static final String C_USER_EMAIL = "";
	public static final String C_USER_PHONE = "";
	public static final String C_USER_PHONE_NORMALIZED = "";
	public static final String C_USER_IS_VERIFIED = "";

	// User Conversation Relationship

	// Tables
	public static final String T_CONVERSATION = "Conversation";
	public static final String T_USERS = "Users";
	public static final String T_VIDEOS = "Videos";

}