package com.moziy.hollerback.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moziy.hollerback.R;
import com.moziy.hollerback.helper.CustomActionBarHelper;

public class SettingsFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.settings_fragment, null);
		initializeView(fragmentView);
		return fragmentView;
	}

	@Override
	protected void initializeView(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		// TODO Auto-generated method stub

	}

}
