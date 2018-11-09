package com.valuestudio.contacts.ui;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.ContactsOperateAdapter;
import com.valuestudio.contacts.base.BaseActivity;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.ToPinYin;
import com.valuestudio.contacts.widget.LetterListView;
import com.valuestudio.contacts.widget.LetterListView.OnTouchingLetterChangedListener;
import com.valuestudio.contacts.widget.SelectedNumAllCheckBox;

/**
 * @author valuestudio
 * @description 手机联系人
 * @date 2013-12-9
 */
public class BaseContactsOperateActivity extends BaseActivity implements
        OnScrollListener {
    protected static final int PHONE_CONTACT_ID = 0;
    protected static final int SIM_CONTACT_ID = 1;
    protected static final int SETTINGS_ID = 2;
    protected static final int DELETE_ID = 3;
    protected static final int COPY_ID = 4;
    protected static final int SELECT_ALL_ID = 5;
    /**
     * 是否复制联系人到手机标识
     */
    protected static final String HAS_COPY2PHONE = "has_copy2phone";
    /**
     * 是否删除sim卡联系人标识
     */
    protected static final String HAS_DELETE_PHONE = "has_delete_phone";
    /**
     * 手机联系人ListView
     */
    protected ListView mListView;
    /**
     * 联系人
     */
    protected List<ContactEntity> mContactList;
    /**
     * 联系人列表适配器
     */
    protected ContactsOperateAdapter mContactAdapter;
    /**
     * 字母ListView
     */
    protected LetterListView mLetterListView;
    /**
     * 汉语拼音首字母和与之对应的列表位置
     */
    protected HashMap<String, Integer> mAlphaIndexer;
    /**
     * 汉语拼音首字母
     */
    private String[] mSections;
    /**
     * 漂浮字母布局
     */
    protected View mOverlayLayout;
    /**
     * 漂浮字母
     */
    protected TextView mOverlay;
    /**
     * 漂浮字母线程
     */
    protected OverlayThread mOverlayThread;
    /**
     * 加载图标
     */
    protected ProgressDialog mProgressDialog;
    /**
     * 暂无联系人
     */
    protected TextView noContactView;
    /**
     * 电话状态
     */
    protected TelephonyManager telMgr;
    /**
     * 删除取消
     */
    protected boolean deleteCancel;
    /**
     * 是否已经点击，用于避免重复点击
     */
    private boolean isClicked = false;
    protected Handler clickHanlder = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Constant.CLICK_FINISH:
                    isClicked = false;
                    break;
            }
        }

        ;
    };
    /**
     * 选择全部控件
     */
    protected SelectedNumAllCheckBox mSelectedNumAllCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_operate_activity);
        // 初始化电话服务检测sim状态
        telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }

    @Override
    public void initViews() {
        mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(
                R.color.theme_white));
        // 设置SmartBar返回按键图片
        ContactsUtil.setBackIcon(getActionBar(),
                getResources().getDrawable(R.drawable.ic_tab_back_black));

        mListView = (ListView) findViewById(R.id.contact_list);
        mListView.setOnScrollListener(this);
        mLetterListView = (LetterListView) findViewById(R.id.letter_list);
        mLetterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());
        noContactView = (TextView) findViewById(R.id.no_contact);
        // 初始化字母弹出框
        initOverlay();
        // 初始化选择全部按钮
        initSelectedNumAllCheckBox();
    }

    /**
     * 初始化选择全部按钮
     *
     * @description
     * @date 2014-11-29
     * @author valuestudio
     */
    protected void initSelectedNumAllCheckBox() {
        mSelectedNumAllCheckBox = new SelectedNumAllCheckBox(mContext);
        mSelectedNumAllCheckBox.getView().setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isClicked) {
                            return;
                        }
                        isClicked = true;
                        clickHanlder.sendEmptyMessageDelayed(
                                Constant.CLICK_FINISH, 500);

                        mSelectedNumAllCheckBox.changeChecked();
                        mContactAdapter.clickSelectAll(mSelectedNumAllCheckBox
                                .getChecked());
                    }
                });
    }

    @Override
    public void bindData() {
        super.bindData();
    }

    /**
     * 获取联系人
     *
     * @description
     * @date 2014-1-11
     * @author valuestudio
     */
    protected void loadContacts() {
    }

    /**
     * 更新列表
     *
     * @param list
     */
    protected void updateContactListData(List<ContactEntity> list) {
        sortList(list);
        if (mContactAdapter == null) {
            mContactAdapter = new ContactsOperateAdapter(mContext, list);
            mListView.setAdapter(mContactAdapter);
        } else {
            mContactAdapter.setPhoneList(list);
            mContactAdapter.initSelectedAdapter();
        }
        // 无联系人时隐藏字母导航
        if (list.size() <= 0) {
            mLetterListView.setVisibility(View.GONE);
            noContactView.setVisibility(View.VISIBLE);
            return;
        }
        mLetterListView.setVisibility(View.VISIBLE);
        noContactView.setVisibility(View.GONE);
        // 每次的数据可能不一样需要重新赋值
        mAlphaIndexer = new HashMap<String, Integer>();
        mOverlayThread = new OverlayThread();
        mSections = new String[list.size()];

        // 获得字母序列
        getAlphaIndex(list);

        // 默认选中右边字母导航中的一个字母
        String currentStr = ToPinYin.getAlpha(list.get(0).getSortKey());
        mLetterListView.setUpdateChoose(currentStr, true);
    }

    /**
     * 列表排序
     *
     * @param list
     * @description
     * @date 2014-2-9
     * @author valuestudio
     */
    protected void sortList(List<ContactEntity> list) {
    }

    private class LetterListViewListener implements
            OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (mAlphaIndexer.get(s) != null) {
                int position = mAlphaIndexer.get(s);
                mListView.setSelection(position);
                mOverlay.setText(mSections[position]);
                mOverlay.setVisibility(View.VISIBLE);
                // 获取焦点后颜色设置为灰色
                mLetterListView.setBackgroundDrawable(getResources()
                        .getDrawable(R.color.light_gray));
                mHandler.removeCallbacks(mOverlayThread);
                // 延迟500毫秒后执行，让overlay不可见
                mHandler.postDelayed(mOverlayThread, 500);
            }
        }
    }

    /**
     * 获得字母序列
     *
     * @param list
     */
    private void getAlphaIndex(List<ContactEntity> list) {
        for (int index = 0; index < list.size(); index++) {
            // 当前汉语拼音首字母
            String currentStr = ToPinYin.getAlpha(list.get(index).getSortKey());
            // 上一个汉语拼音首字母，如果不存在为“ ”
            String previewStr = (index - 1) >= 0 ? ToPinYin.getAlpha(list.get(
                    index - 1).getSortKey()) : " ";
            if (!previewStr.equals(currentStr)) {
                mAlphaIndexer.put(currentStr, index);
                mSections[index] = currentStr;
            }
        }
    }

    /**
     * 初始化汉语拼音首字母弹出提示框
     */
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mOverlayLayout = inflater.inflate(R.layout.overlay, null);
        mOverlay = (TextView) mOverlayLayout.findViewById(R.id.overlay);
        // 设置透明度
        mOverlay.getBackground().setAlpha(180);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mOverlayLayout, lp);
    }

    /**
     * 设置overlay不可见
     */
    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            mOverlay.setVisibility(View.GONE);
            // 默认透明
            mLetterListView.setBackgroundDrawable(getResources().getDrawable(
                    R.color.gray_bg));
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.COPY_SUCCESS:
                case Constant.COPY_FAILED:
                    // 重新显示目录
                    initSelectedNumAllCheckBox();
                    invalidateOptionsMenu();
                    // 初始化Adapter
                    mContactAdapter.initSelectedAdapter();
                    mProgressDialog.dismiss();
                    removeDialog(Constant.COPY_DOING_DIALOG);
                    // 显示复制结果对话框
                    ContactsUtil.showAlertDialog(mContext,
                            getString(R.string.copy_done), msg.obj.toString());
                    break;
                case Constant.DELETE_SUCCESS:
                    // 重新显示目录
                    initSelectedNumAllCheckBox();
                    invalidateOptionsMenu();
                    // 重新加载联系人
                    loadContacts();
                    mProgressDialog.dismiss();
                    removeDialog(Constant.DELETE_CONTACTS_DIALOG);
                    // 显示删除结果对话框
                    ContactsUtil.showAlertDialog(mContext,
                            getString(R.string.delete_done), msg.obj.toString());
                    break;
                case Constant.QUERY_SUCCESS:
                    mProgressDialog.dismiss();
                    // 显示总共人数
                    getActionBar().setSubtitle(
                            getString(R.string.contacts_count_format,
                                    mContactList.size()));
                    updateContactListData(mContactList);
                    break;
                case Constant.UPDATE_PROGRESS:
                    // 更新进度
                    mProgressDialog.setProgress(msg.arg1);
                    break;
            }
        }

        ;
    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mContactList != null && mContactList.size() > 0) {
            String currentStr = ToPinYin.getAlpha(mContactList.get(
                    firstVisibleItem).getSortKey());
            mLetterListView.setUpdateChoose(currentStr, false);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 创建进度条样式
     *
     * @param id
     * @param args
     * @return
     * @description
     * @date 2014-2-15
     * @author valuestudio
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMax(args.getInt(Constant.PROGRESS_MAX));
        mProgressDialog.setTitle(args.getString(Constant.PROGRESS_MESSAGE));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 是否显示取消按钮
        boolean cancel = args.getBoolean(Constant.PROGRESS_CANCEL);
        if (cancel) {
            mProgressDialog.setCancelable(true);
            // 取消监听
            mProgressDialog.setButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCancel = true;
                        }
                    });
        } else {
            mProgressDialog.setCancelable(false);
        }
        return mProgressDialog;
    }

    @Override
    protected void onSlideRight() {
        super.onSlideRight();
        onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mOverlayLayout != null) {
            getWindowManager().removeViewImmediate(mOverlayLayout);
        }
        super.onDestroy();
    }

}
