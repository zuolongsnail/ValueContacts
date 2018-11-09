package com.valuestudio.contacts.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.MeizuUtil;

/**
 * 我们的作品
 * 
 * @description
 * @date 2014-2-21
 * @author valuestudio
 */
public class OurProductActivity extends BaseActivity implements OnClickListener {
	private View randtoneLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.our_product_activity);
		initViews();

	}

	public void initViews() {
		super.initViews();
		randtoneLayout = findViewById(R.id.randtone_layout);
		randtoneLayout.setOnClickListener(this);
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.randtone_layout:
			Uri randtoneUri = null;
			if (ContactsUtil.isMeizuPhone()) {
				// 跳转到软件中心
				randtoneUri = Uri.parse(MeizuUtil.MSTORE_URI
						+ MeizuUtil.RANDTONE_APPIDENTIFY);
			} else {
				// 跳转到市场
				randtoneUri = Uri
						.parse("market://details?id=com.snailstudio.randtone");
			}
			Intent randtoneIntent = new Intent(Intent.ACTION_VIEW, randtoneUri);
			startActivity(randtoneIntent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onSlideRight() {
		super.onSlideRight();
		onBackPressed();
	}

}