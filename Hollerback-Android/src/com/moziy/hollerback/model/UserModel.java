package com.moziy.hollerback.model;

public class UserModel {
	// From Phone
	public long mId;
	public long mContactId;
	public String mDisplayName;
	public String mPhone;

	// From Server
	public String name;
	public boolean isHollerbackUser;
	public boolean isRecentUser;

	public String getName() {
		if (name != null && !name.isEmpty()) {
			return name;
		}
		return mDisplayName;
	}

}
