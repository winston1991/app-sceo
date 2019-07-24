package com.huntkey.software.sceo.bean.eventbus;

/**
 * Created by chenl on 2017/4/6.
 */

public class PrintMsgEvent {

    public int type;
    public String msg;

    public PrintMsgEvent(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

}
