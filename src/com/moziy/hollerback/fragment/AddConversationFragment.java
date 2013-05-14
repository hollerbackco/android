package com.moziy.hollerback.fragment;

import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.R;
import com.moziy.hollerback.adapter.ContactsListAdapter;
import com.moziy.hollerback.helper.CustomActionBarHelper;
import com.moziy.hollerback.model.LocalContactItem;

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
		return fragmentView;
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
				s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	protected List<LocalContactItem> searchForContact(String searchString) {
		List<LocalContactItem> items = null;
		return items;
	}

	@Override
	protected void onActionBarIntialized(CustomActionBarHelper viewHelper) {
		viewHelper.getLeftBtn().setVisibility(View.GONE);
		viewHelper.getRightBtn().setVisibility(View.GONE);
		viewHelper.setHeaderText(HollerbackApplication.getInstance().s(
				R.string.new_conversation));

	}

}
