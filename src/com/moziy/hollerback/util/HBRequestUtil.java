package com.moziy.hollerback.util;

import java.util.ArrayList;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.model.LocalContactItem;

public class HBRequestUtil {

	public static String generatePhoneNumberArrayString(
			ArrayList<LocalContactItem> items) {
		PhoneNumberUtil util = PhoneNumberUtil.getInstance();
		String start = "[";
		String end = "]";

		int count = 0;

		for (LocalContactItem item : items) {
			try {
				PhoneNumber phoneNumber = util.parse(item.mPhone, "US");
				if (util.isValidNumber(phoneNumber)) {
					if (count > 0) {
						start += ",";
					}
					String phone = util.format(phoneNumber,
							PhoneNumberFormat.E164);
					start += phone;
					count++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String numbers = start + end;
		LogUtil.i(numbers);
		return numbers;
	}

	public static ArrayList<String> generateStringArray(
			ArrayList<LocalContactItem> items) {
		PhoneNumberUtil util = PhoneNumberUtil.getInstance();

		ArrayList<String> numbers = new ArrayList<String>();

		for (LocalContactItem item : items) {
			try {
				PhoneNumber phoneNumber = util.parse(item.mPhone, "US");
				if (util.isValidNumber(phoneNumber)) {

					String phone = util.format(phoneNumber,
							PhoneNumberFormat.E164);

					numbers.add(phone);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return numbers;
	}
}
