package com.valuestudio.contacts.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.utils.ZipUtil;

/**
 * 皮肤设置
 * 
 * @description
 * @date 2014-2-21
 * @author valuestudio
 */
public class SkinSetActivity extends BaseActivity implements OnClickListener {
	private Button defaultStartBtn;
	private Button hellokittyStartBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skin_set_activity);
		initViews();

	}

	public void initViews() {
		super.initViews();
		defaultStartBtn = (Button) findViewById(R.id.defaul_start);
		defaultStartBtn.setOnClickListener(this);
		hellokittyStartBtn = (Button) findViewById(R.id.hellokitty_start);
		hellokittyStartBtn.setOnClickListener(this);
		String currentSkin = SharedPrefsUtil.getValue(this,
				Constant.SKIN_SETTINGS_KEY, "default");
		if (currentSkin.equals(Constant.SKIN_DEFAULT)) {
			defaultStartBtn.setEnabled(false);
			defaultStartBtn.setText(getString(R.string.skin_using));
		} else if (currentSkin.equals(Constant.SKIN_HELLOKITTY)) {
			hellokittyStartBtn.setEnabled(false);
			hellokittyStartBtn.setText(getString(R.string.skin_using));
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.defaul_start:
			SharedPrefsUtil.putValue(this, Constant.SKIN_SETTINGS_KEY,
					"default");
			SharedPrefsUtil.putValue(this, Constant.THEME_SET,
					Constant.THEME_WHITE);
			restartApplication();
			break;
		case R.id.hellokitty_start:
			ZipUtil.unZip(getResources().openRawResource(R.raw.hellokitty),
					Constant.SKIN_DIR + "hellokitty.zip", Constant.SKIN_DIR);
			SharedPrefsUtil.putValue(this, Constant.SKIN_SETTINGS_KEY,
					"hellokitty");
			SharedPrefsUtil.putValue(this, Constant.THEME_SET,
					Constant.THEME_HELLOKITTY);
			restartApplication();
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

	/**
	 * 重启应用
	 * 
	 * @description
	 * @date 2014-8-20
	 * @author valuestudio
	 */
	private void restartApplication() {
		final Intent intent = this.getPackageManager()
				.getLaunchIntentForPackage(getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}