package com.valuestudio.contacts.ui;

import java.util.Date;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;

/**
 * 通话记录详情
 * 
 * @author chenyt
 * 
 */
public class CallDetailActivity extends BaseActivity {
	/**
	 * 编辑联系人
	 */
	private final static int EDIT_CONTACT = 0;
	/**
	 * 清除通话记录
	 */
	private final static int CLEAR_LOG = 1;
	private MenuItem editContactItem;
	private MenuItem clearLogItem;
	// 联系人姓名
	private TextView nameText;
	// 联系人信息
	private LinearLayout contactInfoLayout;
	// 通话记录信息
	private LinearLayout callLogLayout;
	// 加载更多
	private TextView loadMoreText;
	// 添加到新联系人
	private RelativeLayout addToNewContact;
	// 添加到已存在联系人
	private RelativeLayout addToExistContact;
	private boolean ifLoadMore = false;

	private String intentName;
	private String intentNumber;
	private int intentType;
	private String intentLocation;
	private int intentContactId;

	private String[] numberTypeArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.BaseWhiteAppTheme);
		setContentView(R.layout.call_detail);
		initActionBar();

		initView();
		bindData();
	}

	@Override
	protected void initActionBar() {
		super.initActionBar();
		// 设置底部actionbar背景
		mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(
				R.color.theme_black));
	}

	public void initView() {
		intentName = getIntent().getStringExtra("name");
		intentNumber = getIntent().getStringExtra("number");
		intentType = getIntent().getIntExtra("type", 0);
		intentLocation = getIntent().getStringExtra("location");

		nameText = (TextView) findViewById(R.id.name);

		contactInfoLayout = (LinearLayout) findViewById(R.id.contact_info_layout);
		callLogLayout = (LinearLayout) findViewById(R.id.calllog_info_layout);
		loadMoreText = (TextView) findViewById(R.id.load_more);
		loadMoreText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ifLoadMore = true;
				loadCallLogInfo();
				loadMoreText.setVisibility(View.GONE);
			}
		});
		addToNewContact = (RelativeLayout) findViewById(R.id.add_contact_layout);
		addToNewContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAddContact = new Intent(Intent.ACTION_INSERT);
				intentAddContact
						.setType(ContactsContract.RawContacts.CONTENT_TYPE);
				intentAddContact.putExtra(
						ContactsContract.Intents.Insert.PHONE, intentNumber);
				startActivityForResult(intentAddContact,
						Constant.CONTACTS_CHANGED_REQ_CODE);
			}
		});
		addToExistContact = (RelativeLayout) findViewById(R.id.add_to_exist_layout);
		addToExistContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentAddToExist = new Intent(
						Intent.ACTION_INSERT_OR_EDIT);
				intentAddToExist.setType(Contacts.CONTENT_ITEM_TYPE);
				intentAddToExist.putExtra(
						ContactsContract.Intents.Insert.PHONE, intentNumber);
				startActivityForResult(intentAddToExist,
						Constant.CONTACTS_CHANGED_REQ_CODE);
			}
		});

		if (TextUtils.isEmpty(intentName)) {
			nameText.setText(intentNumber);
			addToExistContact.setVisibility(View.VISIBLE);
			addToNewContact.setVisibility(View.VISIBLE);
		} else {
			nameText.setText(intentName);
			addToExistContact.setVisibility(View.GONE);
			addToNewContact.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		loadCallLogInfo();
		loadContactInfo();
		super.onResume();
	}

	public void bindData() {
		numberTypeArray = getResources().getStringArray(R.array.number_type);
	}

	private void loadCallLogInfo() {
		// 1、通话记录查询
		String[] projection = { CallLog.Calls.NUMBER, CallLog.Calls.DURATION,
				CallLog.Calls.TYPE, CallLog.Calls.DATE };
		Cursor logcursor = getContentResolver().query(
				CallLog.Calls.CONTENT_URI, projection,
				CallLog.Calls.NUMBER + "='" + intentNumber + "'", null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		if (logcursor.getCount() > 5) {
			loadMoreText.setVisibility(View.VISIBLE);
		} else {
			loadMoreText.setVisibility(View.GONE);
		}
		callLogLayout.removeAllViews();
		if (logcursor.moveToNext()) {
			int i = 0;
			do {
				if (!ifLoadMore && i >= 5) {
					break;
				}
				long lDuration = logcursor.getLong(logcursor
						.getColumnIndex(CallLog.Calls.DURATION));
				int type = logcursor.getInt(logcursor
						.getColumnIndex(CallLog.Calls.TYPE));
				Date date = new Date(logcursor.getLong(logcursor
						.getColumnIndex(CallLog.Calls.DATE)));

				String dateStr = ContactsUtil.toDateString(
						ContactsUtil.MM_dd_HH_mm, date);
				RelativeLayout loglayout = (RelativeLayout) LayoutInflater
						.from(mContext).inflate(
								R.layout.call_detail_calllog_item, null, false);

				((TextView) loglayout.findViewById(R.id.date)).setText(dateStr);
				ImageView typeImage = ((ImageView) loglayout
						.findViewById(R.id.state_image));
				TextView typeText = ((TextView) loglayout
						.findViewById(R.id.state_text));
				// 拒接状态不对
				if (type == CallLog.Calls.INCOMING_TYPE) {
					typeImage.setImageDrawable(getResources().getDrawable(
							R.drawable.call_list_incoming));
					typeText.setText(R.string.call_incoming);
				} else if (type == CallLog.Calls.OUTGOING_TYPE) {
					typeImage.setImageDrawable(getResources().getDrawable(
							R.drawable.call_list_outgoing));
					typeText.setText(R.string.call_outgoing);
				} else if (type == CallLog.Calls.MISSED_TYPE) {
					typeImage.setImageDrawable(getResources().getDrawable(
							R.drawable.call_list_missed));
					typeText.setText(R.string.call_missed);
				} else {
					typeImage.setImageDrawable(getResources().getDrawable(
							R.drawable.call_list_reject));
					typeText.setText(R.string.call_rejected);
				}
				((TextView) loglayout.findViewById(R.id.time))
						.setText(ContactsUtil.toTimeString(this, lDuration));
				callLogLayout.addView(loglayout);
				i++;
			} while (logcursor.moveToNext());
		}
		logcursor.close();
	}

	private void loadContactInfo() {
		// 2、联系人查询
		// 联系人姓名为空时
		contactInfoLayout.removeAllViews();

		String[] phoneProjection = new String[] { Phone.DISPLAY_NAME,
				Phone.NUMBER, Phone.TYPE, Phone.SORT_KEY_PRIMARY,
				Phone.CONTACT_ID };
		String selection;
		if (TextUtils.isEmpty(intentName)) {
			selection = Phone.NUMBER + "='" + intentNumber + "'";
		} else {
			selection = Phone.DISPLAY_NAME + "='" + intentName + "'";
		}
		Cursor cursorContast = getContentResolver().query(
				Constant.PHONE_CONTENT_URI, phoneProjection, selection, null,
				null);// "data2 asc"
		if (cursorContast.moveToNext()) {
			do {
				intentName = cursorContast.getString(cursorContast
						.getColumnIndex(Phone.DISPLAY_NAME));
				nameText.setText(intentName);
				intentContactId = cursorContast.getInt(cursorContast
						.getColumnIndex(Phone.CONTACT_ID));
				final String strNumber = cursorContast.getString(cursorContast
						.getColumnIndex(Phone.NUMBER));
				int type = cursorContast.getInt(cursorContast
						.getColumnIndex(Phone.TYPE));
				final RelativeLayout contactLayout = (RelativeLayout) LayoutInflater
						.from(mContext).inflate(
								R.layout.call_detail_contact_item, null, false);
				((RelativeLayout) contactLayout
						.findViewById(R.id.call_info_layout))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ContactsUtil.call(mContext, strNumber);
							}
						});
				((ImageView) contactLayout.findViewById(R.id.msg_view))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Uri msgUri = Uri.parse("smsto:" + strNumber);
								Intent intentMsg = new Intent(
										Intent.ACTION_SENDTO, msgUri);
								mContext.startActivity(intentMsg);
							}
						});
				((TextView) contactLayout.findViewById(R.id.number))
						.setText(strNumber);
				ImageView stateImage = (ImageView) contactLayout
						.findViewById(R.id.state);
				String location = "";
				if (strNumber.equals(intentNumber)) {
					location = intentLocation;
					stateImage.setVisibility(View.VISIBLE);
					if (intentType == CallLog.Calls.INCOMING_TYPE) {
						stateImage.setImageDrawable(getResources().getDrawable(
								R.drawable.call_list_incoming));
					} else if (intentType == CallLog.Calls.OUTGOING_TYPE) {
						stateImage.setImageDrawable(getResources().getDrawable(
								R.drawable.call_list_outgoing));
					} else if (intentType == CallLog.Calls.MISSED_TYPE) {
						stateImage.setImageDrawable(getResources().getDrawable(
								R.drawable.call_list_missed));
					} else {
						stateImage.setImageDrawable(getResources().getDrawable(
								R.drawable.call_list_reject));
					}
				} else {
					stateImage.setVisibility(View.INVISIBLE);
				}
				if (type >= numberTypeArray.length) {
					type = numberTypeArray.length - 1;
				}
				((TextView) contactLayout.findViewById(R.id.type))
						.setText(numberTypeArray[type] + "  " + location);
				contactInfoLayout.addView(contactLayout);
				LinearLayout div = (LinearLayout) LayoutInflater.from(mContext)
						.inflate(R.layout.div_layout, null, false);
				contactInfoLayout.addView(div);
			} while (cursorContast.moveToNext());
		} else {

			final RelativeLayout contactLayout = (RelativeLayout) LayoutInflater
					.from(mContext).inflate(R.layout.call_detail_contact_item,
							null, false);
			((RelativeLayout) contactLayout.findViewById(R.id.call_info_layout))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							ContactsUtil.call(mContext, intentNumber);
						}
					});
			((ImageView) contactLayout.findViewById(R.id.msg_view))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Uri msgUri = Uri.parse("smsto:" + intentNumber);
							Intent intentMsg = new Intent(Intent.ACTION_SENDTO,
									msgUri);
							mContext.startActivity(intentMsg);
						}
					});
			((TextView) contactLayout.findViewById(R.id.number))
					.setText(intentNumber);
			ImageView stateImage = (ImageView) contactLayout
					.findViewById(R.id.state);
			stateImage.setVisibility(View.VISIBLE);
			if (intentType == CallLog.Calls.INCOMING_TYPE) {
				stateImage.setImageDrawable(getResources().getDrawable(
						R.drawable.call_list_incoming));
			} else if (intentType == CallLog.Calls.OUTGOING_TYPE) {
				stateImage.setImageDrawable(getResources().getDrawable(
						R.drawable.call_list_outgoing));
			} else if (intentType == CallLog.Calls.MISSED_TYPE) {
				stateImage.setImageDrawable(getResources().getDrawable(
						R.drawable.call_list_missed));
			} else {
				stateImage.setImageDrawable(getResources().getDrawable(
						R.drawable.call_list_reject));
			}
			((TextView) contactLayout.findViewById(R.id.type))
					.setText(intentLocation);
			contactInfoLayout.addView(contactLayout);

		}
		cursorContast.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!TextUtils.isEmpty(intentName)) {
			editContactItem = menu.add(0, EDIT_CONTACT, 0,
					R.string.edit_contact);
			editContactItem.setIcon(R.drawable.ic_edit_light);
			editContactItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			editContactItem.setTitle(getString(R.string.edit_contact));
		}
		clearLogItem = menu.add(0, CLEAR_LOG, 0, R.string.clean_call_log_title);
		clearLogItem.setIcon(R.drawable.ic_clean_light);
		clearLogItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		clearLogItem.setTitle(getString(R.string.clean_call_log_title));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_CONTACT:
			// 需要编辑的联系人id
			if (!TextUtils.isEmpty(intentName)) {
				int requestCode = 10;
				Intent i = new Intent(Intent.ACTION_EDIT);
				i.setData(Uri.parse("content://com.android.contacts/contacts/"
						+ intentContactId));
				startActivityForResult(i, requestCode);
			}

			break;
		case CLEAR_LOG:
			AlertDialog.Builder clearRemindDialog = new AlertDialog.Builder(
					mContext);
			clearRemindDialog
					.setIcon(R.drawable.ic_popup_delete)
					.setMessage(getString(R.string.clean_log_remind))
					.setNegativeButton(R.string.cancel_text, null)
					.setPositiveButton(R.string.ok_text,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String[] projection = { CallLog.Calls._ID };
									Cursor logcursor = getContentResolver()
											.query(CallLog.Calls.CONTENT_URI,
													projection,
													CallLog.Calls.NUMBER + "='"
															+ intentNumber
															+ "'",
													null,
													CallLog.Calls.DEFAULT_SORT_ORDER);
									ContentResolver resolver = getContentResolver();
									if (logcursor.moveToFirst()) {
										do {
											int id = logcursor.getInt(logcursor
													.getColumnIndex(CallLog.Calls._ID));
											resolver.delete(
													CallLog.Calls.CONTENT_URI,
													"_id=?", new String[] { id
															+ "" });
										} while (logcursor.moveToNext());
									}
									logcursor.close();
									finish();
								}
							}).create();
			clearRemindDialog.show();
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return true;
	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 10:
			loadContactInfo();
			break;

		default:
			break;
		}
	};

	@Override
	protected void onSlideRight() {
		super.onSlideRight();
		onBackPressed();
	}
}
