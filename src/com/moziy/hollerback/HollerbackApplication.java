package com.moziy.hollerback;

import android.app.Application;

public class HollerbackApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	private static HollerbackApplication sInstance = null;

	public HollerbackApplication() {
		sInstance = this;
	}

	public static HollerbackApplication getInstance() {
		return sInstance;
	}

}
