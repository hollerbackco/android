package com.moziy.hollerbacky.connection;

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

	private static String getAbsoluteUrl(String relativeUrl) {
		return AppEnvironment.BASE_URL + relativeUrl;
	}
	
	private static void setHeaders(){
		client.addHeader("X-PLATFORM", "ANDROID");
	}

}
