package com.valuestudio.contacts.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * @title NetworkUtil
 * @description 检查网络状态工具类
 * @author zuolong
 * @date 2013-6-25
 * @version V1.0
 */
public class NetworkUtil {

	public static enum NetType {
		/** 无网络 */
		NONE,
		/** 无线网络 */
		WIFI,
		/** 移动网络 */
		MOBILE
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo info = null;
			State state = null;
			// wifi
			info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null) {
				state = info.getState();
				if (state == State.CONNECTED) {
					return true;
				}
			}
			// mobile
			info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (info != null) {
				state = info.getState();
				if (state == State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			// NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			// if (info != null) {
			// for (int i = 0; i < info.length; i++) {
			// if (info[i].isAvailable()) {
			// return true;
			// }
			// }
			// }

			NetworkInfo info = null;
			// wifi
			info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null && info.isAvailable()) {
				return true;
			}
			// mobile
			info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (info != null && info.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null) {
				State state = info.getState();
				if (state == State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断MOBILE网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo info = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (info != null) {
				State state = info.getState();
				if (state == State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前的网络连接类型
	 * 
	 * @param context
	 * @return
	 */
	public static NetType getNetType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NetType.NONE;
		}
		int nType = networkInfo.getType();

		if (nType == ConnectivityManager.TYPE_MOBILE) {
			// if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
			// return NetType.CMNET;
			// } else {
			// return NetType.CMWAP;
			// }
			return NetType.MOBILE;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			return NetType.WIFI;
		}
		return NetType.NONE;

	}
}
