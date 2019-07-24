//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                              _oo0oo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             0\  =  /0
//                           ___/`---‘\___
//                        .' \\\|     |// '.
//                       / \\\|||  :  |||// \\
//                      / _ ||||| -:- |||||- \\
//                      | |  \\\\  -  /// |   |
//                      | \_|  ''\---/''  |_/ |
//                      \  .-\__  '-'  __/-.  /
//                    ___'. .'  /--.--\  '. .'___
//                 ."" '<  '.___\_<|>_/___.' >'  "".
//                | | : '-  \'.;'\ _ /';.'/ - ' : | |
//                \  \ '_.   \_ __\ /__ _/   .-' /  /
//            ====='-.____'.___ \_____/___.-'____.-'=====
//                              '=---='
//
//
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                         佛祖保佑                 永无BUG
package com.huntkey.software.sceo;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.huntkey.software.sceo.others.CrashHandler;
import com.huntkey.software.sceo.others.MyActivityLifecycleCallbacks;
import com.huntkey.software.sceo.ui.activity.attence.LocationServer;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 
 * @author chenliang3
 *
 */
public class SceoApplication extends Application {

	private List<Activity> mList = new LinkedList<Activity>();//记录所有启动的activity
	private static SceoApplication instance = null;
	public Bitmap cropped = null;
	public LocationServer locationServer;

	@Override
	public void onCreate() {
		instance = this;
		//异常处理--正式发布的时候将以下两句打开以捕获异常
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		super.onCreate();

		//注册生命周期
		this.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks(getApplicationContext()));

		initOkGo();

		initRealm();

		locationServer = new LocationServer(getApplicationContext());
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);
	}

	private void initRealm(){
		Realm.init(this);
		RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
				.deleteRealmIfMigrationNeeded()
				.name("sceo.realm")
				.build();
		Realm.setDefaultConfiguration(realmConfiguration);
	}

	private void initOkGo() {
		OkGo.init(this);
		try {
			OkGo.getInstance()
					.debug("OkGo", Level.INFO, true)// 打开该调试
					.setConnectTimeout(2 * OkGo.DEFAULT_MILLISECONDS)
					.setReadTimeOut(2 * OkGo.DEFAULT_MILLISECONDS)// 全局的读取超时时间
					.setWriteTimeOut(2 * OkGo.DEFAULT_MILLISECONDS)// 全局的写入超时时间
					.setCacheMode(CacheMode.NO_CACHE)// 全局统一设置缓存模式,默认是不使用缓存
					.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)// 全局统一设置缓存时间,默认永不过期
					.setRetryCount(3)// 设置超时重连次数
					.setCertificates();// 信任所有证书,不安全有风险
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static SceoApplication getInstance(){
		return instance;
	}
	
	public void addActivity(Activity activity){
		mList.add(activity);
	}

	public void removeActivity(Activity activity){
		mList.remove(activity);
	}
	
	/**
	 * 退出
	 */
	public void exit(){
		try {
			for (Activity activity : mList) {
				if (activity != null) {
					activity.finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			System.exit(0);//这里的exit只做重新登录时结束以前打开的activity用，所以不system.exit(0)
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();//垃圾回收
	}
	
}
