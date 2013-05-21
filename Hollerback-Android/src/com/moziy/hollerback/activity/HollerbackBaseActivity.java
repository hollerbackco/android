package com.moziy.hollerback.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.moziy.hollerback.R;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.fragment.AddConversationFragment;
import com.moziy.hollerback.fragment.ConversationListFragment;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.SortedArray;
import com.moziy.hollerback.model.UserModel;

/**
 * Main Activity that gets initiated when user is signed in
 * 
 * @author jianchen
 * 
 */
public class HollerbackBaseActivity extends HollerbackBaseFragmentActivity {

	private static CustomActionBarHelper mActionBarView;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		getSupportActionBar().setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar_layout,
				null);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setCustomView(view);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(
						R.drawable.ad_action_bar_gradient_bak));
		mActionBarView = new CustomActionBarHelper(view);
		setContentView(R.layout.hollerback_main);

		initFragment();
	}

	public static CustomActionBarHelper getCustomActionBar() {
		return mActionBarView;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void initFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		ConversationListFragment fragment = new ConversationListFragment();
		fragmentTransaction.add(R.id.fragment_holder, fragment);
		fragmentTransaction.commit();
	}

	public void addContactListFragment(android.app.FragmentTransaction ft,
			SortedArray result) {
		TempMemoryStore.users = result;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		AddConversationFragment fragment = new AddConversationFragment();
		fragmentTransaction.replace(R.id.fragment_holder, fragment);
		fragmentTransaction.addToBackStack(AddConversationFragment.class
				.getSimpleName());
		fragmentTransaction.commit();

	}
}
