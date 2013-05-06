package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SignInFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.signin_fragment, null);

		initializeView(fragmentView);

		return fragmentView;
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

	@Override
	protected void initializeView(View view) {
		// TODO Auto-generated method stub

	}

	public static SignInFragment newInstance(int num) {

		SignInFragment f = new SignInFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

}
