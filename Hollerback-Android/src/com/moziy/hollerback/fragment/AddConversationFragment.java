package com.moziy.hollerback.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.kpbird.chipsedittextlibrary.ChipsAdapter;
import com.kpbird.chipsedittextlibrary.ChipsItem;
import com.kpbird.chipsedittextlibrary.ChipsMultiAutoCompleteTextview;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.activity.HollerbackCameraActivity;
import com.moziy.hollerback.adapter.ContactsListAdapter;
import com.moziy.hollerback.cache.memory.TempMemoryStore;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.SortedArray;
import com.moziy.hollerback.model.UserModel;
import com.moziy.hollerback.util.CollectionOpUtils;
import com.moziy.hollerbacky.connection.HBRequestManager;

public class AddConversationFragment extends BaseFragment {

	StickyListHeadersListView stickyList;
	ContactsListAdapter mAdapter;
	ChipsMultiAutoCompleteTextview mEditText;
	ChipsAdapter mContactChipsAdapter;

	ArrayList<UserModel> mInvites;

	ArrayList<ChipsItem> mContactChips;

	Button mCreateConversationBtn;

	private int mInvitesCount;

	HashMap<String, String> nameKeys;

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
		mAdapter.setContacts(TempMemoryStore.users.sortedKeys, null);
		HBRequestManager.getContacts(TempMemoryStore.users.array);
		stickyList.setOnItemClickListener(mContactClickListener);

		// CollectionOpUtils.setChipItems(TempMemoryStore.users.array);

		mContactChipsAdapter = new ChipsAdapter(getActivity(),
				CollectionOpUtils.setChipItems(TempMemoryStore.users.array));
		// mEditText.setAdapter(mContactChipsAdapter);

		mInvites = new ArrayList<UserModel>();

		return fragmentView;
	}

	OnItemClickListener mContactClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			UserModel user = TempMemoryStore.users.mUserModelHash
					.get(mAdapter.contactitems.get(position));

			mInvites.add(user);

			Toast.makeText(getActivity(), user.getName(), Toast.LENGTH_LONG)
					.show();

			String partial = "";

			if (!mEditText.getText().toString().trim().isEmpty()) {

				partial = mEditText
						.getText()
						.toString()
						.substring(
								0,
								mEditText.getText().toString().lastIndexOf(",") + 1);
			}

			String c = (partial + user.name.trim() + ",");

			mInvitesCount++;

			String[] chips = new String[mInvitesCount];
			String[] contacts = c.toString().trim().split(",");
			for (int i = 0; i < mInvitesCount - 1; i++) {

				chips[i] = contacts[i];
			}
			chips[mInvitesCount - 1] = contacts[contacts.length - 1];

			c = "";
			for (String s : chips) {
				c += (s + ",");
			}

			LogUtil.i("Running Chips for: " + c);

			mEditText.setText(c);
			mEditText.setSelection(c.length());
			mEditText.setChips();
			searchForContact("");

		}
	};

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
		mEditText = (ChipsMultiAutoCompleteTextview) view
				.findViewById(R.id.et_add_contacts);

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String[] last = s.toString().split(",");
				searchForContact(last[last.length - 1]);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mCreateConversationBtn = (Button) view.findViewById(R.id.b_hollerback);
		mCreateConversationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createNewConversation();

			}
		});

	}

	protected void searchForContact(String searchString) {
		ArrayList<UserModel> searchItems;
		if (!searchString.trim().isEmpty()) {
			searchItems = new ArrayList<UserModel>();

			for (UserModel contact : TempMemoryStore.users.array) {
				if (contact.getName().toLowerCase()
						.contains(searchString.trim().toLowerCase())) {
					searchItems.add(contact);
				}
			}

			SortedArray tempSort = CollectionOpUtils.sortContacts(searchItems);

			mAdapter.setContacts(tempSort.sortedKeys, tempSort.indexes);
		} else {
			mAdapter.setContacts(TempMemoryStore.users.sortedKeys,
					TempMemoryStore.users.indexes);
		}
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
				mAdapter.setContacts(TempMemoryStore.users.sortedKeys,
						TempMemoryStore.users.indexes);
				mAdapter.notifyDataSetChanged();
			}

		}
	};

	public void createNewConversation() {
		TempMemoryStore.invitedUsers = mInvites;
		Intent intent = new Intent(getActivity(),
				HollerbackCameraActivity.class);
		startActivityForResult(intent, 3);
	}

}
