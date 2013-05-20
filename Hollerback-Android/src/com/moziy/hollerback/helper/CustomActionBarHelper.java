package com.moziy.hollerback.helper;

import com.moziy.hollerback.R;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomActionBarHelper {

	private ImageButton mImageLeftBtn, mImageRightBtn;
	private TextView mHeaderText;
	private ImageView mHeaderLogo;

	public CustomActionBarHelper(View actionbar) {
		mImageLeftBtn = (ImageButton) actionbar
				.findViewById(R.id.ib_action_left);
		mImageRightBtn = (ImageButton) actionbar
				.findViewById(R.id.ib_action_right);
		mHeaderLogo = (ImageView) actionbar.findViewById(R.id.iv_action_logo);
		mHeaderText = (TextView) actionbar.findViewById(R.id.tv_action_name);
	}

	public ImageButton getLeftBtn() {
		return mImageLeftBtn;
	}

	public ImageButton getRightBtn() {
		return mImageRightBtn;
	}

	public TextView getHeaderText() {
		return mHeaderText;
	}

	public void setSettings() {
		mImageLeftBtn.setVisibility(View.VISIBLE);
		mImageLeftBtn.setImageResource(R.drawable.settings_btn);
	}

	public void setAddConversation() {
		mImageRightBtn.setVisibility(View.VISIBLE);
		mImageRightBtn.setImageResource(R.drawable.add_convo_btn);
	}

	public void setHeaderText(String text) {
		mHeaderLogo.setVisibility(View.GONE);
		mHeaderText.setVisibility(View.VISIBLE);
		mHeaderText.setText(text);
	}

	public void setHeaderLogo() {
		mHeaderLogo.setVisibility(View.VISIBLE);
		mHeaderText.setVisibility(View.GONE);
	}

	public void setSettingsFragmentSettings() {
		mImageLeftBtn.setVisibility(View.GONE);
	}

	
	
}
