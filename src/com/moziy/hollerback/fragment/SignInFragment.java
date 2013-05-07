package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerbacky.connection.HBRequestManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignInFragment extends BaseFragment implements OnClickListener {

	private EditText mTextFieldEmail, mTextFieldPassword;
	private Button mLoginBtn;

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
		mTextFieldEmail = (EditText) view.findViewById(R.id.textfield_email);
		mTextFieldPassword = (EditText) view
				.findViewById(R.id.textfield_password);
		mLoginBtn = (Button) view.findViewById(R.id.submit_login);

		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.submit_login:
			processLogin();
			break;
		}

	}

	public void processLogin() {
		LogUtil.i("Logging in with: " + mTextFieldEmail.getText().toString()
				+ " " + mTextFieldPassword.getText().toString());

		HBRequestManager.postLogin(mTextFieldEmail.getText().toString(),
				mTextFieldPassword.getText().toString());

	}

}
