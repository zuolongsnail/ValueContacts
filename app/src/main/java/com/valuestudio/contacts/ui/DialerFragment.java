package com.valuestudio.contacts.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meizu.flyme.reflect.ActionBarProxy;
import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.AddActionAdapter;
import com.valuestudio.contacts.adapter.CallLogListAdapter;
import com.valuestudio.contacts.adapter.ContactListAdapter;
import com.valuestudio.contacts.base.BaseFragment;
import com.valuestudio.contacts.base.ContactsApplication;
import com.valuestudio.contacts.base.IAdapterCallback;
import com.valuestudio.contacts.entity.CallLogEntity;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.service.CallLogThread;
import com.valuestudio.contacts.service.ContactsAsyncTask;
import com.valuestudio.contacts.service.LoadContactsThread;
import com.valuestudio.contacts.service.LoadPhoneThread;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.utils.SkinManager;

public class DialerFragment extends BaseFragment implements
		OnLongClickListener, IAdapterCallback {
	/** Tone音的长度，单位：milliseconds */
	private static final int TONE_LENGTH_MS = 150;
	/** 主音量的比例：以80%的主音量播放Tone音 */
	private static final int TONE_RELATIVE_VOLUME = 50;
	/** 主音量的音频种别 */
	private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	/**
	 * Tone音播放器
	 */
	private ToneGenerator mToneGenerator;
	/**
	 * Tone相关的同步锁
	 */
	private Object mToneGeneratorLock = new Object();
	/**
	 * 设定中的Tone音播放设置
	 */
	private boolean mDTMFToneEnabled;
	private ContactsApplication application;
	/**
	 * 通话记录列表
	 */
	private ListView callLogListView;
	private CallLogListAdapter callLogListAdapter;
	/**
	 * 暂无通话记录
	 */
	protected TextView noCallLogView;
	/**
	 * 联系人列表
	 */
	private ListView contactListView;
	private ContactListAdapter contactListAdapter;
	/**
	 * 操作布局
	 */
	private View operateLayout;
	/**
	 * 发送短信
	 */
	private View sendSmsView;
	/**
	 * 加至新联系人
	 */
	private View addContactView;
	/**
	 * 加至已有联系人
	 */
	private View addToExistView;
	/**
	 * 在线查询号码
	 */
	private View searchNumView;
	/**
	 * 拨号盘
	 */
	private View dialpadView;
	private Animation visibleAnima;
	private Animation goneAnima;
	private View dialpadOperateView;
	/**
	 * 标题背景
	 */
	private View titleLayout;
	/**
	 * 数字输入框
	 */
	private EditText numInputView;
	private AudioManager audioManager = null;
	private ImageButton dialNum1;
	private ImageButton dialNum2;
	private ImageButton dialNum3;
	private ImageButton dialNum4;
	private ImageButton dialNum5;
	private ImageButton dialNum6;
	private ImageButton dialNum7;
	private ImageButton dialNum8;
	private ImageButton dialNum9;
	private ImageButton dialNumStar;
	private ImageButton dialNum0;
	private ImageButton dialNumPound;
	private ImageButton dialAddContacts;
	private ImageButton dialCall;
	private ImageButton dialDelete;
	/**
	 * 选择操作
	 */
	private AddActionAdapter addActionAdapter;
	/**
	 * 选择操作的图片
	 */
	private int[] mImageIds = { R.drawable.ic_add_contact,
			R.drawable.ic_edit_dark };

	public static DialerFragment newInstance() {
		return new DialerFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dialer, container,
				false);
		application = (ContactsApplication) getActivity().getApplication();
		initViews(rootView);
		bindData();
		return rootView;
	}

	@Override
	protected void initViews(View parent) {
		super.initViews(parent);
		dialpadView = parent.findViewById(R.id.dialpad_view);
		dialpadOperateView = parent.findViewById(R.id.dial_operate_layout);

		titleLayout = parent.findViewById(R.id.title_layout);
		numInputView = (EditText) parent.findViewById(R.id.number_input_view);
		// 显示光标时禁止弹出输入法
		disableShowSoftInput(numInputView);
		numInputView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					// 按住事件发生后执行代码的区域
					numInputView.setCursorVisible(true);
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					// 移动事件发生后执行代码的区域
					break;
				}
				case MotionEvent.ACTION_UP: {
					// 松开事件发生后执行代码的区域
					break;
				}
				default:
					break;
				}
				return false;
			}
		});
		numInputView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					if (getDialpadVisible()) {
						dialAddContacts.setEnabled(false);
					}
					dialDelete.setEnabled(false);
					if (application.getCallLogList().size() > 0) {
						noCallLogView.setVisibility(View.GONE);
						callLogListView.setVisibility(View.VISIBLE);
						contactListView.setVisibility(View.GONE);
						operateLayout.setVisibility(View.GONE);
					} else {
						noCallLogView.setVisibility(View.VISIBLE);
						callLogListView.setVisibility(View.GONE);
						contactListView.setVisibility(View.GONE);
						operateLayout.setVisibility(View.GONE);
					}
				} else {
					if (getDialpadVisible()) {
						dialAddContacts.setEnabled(true);
					}
					dialDelete.setEnabled(true);
					if (application.getContactList().size() <= 0) {
						if (application.getCallLogList().size() > 0) {
							noCallLogView.setVisibility(View.GONE);
							callLogListView.setVisibility(View.GONE);
							contactListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.VISIBLE);
						} else {
							noCallLogView.setVisibility(View.GONE);
							callLogListView.setVisibility(View.GONE);
							contactListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.VISIBLE);
						}
					} else {
						contactListAdapter.getFilter().filter(s);
					}
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		themeSet();

		dialNum1 = (ImageButton) parent.findViewById(R.id.dial_num_1);
		dialNum1.setOnClickListener(this);
		dialNum1.setOnLongClickListener(this);
		dialNum2 = (ImageButton) parent.findViewById(R.id.dial_num_2);
		dialNum2.setOnClickListener(this);
		dialNum3 = (ImageButton) parent.findViewById(R.id.dial_num_3);
		dialNum3.setOnClickListener(this);
		dialNum4 = (ImageButton) parent.findViewById(R.id.dial_num_4);
		dialNum4.setOnClickListener(this);
		dialNum5 = (ImageButton) parent.findViewById(R.id.dial_num_5);
		dialNum5.setOnClickListener(this);
		dialNum6 = (ImageButton) parent.findViewById(R.id.dial_num_6);
		dialNum6.setOnClickListener(this);
		dialNum7 = (ImageButton) parent.findViewById(R.id.dial_num_7);
		dialNum7.setOnClickListener(this);
		dialNum8 = (ImageButton) parent.findViewById(R.id.dial_num_8);
		dialNum8.setOnClickListener(this);
		dialNum9 = (ImageButton) parent.findViewById(R.id.dial_num_9);
		dialNum9.setOnClickListener(this);
		dialNumStar = (ImageButton) parent.findViewById(R.id.dial_num_star);
		dialNumStar.setOnClickListener(this);
		dialNumStar.setOnLongClickListener(this);
		dialNum0 = (ImageButton) parent.findViewById(R.id.dial_num_0);
		dialNum0.setOnClickListener(this);
		dialNum0.setOnLongClickListener(this);
		dialNumPound = (ImageButton) parent.findViewById(R.id.dial_num_pound);
		dialNumPound.setOnClickListener(this);
		dialNumPound.setOnLongClickListener(this);
		dialAddContacts = (ImageButton) parent
				.findViewById(R.id.dial_add_contacts);
		dialAddContacts.setEnabled(false);
		dialAddContacts.setOnClickListener(this);
		dialCall = (ImageButton) parent.findViewById(R.id.dial_call);
		// dialCall.setEnabled(false);
		dialCall.setOnClickListener(this);
		dialCall.setOnLongClickListener(this);
		dialDelete = (ImageButton) parent.findViewById(R.id.dial_delete);
		dialDelete.setEnabled(false);
		dialDelete.setOnClickListener(this);
		dialDelete.setOnLongClickListener(this);
		visibleAnima = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_visible);
		goneAnima = AnimationUtils.loadAnimation(getActivity(),
				R.anim.push_gone);
		goneAnima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				dialpadView.setVisibility(View.GONE);
			}
		});

		callLogListView = (ListView) parent.findViewById(R.id.call_log_list);
		callLogListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					if (getDialpadVisible()) {
						setDialpadVisible(false);
						if (!ActionBarProxy.hasSmartBar()) {
							Activity contactsAct = getActivity();
							if (contactsAct != null) {
								((ContactsFragmentActivity) contactsAct)
										.setDialerImg(sbDialDisplayPressIconResId);
							}
						} else {
							dialerItem.setIcon(sbDialDisplayPressIconResId);
						}
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		callLogListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				long contactId = application.getCallLogList().get(position)
						.getContactId();
				if (contactId != 0) {
					Intent showCallLog = new Intent();
					showCallLog.setAction(Intent.ACTION_VIEW);
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactId);
					showCallLog.setData(uri);
					startActivityForResult(showCallLog,
							Constant.CONTACTS_CHANGED_REQ_CODE);
				} else {
					Intent showCallLog = new Intent();
					showCallLog.putExtra("number", application.getCallLogList()
							.get(position).getNumber());
					showCallLog.putExtra("name", application.getCallLogList()
							.get(position).getName());
					showCallLog.putExtra("type", application.getCallLogList()
							.get(position).getType());
					showCallLog.putExtra("location", application
							.getCallLogList().get(position).getLocation());
					showCallLog.setClass(getActivity(),
							CallDetailActivity.class);
					startActivityForResult(showCallLog,
							Constant.CONTACTS_CHANGED_REQ_CODE);
				}
			}
		});
		noCallLogView = (TextView) parent.findViewById(R.id.no_call_log);
		noCallLogView.setOnClickListener(this);
		contactListView = (ListView) parent.findViewById(R.id.contact_list);
		contactListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					if (getDialpadVisible()) {
						setDialpadVisible(false);
						if (!ActionBarProxy.hasSmartBar()) {
							Activity contactsAct = getActivity();
							if (contactsAct != null) {
								((ContactsFragmentActivity) contactsAct)
										.setDialerImg(sbDialDisplayPressIconResId);
							}
						} else {
							dialerItem.setIcon(sbDialDisplayPressIconResId);
						}
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		contactListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击列表打开指定联系人界面
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.withAppendedPath(
								ContactsContract.Contacts.CONTENT_URI,
								String.valueOf(contactListAdapter
										.getContactList().get(position)
										.getContactId())));
				startActivityForResult(intent,
						Constant.CONTACTS_CHANGED_REQ_CODE);
			}
		});

		operateLayout = parent.findViewById(R.id.operate_layout);
		sendSmsView = parent.findViewById(R.id.send_sms_layout);
		sendSmsView.setOnClickListener(this);
		addContactView = parent.findViewById(R.id.add_contact_layout);
		addContactView.setOnClickListener(this);
		addToExistView = parent.findViewById(R.id.add_to_exist_layout);
		addToExistView.setOnClickListener(this);
		searchNumView = parent.findViewById(R.id.search_num_layout);
		searchNumView.setOnClickListener(this);
		// 强制隐藏输入法
		((InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				numInputView.getWindowToken(), 0);

		// skin set
		if (!SharedPrefsUtil.getValue(getActivity(),
				Constant.SKIN_SETTINGS_KEY, "default").equals("default")) {
			dialpadView.setBackgroundDrawable(SkinManager.createDrawableByPath(
					getActivity(), Constant.SKIN_DIR
							+ "dial_num_background.png"));
			dialpadOperateView.setBackgroundDrawable(SkinManager
					.createDrawableByPath(getActivity(), Constant.SKIN_DIR
							+ "dial_operate_background.png"));
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dial_num_1:
			playTone(1);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_2:
			playTone(2);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_3:
			playTone(3);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_4:
			playTone(4);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_5:
			playTone(5);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_6:
			playTone(6);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_7:
			playTone(7);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_8:
			playTone(8);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_9:
			playTone(9);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_star:
			playTone(10);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_0:
			playTone(0);
			input(v.getTag().toString());
			break;
		case R.id.dial_num_pound:
			playTone(11);
			input(v.getTag().toString());
			break;
		case R.id.dial_add_contacts:// 添加联系人
			final String numberAdd = numInputView.getText().toString();
			if (!TextUtils.isEmpty(numberAdd)) {
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.select_action);
				builder.setAdapter(addActionAdapter,
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:// 新联系人
									Intent intentAddContact = new Intent(
											Intent.ACTION_INSERT);
									intentAddContact
											.setType(ContactsContract.RawContacts.CONTENT_TYPE);
									intentAddContact
											.putExtra(
													ContactsContract.Intents.Insert.PHONE,
													numberAdd);
									startActivityForResult(intentAddContact,
											Constant.CONTACTS_CHANGED_REQ_CODE);
									break;
								case 1:// 已有联系人
									Intent intentAddToExist = new Intent(
											Intent.ACTION_INSERT_OR_EDIT);
									intentAddToExist
											.setType(Contacts.CONTENT_ITEM_TYPE);
									intentAddToExist
											.putExtra(
													ContactsContract.Intents.Insert.PHONE,
													numberAdd);
									startActivityForResult(intentAddToExist,
											Constant.CONTACTS_CHANGED_REQ_CODE);
									break;
								}
							}
						});
				builder.create().show();
			}
			break;
		case R.id.dial_call:
			String number = numInputView.getText().toString();
			if (number.length() > 0) {
				call(number);
			} else {
				// 拨号盘无号码时点击拨号按钮显示上一次拨出去的号码
				number = CallLog.Calls.getLastOutgoingCall(getActivity());
				if (!TextUtils.isEmpty(number)) {
					input(number);
				}
			}
			break;
		case R.id.dial_delete:
			delete();
			break;
		case R.id.send_sms_layout:
			String numberSms = numInputView.getText().toString();
			Uri msgUri = Uri.parse("smsto:" + numberSms);
			Intent intentMsg = new Intent(Intent.ACTION_SENDTO, msgUri);
			startActivity(intentMsg);
			break;
		case R.id.add_contact_layout:
			String numberAddContact = numInputView.getText().toString();
			Intent intentAddContact = new Intent(Intent.ACTION_INSERT);
			intentAddContact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
			intentAddContact.putExtra(ContactsContract.Intents.Insert.PHONE,
					numberAddContact);
			startActivityForResult(intentAddContact,
					Constant.CONTACTS_CHANGED_REQ_CODE);
			break;
		case R.id.add_to_exist_layout:
			String numberAddToExist = numInputView.getText().toString();
			Intent intentAddToExist = new Intent(Intent.ACTION_INSERT_OR_EDIT);
			intentAddToExist.setType(Contacts.CONTENT_ITEM_TYPE);
			intentAddToExist.putExtra(ContactsContract.Intents.Insert.PHONE,
					numberAddToExist);
			startActivityForResult(intentAddToExist,
					Constant.CONTACTS_CHANGED_REQ_CODE);
			break;
		case R.id.search_num_layout:
			String numberSearch = numInputView.getText().toString();
			Intent intentSearch = new Intent(getActivity(),
					SearchNumActivity.class);
			intentSearch.putExtra(SearchNumActivity.KEYWORD, numberSearch);
			startActivity(intentSearch);
			break;
		case R.id.no_call_log:
			loadCallLog();
			loadContacts();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取手机通讯录联系人
	 * 
	 * @description
	 * @date 2014-1-11
	 * @author valuestudio
	 */
	protected void loadContacts() {
		// 判断是否合并联系人
		if (SharedPrefsUtil.getValue(getActivity(), Constant.MERGE_CONTACTS,
				false)) {
			new Thread(new LoadContactsThread(getActivity(),
					mHandlerQueryContacts)).start();
		} else {
			new Thread(
					new LoadPhoneThread(getActivity(), mHandlerQueryContacts))
					.start();
		}
	}

	Handler mHandlerQueryContacts = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ContactsAsyncTask.QUERY_START_MESSAGE:
				break;
			case ContactsAsyncTask.QUERY_END_MESSAGE:
				Bundle bundle = msg.getData();
				ArrayList<ContactEntity> contactList = (ArrayList<ContactEntity>) bundle
						.get(Constant.QUERY_RESULT);
				if (contactList.size() > 0) {
					application.setContactList(contactList);
					contactListAdapter.setAllContactList(contactList);
					if (!TextUtils.isEmpty(numInputView.getText().toString())) {
						contactListAdapter.getFilter().filter(
								numInputView.getText().toString());
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.dial_num_1:
			String p = numInputView.getText().toString();
			if (TextUtils.isEmpty(p)) {
				Uri uri = Uri.parse("voicemail:");
				Intent intent = new Intent(Intent.ACTION_CALL, uri);
				startActivity(intent);
			}
			return false;
		case R.id.dial_num_star:
			playTone(10);
			input(",");
			break;
		case R.id.dial_num_0:
			playTone(0);
			input("+");
			break;
		case R.id.dial_num_pound:
			playTone(11);
			input(";");
			break;
		case R.id.dial_delete:
			numInputView.setText("");
			resetDialpad();
			break;
		case R.id.dial_call:
			// 长按ip拨号
			String ipCallNum = SharedPrefsUtil.getValue(getActivity(),
					Constant.IP_CALL_NUM, "");
			if (!TextUtils.isEmpty(ipCallNum)) {
				String number = numInputView.getText().toString();
				if (number.length() > 0) {
					Toast.makeText(getActivity(), R.string.call_ip_num,
							Toast.LENGTH_SHORT).show();
					call(ipCallNum + number);
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	void playTone(int tone) {
		// 设定中没有选中的话，就不播
		if (!mDTMFToneEnabled) {
			return;
		}
		// 静音模式的时候也不播，需要每次都检查，因为没有Activity切换也能设成静音模式
		// 设定中的那个就不需要，因为要设定必须要先切入设定Activity才行
		int ringerMode = audioManager.getRingerMode();
		if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
				|| (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
			return;
		}
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}
			// Start the new tone (will stop anyplaying tone)
			mToneGenerator.startTone(tone, TONE_LENGTH_MS);
		}
	}

	/**
	 * 输入数字
	 * 
	 * @description
	 * @param str
	 * @date 2014-8-2
	 * @author valuestudio
	 */
	private void input(String str) {
		// 输入框当前字符
		String p = numInputView.getText().toString();
		// 光标开始位置
		int start = numInputView.getSelectionStart();
		// 光标结束位置
		int end = numInputView.getSelectionEnd();
		// 设置输入框中字符的显示
		StringBuffer buffer = new StringBuffer(p);
		numInputView.setText(buffer.replace(start, end, str));
		// 数字超过屏幕时显示最后的
		numInputView.setSelection(start + 1);
		// 有数字时设置按钮可用
		// dialCall.setEnabled(true);

		// 显示IMEI
		if ("*#06#".equals(numInputView.getText().toString())) {
			// get imei
			TelephonyManager tpManager = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tpManager.getDeviceId();
			if (TextUtils.isEmpty(imei)) {
				imei = "null";
			}
			AlertDialog.Builder imeiDialog = new AlertDialog.Builder(
					getActivity());
			imeiDialog.setTitle("IMEI");
			imeiDialog.setMessage(imei);
			imeiDialog.setCancelable(false);
			imeiDialog.setIcon(R.drawable.ic_popup_about);
			imeiDialog.setPositiveButton(R.string.ok_text,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							resetDialpad();
						}
					}).create();
			imeiDialog.show();
		}
	}

	/**
	 * 删除数字
	 * 
	 * @description
	 * @date 2014-8-2
	 * @author valuestudio
	 */
	private void delete() {
		String p = numInputView.getText().toString();
		// 光标开始位置
		int start = numInputView.getSelectionStart();
		// 光标结束位置
		int end = numInputView.getSelectionEnd();
		if (end > 0) {
			// 设置输入框中字符的显示
			StringBuffer buffer = new StringBuffer(p);
			if (start == end) {
				numInputView.setText(buffer.replace(start - 1, end, ""));
				// 数字超过屏幕时显示最后的
				numInputView.setSelection(start - 1);
			} else {
				numInputView.setText(buffer.replace(start, end, ""));
				// 数字超过屏幕时显示最后的
				numInputView.setSelection(start);
			}
			// numInputView.setText(p.substring(0, p.length() - 1));
		}
		// 没有数字时设置按钮不可用
		p = numInputView.getText().toString();
		if (p.length() <= 0) {
			numInputView.setCursorVisible(false);
			// dialCall.setEnabled(false);
		}
	}

	/**
	 * 重置拨号盘
	 * 
	 * @description
	 * @date 2014-8-2
	 * @author valuestudio
	 */
	private void resetDialpad() {
		numInputView.setText("");
		// dialCall.setEnabled(false);
	}

	/**
	 * 拨打电话
	 * 
	 * @description
	 * @param phoneNumber
	 * @date 2014-8-1
	 * @author valuestudio
	 */
	private void call(String phoneNumber) {
		ContactsUtil.call(getActivity(), phoneNumber);
		resetDialpad();
	}

	protected void bindData() {
		audioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);

		callLogListAdapter = new CallLogListAdapter(getActivity(),
				application.getCallLogList());
		callLogListView.setAdapter(callLogListAdapter);

		contactListAdapter = new ContactListAdapter(getActivity(),
				application.getContactList());
		contactListAdapter.setAdapterCallback(this);
		contactListView.setAdapter(contactListAdapter);
		contactListView.setTextFilterEnabled(true);

		addActionAdapter = new AddActionAdapter(getActivity(), mImageIds);
	}

	@Override
	public void onResume() {
		loadCallLog();
		loadContacts();

		// 第三方应用调用拨号后获取号码显示
		Intent intent = getActivity().getIntent();
		String action = intent.getAction();
		if ((Intent.ACTION_DIAL.equals(action))
				|| (Intent.ACTION_VIEW.equals(action))) {
			Uri uri = intent.getData();
			if ((uri != null) && ("tel".equals(uri.getScheme()))) {
				String number = uri.getSchemeSpecificPart();
				if (!TextUtils.isEmpty(number)) {
					input(number);
				}
			}
		}

		int volume = TONE_RELATIVE_VOLUME;
		int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		// 音量最高15，最低0,。大于10时取50%，大于5时取55%，小于5时取60%
		if (current > 10) {
			volume = 50;
		} else if (current > 5) {
			volume = 55;
		} else {
			volume = 60;
		}
		// 读取设定的值
		mDTMFToneEnabled = Settings.System.getInt(getActivity()
				.getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING,
				1) == 1;
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				try {
					// we want the user to be ableto control the volume of the
					// dial tones
					// outside of a call, so we usethe stream type that is also
					// mapped to the
					// volume control keys for thisactivity
					mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,
							volume);
					getActivity().setVolumeControlStream(DIAL_TONE_STREAM_TYPE);
				} catch (RuntimeException e) {
					mDTMFToneEnabled = false;
					mToneGenerator = null;
				}
			}
		}
		super.onResume();
	}

	public void onPause() {
		super.onPause();
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator != null) {
				mToneGenerator.release();
				mToneGenerator = null;
			}
		}
	}

	/**
	 * 获取通话记录列表
	 * 
	 * @description
	 * @date 2014-1-11
	 * @author valuestudio
	 */
	protected void loadCallLog() {
		new Thread(new CallLogThread(getActivity(), handlerQueryCallLog))
				.start();
	}

	Handler handlerQueryCallLog = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ContactsAsyncTask.QUERY_START_MESSAGE:
				break;
			case ContactsAsyncTask.QUERY_END_MESSAGE:
				Bundle bundle = msg.getData();
				ArrayList<CallLogEntity> callLogList = (ArrayList<CallLogEntity>) bundle
						.get(Constant.QUERY_RESULT);
				if (callLogList.size() > 0) {
					if (TextUtils.isEmpty(numInputView.getText().toString())) {
						noCallLogView.setVisibility(View.GONE);
						callLogListView.setVisibility(View.VISIBLE);
						contactListView.setVisibility(View.GONE);
						operateLayout.setVisibility(View.GONE);
					} else {
						if (contactListAdapter.getContactList().size() <= 0) {
							noCallLogView.setVisibility(View.GONE);
							contactListView.setVisibility(View.GONE);
							callLogListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.VISIBLE);
						} else {
							noCallLogView.setVisibility(View.GONE);
							contactListView.setVisibility(View.VISIBLE);
							callLogListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.GONE);
						}
					}
					application.setCallLogList(callLogList);
					setAdapter(callLogList);
				} else {
					if (TextUtils.isEmpty(numInputView.getText().toString())) {
						noCallLogView.setVisibility(View.VISIBLE);
						callLogListView.setVisibility(View.GONE);
						contactListView.setVisibility(View.GONE);
						operateLayout.setVisibility(View.GONE);
					} else {
						if (contactListAdapter.getContactList().size() <= 0) {
							noCallLogView.setVisibility(View.GONE);
							contactListView.setVisibility(View.GONE);
							callLogListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.VISIBLE);
						} else {
							noCallLogView.setVisibility(View.GONE);
							contactListView.setVisibility(View.VISIBLE);
							callLogListView.setVisibility(View.GONE);
							operateLayout.setVisibility(View.GONE);
						}
					}
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void setAdapter(List<CallLogEntity> list) {
		callLogListAdapter.setCallLogList(list);
		callLogListAdapter.notifyDataSetChanged();
	}

	/**
	 * 显示隐藏拨号键
	 * 
	 * @description
	 * @param visible
	 * @date 2014-7-31
	 * @author zuolong
	 */
	public void setDialpadVisible(boolean visible) {
		if (dialpadView != null) {
			if (visible) {
				dialpadView.startAnimation(visibleAnima);
				dialpadView.setVisibility(View.VISIBLE);
			} else {
				dialpadView.startAnimation(goneAnima);
				// dialpadView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 判断拨号键是否隐藏
	 * 
	 * @description
	 * @return
	 * @date 2014-7-31
	 * @author zuolong
	 */
	public boolean getDialpadVisible() {
		if (dialpadView == null) {
			return false;
		}
		return dialpadView.getVisibility() != View.GONE;
	}

	@Override
	public void filterCallback() {
		if (contactListAdapter.getContactList().size() <= 0) {
			noCallLogView.setVisibility(View.GONE);
			contactListView.setVisibility(View.GONE);
			callLogListView.setVisibility(View.GONE);
			operateLayout.setVisibility(View.VISIBLE);
		} else {
			noCallLogView.setVisibility(View.GONE);
			contactListView.setVisibility(View.VISIBLE);
			callLogListView.setVisibility(View.GONE);
			operateLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void afterCall() {
		resetDialpad();
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
		numInputView.setTextColor(getResources().getColor(
				a.getResourceId(R.styleable.ContactsAppView_titleColor,
						R.color.white)));
		numInputView.setHintTextColor(getResources().getColor(
				a.getResourceId(R.styleable.ContactsAppView_titleColor,
						R.color.white)));
		a.recycle();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// 默认显示拨号盘
		if (dialpadView != null) {
			dialpadView.setVisibility(View.VISIBLE);
		}
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
		dialerItem.setIcon(sbDialPressIconResId);

		settingsItem = menu.add(0, SETTINGS_ID, 0, R.string.settings);
		settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		settingsItem.setIcon(sbSettingsIconResId);

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
			if (getDialpadVisible()) {
				setDialpadVisible(false);
				if (!ActionBarProxy.hasSmartBar()) {
					contactsAct = getActivity();
					if (contactsAct != null) {
						((ContactsFragmentActivity) contactsAct)
								.setDialerImg(sbDialDisplayPressIconResId);
					}
				} else {
					dialerItem.setIcon(sbDialDisplayPressIconResId);
				}
			} else {
				setDialpadVisible(true);
				if (!ActionBarProxy.hasSmartBar()) {
					contactsAct = getActivity();
					if (contactsAct != null) {
						((ContactsFragmentActivity) contactsAct)
								.setDialerImg(sbDialPressIconResId);
					}
				} else {
					dialerItem.setIcon(sbDialPressIconResId);
				}
			}
			break;
		case SETTINGS_ID:
			Activity settingsAct = getActivity();
			if (settingsAct != null) {
				((ContactsFragmentActivity) settingsAct)
						.setCurrentItem(SETTINGS_ID);
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

	/**
	 * 禁止Edittext弹出软件盘，光标依然正常显示。
	 */
	public void disableShowSoftInput(EditText editText) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method method;
			try {
				method = cls
						.getMethod("setShowSoftInputOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				method = cls.getMethod("setSoftInputShownOnFocus",
						boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
