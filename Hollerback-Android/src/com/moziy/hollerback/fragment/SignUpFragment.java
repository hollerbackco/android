package com.moziy.hollerback.fragment;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.activity.SplashScreenActivity;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.Country;
import com.moziy.hollerback.util.ISOUtil;
import com.moziy.hollerback.util.NumberUtil;
import com.moziy.hollerback.validator.TextValidator;
import com.moziy.hollerbacky.connection.HBRequestManager;

public class SignUpFragment extends BaseFragment implements OnClickListener {

	private EditText mNameField, mEmailField, mPasswordField,
			mPhoneNumberField;

	private Button mSubmitButton;

	private View mRLCountrySelector;

	private TextView mCountryText, mPhoneNumberCode;

	private AlertDialog countriesDialog;

	private List<Country> mCountries;

	private CharSequence[] mCharCountries;

	private String mRegistrationName;
	private String mRegistrationEmail;
	private String mRegistrationPassword;
	private String mRegistrationPhone;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		IABroadcastManager.unregisterLocalReceiver(receiver);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	private Country mSelectedCountry;

	private PhoneNumberUtil util;

	@Override
	protected void initializeView(View view) {
		mNameField = (EditText) view.findViewById(R.id.textfield_name);
		mEmailField = (EditText) view.findViewById(R.id.textfield_email);
		mPasswordField = (EditText) view.findViewById(R.id.textfield_password);
		mPhoneNumberField = (EditText) view
				.findViewById(R.id.textfield_phonenumber);

		mSubmitButton = (Button) view.findViewById(R.id.register_submit);

		mSubmitButton.setOnClickListener(this);

		mRLCountrySelector = view.findViewById(R.id.rl_country_selector);

		mCountryText = (TextView) view.findViewById(R.id.tv_country_selector);
		mRLCountrySelector.setOnClickListener(this);
		mPhoneNumberCode = (TextView) view
				.findViewById(R.id.tv_phone_number_code);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.signup_fragment, null);
		initializeView(fragmentView);

		util = PhoneNumberUtil.getInstance();
		Set<String> set = util.getSupportedRegions();

		mCountries = ISOUtil.getCountries(set.toArray(new String[set.size()]));

		mCharCountries = new CharSequence[mCountries.size()];

		Locale locale = Locale.getDefault();

		mSelectedCountry = new Country(locale.getISO3Country(),
				locale.getCountry(), locale.getDisplayCountry());

		mCountryText.setText(mSelectedCountry.name);

		mPhoneNumberCode.setText("+"
				+ Integer.toString(util
						.getCountryCodeForRegion(mSelectedCountry.code)));

		for (int i = 0; i < mCountries.size(); i++) {
			mCharCountries[i] = mCountries.get(i).name;
		}

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
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_REGISTER_REQUEST);
	}

	public static SignUpFragment newInstance(int num) {

		SignUpFragment f = new SignUpFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

	public void processSubmit() {

		if (verifyFields()) {
			HBRequestManager.postRegistration(mRegistrationName,
					mRegistrationEmail, mRegistrationPassword,
					mRegistrationPhone);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_submit:
			processSubmit();
			break;
		case R.id.rl_country_selector:
			showDialog();
			break;
		}

	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		// TODO Auto-generated method stub

	}

	public void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select Your Country");
		builder.setSingleChoiceItems(mCharCountries, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						mSelectedCountry = mCountries.get(item);
						mCountryText.setText(mCountries.get(item).name);
						mPhoneNumberCode.setText("+"
								+ Integer.toString(util
										.getCountryCodeForRegion(mSelectedCountry.code)));
						countriesDialog.dismiss();
					}
				});
		countriesDialog = builder.create();
		countriesDialog.show();

	}

	private PhoneNumber getPhoneNumber() {
		if (mPhoneNumberField.getText().toString() == null
				|| mPhoneNumberField.getText().toString().trim().isEmpty()
				|| mPhoneNumberField.getText().toString().trim().length() < 3) {
			return null;
		}
		return NumberUtil.getPhoneNumber("+"
				+ util.getCountryCodeForRegion(mSelectedCountry.code)
				+ mPhoneNumberField.getText().toString());
	}

	public boolean verifyFields() {
		String validEmail = TextValidator.isValidEmailAddress(mEmailField
				.getText().toString());
		String validPhone = TextValidator.isValidPhone(getPhoneNumber());
		String validPassword = TextValidator.isValidPassword(mPasswordField
				.getText().toString());
		String validName = TextValidator.isValidName(mNameField.getText()
				.toString());

		boolean valid = (validEmail == null && validPhone == null
				&& validPassword == null && validName == null);

		if (!valid) {
			String message = (validName != null ? validName + "\n" : "")
					+ (validEmail != null ? validEmail + "\n" : "")
					+ (validPassword != null ? validPassword : "")
					+ (validPhone != null ? "\n" + validPhone : "");

			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		} else {
			mRegistrationName = mNameField.getText().toString();
			mRegistrationEmail = mEmailField.getText().toString();
			mRegistrationPassword = mPasswordField.getText().toString();
			mRegistrationPhone = "+"
					+ util.getCountryCodeForRegion(mSelectedCountry.code)
					+ mPhoneNumberField.getText().toString();
		}

		return valid;

	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (IABIntent.isIntent(intent, IABIntent.INTENT_REGISTER_REQUEST)) {
				if (intent.hasExtra(IABIntent.PARAM_AUTHENTICATED)) {
					// Toast.makeText(getActivity(), "Registration Successful",
					// Toast.LENGTH_LONG).show();
					Intent i = new Intent(getActivity(),
							HollerbackBaseActivity.class);
					getActivity().startActivity(i);
					getActivity().finish();
				}
			}
		}
	};
}
