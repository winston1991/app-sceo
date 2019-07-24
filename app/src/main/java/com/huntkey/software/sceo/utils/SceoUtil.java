package com.huntkey.software.sceo.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huntkey.software.sceo.bean.EmpInfo;
import com.huntkey.software.sceo.entity.Domain;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 公共处理类
 *
 * @author chenliang3
 */
public class SceoUtil {

    public static final String SAVE_PATH = "/sceo/";//文件保存路径
    public static final String SHARE_NAME = "sceo";//共享存储的库名称
    public static final int SHARE_MODE = Context.MODE_PRIVATE;//共享存储的模式(仅sceo有权限读写)
    public static final String STRING_EMPTY = "";//空字符串
    public static final int INT_NEGATIVE = -1;//-1
    public static final int DEFAULT_INTERVAL = 6000;//默认向服务器发送请求的时间间隔：6s
    public static final int DEFAULT_INTERVAL2 = 1000;//默认向服务器发送请求的时间间隔：1s
    public static final int DEFAULT_OUTTIME_EXIT = 300;//默认超时退出时间：5min

    public static final String SHARE_EMP_EXIT = "sceo_exit";//存储键：是否存在用户
    public static final String SHARE_EMP_SESSIONKEY = "sceo_emp_sessionkey";//存储键：令牌
    public static final String SHARE_EMP_ID = "sceo_emp_id";//存储键：用户id
    public static final String SHARE_EMP_NAME = "sceo_emp_name";//存储键：用户名
    public static final String SHARE_EMP_SEX = "sceo_emp_sex";//存储键：用户性别
    public static final String SHARE_EMP_PHOTO = "sceo_emp_photo";//存储键：用户头像

    public static final String SHARE_EMP_ACCOUNT = "sceo_emp_account";//存储键：登录账号
    public static final String SHARE_EMP_PWD = "sceo_emp_pwd";//存储键：登录密码

    public static final String SHARE_EMP_INVOICE = "sceo_emp_invoice";//存储键：待审单据数量
    public static final String SHARE_AFFAIRS_NO = "sceo_affairs_number";//存储键：待办事务数量

    public static final String SHARE_HEARTBEAT_INTERVAL = "sceo_heartbeat_interval";//存储键：向服务器发送请求的间隔时间
    public static final String SHARE_HEARTBEAT_INTERVAL2 = "sceo_heartbeat_interval2";//存储键：向服务器发送请求的间隔时间
    public static final String SHARE_OUTTIME_EXIT = "sceo_outtime_exit";//存储键：超时退出

    public static final String SHARE_TMP_CHAT_SELECTION = "sceo_tmp_chat_selection";//存储键：暂存chat的list点击位置
    public static final String SHARE_IS_NAME_SHOW = "sceo_is_name_show";//存储键：是否显示姓名

    public static final String SHARE_IS_COLLECT_CHANGE = "sceo_is_collect_change";//存储键：收藏/取消是否有点击
    public static final String SHARE_IS_CREAT_NEW_AFFAIRS = "sceo_is_creat_new_affairs";//存储键：是否新建了事务
    public static final String SHARE_ONCE_WORK = "sceo_once_work";//存储键：只作用一次,第二次失效

    public static final String SHARE_TMP_DROPMENU_TYPE_SELECTION = "sceo_tmp_dropmenu_type_selection";//暂存待审单据dropmenu的选择项
    public static final String SHARE_TMP_DROPMENU_TIME_SELECTION = "sceo_tmp_dropmenu_time_selection";
    public static final String SHARE_TMP_DROPMENU_ORDER_SELECTION = "sceo_tmp_dropmenu_order_selection";

    public static final String SHARE_LOCATION = "sceo_location";//存储键：保存地域信息
    public static final String SHARE_ACCTID = "sceo_acctid";//存储键：保存所选公司的id
    public static final String SHARE_SERVICE_URL = "sceo_service_url";//存储键：保存所选的url

    public static final String SHARE_HEARTBEAT_SERVICE_LOADING = "sceo_heartbeat_service_loading";//当chatActivity正在请求数据的时候不让service请求

    public static final String ACTION_CHAT_MESSAGE_RECEIVER =
            "com.huntkey.software.sceo.chat.message.receiver";//当chatActivity可见时通知更新消息广播
    public static final String ACTION_AFFAIRS_REFRESH_RECEIVER =
            "com.huntkey.software.sceo.affairs.refresh.receiver";//当MainActivity可见时通知affairs刷新广播
    public static final String ACTION_INVOICE_GOT_NEWS =
            "com.huntkey.software.sceo.invoice.got.news";//当MainActivity可见时有新事物给invoice通知
    public static final String ACTION_HAS_NEW_VERSION =
            "com.huntkey.software.sceo.hasnewversion";//是否有版本升级的小红点逻辑处理

    public static final String HAS_NEW_VENSION = "sceo_has_new vension";//是否有新版本
    public static final String FORM_EDIT_CHANGE = "sceo_form_edit_change";//即时快报编辑界面是否有变化

    public static final String SHARE_GESTURE_PASSWORD = "sceo_gesture_password";//手势密码

    public static final String SHARE_FORM_CCCODE = "sceo_form_cccode";//即时快报部门编码
    public static final String SHARE_FORM_CCNAME = "sceo_form_ccname";//即时快报部门名称
    public static final String SHARE_FORM_CCLEVEL = "sceo_form_cclevel";//即时快报部门名称
    public static final String SHARE_FORM_IS_INCLUDE_SUB = "sceo_is_include_sub";//是否含下级
    public static final String SHARE_APP_FLAG = "sceo_app_flag";//是否申请了手机权限
    public static final String SHARE_CURRENT_YMD = "sceo_current_ymd";//当前年月日

    public static final String SHARE_DEFAULT_BLUETOOTH_ADDRESS = "sceo_default_bluetooth_address";//蓝牙设备地址
    public static final String SHARE_DEFAULT_BLUETOOTH_NAME = "sceo_default_bluetooth_name";//蓝牙设备名称
    public static final String SHARE_CHOOSED_PRINTER_ID = "sceo_choosed_printer_id";//选择的打印机id

    public static final String SHARE_KEYBOARD_PERMISSION = "sceo_keyboard_permission";//软键盘使用权限

    public static final String ACTION_PRINT_BITMAP = "action_print_bitmap";

    /**
     * 手势密码点的状态
     */
    public static final int POINT_STATE_NORMAL = 0;//正常状态
    public static final int POINT_STATE_SELECTED = 1;//按下状态
    public static final int POINT_STATE_WRONG = 2;//错误状态

    public static final String SHARE_LOCATION_URL = "sceo_location_url";

    private static final Gson gson = new Gson();

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARE_NAME, SHARE_MODE);
    }

    /**
     * 移除所有preference
     *
     * @param context
     */
    public static void removeAllPreference(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存string类型数据到共享存储中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void shareSet(Context context, String key, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取共享存储中的string类型数据值
     *
     * @param context
     * @param key
     * @return
     */
    public static String shareGetString(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(key, STRING_EMPTY);
    }

    /**
     * 保存boolean类型数据到共享存储中
     *
     * @param context
     * @param key
     * @param flag
     */
    public static void shareSet(Context context, String key, boolean flag) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(key, flag).commit();
    }

    /**
     * 获取共享存储中的boolean类型数据值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean shareGetBoolean(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    /**
     * 保存int类型数据到共享存储中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void shareSet(Context context, String key, int value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 获取共享存储中的int类型数据值
     *
     * @param context
     * @param key
     * @return
     */
    public static int shareGetInt(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(key, INT_NEGATIVE);
    }

    /**
     * 存储用户
     *
     * @param context
     * @param info
     */
    public static void saveEmp(Context context, EmpInfo info) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_EMP_SESSIONKEY, info.getSessionkey())
                .putString(SHARE_EMP_ID, info.getEmp_id())
                .putString(SHARE_EMP_NAME, info.getEmp_name())
                .putString(SHARE_EMP_PHOTO, info.getEmp_photo())
                .putString(getAcctid(context) + SHARE_FORM_CCCODE, info.getUpper_code())
                .putString(getAcctid(context) + SHARE_FORM_CCNAME, info.getUpper_desc())
                .putString(getAcctid(context) + SHARE_FORM_IS_INCLUDE_SUB, info.getInclude())
                .putString(getAcctid(context) + SHARE_APP_FLAG, info.getAppflag())
                .putString("DOMAIN", "")
                .putInt("DOMAINACCTID", INT_NEGATIVE)
                .commit();
    }

    /**
     * 获取sessionKey
     */
    public static String getSessionKey(Context context) {
        String sessionkey = getDomain(context);
        if (TextUtils.isEmpty(sessionkey)) {
            SharedPreferences sp = getSharedPreferences(context);
            sessionkey = sp.getString(SHARE_EMP_SESSIONKEY, STRING_EMPTY);
        }
        return sessionkey;
    }

    public static void setSessionKey(Context context, String sessionkey) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_EMP_SESSIONKEY, sessionkey).commit();
    }

    /**
     * 获取用户名称
     */
    public static String getEmpName(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_EMP_NAME, STRING_EMPTY);
    }

    /**
     * 获取empCode(以员工号为id)
     */
    public static String getEmpCode(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_EMP_ID, STRING_EMPTY);
    }

    /**
     * 获取用户头像
     */
    public static String getEmpPhoto(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_EMP_PHOTO, STRING_EMPTY);
    }

    /**
     * 获取即时快报起始部门id
     */
    public static String getFormCCCode(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(getAcctid(context) + SHARE_FORM_CCCODE, STRING_EMPTY);
    }

    /**
     * 获取即时快报起始部门名称
     */
    public static String getFormCCName(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(getAcctid(context) + SHARE_FORM_CCNAME, STRING_EMPTY);
    }

    /**
     * 获取即时快报是否包含下级
     */
    public static String getFormIsIncludeSub(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(getAcctid(context) + SHARE_FORM_IS_INCLUDE_SUB, STRING_EMPTY);
    }

    /**
     * 获取是否申请了手机app权限
     */
    public static String getAppFlag(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(getAcctid(context) + SHARE_APP_FLAG, STRING_EMPTY);
    }

    /**
     * 深圳/合肥/河源
     */
    public static void setLocation(Context context, int value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_LOCATION, value).commit();
    }

    /**
     * 默认为深圳,location=0
     */
    public static int getLocation(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_LOCATION, 0);
    }

    /**
     * 深圳/合肥/河源的id
     */
    public static void setAcctid(Context context, int value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_ACCTID, value).commit();
    }

    /**
     * 获取公司id
     */
    public static int getAcctid(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_ACCTID, INT_NEGATIVE);
    }

    /**
     * 保存所选的url
     */
    public static void setServiceUrl(Context context, String url) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_SERVICE_URL, url).commit();
    }

    /**
     * 获取所选的url
     */
    public static String getServiceUrl(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_SERVICE_URL, STRING_EMPTY);
    }

    /**
     * 存储向服务器发送请求的时间间隔(6s)
     */
    public static void setInterval(Context context, int interval) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_HEARTBEAT_INTERVAL, interval).commit();
    }

    /**
     * 获取向服务器发送请求的时间间隔(6s)
     */
    public static int getInterval(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_HEARTBEAT_INTERVAL, DEFAULT_INTERVAL);//默认向服务器发送请求的时间间隔：6s
    }

    /**
     * 存储向服务器发送请求的时间间隔(10min)
     */
    public static void setInterval2(Context context, int interval) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_HEARTBEAT_INTERVAL2, interval).commit();
    }

    /**
     * 获取向服务器发送请求的时间间隔(10min)
     */
    public static int getInterval2(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_HEARTBEAT_INTERVAL2, DEFAULT_INTERVAL2);//默认向服务器发送请求的时间间隔：1min
    }

    /**
     * 存储超时退出时间
     */
    public static void setOutTimeExit(Context context, int time) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_OUTTIME_EXIT, time).commit();
    }

    /**
     * 获取超时退出时间
     */
    public static int getOutTimeExit(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_OUTTIME_EXIT, DEFAULT_OUTTIME_EXIT) * 1000;
    }

    /**
     * 暂存chatMsgList的点击位置
     */
    public static void setTmpChatSelection(Context context, int selection) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_TMP_CHAT_SELECTION, selection).commit();
    }

    /**
     * 获得chatMsgList的点击位置
     */
    public static int getTmpChatSelection(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_TMP_CHAT_SELECTION, INT_NEGATIVE);
    }

    /**
     * 暂存是否显示姓名bool值
     */
    public static void setIsNameShow(Context context, boolean isShow) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(SHARE_IS_NAME_SHOW, isShow).commit();
    }

    /**
     * 获取是否显示姓名的bool值
     */
    public static boolean getIsNameShow(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(SHARE_IS_NAME_SHOW, false);//默认不显示
    }

    /**
     * 存储是否点击了收藏/取消的bool值
     */
    public static void setIsCollectChange(Context context, boolean isClick) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(SHARE_IS_COLLECT_CHANGE, isClick).commit();
    }

    /**
     * 获取是否点击了收藏/取消的bool值
     */
    public static boolean getIsCollectChange(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(SHARE_IS_COLLECT_CHANGE, false);
    }

    /**
     * 存储是否新建了事务
     */
    public static void setIsCreatNewAffairs(Context context, boolean isCreat) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(SHARE_IS_CREAT_NEW_AFFAIRS, isCreat).commit();
    }

    /**
     * 获取是否新建了事务
     */
    public static boolean getIsCreatNewAffairs(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(SHARE_IS_CREAT_NEW_AFFAIRS, false);
    }

    /**
     * 存储只作用一次的bool值
     */
    public static void setOnceWork(Context context, boolean isWorked) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(SHARE_ONCE_WORK, isWorked).commit();
    }

    /**
     * 获取只作用一次的bool值
     */
    public static boolean getOnceWork(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(SHARE_ONCE_WORK, true);
    }

    /**
     * 存储待审单据dropmenu的type
     */
    public static void setDropmenuType(Context context, int type) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_TMP_DROPMENU_TYPE_SELECTION, type).commit();
    }

    /**
     * 获取待审单据dropmenu的type
     */
    public static int getDropmenuType(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_TMP_DROPMENU_TYPE_SELECTION, 0);
    }

    /**
     * 存储待审单据dropmenu的time
     */
    public static void setDropmenuTime(Context context, int time) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_TMP_DROPMENU_TIME_SELECTION, time).commit();
    }

    /**
     * 获取待审单据dropmenu的type
     */
    public static int getDropmenuTime(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_TMP_DROPMENU_TIME_SELECTION, 0);
    }

    /**
     * 存储待审单据dropmenu的type
     */
    public static void setDropmenuOrder(Context context, int order) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_TMP_DROPMENU_ORDER_SELECTION, order).commit();
    }

    /**
     * 获取待审单据dropmenu的type
     */
    public static int getDropmenuOrder(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_TMP_DROPMENU_ORDER_SELECTION, 0);
    }

    /**
     * 存储是否有新版本flag
     */
    public static void setHasNewVersion(Context context, boolean hasnew) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(HAS_NEW_VENSION, hasnew).commit();
    }

    /**
     * 获取是否有新版本flag
     */
    public static boolean getHasNewVersion(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(HAS_NEW_VENSION, false);
    }

    /**
     * 存储即时快报编辑界面是否有改动
     */
    public static void setFormEditChange(Context context, boolean isChange) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(FORM_EDIT_CHANGE, isChange).commit();
    }

    /**
     * 获取即时快报编辑界面是否有改动
     */
    public static boolean getFormEditChange(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(FORM_EDIT_CHANGE, false);
    }

    /**
     * 存储chatActivity是否在请求数据
     */
    public static void setCanServiceLoading(Context context, boolean isLoading) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean(SHARE_HEARTBEAT_SERVICE_LOADING, isLoading).commit();
    }

    /**
     * 获取chatActivity是否在请求数据
     */
    public static boolean getCanServiceLoading(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean(SHARE_HEARTBEAT_SERVICE_LOADING, false);
    }

    /**
     * 存储手势密码
     */
    public static void setGesturePassword(Context context, String password) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_GESTURE_PASSWORD, password).commit();
    }

    /**
     * 获取手势密码
     */
    public static String getGesturePassword(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_GESTURE_PASSWORD, STRING_EMPTY);
    }

    /**
     * 存储当前年月日
     */
    public static void setCurrentYMD(Context context, String ymd) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_CURRENT_YMD, ymd).commit();
    }

    /**
     * 获取当前年月日
     */
    public static String getCurrentYMD(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_CURRENT_YMD, STRING_EMPTY);
    }

    /**
     * 存储蓝牙地址
     */
    public static void setDefaultBluetoothDeviceAddress(Context context, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_DEFAULT_BLUETOOTH_ADDRESS, value).commit();
    }

    /**
     * 获取蓝牙地址
     */
    public static String getDefaultBluetoothDeviceAddress(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_DEFAULT_BLUETOOTH_ADDRESS, STRING_EMPTY);
    }

    /**
     * 存储蓝牙设备名
     */
    public static void setDefaultBluetoothDeviceName(Context context, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_DEFAULT_BLUETOOTH_NAME, value).commit();
    }

    /**
     * 获取蓝牙设备名
     */
    public static String getDefaultBluetoothDeviceName(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_DEFAULT_BLUETOOTH_NAME, STRING_EMPTY);
    }

    /**
     * 存储软键盘使用权限
     */
    public static void setKeyboardPermission(Context context, int value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putInt(SHARE_KEYBOARD_PERMISSION, value).commit();
    }

    /**
     * 获取软键盘使用权限
     */
    public static int getKeyboardPermission(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getInt(SHARE_KEYBOARD_PERMISSION, 1);
    }

    /**
     * 存储选择的打印机id
     */
    public static void setChoosedPrinterId(Context context, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(SHARE_CHOOSED_PRINTER_ID, value).commit();
    }

    /**
     * 获取选择的打印机id
     */
    public static String getChoosedPrinterId(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(SHARE_CHOOSED_PRINTER_ID, STRING_EMPTY);
    }

    /**
     * 获取文件的保存目录
     */
    public static String getSavePath(String fileName) {
        String path = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName;
        }
        return path;
    }

    /**
     * 获取当前设备号
     */
    public static String getDeviceUuid(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 获取当前手机的设备id
     * 使用双卡手机的时候有些用户因为切换手机卡导致不能登录，所以现在将deviceId和uuid都传给后台，符合其中一个即通过
     */
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice;
        tmDevice = "" + tm.getDeviceId();
        return tmDevice;
    }

    /**
     * dp-px
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 将json字符串转换成相应的java对象
     *
     * @param json
     * @param cls
     * @return
     */
    public static <T> T parseJson(String json, Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            return gson.fromJson("{\"status\":403,\"message\":\"返回数据错误\",\"data\":{}}", cls);
        } else {
            try {
                return gson.fromJson(json, cls);
            } catch (Exception e) {
                e.printStackTrace();
                return gson.fromJson("{\"status\":404,\"message\":\"请求失败\",\"data\":{}}", cls);
            }
        }
    }

    // 将对象转为字符串
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 重新登录操作(判断status)
     */
    public static void gotoLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    /**
     * scrollView嵌套listview只显示一行的解决办法
     * item布局必须为LinearLayout
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter   
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高   
            listItem.measure(0, 0);
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);
    }

    /**
     * 解决viewpager+fragment报
     * The specified child already has a parent. You must call removeView() on the child's parent first.
     * 的错误
     */
    public static void removeViewFromParent(View view) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
        }
    }

    /**
     * 判断activity是否在运行(可见)
     */
    public static boolean isActivityRunning(Context mContext, String activityClassName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            ComponentName component = info.get(0).topActivity;
            if (activityClassName.equals(component.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断应用是否在运行
     */
    public static boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> infos = am.getRunningTasks(100);
        for (RunningTaskInfo info : infos) {
            if (info.topActivity.getPackageName().equals(context.getPackageName()) &&
                    info.baseActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断service是否在运行
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> infos = activityManager.getRunningServices(40);
        if (infos.size() <= 0) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String name = infos.get(i).service.getClassName().toString();
            if (name.equals(serviceName)) {
                isWork = true;
                break;
            }
        }

        return isWork;
    }

    /**
     * 判断app是否打开运行状态
     */
    public static boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcessInfos) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    /**
     * 发送notification
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendLocatNotification(Context context, String title,
                                             String message, Intent mIntent, int notifId) {
        int appIconResId = 0;
        PendingIntent pIntent = null;
        if (mIntent != null) {
            pIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        final PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            appIconResId = applicationInfo.icon;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Notification notification = null;
        if (mIntent != null) {
            notification = new Notification.Builder(context)
                    .setSmallIcon(appIconResId)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(pIntent)
                    .getNotification();
        }
        //Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        //Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        //light
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000;

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // manager.notify(0, notification);
        manager.notify(notifId, notification);
    }

    /**
     * 设置切换域列表
     * @param context
     * @param domains
     */
    public static void setDomains(Context context, List<Domain.Lists.Item> domains) {
        shareSet(context, "DOMAINS", toJson(domains));
    }

    /**
     * 获取切换域列表
     * @param context
     * @return
     */
    public static List<Domain.Lists.Item> getDomains(Context context) {
        return gson.fromJson(shareGetString(context, "DOMAINS"), new TypeToken<List<Domain.Lists.Item>>() {
        }.getType());
    }

    /**
     * 设置切换域后的sessionkey
     * @param context
     * @param domain
     */
    public static void setDomain(Context context, String domain) {
        shareSet(context, "DOMAIN", domain);
    }

    /**
     * 获取切换域后的sessionkey
     * @param context
     * @return
     */
    public static String getDomain(Context context) {
        return shareGetString(context, "DOMAIN");
    }

    /**
     * 设置切换域后的acctid
     * @param context
     * @param acctid
     */
    public static void setDomainAcctid(Context context, int acctid) {
        shareSet(context, "DOMAINACCTID", acctid);
    }

    /**
     * 获取切换域后的acctid
     * @param context
     * @return
     */
    public static int getDomainAcctid(Context context) {
        return shareGetInt(context, "DOMAINACCTID");
    }
}
