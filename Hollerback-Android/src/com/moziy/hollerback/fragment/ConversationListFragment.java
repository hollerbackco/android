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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.ConversationListAdapter;
import com.moziy.hollerback.background.ContactFetchAsyncTask;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.ConversationModel;
import com.moziy.hollerback.model.VideoModel;
import com.moziy.hollerback.util.AppEnvironment;
import com.moziy.hollerback.util.QU;
import com.moziy.hollerbacky.connection.HBRequestManager;

public class ConversationListFragment extends BaseFragment {

	PullToRefreshListView mConversationList;
	ConversationListAdapter mConversationListAdapter;

	AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(
			AppEnvironment.getInstance().ACCESS_KEY_ID,
			AppEnvironment.getInstance().SECRET_KEY));

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.message_list_fragment,
				null);
		initializeView(fragmentView);
		// HBRequestManager.getConversations();

		QU.getDM().getConversations(false);

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
	}

	OnItemClickListener mOnListItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.i("Starting Conversation: " + position + " id: " + id);

			startConversationFragment(Integer.toString(mConversationListAdapter
					.getItem((int) id).getConversation_Id()));
		}

	};

	@Override
	protected void initializeView(View view) {
		mConversationList = (PullToRefreshListView) view
				.findViewById(R.id.message_listview);

		mConversationList.setShowIndicator(false);

		mConversationList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				LogUtil.i("Refresh the Listview");
				HBRequestManager.getConversations();
			}
		});

		mConversationListAdapter = new ConversationListAdapter(getActivity());
		// mConversationListAdapter
		// .setConversations((ArrayList<ConversationModel>) ActiveRecordHelper
		// .getAllConversations());

		mConversationList.setAdapter(mConversationListAdapter);

		mConversationList.setOnItemClickListener(mOnListItemClickListener);

	}

	public void startConversationFragment(String conversationId) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		// TODO: Fetch data from API call
		ConversationFragment fragment = ConversationFragment
				.newInstance(conversationId);
		fragmentTransaction.replace(R.id.fragment_holder, fragment);
		fragmentTransaction.addToBackStack(ConversationFragment.class
				.getSimpleName());
		fragmentTransaction.commit();
	}

	private void startSettingsFragment() {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		SettingsFragment fragment = new SettingsFragment();
		fragmentTransaction.replace(R.id.fragment_holder, fragment);
		fragmentTransaction.addToBackStack(SettingsFragment.class
				.getSimpleName());
		fragmentTransaction.commit();
	}

	private void startAddConversationFragment() {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		AddConversationFragment fragment = new AddConversationFragment();
		fragmentTransaction.replace(R.id.fragment_holder, fragment);
		fragmentTransaction.addToBackStack(AddConversationFragment.class
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
				// mConversationListAdapter
				// .setConversations(TempMemoryStore.conversations);

				String hash = intent
						.getStringExtra(IABIntent.PARAM_INTENT_DATA);

				ArrayList<ConversationModel> conversations = (ArrayList<ConversationModel>) QU
						.getDM().getObjectForToken(hash);

				mConversationListAdapter.setConversations(conversations);
				mConversationListAdapter.notifyDataSetChanged();

				mConversationList.onRefreshComplete();
			}

		}
	};

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		viewHelper.setAddConversation();
		viewHelper.setHeaderLogo();
		viewHelper.setSettings();
		viewHelper.getRightBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContactFetchAsyncTask mTask = new ContactFetchAsyncTask(
						getActivity(), null);
				mTask.execute();
				// startAddConversationFragment();
			}
		});

		viewHelper.getLeftBtn().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startSettingsFragment();
			}
		});
	}

}
