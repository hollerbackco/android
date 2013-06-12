package com.moziy.hollerback.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class HollerbackVideoService extends IntentService {

	/**
	 * Main each connection for up to 120 seconds
	 */
	private static long MAX_UPLOAD_TIMEOUT = 120000;
	private static long MAX_NUMBER_RETRY = 3;

	public HollerbackVideoService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

	public void uploadVideoService(Bundle bundle) {

	}

	public void startRetryVideoService() {

	}

}
