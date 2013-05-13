package com.moziy.hollerback.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.ContactsListAdapter;
import com.moziy.hollerback.helper.CustomActionBarHelper;

public class AddConversationFragment extends BaseFragment {

	StickyListHeadersListView stickyList;
	ContactsListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.add_convo_fragment, null);

		stickyList = (StickyListHeadersListView) fragmentView
				.findViewById(R.id.list);
		stickyList.addHeaderView(inflater.inflate(R.layout.list_header, null));
		stickyList.addFooterView(inflater.inflate(R.layout.list_footer, null));
		initializeView(fragmentView);
		return fragmentView;
	}

	@Override
	protected void initializeView(View view) {
		mAdapter = new ContactsListAdapter(getActivity());
		stickyList.setAdapter(mAdapter);
	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		viewHelper.getLeftBtn().setVisibility(View.GONE);
		viewHelper.getRightBtn().setVisibility(View.GONE);
		viewHelper.setHeaderText(HollerbackApplication.getInstance().s(
				R.string.new_conversation));

	}

}
