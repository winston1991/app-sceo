package com.huntkey.software.sceo.ui.activity;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.LJWebView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * webview
 * @author chenliang3
 *
 */
public class WebViewActivity extends BaseActivity {

	@ViewInject(R.id.webview_title)
	BackTitle title;
	@ViewInject(R.id.webview_ljwebview)
	LJWebView ljWebView;
	
	private String url;
	private String titleName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		ViewUtils.inject(this);
		
		url = getIntent().getStringExtra("webUrl")+"&sessionkey="+SceoUtil.getSessionKey(WebViewActivity.this)+
				"&sysguid="+SceoUtil.getSessionKey(WebViewActivity.this);
		titleName = getIntent().getStringExtra("titleName");
		
		if(!url.contains("http://")){
			url=Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?" + url;
		}
		
		initTitle();
		initWebView();
	}
	
	/**
	 * 横竖屏切换时，如果设置了android:configChanges="orientation|keyboardHidden"
	 * 则不会重新调用各生命周期，只会调用onConfigurationChanged方法
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
			title.setVisibility(View.VISIBLE);
		}else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
			title.setVisibility(View.GONE);
		}
	}
	
	private void initWebView() {
		ljWebView.setBarHeight(8);
		ljWebView.setClickable(true);
		ljWebView.setUseWideViewPort(true);
		ljWebView.setSupportZoom(true);
		ljWebView.setBuiltInZoomControls(true);
		ljWebView.setJavaScriptEnabled(true);
		ljWebView.setCacheMode(WebSettings.LOAD_DEFAULT);
		ljWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
		});
		
		ljWebView.loadUrl(url);
		ljWebView.setWebViewOnKeyListener(true);
	}

	private void initTitle() {
		if (titleName != null && !"".equals(titleName)) {
			title.setBackTitle(titleName);
		}else {			
			title.setBackTitle("详情");
		}
		title.setBackTitleColor(getResources().getColor(R.color.white));
		
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
}
