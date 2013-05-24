package com.moziy.hollerback.fragment;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.moziy.hollerback.R;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.Country;
import com.moziy.hollerback.util.ISOUtil;
import com.moziy.hollerback.validator.TextValidator;

public class SignUpFragment extends BaseFragment implements OnClickListener {

	private EditText mNameField, mEmailField, mPasswordField,
			mPhoneNumberField;

	private Button mSubmitButton;

	private View mRLCountrySelector;

	private TextView mCountryText;

	private AlertDialog countriesDialog;

	private List<Country> mCountries;

	private CharSequence[] mCharCountries;

	private Country mSelectedCountry;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.signup_fragment, null);
		initializeView(fragmentView);

		PhoneNumberUtil util = PhoneNumberUtil.getInstance();
		Set<String> set = util.getSupportedRegions();

		mCountries = ISOUtil.getCountries(set.toArray(new String[set.size()]));

		mCharCountries = new CharSequence[mCountries.size()];

		Locale locale = Locale.getDefault();

		mSelectedCountry = new Country(locale.getCountry(),
				locale.getCountry(), locale.getDisplayCountry());

		mCountryText.setText(mSelectedCountry.name);

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
			Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT)
					.show();
		}

		return false;
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
						countriesDialog.dismiss();
					}
				});
		countriesDialog = builder.create();
		countriesDialog.show();

	}
}
