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

	public static int getIndexForConversationId(int id) {
		int i = 0;
		for (ConversationModel conversation : conversations) {
			if (conversation.getConversation_id() == id) {
				return i;
			}
			i++;
		}

		return -1;
	}

}
