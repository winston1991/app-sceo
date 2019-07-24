package com.huntkey.software.sceo.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chenl on 2017/3/28.
 * EventBus 单例
 */

public class EventBusUtil {

    private static EventBus singleton;

    public static EventBus getInstence(){
        if (singleton == null){
            synchronized (EventBusUtil.class){
                if (singleton == null){
                    singleton = EventBus.getDefault();
                }
            }
        }
        return singleton;
    }

}
