package com.moziy.hollerback.cache.memory;

import java.util.ArrayList;
import java.util.HashMap;

import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.SortedArray;
import com.moziy.hollerback.model.UserModel;

public class TempMemoryStore {

	public static ArrayList<ConversationModel> conversations;
	// public static ArrayList<UserModel> contacts;

	public static SortedArray users;
	
	public static ArrayList<UserModel> invitedUsers;

}
