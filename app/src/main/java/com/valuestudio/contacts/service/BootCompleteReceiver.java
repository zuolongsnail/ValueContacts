package com.valuestudio.contacts.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.SharedPrefsUtil;

/**
 * 开机完成广播
 * 
 * @description
 * @date 2014-6-6
 * @author zuolong
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (SharedPrefsUtil.getValue(context, Constant.RELAY_DIALER, false)) {
			// 启动服务
			if (!ContactsUtil.checkServiceRunning(context,
					RelayDialerService.class.getName())) {
				Intent relayIntent = new Intent(context,
						RelayDialerService.class);
				context.startService(relayIntent);
			}
		}
	}

}
