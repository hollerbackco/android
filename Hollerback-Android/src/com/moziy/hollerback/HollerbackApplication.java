package com.moziy.hollerback;

import com.moziy.hollerback.util.DataModelManager;

public class HollerbackApplication extends com.activeandroid.app.Application {
	private static HollerbackApplication sInstance = null;

	private static DataModelManager sDataModelManager = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		sDataModelManager = new DataModelManager();
	}

	public DataModelManager getDM() {
		return sDataModelManager;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
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
