package com.moziy.hollerback.helper;

import com.moziy.hollerback.R;

import android.view.View;
import android.widget.ImageButton;

public class CustomActionBarHelper {

	private ImageButton mImageLeftBtn, mImageRightBtn;

	public CustomActionBarHelper(View actionbar) {
		mImageLeftBtn = (ImageButton) actionbar
				.findViewById(R.id.ib_action_left);
		mImageRightBtn = (ImageButton) actionbar
				.findViewById(R.id.ib_action_right);

	}

	public ImageButton getLeftBtn() {
		return mImageLeftBtn;
	}

	public ImageButton getRightBtn() {
		return mImageRightBtn;
	}

}
