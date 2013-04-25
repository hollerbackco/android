package com.jianchen.hollerbackyo.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TestHttpClient {

	public static String postData(String filename, JSONObject obj) {
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://hollerbackjian.herokuapp.com/video?filename=" + filename);
		httppost.setHeader("Content-Type", "application/json");
		try {
			if (obj != null) {
				StringEntity se = new StringEntity(obj.toString());
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httppost.setEntity(se);
			}
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			// JSONObject jsonResponse = (JSONObject) response.getEntity();
			String temp = EntityUtils.toString(response.getEntity());

			Log.i("Response", temp);
			
			return temp;

		} catch (ClientProtocolException e) {
			return null;
			// TODO Auto-generated catch block
		} catch (IOException e) {
			return null;
			// TODO Auto-generated catch block
		}
	}

}
