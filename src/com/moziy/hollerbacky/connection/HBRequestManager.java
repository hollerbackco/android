package com.moziy.hollerbacky.connection;

import org.json.JSONObject;

/**
 * Manage all Requests here so other classes can make request agnostically
 * 
 * @author jianchen
 * 
 */
public class HBRequestManager {

	static boolean isS3Upload;

	public static boolean postVideo(JSONObject jsonObject, String filePath,
			String fileName) {
		if (isS3Upload) {

		}
		return true;
	}

}