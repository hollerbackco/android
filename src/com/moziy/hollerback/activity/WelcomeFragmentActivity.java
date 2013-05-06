package com.moziy.hollerback.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.moziy.hollerback.R;
import com.moziy.hollerback.fragment.WelcomeFragment;

public class WelcomeFragmentActivity extends HollerbackBaseFragmentActivity {

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.welcome_fragment_activity);
		initFragment();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	// TODO Abstract to fragment manager
	public void initFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		WelcomeFragment fragment = new WelcomeFragment();
		fragmentTransaction.add(R.id.fragment_holder, fragment);
		fragmentTransaction.commit();
	}

}
