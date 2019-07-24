package com.huntkey.software.sceo.ui.activity.leave;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.LeaveInitType;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.LeaveTypeAdapter;
import com.huntkey.software.sceo.widget.BackTitle;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 请假类型
 * @author chenliang3
 *
 */
public class LeaveTypeActivity extends BaseActivity {

	@ViewInject(R.id.leave_type_title)
	BackTitle title;
	@ViewInject(R.id.leave_type_listview)
	ListView listView;
	
	private List<LeaveInitType> types;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave_type);
		ViewUtils.inject(this);
		
		types = (List<LeaveInitType>) getIntent().getSerializableExtra("leaveTypes");
		
		initTitle();
		initView();
	}

	private void initView() {
		LeaveTypeAdapter typeAdapter = new LeaveTypeAdapter(LeaveTypeActivity.this, types);
		listView.setAdapter(typeAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent();
				intent.putExtra("typeCode", types.get(position).getAdlt_code());
				intent.putExtra("typeNmae", types.get(position).getAdlt_name());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initTitle() {
		title.setBackTitle("选择请假类型");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
