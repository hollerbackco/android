package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.activity.SplashScreenActivity;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerbacky.connection.HBRequestManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
		IABroadcastManager.unregisterLocalReceiver(receiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_SESSION_REQUEST);
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

	private void processLogin() {
		LogUtil.i("Logging in with: " + mTextFieldEmail.getText().toString()
				+ " " + mTextFieldPassword.getText().toString());

		HBRequestManager.postLogin(mTextFieldEmail.getText().toString(),
				mTextFieldPassword.getText().toString());

	}

	private void loginUser() {
		Toast.makeText(getActivity(), "Logged In!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(getActivity(), HollerbackBaseActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish();
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (IABIntent.isIntent(intent, IABIntent.INTENT_SESSION_REQUEST)) {
				if (intent
						.getBooleanExtra(IABIntent.PARAM_AUTHENTICATED, false)) {
					loginUser();
				} else {
					Toast.makeText(getActivity(), "Login Error!", Toast.LENGTH_SHORT).show();

				}
			}
		}
	};

}
