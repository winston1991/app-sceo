package com.huntkey.software.sceo.ui.activity.home.instorage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.widget.ClearEditText;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/3/29.
 */

public class InputTaskNumActivity extends KnifeBaseActivity {

    @BindView(R.id.is_toolbar)
    Toolbar toolbar;
    @BindView(R.id.is_edittext)
    ClearEditText nbrEt;

    private int isDirect = 1;//0-点货 1-直接入库

    @Override
    protected int getContentViewId() {
        return R.layout.activity_inputtasknum;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "入库扫描作业");

        nbrEt.requestFocus();
        nbrEt.setOnKeyListener(new MyOnKeyListener());

        String nbr = getIntent().getStringExtra("nbr");
        nbrEt.setText(nbr);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                nextStep();
            }
            return false;
        }
    }

    @OnClick(R.id.is_checkbtn)
    void doCheck(){
        isDirect = 0;
    }

    @OnClick(R.id.is_gobtn)
    void doDirect(){
        isDirect = 1;
    }

    @OnClick(R.id.is_btn)
    void nextStep(){
        if (nbrEt.getText().toString().trim().length() == 0){
            Toasty.warning(this, "任务单号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (0 == isDirect){//点货
            Intent intent = new Intent(InputTaskNumActivity.this, WarehousingActivity.class);
            intent.putExtra("nbr", nbrEt.getText().toString().trim());
            startActivity(intent);
        }else if (1 == isDirect){//直接入库
            Intent intent = new Intent(InputTaskNumActivity.this, DirectWarehousingActivity.class);
            intent.putExtra("nbr", nbrEt.getText().toString().trim());
            startActivity(intent);
        }
    }
}
