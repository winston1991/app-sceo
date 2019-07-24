package com.huntkey.software.sceo.ui;

import com.huntkey.software.sceo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.widget.ProgressBar;

import java.util.List;

/**
 * 加载动画dialog
 * @author chenliang3
 *
 */
public class LoadingDialog extends AlertDialog {

	public LoadingDialog(Context context) {
		super(context, R.style.NoBackDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_loading);
		setCanceledOnTouchOutside(false);
	}
}
