package com.valuestudio.contacts.ui;

import android.os.Bundle;

import com.valuestudio.contacts.R;
import com.valuestudio.contacts.base.BaseActivity;

public class CheckUpdateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_update_activity);
    }

    @Override
    protected void onSlideRight() {
        super.onSlideRight();
        onBackPressed();
    }

}
