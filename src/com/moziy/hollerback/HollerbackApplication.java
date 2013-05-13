package com.moziy.hollerback;

import android.app.Application;

public class HollerbackApplication extends Application {
	private static HollerbackApplication sInstance = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	public HollerbackApplication() {
		sInstance = this;
	}

	public static HollerbackApplication getInstance() {
		return sInstance;
	}

	public String s(int id) {
		return getResources().getString(id);
	}

}
