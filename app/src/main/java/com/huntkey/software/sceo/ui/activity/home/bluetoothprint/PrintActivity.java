package com.huntkey.software.sceo.ui.activity.home.bluetoothprint;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.eventbus.PrintMsgEvent;
import com.huntkey.software.sceo.entity.OutputPrintItem;
import com.huntkey.software.sceo.service.BtService;
import com.huntkey.software.sceo.utils.BluetoothUtil;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BluetoothPrintView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/5.
 */

public class PrintActivity extends KnifeBaseActivity {

    @BindView(R.id.print_toolbar)
    Toolbar toolbar;
    @BindView(R.id.print_layout_print)
    LinearLayout printLayout;
    @BindView(R.id.print_bt_status)
    TextView btStatusTv;
    @BindView(R.id.print_bt_switch)
    Switch mSwitch;
    @BindView(R.id.print_bind_device)
    TextView btDeviceTv;

    private BluetoothAdapter bluetoothAdapter;
    private boolean bondEnable = true;

    private List<OutputPrintItem> btList;
    private List<BluetoothPrintView> printViewList = new ArrayList<>();
    private String ids;
    private String reprint;
    private boolean flag;//是否为手工打印

    @Override
    protected int getContentViewId() {
        return R.layout.activity_print;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "打印");

        if (!EventBus.getDefault().isRegistered(this)){
            EventBusUtil.getInstence().register(this);
        }

        btList = (List<OutputPrintItem>) getIntent().getSerializableExtra("data");
        ids = getIntent().getStringExtra("ids");
        reprint = getIntent().getStringExtra("reprint");
        flag = getIntent().getBooleanExtra("flag", false);

        init();
        doQrwork();
    }

    private void doQrwork() {
        for (OutputPrintItem item : btList){
            final BluetoothPrintView view = new BluetoothPrintView(this, null, this);
            view.setDcTv(item.getBcls_dc());
            view.setIdTv(SceoUtil.getEmpCode(PrintActivity.this));
            view.setPnTv(item.getBcls_part());
            view.setQtyTv(item.getBcls_qty());
            view.setSnTv(item.getBcls_sn());
            view.setVdTv(item.getBcls_vend());
            view.qrMaker(item.getBcls_lot());
            view.setSplNameTv(item.getSpl_name());
            printLayout.addView(view);
            printViewList.add(view);
        }
    }

    private void refresh(){
        printViewList.clear();
        printLayout.removeAllViews();
        doQrwork();
    }

    private void init() {
        if (null == bluetoothAdapter){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (null == bluetoothAdapter){
            btDeviceTv.setText("该设备没有蓝牙模块");
            bondEnable = false;
            return;
        }

        if (BluetoothUtil.isOpen(bluetoothAdapter)){
            mSwitch.setChecked(true);
            btStatusTv.setText("开启");
        }else {
            mSwitch.setChecked(false);
            btStatusTv.setText("关闭");
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    openBluetooth();
                    btStatusTv.setText("开启");
                }else {
                    closeBluetooth();
                    btStatusTv.setText("关闭");
                }
            }
        });

        String address = SceoUtil.getDefaultBluetoothDeviceAddress(this);
        if (TextUtils.isEmpty(address)){
            btDeviceTv.setText("尚未绑定蓝牙设备");
            return;
        }
        String name = SceoUtil.getDefaultBluetoothDeviceName(this);
        btDeviceTv.setText("已绑定蓝牙：" + name);
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
        Toasty.info(this, "正在打开蓝牙", Toast.LENGTH_SHORT, true).show();
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth(){
        bluetoothAdapter.disable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED){
            Toasty.warning(this, "您已拒绝使用蓝牙", Toast.LENGTH_SHORT, true).show();
            finish();
        }else if (requestCode == 2 && resultCode == 0x1134){
            btDeviceTv.setText(SceoUtil.getDefaultBluetoothDeviceName(PrintActivity.this));
        }
    }

    @OnClick(R.id.print_layout2)
    void bondDevice(){
        if (bondEnable){
            Intent intent = new Intent(PrintActivity.this, BluetoothActivity.class);
            startActivityForResult(intent, 2);
        }
    }

    //批量打印
    @OnClick(R.id.btn_print)
    void print(){
        if (TextUtils.isEmpty(SceoUtil.getDefaultBluetoothDeviceAddress(this))){
            Toasty.warning(PrintActivity.this, "尚未绑定蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<byte[]> printBytes = new ArrayList<>();
        for (BluetoothPrintView view : printViewList){
            printBytes.add(convertView2Byte(view));
        }
//        printBitmap(printBytes);
        Intent intent = new Intent(PrintActivity.this, BtService.class);
        intent.putExtra("data", printBytes);
        intent.setAction(SceoUtil.ACTION_PRINT_BITMAP);
        startService(intent);
        updatePrintStatus();
        refresh();
    }

    private void updatePrintStatus(){
        if (flag){
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Update")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("ids", ids)
                .params("reprint", reprint)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
//                            Toasty.success(PrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PrintActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    //将view转换为byte
    private byte[] convertView2Byte(View view){
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(20, 20, view.getMeasuredWidth()+20, view.getMeasuredHeight()+20);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();

        return bytes;
    }

//    //批量打印图片
//    private void printBitmap(ArrayList<byte[]> printBytes) {
//        ArrayList<byte[]> byteList = new ArrayList<>();
//        for (byte[] item : printBytes){
//            byteList.add(GPrinterCommand.reset);
//            byteList.add(GPrinterCommand.print);
//            byteList.add(item);
//            byteList.add(GPrinterCommand.reset);
//            byteList.add(GPrinterCommand.print);
//            byteList.add(GPrinterCommand.walkPaper(Byte.parseByte("2")));
//        }
//
//        PrintQueue.getQueue(getApplicationContext()).add(byteList);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        PrintQueue.getQueue(this).disconnect();
        if (EventBus.getDefault().isRegistered(this)){
            EventBusUtil.getInstence().unregister(this);
        }
    }

    @Subscribe
    public void onEventMainThread(PrintMsgEvent event){
        if (event.type == 2) {
            Toasty.info(this, event.msg, Toast.LENGTH_SHORT, true).show();
        }
    }
}
