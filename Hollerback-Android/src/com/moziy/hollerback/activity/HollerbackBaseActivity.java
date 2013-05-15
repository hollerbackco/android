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

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// // SubMenu subMenu1 = menu.addSubMenu("Action Item");
	// // subMenu1.add("Appearance");
	// // subMenu1.add("Menu");
	// // subMenu1.add("Items");
	//
	// // MenuItem subMenu1Item = subMenu1.getItem();
	// // // subMenu1Item.setIcon(R.drawable.abs__ic_go);
	// // subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
	// // | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	//
	// // SubMenu subMenu2 = menu.addSubMenu("Appearance");
	// // subMenu2.add("Background");
	// // SubMenu subMenu3 = subMenu2.addSubMenu("Themes");
	// // subMenu3.add("Edit Current");
	// // subMenu3.add("Download More");
	// // subMenu3.add("Share Theme");
	//
	// // MenuItem subMenu2Item = subMenu2.getItem();
	// // subMenu2Item.setIcon(R.drawable.ic_compose);
	//
	// return super.onCreateOptionsMenu(menu);
	// }

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
