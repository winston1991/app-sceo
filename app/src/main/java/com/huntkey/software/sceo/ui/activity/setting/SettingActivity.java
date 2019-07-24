package com.huntkey.software.sceo.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.chat.ChatTimeSettingActivity;
import com.huntkey.software.sceo.ui.adapter.SettingAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 设置
 * @author chenliang3
 *
 */
public class SettingActivity extends BaseActivity {

	@ViewInject(R.id.setting_listview)
	ListView listView;
	@ViewInject(R.id.setting_title)
	BackTitle title;
	
	private String[] titles = {"超时退出设置", "启用手势密码"};
	private int[] images = {R.drawable.ic_alarm, R.drawable.ic_lock};
	private boolean gestureState;
	private SettingAdapter adapter;
	
	private int GESTURE_EDIT_REQUESTCODE = 0;//手势密码设置请求码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);
		
		initTitle();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		if (SceoUtil.getGesturePassword(SettingActivity.this) != null && !"".equals(SceoUtil.getGesturePassword(SettingActivity.this))) {
			gestureState = true;
		}else {
			gestureState = false;
		}
		
		adapter = new SettingAdapter(this, titles, images, gestureState);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
				case 0:
					Intent intent = new Intent(SettingActivity.this, ChatTimeSettingActivity.class);
					int time = SceoUtil.getOutTimeExit(SettingActivity.this)/1000;
					intent.putExtra("time", time);
					intent.putExtra("flag", "meFragment");
					startActivity(intent);
					break;
				case 1:
					if (SceoUtil.getGesturePassword(SettingActivity.this) != null && !"".equals(SceoUtil.getGesturePassword(SettingActivity.this))) {
						Intent intent2 = new Intent(SettingActivity.this, GestureSetActivity.class);
						startActivityForResult(intent2, GESTURE_EDIT_REQUESTCODE);
					}else {						
						Intent intent2 = new Intent(SettingActivity.this, GestureEditActivity.class);
						startActivityForResult(intent2, GESTURE_EDIT_REQUESTCODE);
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void initTitle() {
		title.setBackTitle("设置");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GESTURE_EDIT_REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				gestureState = data.getBooleanExtra("gestureState", false);
				adapter.updateGestureState(gestureState);
				adapter.notifyDataSetChanged();
			}else if (resultCode == 0x11) {
				gestureState = data.getBooleanExtra("gestureStatus", false);
				adapter.updateGestureState(gestureState);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
}
