package com.huntkey.software.sceo.ui.activity.linkman;

import android.os.Bundle;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.fragment.LinkmanFragment;

public class LinkmanActivity extends BaseActivity {

	private LinkmanFragment linkmanFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_linkman);
		
		if (savedInstanceState == null) {
			linkmanFragment = new LinkmanFragment();
			getSupportFragmentManager().beginTransaction()
				.add(R.id.activity_linkman_container, linkmanFragment)
				.commit();
		}
	}
	
}
