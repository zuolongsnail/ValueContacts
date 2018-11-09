package com.valuestudio.contacts.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.ContactsOperateAdapter;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.MeizuUtil;
import com.valuestudio.contacts.utils.SharedPrefsUtil;
import com.valuestudio.contacts.utils.ToPinYin;

/**
 * @author valuestudio
 * @description 手机联系人
 * @date 2013-12-9
 */
public class PhoneContactActivity extends BaseContactsOperateActivity implements
        OnScrollListener, OnItemClickListener {
    /**
     * 查询联系人下面是否含有号码条件
     */
    protected static final String[] HAS_PHONE_NUMBER_PROJECTION = new String[]{ContactsContract.Contacts.HAS_PHONE_NUMBER};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        bindData();
    }

    @Override
    public void initViews() {
        super.initViews();
        setTitle(R.string.phone_contacts_title);
        mListView.setOnItemClickListener(this);

        mProgressDialog = ProgressDialog.show(mContext, null,
                getString(R.string.contacts_loading), false, true);
        mProgressDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mProgressDialog.dismiss();
                }
                return true;
            }
        });
    }

    @Override
    public void bindData() {
        super.bindData();
        mContactList = new ArrayList<ContactEntity>();
        mContactAdapter = new ContactsOperateAdapter(mContext, mContactList);
        mListView.setAdapter(mContactAdapter);
        mSimContactList = new ArrayList<ContactEntity>();

        loadContacts();
    }

    /**
     * 获取手机通讯录联系人
     *
     * @description
     * @date 2014-1-11
     * @author valuestudio
     */
    @Override
    protected void loadContacts() {
        new Thread() {
            @Override
            public void run() {
                mContactList = new ArrayList<ContactEntity>();
                Cursor cursor = null;
                // 判断是否为三星手机，三星手机需要查询“is_sim”字段
                if (!ContactsUtil.isSamsungPhone()) {
                    cursor = getContentResolver().query(
                            Constant.PHONE_CONTENT_URI,
                            Constant.PHONE_PROJECTION, null, null,
                            "sort_key COLLATE LOCALIZED asc");
                } else {
                    cursor = getContentResolver().query(
                            Constant.PHONE_CONTENT_URI,
                            Constant.PHONE_PROJECTION_SAMSUNG, null, null,
                            "sort_key COLLATE LOCALIZED asc");
                }
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        // 得到手机号码
                        String phoneNumber = cursor
                                .getString(Constant.NUMBER_INDEX);
                        // 当手机号码为空的或者为空字段 跳过当前循环
                        if (TextUtils.isEmpty(phoneNumber)) {
                            continue;
                        }
                        // 判断是否为sim卡联系人（针对三星手机），1表示是则跳过
                        int isSimIndex = cursor.getColumnIndex(Constant.IS_SIM);
                        if (isSimIndex >= 0) {
                            int isSim = cursor.getInt(isSimIndex);
                            if (isSim == 1) {
                                continue;
                            }
                        }
                        // 得到联系人ID
                        Long rawContactId = cursor.getLong(Constant.ID_INDEX);
                        // 得到联系人名称
                        String contactName = cursor
                                .getString(Constant.NAME_INDEX);
                        // 得到联系人头像ID
                        Long photoId = cursor.getLong(Constant.PHOTO_ID_INDEX);
                        // sort_key
                        // String sortKey = cursor
                        // .getString(Constant.SORT_KEY_INDEX);
                        String sortKey = ToPinYin.hanZiToPinYin(contactName);
                        // 得到联系人ID
                        Long contactId = cursor
                                .getLong(Constant.CONTACT_ID_INDEX);

                        ContactEntity entity = new ContactEntity();
                        entity.setRawContactId(rawContactId);
                        entity.setName(contactName);
                        entity.setNumber(phoneNumber);
                        entity.setPhotoId(photoId);
                        entity.setSortKey(sortKey);
                        entity.setContactId(contactId);
                        mContactList.add(entity);
                    }
                    cursor.close();
                }
                if (mContactList.size() <= 0) {
                    // 首次启动判断是否为魅族手机，是则测试显示魅族手机无联系人文字
                    if (SharedPrefsUtil.getValue(mContext,
                            Constant.FIRST_LAUNCH, true)) {
                        if (ContactsUtil.isMeizuPhone()) {
                            noContactView.setText(R.string.no_contact);
                        }
                    }
                }
                // 手机联系人加载完以后加载sim卡联系人
                loadSimContacts();
                // 查询完成
                Message message = new Message();
                message.what = Constant.QUERY_SUCCESS;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 删除
        MenuItem deleteItem = menu.add(0, DELETE_ID, 0, R.string.delete);
        deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        deleteItem.setIcon(R.drawable.ic_tab_delete);
        // 复制
        MenuItem copyItem = menu.add(0, COPY_ID, 0, R.string.copy2sim);
        copyItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        copyItem.setIcon(R.drawable.ic_tab_copy);
        // 全选
        MenuItem selectAllItem = menu.add(0, SELECT_ALL_ID, 0,
                R.string.select_all);
        selectAllItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        selectAllItem.setActionView(mSelectedNumAllCheckBox.getView());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                // 选中的数量
                final int deleteSize = mContactAdapter.getCheckedIndexSet().size();
                if (deleteSize <= 0) {
                    Toast.makeText(mContext, R.string.select_least_one,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                AlertDialog.Builder deleteRemindDialog = new AlertDialog.Builder(
                        mContext);
                deleteRemindDialog
                        .setTitle(R.string.remind)
                        .setIcon(R.drawable.ic_dialog_warning)
                        .setMessage(getString(R.string.delete_confirm_content))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        deleteCancel = false;
                                        // 显示删除过程中的进度条对话框
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constant.PROGRESS_MESSAGE,
                                                getString(R.string.delete_doing));
                                        bundle.putBoolean(Constant.PROGRESS_CANCEL,
                                                true);
                                        bundle.putInt(Constant.PROGRESS_MAX,
                                                deleteSize);
                                        showDialog(Constant.DELETE_CONTACTS_DIALOG,
                                                bundle);
                                    }
                                }).create();
                deleteRemindDialog.show();
                break;
            case COPY_ID:
                // 复制前检测sim状态
                if (telMgr.getSimState() != TelephonyManager.SIM_STATE_READY) {
                    if (telMgr.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                        Toast.makeText(mContext, R.string.no_sim,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (ContactsUtil.isAirplaneMode(mContext)) {
                            Toast.makeText(mContext, R.string.airplanemode_sim,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext,
                                    R.string.locked_or_unknown_sim,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                // 选中的数量
                final int copySize = mContactAdapter.getCheckedIndexSet().size();
                if (copySize <= 0) {
                    Toast.makeText(mContext, R.string.select_least_one,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                AlertDialog.Builder copyRemindDialog = new AlertDialog.Builder(
                        mContext);
                copyRemindDialog
                        .setTitle(R.string.remind)
                        .setIcon(R.drawable.ic_dialog_alert)
                        .setMessage(getString(R.string.copy2sim_confirm_content))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // 显示复制过程中的进度条对话框
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constant.PROGRESS_MESSAGE,
                                                getString(R.string.copy_doing));
                                        bundle.putBoolean(Constant.PROGRESS_CANCEL,
                                                false);
                                        bundle.putInt(Constant.PROGRESS_MAX,
                                                copySize);
                                        showDialog(Constant.COPY_DOING_DIALOG,
                                                bundle);
                                    }
                                }).create();
                copyRemindDialog.show();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    /**
     * 删除手机联系人线程
     *
     * @description
     * @date 2014-2-12
     * @author valuestudio
     */
    private void deletePhoneRunnable() {
        new Thread() {
            @Override
            public void run() {
                HashSet<Integer> delIndexSet = mContactAdapter
                        .getCheckedIndexSet();
                Iterator<Integer> delIterator = delIndexSet.iterator();
                // 完成数
                int doneNumber = 0;
                // 删除成功数
                int successNumber = 0;
                while (delIterator.hasNext()) {
                    if (deleteCancel) {
                        break;
                    }
                    int index = delIterator.next();
                    int result = deleteFromPhoneById(mContactList.get(index));
                    if (result > 0) {
                        successNumber = successNumber + 1;
                    }
                    doneNumber = doneNumber + 1;
                    // 更新复制进度
                    Message message = new Message();
                    message.what = Constant.UPDATE_PROGRESS;
                    message.arg1 = doneNumber;
                    mHandler.sendMessage(message);
                }

                Message message = new Message();
                message.what = Constant.DELETE_SUCCESS;
                // 被取消数
                int cancelNumber = delIndexSet.size() - doneNumber;
                // 判断失败数是否等于0，等于则为全部删除成功，否则删除不成功
                int failedNumber = delIndexSet.size() - successNumber
                        - cancelNumber;
                if (failedNumber == 0) {
                    if (doneNumber == delIndexSet.size()) {
                        message.obj = getString(R.string.delete_success_format,
                                successNumber);
                    } else {
                        message.obj = getString(
                                R.string.delete_success_cancel_format,
                                successNumber, cancelNumber);
                    }
                } else {
                    if (doneNumber == delIndexSet.size()) {
                        message.obj = getString(R.string.delete_failed_format,
                                successNumber, failedNumber);
                    } else {
                        message.obj = getString(
                                R.string.delete_failed_cancel_format,
                                successNumber, failedNumber, cancelNumber);
                    }
                }
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 删除指定id的联系人
     *
     * @param entity
     * @return
     * @description
     * @date 2014-2-12
     * @author valuestudio
     */
    public int deleteFromPhoneById(ContactEntity entity) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI,
                entity.getRawContactId());
        int number = getContentResolver().delete(uri, null, null);
        if (!hasPhoneNumber(entity.getContactId())) {
            // 删除整个联系人
            Uri deleteUri = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    entity.getContactId());
            getContentResolver().delete(deleteUri, null, null);
        }
        return number;
    }

    /**
     * 查询联系人下是否含有号码
     *
     * @param contactId
     * @return
     * @description
     * @date 2014-2-16
     * @author valuestudio
     */
    private boolean hasPhoneNumber(long contactId) {
        Cursor hasPhoneNumberCursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                HAS_PHONE_NUMBER_PROJECTION,
                ContactsContract.Contacts._ID + " = ?",
                new String[]{String.valueOf(contactId)}, null);
        if (hasPhoneNumberCursor != null) {
            if (hasPhoneNumberCursor.moveToNext()) {
                // 获取是否含有号码字段值，1表示至少有一个，0表示没有
                int hasPhoneNumber = hasPhoneNumberCursor.getInt(0);
                if (hasPhoneNumber == 0) {
                    hasPhoneNumberCursor.close();
                    return false;
                }
            }
            hasPhoneNumberCursor.close();
        }
        return true;
    }

    /**
     * 复制联系人到sim卡
     *
     * @description
     * @date 2014-2-12
     * @author valuestudio
     */
    private void copy2SimRunnable() {
        new Thread() {
            @Override
            public void run() {
                // 完成数
                int doneNumber = 0;
                // 复制成功数
                int successNumber = 0;
                // 复制重复数
                int dupleNumber = 0;
                HashSet<Integer> copyIndexSet = mContactAdapter
                        .getCheckedIndexSet();
                Iterator<Integer> copyIterator = copyIndexSet.iterator();
                // 获取所选的需要复制到sim卡的联系人列表
                List<ContactEntity> tempContactList = new ArrayList<ContactEntity>();
                while (copyIterator.hasNext()) {
                    int index = copyIterator.next();
                    tempContactList.add(mContactList.get(index));
                }
                for (ContactEntity entity : tempContactList) {
                    // 判断sim卡是否已经含有该联系人
                    if (mSimContactList.contains(entity)) {
                        dupleNumber = dupleNumber + 1;
                    } else {
                        boolean result = copy2Sim(entity);
                        if (result) {
                            // 复制到sim卡成功则更新内存中的sim卡联系人列表
                            mSimContactList.add(entity);
                            successNumber = successNumber + 1;
                        }
                    }
                    doneNumber = doneNumber + 1;
                    // 更新复制进度
                    Message message = new Message();
                    message.what = Constant.UPDATE_PROGRESS;
                    message.arg1 = doneNumber;
                    mHandler.sendMessage(message);
                }
                Message message = new Message();
                // 判断失败数是否等于0，等于则为全部复制成功，否则复制不成功
                int failedNumber = copyIndexSet.size() - successNumber
                        - dupleNumber;
                if (failedNumber == 0) {
                    message.what = Constant.COPY_SUCCESS;
                    // 判断重复数是否大于0，大于则显示重复信息
                    if (dupleNumber > 0) {
                        message.obj = getString(R.string.copy_duple_format,
                                successNumber, dupleNumber);
                    } else {
                        message.obj = getString(R.string.copy_success_format,
                                successNumber);
                    }
                } else {
                    message.what = Constant.COPY_FAILED;
                    // 判断重复数是否大于0，大于则显示重复信息
                    if (dupleNumber > 0) {
                        message.obj = getString(
                                R.string.copy2sim_duple_failed_format,
                                successNumber, failedNumber, dupleNumber);
                    } else {
                        message.obj = getString(
                                R.string.copy2sim_failed_format, successNumber,
                                failedNumber);
                    }
                }
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 插入单个sim卡联系人
     *
     * @param entity
     * @description
     * @date 2014-2-11
     * @author valuestudio
     */
    public boolean copy2Sim(ContactEntity entity) {
        // 判断是否是魅族手机
        if (ContactsUtil.isMeizuPhone()) {
            return MeizuUtil.copy2Sim(mContext, entity);
        } else {
            ContentValues values = new ContentValues();
            // sim存储时姓名长度处理
            String name = entity.getName();
            if (ToPinYin.isChinese(name) && name.length() > 6) {
                name = name.substring(0, 6);
            }
            values.put(Constant.SIM_CONTACT_TAG, name);
            values.put(Constant.SIM_CONTACT_NUMBER, entity.getNumber());
            Uri insertUri = getContentResolver().insert(
                    Constant.SIM_CONTENT_URI, values);
            if (insertUri != null) {
                // 获取执行结果
                String insertUriStr = insertUri.toString();
                String numberStr = insertUriStr.substring(insertUriStr
                        .indexOf(Constant.SIM_CONTENT_URI_STR)
                        + Constant.SIM_CONTENT_URI_STR.length());
                try {
                    int number = Integer.parseInt(numberStr);
                    // 注：大于等于零即为成功
                    if (number >= 0) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);
        switch (id) {
            case Constant.COPY_DOING_DIALOG:
                copy2SimRunnable();
                break;
            case Constant.DELETE_CONTACTS_DIALOG:
                deletePhoneRunnable();
                break;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 连续按两次返回键退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            // 若编辑栏显示，按返回键时隐藏编辑栏
            if (mContactAdapter.getCheckedIndexSet().size() > 0) {
                // 初始化Adapter
                mContactAdapter.initSelectedAdapter();
                // 重新显示目录
                initSelectedNumAllCheckBox();
                invalidateOptionsMenu();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * sim卡联系人，用于检测复制重复的sim卡联系人
     */
    protected List<ContactEntity> mSimContactList;

    /**
     * 加载sim卡联系人
     *
     * @description
     * @date 2014-2-16
     * @author valuestudio
     */
    protected void loadSimContacts() {
        // 初始化电话服务检测sim状态
        if (telMgr.getSimState() != TelephonyManager.SIM_STATE_READY) {
            return;
        }

        Cursor cursor = getContentResolver().query(Constant.SIM_CONTENT_URI,
                null, null, null, "name COLLATE LOCALIZED asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 得到号码
                int numberColumnIndex = cursor
                        .getColumnIndex(Constant.SIM_CONTACT_NUMBER);
                String phoneNumber = cursor.getString(numberColumnIndex);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                // 得到联系人ID
                int idColumnIndex = cursor
                        .getColumnIndex(Constant.SIM_CONTACT_ID);
                Long rawContactId = cursor.getLong(idColumnIndex);
                // 得到联系人名称
                int nameColumnIndex = cursor
                        .getColumnIndex(Constant.SIM_CONTACT_NAME);
                String contactName = cursor.getString(nameColumnIndex);
                // sort_key
                String sortKey = ToPinYin.hanZiToPinYin(contactName);

                ContactEntity entity = new ContactEntity();
                entity.setRawContactId(rawContactId);
                entity.setName(contactName);
                entity.setNumber(phoneNumber);
                entity.setSortKey(sortKey);
                mSimContactList.add(entity);
            }
            cursor.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        CheckBox check = (CheckBox) view.findViewById(R.id.item_check);
        check.setChecked(!check.isChecked());
        mContactAdapter.selectContacts(position, check.isChecked());
        // 改变全部选择按钮
        int selectedNum = mContactAdapter.getCheckedIndexSet().size();
        if (selectedNum == 0) {
            // 重新显示目录
            initSelectedNumAllCheckBox();
            invalidateOptionsMenu();
        } else if (selectedNum == mContactAdapter.getPhoneList().size()) {
            mSelectedNumAllCheckBox.setChecked(true);
        } else {
            mSelectedNumAllCheckBox.setChecked(false);
            mSelectedNumAllCheckBox.setText(String.valueOf(selectedNum));
        }
    }

}
