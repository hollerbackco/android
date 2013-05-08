package com.moziy.hollerback.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.WelcomeFragmentActivity;
import com.moziy.hollerback.adapter.ConversationListAdapter;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.util.HollerbackAppState;
import com.moziy.hollerbacky.connection.HBRequestManager;

public class ConversationListFragment extends BaseFragment {

	ListView mConversationList;
	ConversationListAdapter mConversationListAdapter;
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
		IABroadcastManager.unregisterLocalReceiver(receiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_GET_CONVERSATIONS);
		HBRequestManager.getConversations();
	}

	OnItemClickListener mOnListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			startConversationFragment(position);
		}

	};

	@Override
	protected void initializeView(View view) {
		mConversationList = (ListView) view.findViewById(R.id.message_listview);

		mConversationListAdapter = new ConversationListAdapter(getActivity());
		mConversationListAdapter
				.setConversations(new ArrayList<ConversationModel>());

		mConversationList.setAdapter(mConversationListAdapter);

		mConversationList.setOnItemClickListener(mOnListItemClickListener);

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

	public void startConversationFragment(int index) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		ConversationFragment fragment = ConversationFragment.newInstance(index);
		fragmentTransaction.replace(R.id.fragment_holder, fragment);
		fragmentTransaction.addToBackStack(ConversationFragment.class
				.getSimpleName());
		fragmentTransaction.commit();
	}

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static ConversationListFragment newInstance(int num) {

		ConversationListFragment f = new ConversationListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		return f;
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (IABIntent.isIntent(intent, IABIntent.INTENT_GET_CONVERSATIONS)) {
				mConversationListAdapter
						.setConversations(TempMemoryStore.conversations);
			}

		}
	};

}
