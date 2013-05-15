package com.moziy.hollerback.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.SortedArray;
import com.moziy.hollerback.model.UserModel;

public class CollectionOpUtils {

	public static SortedArray sortContacts(ArrayList<UserModel> users) {

		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

		SortedArray sortedArray = new SortedArray();

		ArrayList<UserModel> recentUsers = new ArrayList<UserModel>();
		ArrayList<UserModel> hollerbackUsers = new ArrayList<UserModel>();
		ArrayList<UserModel> phoneBookUsers = new ArrayList<UserModel>();

		for (UserModel user : users) {
			if (user.isRecentUser && user.isHollerbackUser) {
				recentUsers.add(user);
			} else if (user.isHollerbackUser) {
				hollerbackUsers.add(user);
			} else {
				phoneBookUsers.add(user);
			}
		}

		sortedArray.array.addAll(recentUsers);
		sortedArray.array.addAll(hollerbackUsers);
		sortedArray.array.addAll(phoneBookUsers);
		

		for (UserModel user : sortedArray.array) {
			PhoneNumber number;
			try {
				number = phoneUtil.parse(user.mPhone, "US");
				String formattedNumber =phoneUtil.format(number, PhoneNumberFormat.E164);
				sortedArray.mUserModelHash.put(
						formattedNumber, user);
				sortedArray.sortedKeys.add(formattedNumber);
			} catch (NumberParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		sortedArray.indexes.add(0);
		sortedArray.indexes.add(recentUsers.size());
		sortedArray.indexes.add(hollerbackUsers.size());

		return sortedArray;
	}

}
