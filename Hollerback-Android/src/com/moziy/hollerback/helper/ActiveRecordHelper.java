package com.moziy.hollerback.helper;

import java.util.List;

import com.activeandroid.query.Select;
import com.moziy.hollerback.model.ConversationModel;

public class ActiveRecordHelper {

	public static List<ConversationModel> getAllConversations() {
		return new Select().from(ConversationModel.class).where("*").execute();
	}
}
