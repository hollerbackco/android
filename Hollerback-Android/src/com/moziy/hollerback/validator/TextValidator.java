package com.moziy.hollerback.validator;

public class TextValidator {

	public static final int TYPE_EMAIL = 3;
	public static final int TYPE_NAME = 2;

	public static boolean isValid(String string, int type) {
		switch (type) {
		case TYPE_EMAIL:
			return isValidEmailAddress(string);
		case TYPE_NAME:
			return isValidName(string);
		default:
			return false;
		}
	}

	public static boolean isValidEmailAddress(String email) {

		EmailValidator validator = new EmailValidator();
		return validator.validate(email);

	}

	private static boolean isValidName(String name) {
		return true;
	}

}
