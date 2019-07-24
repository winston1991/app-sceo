package com.huntkey.software.sceo;

import com.huntkey.software.sceo.utils.Settings;

/**
 * 常量
 * @author chenliang3
 *
 */
public class Conf{
	public static int RELEASE_TYPE = 1;//0-开发环境,1-正式环境,其他-测试环境
	/**
	 * 服务器请求地址
	 */
//	public static String g_SERVICE_URL = "http://192.168.12.107/www/sceosrv/csp/sceosrv.dll?";
	public static String g_SERVICE_URL = "http://192.168.12.107/www/";
	public static String SERVICE_URL = "";
	//下载包类型
	public static String APP_PREFIX = "";
	//下载包保存路径
	public static final String savePath = "/sceo/app/";
	//下载包保存名
	public static final String saveFileName = "sceo.apk";
		
	static{
		if(RELEASE_TYPE == 0){
			//开发环境
			g_SERVICE_URL = "http://192.168.24.89/";
			SERVICE_URL = g_SERVICE_URL;
			APP_PREFIX = "sde";
		}else if(RELEASE_TYPE == 1){
			//正式环境
			g_SERVICE_URL = "http://app.huntkey.com:1818/www/";
			APP_PREFIX = "";
		}else{
			//测试环境
			g_SERVICE_URL = "http://192.168.12.107/www/";
			APP_PREFIX = "test";
		}
	}

	public static String getSERVICE_URL() {
		return SERVICE_URL;
	}

	public static void setSERVICE_URL(String sERVICE_URL) {
		SERVICE_URL = sERVICE_URL;
	}
	
}
