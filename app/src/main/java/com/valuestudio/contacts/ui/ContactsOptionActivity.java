package com.valuestudio.contacts.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;

public class ContactsOptionActivity extends BaseActivity implements
		OnClickListener {

	private View phoneContactsLayout;
	private View simContactsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_option_activity);
		initViews();
		bindData();
	}

	@Override
	public void initViews() {
		super.initViews();
		phoneContactsLayout = findViewById(R.id.phone_contacts_layout);
		phoneContactsLayout.setOnClickListener(this);
		simContactsLayout = findViewById(R.id.sim_contacts_layout);
		simContactsLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.phone_contacts_layout:
			Intent phoneContactsIntent = new Intent(mContext,
					PhoneContactActivity.class);
			startActivity(phoneContactsIntent);
			break;
		case R.id.sim_contacts_layout:
			Intent simContactsIntent = new Intent(mContext,
					SimContactActivity.class);
			startActivity(simContactsIntent);
			break;
		}
	}

	@Override
	protected void onSlideRight() {
		super.onSlideRight();
		onBackPressed();
	}

}
