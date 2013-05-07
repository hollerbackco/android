package com.moziy.hollerback.fragment;

import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.activity.WelcomeFragmentActivity;
import com.moziy.hollerback.util.HollerbackAppState;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class MessageListFragment extends BaseFragment {

	ListView mMessageList;

	Button mLogoutBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.message_list_fragment,
				null);
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
		mMessageList = (ListView) view.findViewById(R.id.message_listview);
		mLogoutBtn = (Button) view.findViewById(R.id.btn_logout);
		mLogoutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (HollerbackAppState.isValidSession()) {
					HollerbackAppState.logOut();
					Intent intent = new Intent(getActivity(),
							WelcomeFragmentActivity.class);
					getActivity().startActivity(intent);
					getActivity().finish();
				}

			}
		});
	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static MessageListFragment newInstance(int num) {

		MessageListFragment f = new MessageListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

}
