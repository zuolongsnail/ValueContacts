package com.valuestudio.contacts.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.MeizuUtil;

public class MoreActivity extends BaseActivity implements OnClickListener {
	private View buyAppLayout;
	// private View feedbackLayout;
	private View shareLayout;
	private View checkUpdateLayout;
	private View aboutUsLayout;
	private View ourProductLayout;

	public static MoreActivity newInstance() {
		return new MoreActivity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_activity);
		initViews();
		bindData();
	}

	public void initViews() {
		buyAppLayout = findViewById(R.id.buy_app_layout);
		buyAppLayout.setOnClickListener(this);
		// feedbackLayout = parent.findViewById(R.id.feedback_layout);
		// feedbackLayout.setOnClickListener(this);
		shareLayout = findViewById(R.id.share_layout);
		shareLayout.setOnClickListener(this);
		checkUpdateLayout = findViewById(R.id.check_update_layout);
		checkUpdateLayout.setOnClickListener(this);
		aboutUsLayout = findViewById(R.id.about_us_layout);
		aboutUsLayout.setOnClickListener(this);
		ourProductLayout = findViewById(R.id.our_product_layout);
		ourProductLayout.setOnClickListener(this);
		// supportUsLayout = findViewById(R.id.support_us_layout);
		// supportUsLayout.setOnClickListener(this);
	}

	public void bindData() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buy_app_layout:
			Uri appUri = null;
			if (ContactsUtil.isMeizuPhone()) {
				// Android版本为4.4以上（sdk版本大于等于19）
				if (Build.VERSION.SDK_INT >= 19) {
					// 跳转到市场
					appUri = Uri
							.parse("market://details?id=com.valuestudio.contacts");
				} else {
					// 跳转到软件中心
					appUri = Uri.parse(MeizuUtil.MSTORE_URI
							+ MeizuUtil.APPIDENTIFY);
				}
			} else {
				// 跳转到市场
				appUri = Uri
						.parse("market://details?id=com.valuestudio.contacts");
			}
			Intent intent = new Intent(Intent.ACTION_VIEW, appUri);
			startActivity(intent);
			break;
		// case R.id.feedback_layout:
		// break;
		case R.id.share_layout:
			// 分享软件
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT,
					getString(R.string.share_app));
			shareIntent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.share_content));
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(shareIntent,
					getString(R.string.share_title)));
			break;
		case R.id.check_update_layout:
			Intent checkUpdateIntent = new Intent(mContext,
					CheckUpdateActivity.class);
			startActivity(checkUpdateIntent);
			break;
		case R.id.about_us_layout:
			Intent aboutUsIntent = new Intent(mContext, AboutUsActivity.class);
			startActivity(aboutUsIntent);
			break;
		case R.id.our_product_layout:
			Intent ourProductIntent = new Intent(mContext,
					OurProductActivity.class);
			startActivity(ourProductIntent);
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
