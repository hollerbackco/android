package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.validator.TextValidator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpFragment extends BaseFragment implements OnClickListener {

	private EditText mNameField, mEmailField, mPasswordField,
			mPhoneNumberField;

	private Button mSubmitButton;

	@Override
	protected void initializeView(View view) {
		mNameField = (EditText) view.findViewById(R.id.textfield_name);
		mEmailField = (EditText) view.findViewById(R.id.textfield_email);
		mPasswordField = (EditText) view.findViewById(R.id.textfield_password);
		mPhoneNumberField = (EditText) view
				.findViewById(R.id.textfield_phonenumber);

		mSubmitButton = (Button) view.findViewById(R.id.register_submit);

		mSubmitButton.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.signup_fragment, null);
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

	public static SignUpFragment newInstance(int num) {

		SignUpFragment f = new SignUpFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

	public boolean processSubmit() {

		if (TextValidator.isValidEmailAddress(mEmailField.getText().toString())) {
		} else {
			Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_submit:
			processSubmit();
			break;
		}

	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		// TODO Auto-generated method stub
		
	}

}
