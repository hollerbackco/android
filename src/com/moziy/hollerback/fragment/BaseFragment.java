package com.moziy.hollerback.fragment;

import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.helper.CustomActionBarHelper;

public abstract class BaseFragment extends SherlockFragment {

	@Override
	public void onResume() {
		onActionBarIntialized(HollerbackBaseActivity.getCustomActionBar());
		super.onResume();
	}

	protected abstract void initializeView(View view);

	protected abstract void onActionBarIntialized(
			CustomActionBarHelper viewHelper);

}
