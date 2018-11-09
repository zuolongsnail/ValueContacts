package com.valuestudio.contacts.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;

import com.valuestudio.contacts.base.ContactsApplication;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.Constant;

public class T9Service extends Service {
	private AsyncQueryHandler asyncQuery;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initSQL();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onRebind(Intent intent) {
	}

	public boolean onUnbind(Intent intent) {
		return true;
	}

	protected void initSQL() {
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		asyncQuery.startQuery(0, null, uri, Constant.PHONE_PROJECTION, null,
				null, "sort_key COLLATE LOCALIZED asc");
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {
		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			querying(cursor);
		}
	}

	private void querying(final Cursor cursor) {

		Handler handlerInsertOrder = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ContactsAsyncTask.QUERY_START_MESSAGE:
					break;
				case ContactsAsyncTask.QUERY_END_MESSAGE:
					Bundle bundle = msg.getData();
					ArrayList<ContactEntity> list = (ArrayList<ContactEntity>) bundle
							.get(Constant.QUERY_RESULT);
					ContactsApplication application = (ContactsApplication) getApplication();
					application.setContactList(list);
					// for(ContactInfo ci : list){
					// System.out.println(ci.getName());
					// System.out.println(ci.getPhoneNum());
					// System.out.println(ci.getFormattedNumber());
					// System.out.println(ci.getPinyin());
					// System.out.println("--------------------------------");
					// }
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		ContactsAsyncTask.startQueryContacts(T9Service.this,
				handlerInsertOrder, cursor);
	}

}
