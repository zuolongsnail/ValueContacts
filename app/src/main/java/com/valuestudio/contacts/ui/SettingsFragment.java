package com.valuestudio.contacts.ui;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.meizu.flyme.reflect.ActionBarProxy;
import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.ColorsListAdapter;
import com.valuestudio.contacts.base.BaseFragment;
import com.valuestudio.contacts.service.RelayDialerService;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.widget.WebViewWidget;

public class SettingsFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener {
	/**
	 * 标题背景
	 */
	private View titleLayout;
	private TextView titleView;
	private View mergeContactsLayout;
	private Switch mergeContactsSwitch;
	private View contactsOptionLayout;
	private View relayDialerLayout;
	private Switch relayDialerSwitch;
	private View spamCallLayout;
	private View callRecordLayout;
	private ImageView callRecordSettings;
	private View ipCallLayout;
	private TextView ipCallSummary;
	private View cleanCallLogLayout;
	private View themeSetLayout;
	private TextView themeSetSummary;
	private View colorsRingLayout;
	/**
	 * 皮肤设置
	 */
	private View skinSetLayout;
	private TextView skinSetSummary;
	/**
	 * 颜色名称数组
	 */
	private String[] themeSetArr;

	public static SettingsFragment newInstance() {
		return new SettingsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);
		initViews(rootView);
		bindData();
		return rootView;
	}

	@Override
	protected void initViews(View parent) {
		super.initViews(parent);
		titleLayout = parent.findViewById(R.id.title_layout);
		titleView = (TextView) parent.findViewById(R.id.title_view);
		mergeContactsLayout = parent.findViewById(R.id.merge_contacts_layout);
		mergeContactsLayout.setOnClickListener(this);
		mergeContactsSwitch = (Switch) parent
				.findViewById(R.id.merge_contacts_switch);
		mergeContactsSwitch.setOnCheckedChangeListener(this);
		// 初始化开关
		mergeContactsSwitch.setChecked(SharedPrefsUtil.getValue(getActivity(),
				Constant.MERGE_CONTACTS, false));
		contactsOptionLayout = parent
				.findViewById(R.id.contacts_operate_layout);
		contactsOptionLayout.setOnClickListener(this);
		relayDialerLayout = parent.findViewById(R.id.relay_dialer_layout);
		relayDialerLayout.setOnClickListener(this);
		relayDialerSwitch = (Switch) parent
				.findViewById(R.id.relay_dialer_switch);
		relayDialerSwitch.setOnCheckedChangeListener(this);
		// 初始化开关
		relayDialerSwitch.setChecked(SharedPrefsUtil.getValue(getActivity(),
				Constant.RELAY_DIALER, false));
		spamCallLayout = parent.findViewById(R.id.spam_call_layout);
		spamCallLayout.setOnClickListener(this);
		callRecordLayout = parent.findViewById(R.id.call_record_layout);
		callRecordLayout.setOnClickListener(this);
		callRecordSettings = (ImageView) parent
				.findViewById(R.id.call_record_settings);
		callRecordSettings.setOnClickListener(this);
		ipCallLayout = parent.findViewById(R.id.ip_call_layout);
		ipCallLayout.setOnClickListener(this);
		ipCallSummary = (TextView) parent.findViewById(R.id.ip_call_summary);
		// 显示前缀IP号码
		String ipCallNum = SharedPrefsUtil.getValue(getActivity(),
				Constant.IP_CALL_NUM, "");
		if (!TextUtils.isEmpty(ipCallNum)) {
			ipCallSummary
					.setText(getString(R.string.ip_call_summary, ipCallNum));
		} else {
			ipCallSummary.setText(getString(R.string.ip_call_summary,
					getString(R.string.ip_call)));
		}
		cleanCallLogLayout = parent.findViewById(R.id.clean_call_log_layout);
		cleanCallLogLayout.setOnClickListener(this);
		themeSetLayout = parent.findViewById(R.id.theme_set_layout);
		themeSetLayout.setOnClickListener(this);
		themeSetSummary = (TextView) parent
				.findViewById(R.id.theme_set_summary);
		colorsRingLayout = parent.findViewById(R.id.colors_ring_layout);
		colorsRingLayout.setOnClickListener(this);

		// skinSetLayout = parent.findViewById(R.id.skin_set_layout);
		// skinSetLayout.setOnClickListener(this);
		// skinSetSummary = (TextView)
		// parent.findViewById(R.id.skin_set_summary);
		// String currentSkin = SharedPrefsUtil.getValue(getActivity(),
		// Constant.SKIN_SETTINGS_KEY, "default");
		// if (currentSkin.equals(Constant.SKIN_DEFAULT)) {
		// skinSetSummary.setText(getActivity().getString(
		// R.string.skin_default));
		// } else if (currentSkin.equals(Constant.SKIN_HELLOKITTY)) {
		// skinSetSummary.setText(getActivity().getString(
		// R.string.skin_hellokitty));
		// }
	}

	/**
	 * 设置主题
	 * 
	 * @description
	 * @date 2014-8-19
	 * @author valuestudio
	 */
	public void themeSet() {
		TypedArray a = getActivity().obtainStyledAttributes(null,
				R.styleable.ContactsAppView, R.attr.ContactsAppStyle, 0);
		titleLayout.setBackgroundDrawable(getResources().getDrawable(
				a.getResourceId(R.styleable.ContactsAppView_themeColor,
						R.color.theme_black)));
		titleView.setTextColor(getResources().getColor(
				a.getResourceId(R.styleable.ContactsAppView_titleColor,
						R.color.white)));
		a.recycle();
	}

	protected void bindData() {
		themeSetArr = getActivity().getResources().getStringArray(
				R.array.theme_set_array);
		// 设置主题文字
		int themeSetValue = SharedPrefsUtil.getValue(getActivity(),
				Constant.THEME_SET, Constant.THEME_WHITE);
		themeSetSummary.setText(themeSetArr[themeSetValue]);

		themeSet();
	}

	@Override
	public void onResume() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				&& new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
						.resolveActivity(getActivity().getPackageManager()) != null) {// 5.0以后
			// 判断使用情况权限是否打开
			UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity()
					.getSystemService("usagestats");
			long time = System.currentTimeMillis();
			List<UsageStats> stats = mUsageStatsManager.queryUsageStats(
					UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
			if (stats == null || stats.size() <= 0) {
				relayDialerSwitch.setChecked(false);
			}
		}
		super.onResume();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.relay_dialer_switch:
			SharedPrefsUtil.putValue(getActivity(), Constant.RELAY_DIALER,
					isChecked);
			if (isChecked) {
				// 启动服务
				if (!ContactsUtil.checkServiceRunning(getActivity(),
						RelayDialerService.class.getName())) {
					Intent intent = new Intent(getActivity(),
							RelayDialerService.class);
					getActivity().startService(intent);
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
						&& new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
								.resolveActivity(getActivity()
										.getPackageManager()) != null) {// 5.0以后
					// 判断使用情况权限是否打开
					UsageStatsManager mUsageStatsManager = (UsageStatsManager) getActivity()
							.getSystemService("usagestats");
					long time = System.currentTimeMillis();
					List<UsageStats> stats = mUsageStatsManager
							.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
									time - 1000 * 10, time);
					if (stats == null || stats.size() <= 0) {
						// 打开使用情况权限页面
						Intent usageStatsIntent = new Intent(
								Settings.ACTION_USAGE_ACCESS_SETTINGS);
						usageStatsIntent
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(usageStatsIntent);
						Toast.makeText(getActivity(), "接管拨号请开启查看使用情况权限开关",
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				// 关闭服务
				if (ContactsUtil.checkServiceRunning(getActivity(),
						RelayDialerService.class.getName())) {
					Intent intent = new Intent(getActivity(),
							RelayDialerService.class);
					getActivity().stopService(intent);
				}
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.merge_contacts_layout:
			boolean mergeContacts = SharedPrefsUtil.getValue(getActivity(),
					Constant.MERGE_CONTACTS, false);
			if (!mergeContacts) {
				mergeContactsSwitch.setChecked(true);
				SharedPrefsUtil.putValue(getActivity(),
						Constant.MERGE_CONTACTS, true);
			} else {
				mergeContactsSwitch.setChecked(false);
				SharedPrefsUtil.putValue(getActivity(),
						Constant.MERGE_CONTACTS, false);
			}
			ContactsUtil.restartApplication(getActivity());
			break;
		case R.id.contacts_operate_layout:
			Intent intent = new Intent(getActivity(),
					ContactsOptionActivity.class);
			startActivity(intent);
			break;
		case R.id.relay_dialer_layout:
			boolean relayDialer = SharedPrefsUtil.getValue(getActivity(),
					Constant.RELAY_DIALER, false);
			if (!relayDialer) {
				relayDialerSwitch.setChecked(true);
			} else {
				relayDialerSwitch.setChecked(false);
			}
			break;
		case R.id.spam_call_layout:
			try {
				// Android版本为4.4以下（sdk版本小于19）
				ComponentName component = new ComponentName(
						"com.android.contacts",
						"com.meizu.contacts.SpamPreferenceActivity");
				Intent spamIntent = new Intent(Intent.ACTION_VIEW);
				spamIntent.setComponent(component);
				startActivity(spamIntent);
			} catch (Exception e) {
				e.printStackTrace();
				if (ContactsUtil.checkPackage(getActivity(),
						"com.aliyun.SecurityCenter")) {// 适配yunos
					ComponentName component = new ComponentName(
							"com.aliyun.SecurityCenter",
							"com.aliyun.SecurityCenter.flyme.FlymeBlockServiceMainActivity");
					Intent securityCenterIntent = new Intent(Intent.ACTION_VIEW);
					securityCenterIntent.setComponent(component);
					startActivity(securityCenterIntent);
					return;
				} else if (ContactsUtil.checkPackage(getActivity(),
						"com.meizu.blockservice")) {
					ComponentName component = new ComponentName(
							"com.meizu.blockservice",
							"com.meizu.blockService.BlockPreference");
					Intent securityCenterIntent = new Intent(Intent.ACTION_VIEW);
					securityCenterIntent.setComponent(component);
					startActivity(securityCenterIntent);
					return;
				} else if (ContactsUtil.checkPackage(getActivity(),
						"com.meizu.safe")) {
					ComponentName component = new ComponentName(
							"com.meizu.safe",
							"com.meizu.safe.blockService.blockui.BlockServiceMainActivity");
					Intent securityCenterIntent = new Intent(Intent.ACTION_VIEW);
					securityCenterIntent.setComponent(component);
					startActivity(securityCenterIntent);
					return;
				}
				Toast.makeText(getActivity(), "暂不支持此固件", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.call_record_layout:
			// 适配flyme5
			if (ContactsUtil.checkPackage(getActivity(),
					"com.meizu.callsetting")) {
				ComponentName component = new ComponentName(
						"com.meizu.callsetting",
						"com.meizu.callsetting.soundrecorder.ActivityBase");
				Intent callsettingIntent = new Intent(Intent.ACTION_VIEW);
				callsettingIntent.setComponent(component);
				startActivity(callsettingIntent);
				return;
			}
			try {
				ComponentName component = new ComponentName(
						"com.android.soundrecorder",
						"com.android.soundrecorder.CallRecordActivity");
				Intent callRecordIntent = new Intent(Intent.ACTION_VIEW);
				callRecordIntent.setComponent(component);
				startActivity(callRecordIntent);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "暂不支持此固件", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.call_record_settings:
			// 适配flyme5
			if (ContactsUtil.checkPackage(getActivity(),
					"com.meizu.callsetting")) {
				ComponentName component = new ComponentName(
						"com.meizu.callsetting",
						"com.meizu.callsetting.AutoRecordActivity");
				Intent callsettingIntent = new Intent(Intent.ACTION_VIEW);
				callsettingIntent.setComponent(component);
				startActivity(callsettingIntent);
				return;
			}
			try {
				// Android版本为5.0以上（sdk版本大于等于21）
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					ComponentName component = new ComponentName(
							"com.android.phone",
							"com.meizu.phone.AutoRecordActivity");
					Intent spamIntent = new Intent(Intent.ACTION_VIEW);
					spamIntent.setComponent(component);
					startActivity(spamIntent);
				} else {
					ComponentName component = new ComponentName(
							"com.android.phone",
							"com.android.phone.AutoRecordActivity");
					Intent spamIntent = new Intent(Intent.ACTION_VIEW);
					spamIntent.setComponent(component);
					startActivity(spamIntent);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "暂不支持此固件", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.ip_call_layout:
			final EditText editText = new EditText(getActivity());
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
			editText.setHint(R.string.input_ip_call);
			String ipCallNum = SharedPrefsUtil.getValue(getActivity(),
					Constant.IP_CALL_NUM, "");
			if (!TextUtils.isEmpty(ipCallNum)) {
				editText.setText(ipCallNum);
				editText.setSelection(editText.getText().toString().length());
			}
			AlertDialog.Builder ipCallDialog = new AlertDialog.Builder(
					getActivity());
			ipCallDialog
					.setIcon(R.drawable.ic_popup_about)
					.setView(editText)
					.setTitle(getString(R.string.ip_call_title))
					.setNegativeButton(R.string.cancel_text, null)
					.setPositiveButton(R.string.ok_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String ipCallNum = editText.getText()
											.toString();
									if (!TextUtils.isEmpty(ipCallNum)) {
										SharedPrefsUtil
												.putValue(getActivity(),
														Constant.IP_CALL_NUM,
														ipCallNum);
										// 显示前缀IP号码
										ipCallSummary.setText(getString(
												R.string.ip_call_summary,
												ipCallNum));
									} else {
										SharedPrefsUtil.putValue(getActivity(),
												Constant.IP_CALL_NUM, "");
										ipCallSummary.setText(getString(
												R.string.ip_call_summary,
												getString(R.string.ip_call)));
									}
								}
							}).create();
			ipCallDialog.show();
			break;
		case R.id.clean_call_log_layout:
			AlertDialog.Builder deleteRemindDialog = new AlertDialog.Builder(
					getActivity());
			deleteRemindDialog
					.setIcon(R.drawable.ic_popup_delete)
					.setMessage(getString(R.string.clean_call_log_remind))
					.setNegativeButton(R.string.cancel_text, null)
					.setPositiveButton(R.string.ok_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									cleanCallLog();
									// 清除所有通话记录后调用拨号盘页面重新加载通话记录
									((ContactsFragmentActivity) getActivity())
											.loadCallLog();
								}
							}).create();
			deleteRemindDialog.show();
			break;
		case R.id.theme_set_layout:
			final int themeSetValue = SharedPrefsUtil.getValue(getActivity(),
					Constant.THEME_SET, Constant.THEME_WHITE);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.theme_set_title);
			Integer[] res = new Integer[] { R.drawable.white_round,
					R.drawable.black_round, R.drawable.blue_round,
					R.drawable.purple_round, R.drawable.green_round,
					R.drawable.yellow_round, R.drawable.red_round,
					R.drawable.gold_round };
			List<Integer> list = Arrays.asList(res);
			ColorsListAdapter adapter = new ColorsListAdapter(getActivity(),
					list);
			adapter.setCheckItem(themeSetValue);
			GridView gridView = (GridView) LayoutInflater.from(getActivity())
					.inflate(R.layout.colors_panel_layout, null);
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gridView.setCacheColorHint(0);
			gridView.setAdapter(adapter);
			builder.setView(gridView);
			final AlertDialog dialog = builder.show();
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dialog.dismiss();
					if (themeSetValue != position) {
						// 确定以后设置SmartBar
						SharedPrefsUtil.putValue(getActivity(),
								Constant.THEME_SET, position);
						ContactsUtil.restartApplication(getActivity());
						Toast.makeText(getActivity(),
								R.string.theme_set_success, Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
			break;
		case R.id.colors_ring_layout:
			Intent colorsRingIntent = new Intent(getActivity(),
					WebViewWidget.class);
			startActivity(colorsRingIntent);
			break;
		// case R.id.skin_set_layout:
		// Intent skinIntent = new Intent(getActivity(), SkinSetActivity.class);
		// startActivity(skinIntent);
		// break;
		default:
			break;
		}
	}

	private void cleanCallLog() {
		ContentResolver resolver = getActivity().getContentResolver();
		resolver.delete(CallLog.Calls.CONTENT_URI, null, null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!ActionBarProxy.hasSmartBar()) {
			return;
		}
		contactsItem = menu.add(0, CONTACTS_ID, 0, R.string.contacts);
		contactsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		contactsItem.setIcon(sbContactsIconResId);

		dialerItem = menu.add(0, DIALER_ID, 0, R.string.dialer);
		dialerItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		dialerItem.setIcon(sbDialIconResId);

		settingsItem = menu.add(0, SETTINGS_ID, 0, R.string.settings);
		settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		settingsItem.setIcon(sbSettingsPressIconResId);

		moreItem = menu.add(0, MORE_ID, 0, R.string.more);
		moreItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		moreItem.setIcon(sbMoreIconResId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTACTS_ID:
			Activity contactsAct = getActivity();
			if (contactsAct != null) {
				((ContactsFragmentActivity) contactsAct)
						.setCurrentItem(CONTACTS_ID);
			}
			break;
		case DIALER_ID:
			Activity dialerAct = getActivity();
			if (dialerAct != null) {
				((ContactsFragmentActivity) dialerAct)
						.setCurrentItem(DIALER_ID);
			}
			break;
		case MORE_ID:
			Intent intent = new Intent(getActivity(), MoreActivity.class);
			startActivity(intent);
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return true;
	}

}