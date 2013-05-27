package com.moziy.hollerback.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.helper.CustomActionBarHelper;

public abstract class BaseFragment extends Fragment {

	@Override
	public void onResume() {
		onActionBarIntialized(HollerbackBaseActivity.getCustomActionBar());
		super.onResume();
	}

	protected abstract void initializeView(View view);

	protected abstract void onActionBarIntialized(
			CustomActionBarHelper viewHelper);

}
