package com.huntkey.software.sceo.base;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenl on 2017/3/30.
 */

public class AppManager {

    //getSimpleName()返回基础类的名称
    private static final String TAG = AppManager.class.getSimpleName();

    private static AppManager instance = null;
    private static List<Activity> mActivities = new LinkedList<Activity>();

    private AppManager(){

    }

    //单一实例
    public synchronized static AppManager getInstance(){
        if (instance == null){
            instance = new AppManager();
        }
        return instance;
    }

    public int size(){
        return mActivities.size();
    }

    /**
     * 获取当前Activity
     * @return
     */
    public synchronized Activity getForwardActivity(){
        return size() > 0 ? mActivities.get(size() - 1) : null;
    }

    /**
     * 添加activity
     * @param activity
     */
    public synchronized void addActivity(Activity activity){
        mActivities.add(activity);
    }

    /**
     * 移除activity
     * @param activity
     */
    public synchronized void removeActivity(Activity activity){
        if (mActivities.contains(activity)){
            mActivities.remove(activity);
        }
    }

    /**
     * 结束所有activity
     */
    public synchronized void clear(){
        for (int i = mActivities.size() - 1; i > -1; i--){
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size();
        }
    }

    /**
     * 结束topActivity
     */
    public synchronized void clearTop(){
        for (int i = mActivities.size() - 2; i > -1; i--){
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
            i = mActivities.size() - 1;
        }
    }

    public synchronized void finishSingleActivity(Activity activity) {
        if (activity != null) {
            if (mActivities.contains(activity)) {
                mActivities.remove(activity);
            }
            activity.finish();
            activity = null;
        }
    }

    public synchronized void finishSingleActivityByClass(Class<?> cls) {
        Activity tempActivity = null;
        for (Activity activity : mActivities) {
            if (activity.getClass().equals(cls)) {
                tempActivity = activity;
            }
        }
        finishSingleActivity(tempActivity);
    }

}
