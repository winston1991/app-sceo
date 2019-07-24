package com.huntkey.software.sceo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import com.huntkey.software.sceo.R;

/**
 * 加载动画dialog
 * @author chenliang3
 *
 */
public class LoadingUncancelable extends AlertDialog implements DialogInterface.OnKeyListener {

	public LoadingUncancelable(Context context) {
		super(context, R.style.NoBackDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_loading);
//		setCanceledOnTouchOutside(false);
		setCancelable(false);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			return true;
		}else {
			return false;
		}
	}
}
