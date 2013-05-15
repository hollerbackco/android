package com.moziy.hollerback.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.ContactsListAdapter;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.UserModel;
import com.moziy.hollerbacky.connection.HBRequestManager;

public class AddConversationFragment extends BaseFragment {

	StickyListHeadersListView stickyList;
	ContactsListAdapter mAdapter;
	EditText mEditText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.add_convo_fragment, null);

		stickyList = (StickyListHeadersListView) fragmentView
				.findViewById(R.id.list);
		// stickyList.addHeaderView(inflater.inflate(R.layout.list_header,
		// null));
		stickyList.addFooterView(inflater.inflate(R.layout.list_footer, null));
		initializeView(fragmentView);
		mAdapter.setContacts(new ArrayList<UserModel>(TempMemoryStore.usersHash
				.values()));
		HBRequestManager.getContacts(mAdapter.contactitems);

		return fragmentView;
	}

	@Override
	public void onResume() {

		super.onResume();
		IABroadcastManager.registerForLocalBroadcast(receiver,
				IABIntent.INTENT_GET_CONTACTS);
	}

	@Override
	protected void initializeView(View view) {
		mAdapter = new ContactsListAdapter(getActivity());
		stickyList.setAdapter(mAdapter);
		mEditText = (EditText) view.findViewById(R.id.et_add_contacts);

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				searchForContact(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	ArrayList<UserModel> searchItems;

	protected List<UserModel> searchForContact(String searchString) {
		if (!searchString.trim().isEmpty()) {
			searchItems = new ArrayList<UserModel>();
			for (UserModel contact : mAdapter.contactitems) {
				if (contact.mDisplayName.toLowerCase().contains(
						searchString.trim().toLowerCase())) {
					searchItems.add(contact);
				}
			}
		} else {
			searchItems = mAdapter.contactitems;
		}
		mAdapter.clear();
		mAdapter.setContacts(searchItems);
		return searchItems;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		IABroadcastManager.unregisterLocalReceiver(receiver);
	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		viewHelper.getLeftBtn().setVisibility(View.GONE);
		viewHelper.getRightBtn().setVisibility(View.GONE);
		viewHelper.setHeaderText(HollerbackApplication.getInstance().s(
				R.string.new_conversation));
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (IABIntent.isIntent(intent, IABIntent.INTENT_GET_CONTACTS)) {
				mAdapter.setContacts(new ArrayList<UserModel>(
						TempMemoryStore.usersHash.values()));
				mAdapter.notifyDataSetChanged();
			}
		}
	};

}
