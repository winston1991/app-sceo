package com.huntkey.software.sceo.ui.activity.forms;

import android.os.Bundle;
import android.util.Log;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.fragment.NavFragment;

/**
 * Created by chenl on 2018/7/17.
 */

public class FormActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        String code;
        String theTitle;
        String issys;

        code = getIntent().getStringExtra("code");
        theTitle = getIntent().getStringExtra("title");
        issys = getIntent().getStringExtra("issys");

        NavFragment navFragment = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.content_fragment);
        navFragment.setParams(code, theTitle, issys);
    }
}
