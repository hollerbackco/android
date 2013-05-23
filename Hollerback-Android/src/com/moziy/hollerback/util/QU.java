package com.moziy.hollerback.util;

import com.moziy.hollerback.HollerbackApplication;

/**
 * Access a lot of things in the app stands for quickutil
 * 
 * @author jianchen
 * 
 */
public class QU {

	/**
	 * Get DataModelManager
	 */
	public static DataModelManager getDM() {
		return HollerbackApplication.getInstance().getDM();
	}

	/**
	 * Get String from strings file
	 */
	public static String s(int id) {
		return HollerbackApplication.getInstance().getString(id);
	}

}
