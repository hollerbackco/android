package com.moziy.hollerback.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;

import com.moziy.hollerback.HollerbackApplication;
import com.moziy.hollerback.communication.IABIntent;
import com.moziy.hollerback.communication.IABroadcastManager;
import com.moziy.hollerback.debug.LogUtil;

public class JSONUtil {

	public static void processSignIn(JSONObject object) {
		try {
			LogUtil.i("HB", object.toString());

			PreferenceManagerUtil.setPreferenceValue(
					HollerbackPreferences.ACCESS_TOKEN,
					object.getString("access_token"));

			JSONObject user = object.getJSONObject("user");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(HollerbackApplication.getInstance(), "DONE",
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(IABIntent.INTENT_SESSION_REQUEST);
		intent.putExtra(IABIntent.PARAM_AUTHENTICATED, IABIntent.VALUE_TRUE);
		IABroadcastManager.sendLocalBroadcast(intent);

	}

}
