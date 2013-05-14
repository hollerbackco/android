package com.moziy.hollerback.background;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.moziy.hollerback.activity.HollerbackBaseActivity;
import com.moziy.hollerback.model.LocalContactItem;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactFetchAsyncTask extends
		AsyncTask<Void, Void, ArrayList<LocalContactItem>> {
	private FragmentTransaction ft;
	private Activity activity;

	public ContactFetchAsyncTask(Activity activity, FragmentTransaction ft) {
		this.activity = activity;
		this.ft = ft;
	}

	public ContactFetchAsyncTask() {
	}

	@SuppressWarnings("unused")
	protected ArrayList<LocalContactItem> doInBackground(Void... params) {
		Cursor c = activity.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
						Data.CONTACT_ID, Phone.TYPE, Phone.LABEL },
				Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'", null,
				Data.DISPLAY_NAME);

		int count = c.getCount();
		boolean b = c.moveToFirst();
		String[] columnNames = c.getColumnNames();
		int displayNameColIndex = c.getColumnIndex("display_name");
		int idColIndex = c.getColumnIndex("_id");
		// int contactIdColIndex = c.getColumnIndex("contact_id");
		int col2Index = c.getColumnIndex(columnNames[2]);
		int col3Index = c.getColumnIndex(columnNames[3]);
		int col4Index = c.getColumnIndex(columnNames[4]);

		ArrayList<LocalContactItem> contactItemList = new ArrayList<LocalContactItem>();
		for (int i = 0; i < count; i++) {
			String displayName = c.getString(displayNameColIndex);
			String phoneNumber = c.getString(col2Index);
			int contactId = c.getInt(col3Index);
			String phoneType = c.getString(col4Index);

			long _id = c.getLong(idColIndex);
			LocalContactItem contactItem = new LocalContactItem();
			contactItem.mId = _id;
			contactItem.mContactId = contactId;
			contactItem.mDisplayName = displayName;
			contactItem.mPhone = phoneNumber;
			contactItemList.add(contactItem);
			boolean b2 = c.moveToNext();
		}
		c.close();
		return contactItemList;
	}

	protected void onPostExecute(ArrayList<LocalContactItem> result) {
		((HollerbackBaseActivity) activity).addContactListFragment(ft, result);

	}
}
