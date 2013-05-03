package com.moziy.hollerback.activity;

import com.moziy.hollerback.HaveFunVideoActivity;
import com.moziy.hollerback.R;
import com.moziy.hollerback.model.UserModel;
import com.moziy.hollerback.util.HollerbackAppState;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		initializeApplication();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void initializeApplication() {
		UserModel m = HollerbackAppState.isUserLoggedIn();
		Intent i = new Intent(SplashScreenActivity.this,
				HollerbackBaseActivity.class);
		startActivity(i);
		this.finish();

	}

}
