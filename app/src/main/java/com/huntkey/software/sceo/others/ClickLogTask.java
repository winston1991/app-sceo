package com.huntkey.software.sceo.others;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
/**
 * 
 * @author chenliang3
 * logdesc 菜单名称
 * pagecode 菜单编码
 * logtype 2-点击菜单  3-退出系统
 *
 */
public class ClickLogTask extends AsyncTask<Object, Integer, String> {

	@Override
	protected String doInBackground(Object... obj) {
		Context context = (Context)obj[0];
		String logdesc = (String)obj[1];
		String pagecode = (String)obj[2];
		final int logtype = (int)obj[3];
		final int sysExit = (int)obj[4];//主界面退出的时候传1以传数据到服务器后再sysExit
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(context));
		params.addBodyParameter("uid", SceoUtil.getDeviceId(context));
		params.addBodyParameter("hostname", android.os.Build.MODEL);
		params.addBodyParameter("sysver", android.os.Build.VERSION.RELEASE);
		params.addBodyParameter("logdesc", logdesc);
		params.addBodyParameter("pagecode", pagecode);
		params.addBodyParameter("logtype", logtype+"");
		params.addBodyParameter("versionname", Settings.getVersionName(context));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=SaveSyLog&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						if (sysExit == 1) {								
							System.exit(0);
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (sysExit == 1) {								
							System.exit(0);
//							SceoApplication.getInstance().exit();
						}
					}
				});
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

}
