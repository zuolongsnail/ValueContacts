package com.valuestudio.contacts.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.valuestudio.contacts.entity.CallLogEntity;
import com.valuestudio.contacts.entity.ContactEntity;

public class ContactsApplication extends Application {

	private List<ContactEntity> contactList = new ArrayList<ContactEntity>();

	public List<ContactEntity> getContactList() {
		return contactList;
	}

	public void setContactList(List<ContactEntity> contactList) {
		this.contactList = contactList;
	}

	private List<CallLogEntity> callLogList = new ArrayList<CallLogEntity>();

	public List<CallLogEntity> getCallLogList() {
		return callLogList;
	}

	public void setCallLogList(List<CallLogEntity> callLogList) {
		this.callLogList = callLogList;
	}

	@Override
	public void onCreate() {
		// CrashHandler crashHandler = CrashHandler.getInstance(true);
		// crashHandler.init(this, Constant.APP_SD_PATH +
		// Constant.LOG_FILE_DIR);
		// Constant.initSkinDir(this);
	}

}
