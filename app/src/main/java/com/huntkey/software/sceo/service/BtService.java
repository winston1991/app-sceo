package com.huntkey.software.sceo.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.GPrinterCommand;
import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.PrintQueue;
import com.huntkey.software.sceo.utils.SceoUtil;

import java.util.ArrayList;

/**
 * Created by chenl on 2017/6/13.
 */

public class BtService extends IntentService {

    public BtService() {
        super("BtService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BtService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null){
            return;
        }
        if (intent.getAction().equals(SceoUtil.ACTION_PRINT_BITMAP)){
            printBitmap((ArrayList<byte[]>) intent.getSerializableExtra("data"));
        }
    }

    //批量打印图片
    private void printBitmap(ArrayList<byte[]> printBytes) {
        String tmpStr = "\n\n\n";
        ArrayList<byte[]> byteList = new ArrayList<>();
        for (byte[] item : printBytes){
            byteList.add(GPrinterCommand.reset);
            byteList.add(GPrinterCommand.print);
            byteList.add(item);
            byteList.add(GPrinterCommand.reset);
            byteList.add(GPrinterCommand.print);
            byteList.add(tmpStr.getBytes());
        }

        PrintQueue.getQueue(getApplicationContext()).add(byteList);
    }
}
