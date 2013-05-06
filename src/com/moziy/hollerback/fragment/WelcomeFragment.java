package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WelcomeFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.welcome_fragment, null);

		initializeView(fragmentView);
		return fragmentView;
	}

	@Override
	protected void initializeView(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public static WelcomeFragment newInstance(int num) {

		WelcomeFragment f = new WelcomeFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}
}
