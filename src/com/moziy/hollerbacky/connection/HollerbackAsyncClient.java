package com.moziy.hollerbacky.connection;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moziy.hollerback.util.AppEnvironment;

public class HollerbackAsyncClient {

	private static AsyncHttpClient client = new AsyncHttpClient();

	private static HollerbackAsyncClient sInstance;

	private HollerbackAsyncClient() {
	}

	public static HollerbackAsyncClient getInstance() {
		if (sInstance == null) {
			sInstance = new HollerbackAsyncClient();
			setHeaders();
		}
		return sInstance;
	}

	public static void get(String url, RequestParams params,
			JsonHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			JsonHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, JSONObject object,
			RequestParams params, JsonHttpResponseHandler responseHandler) {
		// params is a JSONObject
		StringEntity se = null;
		try {
			se = new StringEntity(params.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		client.post(null, getAbsoluteUrl(url), se, "application/json",
				responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return AppEnvironment.BASE_URL + relativeUrl;
	}

	private static void setHeaders() {
		client.addHeader("X-PLATFORM", "ANDROID");
	}

}