package com.moziy.hollerback.helper;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moziy.hollerback.R;

public class ProgressHelper {

	RelativeLayout mParent;
	TextView mProgressText;
	ProgressBar mProgressSpinner;

	public ProgressHelper(View view) {
		mParent = (RelativeLayout) view;
		mProgressText = (TextView) view.findViewById(R.id.tv_progress_spinner);
		mProgressSpinner = (ProgressBar) view.findViewById(R.id.pb_spinner);
	}

	public void startIndeterminateSpinner() {
		mProgressText.setText("");
		mParent.setVisibility(View.VISIBLE);

	}

	public void startUpdateProgress(int text) {
		mParent.setVisibility(View.VISIBLE);
		mProgressSpinner.setVisibility(View.INVISIBLE);
		mProgressText.setVisibility(View.VISIBLE);
		mProgressText.setText(String.valueOf(text));
	}
	
	public void hideLoader(){
		mParent.setVisibility(View.GONE);
	}

}
