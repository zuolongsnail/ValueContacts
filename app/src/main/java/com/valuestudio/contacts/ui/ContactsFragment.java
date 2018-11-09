package com.valuestudio.contacts.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meizu.flyme.reflect.ActionBarProxy;
import com.valuestudio.contacts.R;
import com.valuestudio.contacts.adapter.ContactsAdapter;
import com.valuestudio.contacts.base.BaseFragment;
import com.valuestudio.contacts.base.ContactsApplication;
import com.valuestudio.contacts.entity.ContactEntity;
import com.valuestudio.contacts.service.ContactsAsyncTask;
import com.valuestudio.contacts.utils.Constant;
import com.valuestudio.contacts.utils.ContactsUtil;
import com.valuestudio.contacts.utils.ToPinYin;
import com.valuestudio.contacts.widget.LetterListView;
import com.valuestudio.contacts.widget.LetterListView.OnTouchingLetterChangedListener;

public class ContactsFragment extends BaseFragment implements OnClickListener,
        OnScrollListener {
    /**
     * 查询成功码
     */
    public static final int QUERY_SUCCESS = 1;
    /**
     * 标题背景
     */
    private View titleLayout;
    /**
     * 标题
     */
    protected EditText titleView;
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
    protected ContactsAdapter mContactAdapter;
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
     * 暂无联系人
     */
    protected TextView noContactView;
    /**
     * 清空按钮
     */
    private ImageView cleanBtn;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container,
                false);
        initViews(rootView);
        bindData();
        return rootView;
    }

    @Override
    protected void initViews(View parent) {
        super.initViews(parent);
        titleLayout = parent.findViewById(R.id.title_layout);
        titleView = (EditText) parent.findViewById(R.id.title_view);
        titleView.setHint(getActivity().getString(
                R.string.search_contact_format, 0));
        cleanBtn = (ImageView) parent.findViewById(R.id.search_clean_btn);
        cleanBtn.setOnClickListener(this);
        themeSet();
        mListView = (ListView) parent.findViewById(R.id.contact_list);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击列表打开指定联系人界面
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                        .withAppendedPath(
                                ContactsContract.Contacts.CONTENT_URI,
                                String.valueOf(mContactAdapter.getContactList()
                                        .get(position).getContactId())));
                startActivityForResult(intent,
                        Constant.CONTACTS_CHANGED_REQ_CODE);
            }
        });
        mLetterListView = (LetterListView) parent
                .findViewById(R.id.letter_list);
        mLetterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());
        noContactView = (TextView) parent.findViewById(R.id.no_contact);
        noContactView.setOnClickListener(this);

        // 初始化字母弹出框
        initOverlay();

        titleView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (mContactAdapter != null) {// 设置手机主题后打开应用，此处报空指针
                    mContactAdapter.getFilter().filter(s);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    cleanBtn.setVisibility(View.GONE);
                } else {
                    cleanBtn.setVisibility(View.VISIBLE);
                }
            }
        });
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
        titleView.setHintTextColor(getResources().getColor(
                a.getResourceId(R.styleable.ContactsAppView_titleColor,
                        R.color.white)));
        // 联系人页签搜索栏左图标
        Drawable searchIcon = getResources().getDrawable(
                a.getResourceId(
                        R.styleable.ContactsAppView_contactsSearchLeftIcon,
                        R.drawable.ic_search_light));
        searchIcon.setBounds(0, 0, searchIcon.getMinimumWidth(),
                searchIcon.getMinimumHeight());
        titleView.setCompoundDrawables(searchIcon, null, null, null);
        // 联系人页签搜索栏清除图标
        Drawable searchCleanIcon = getResources().getDrawable(
                a.getResourceId(
                        R.styleable.ContactsAppView_contactsSearchCleanIcon,
                        R.drawable.ic_clean_light));
        cleanBtn.setImageDrawable(searchCleanIcon);
        a.recycle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_contact:
                loadContacts();
                break;
            case R.id.search_clean_btn:
                titleView.setText("");
                break;
            default:
                break;
        }
    }

    protected void bindData() {
        mContactList = new ArrayList<ContactEntity>();
    }

    @Override
    public void onResume() {
        loadContacts();
        super.onResume();
    }

    /**
     * 获取手机通讯录联系人
     *
     * @description
     * @date 2014-1-11
     * @author valuestudio
     */
    protected void loadContacts() {
        Cursor cursor = getActivity().getContentResolver().query(
                Constant.PHONE_CONTENT_URI, Constant.PHONE_PROJECTION, null,
                null, "sort_key COLLATE LOCALIZED asc");
        ContactsAsyncTask.startQueryContacts(getActivity(), handlerQuery,
                cursor);
    }

    Handler handlerQuery = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ContactsAsyncTask.QUERY_START_MESSAGE:
                    break;
                case ContactsAsyncTask.QUERY_END_MESSAGE:
                    Bundle bundle = msg.getData();
                    mContactList = (ArrayList<ContactEntity>) bundle
                            .get(Constant.QUERY_RESULT);
                    if (mContactList.size() <= 0) {
                        noContactView.setText(R.string.no_contact);
                    }
                    // 搜索栏显示联系人数量
                    if (titleView != null) {
                        titleView.setHint(getActivity()
                                .getString(R.string.search_contact_format,
                                        mContactList.size()));
                    }
                    Activity activity = getActivity();
                    if (activity != null) {
                        ContactsApplication application = (ContactsApplication) activity
                                .getApplication();
                        application.setContactList(mContactList);
                        updateContactListData(mContactList);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 测试读取联系人权限
     *
     * @return
     * @description
     * @date 2014-8-3
     * @author valuestudio
     */
    private boolean testReadContactPermission() {
        // 首次启动判断是否为魅族手机，是则测试读取联系人的权限
        if (!ContactsUtil.getReadContactPerm(getActivity())) {
            Cursor cursorTest = getActivity().getContentResolver().query(
                    Constant.PHONE_CONTENT_URI, null, null, null, null);
            if (cursorTest != null) {
                if (cursorTest.getCount() > 0) {
                    ContactsUtil.setReadContactPerm(getActivity(), true);
                    cursorTest.close();
                    return true;
                } else {
                    cursorTest.close();
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 更新列表
     *
     * @param list
     */
    protected void updateContactListData(List<ContactEntity> list) {
        // sortList(list);
        if (mContactAdapter == null) {
            mContactAdapter = new ContactsAdapter(getActivity(), list);
            mListView.setAdapter(mContactAdapter);
            // 搜索栏不为空时过滤联系人
            if (!TextUtils.isEmpty(titleView.getText().toString())) {
                mContactAdapter.getFilter().filter(
                        titleView.getText().toString());
            }
        } else {
            // 搜索栏不为空时过滤联系人
            if (TextUtils.isEmpty(titleView.getText().toString())) {
                mContactAdapter.setPhoneList(list);
                mContactAdapter.notifyDataSetChanged();
            } else {
                mContactAdapter.getFilter().filter(
                        titleView.getText().toString());
            }
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
                handlerQuery.removeCallbacks(mOverlayThread);
                // 延迟500毫秒后执行，让overlay不可见
                handlerQuery.postDelayed(mOverlayThread, 500);
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
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
        WindowManager windowManager = (WindowManager) getActivity()
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
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            // 隐藏输入法
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    titleView.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        if (mOverlayLayout != null) {
            getActivity().getWindowManager().removeViewImmediate(mOverlayLayout);
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!ActionBarProxy.hasSmartBar()) {
            return;
        }
        contactsItem = menu.add(0, CONTACTS_ID, 0, R.string.contacts);
        contactsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        contactsItem.setIcon(sbContactsPressIconResId);

        dialerItem = menu.add(0, DIALER_ID, 0, R.string.dialer);
        dialerItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        dialerItem.setIcon(sbDialIconResId);

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
            case DIALER_ID:
                Activity dialerAct = getActivity();
                if (dialerAct != null) {
                    ((ContactsFragmentActivity) dialerAct)
                            .setCurrentItem(DIALER_ID);
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

}
