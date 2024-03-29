package com.moziy.hollerback.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flurry.android.FlurryAgent;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.fragment.WelcomeFragment;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerbacky.connection.RequestCallbacks.OnGCMReceivedListener;

public class WelcomeFragmentActivity extends HollerbackBaseFragmentActivity {

	public static boolean GCM_RECEIVED;
	public static String GCM_TOKEN;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.welcome_fragment_activity);

		initFragment();

		HollerbackApplication.getInstance().registerGCM();
		FlurryAgent
				.onStartSession(this, AppEnvironment.getInstance().FLURRY_ID);
	}

	OnGCMReceivedListener mGCMListener = new OnGCMReceivedListener() {

		@Override
		public void onGCMReceived(String token) {
			GCM_RECEIVED = true;
			GCM_TOKEN = token;
		}
	};

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

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		FlurryAgent.onEndSession(this);
		super.onStop();
	}

}
