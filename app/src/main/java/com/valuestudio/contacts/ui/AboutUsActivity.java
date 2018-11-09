package com.valuestudio.contacts.ui;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.utils.ContactsUtil;

public class AboutUsActivity extends BaseActivity {
	/**
	 * 新浪微博包名
	 */
	private static final String WEIBO_PACKAGE_NAME = "com.sina.weibo";
	/**
	 * 腾讯QQ包名
	 */
	private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);
	}

	@Override
	protected void onSlideRight() {
		super.onSlideRight();
		onBackPressed();
	}
}
