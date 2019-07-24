package com.huntkey.software.sceo.ui.activity.home.bluetoothprint;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.ui.adapter.home.BluetoothDeviceAdapter;
import com.huntkey.software.sceo.utils.BluetoothUtil;
import com.huntkey.software.sceo.utils.SceoUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/4/1.
 */

public class BluetoothActivity extends KnifeBaseActivity {

    @BindView(R.id.bluetooth_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bluetooth_textview)
    TextView deviceTv;
    @BindView(R.id.bluetooth_bond_device)
    ListView deviceList;

    private BluetoothDeviceAdapter deviceAdapter;
    private BluetoothAdapter bluetoothAdapter;

    private static final int OPEN_BLUETOOTH_REQUEST = 100;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "蓝牙");

        if (null == deviceAdapter){
            deviceAdapter = new BluetoothDeviceAdapter(this, null);
        }
        deviceList.setAdapter(deviceAdapter);
        deviceList.setOnItemClickListener(new MyOnItemClickListener());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        searchDeviceOrOpenBluetooth();
        BluetoothUtil.registerBluetoothReceiver(btReceiver, this);
    }

    private void init() {
        if (null != bluetoothAdapter){
            Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
            if (null != deviceSet){
                deviceAdapter.addDevices(new ArrayList<BluetoothDevice>(deviceSet));
            }
        }
        if (!BluetoothUtil.isOpen(bluetoothAdapter)){
            deviceTv.setText("未连接蓝牙打印机");
        }else {
            if (!BluetoothUtil.isBondPrinter(this, bluetoothAdapter)){
                //未绑定蓝牙打印机
                deviceTv.setText("未连接蓝牙打印机");
            }else {
                //已绑定蓝牙设备
                deviceTv.setText(getPrinterName() + "已连接");
            }
        }
    }

    private void searchDeviceOrOpenBluetooth() {
        if (!BluetoothUtil.isOpen(bluetoothAdapter)){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, OPEN_BLUETOOTH_REQUEST);
        }else {
            BluetoothUtil.searchDevices(bluetoothAdapter);
        }
    }

    private String getPrinterName(){
        String name = SceoUtil.getDefaultBluetoothDeviceName(this);
        if (TextUtils.isEmpty(name)){
            name = "未知设备";
        }
        return name;
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothUtil.cancelDiscovery(bluetoothAdapter);
        BluetoothUtil.unregisterBluetoothReceiver(btReceiver, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_BLUETOOTH_REQUEST && resultCode == Activity.RESULT_CANCELED){
            Toasty.info(BluetoothActivity.this, "您已拒绝使用蓝牙", Toast.LENGTH_SHORT, true).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceAdapter = null;
        bluetoothAdapter = null;
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null == deviceAdapter){
                return;
            }
            final BluetoothDevice bluetoothDevice = (BluetoothDevice) deviceAdapter.getItem(position);
            if (null == bluetoothDevice){
                return;
            }
            new AlertDialog.Builder(BluetoothActivity.this)
                    .setTitle("绑定" + getPrinterName(bluetoothDevice.getName()) + "?")
                    .setMessage("点击确认绑定蓝牙设备")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                BluetoothUtil.cancelDiscovery(bluetoothAdapter);
                                SceoUtil.setDefaultBluetoothDeviceAddress(BluetoothActivity.this, bluetoothDevice.getAddress());
                                SceoUtil.setDefaultBluetoothDeviceName(BluetoothActivity.this, bluetoothDevice.getName());

                                Intent intent = new Intent();
                                setResult(0x1134, intent);

                                if (null != deviceAdapter){
                                    deviceAdapter.setConnectedDeviceAddress(bluetoothDevice.getAddress());
                                }
                                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                                    init();
                                    // TODO: 2017/4/6
                                    finish();
                                }else {
                                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                                    createBondMethod.invoke(bluetoothDevice);
                                }
                                PrintQueue.getQueue(BluetoothActivity.this).disconnect();
                            } catch (Exception e){
                                e.printStackTrace();
                                SceoUtil.setDefaultBluetoothDeviceAddress(BluetoothActivity.this, "");
                                SceoUtil.setDefaultBluetoothDeviceName(BluetoothActivity.this, "");
                                Toasty.error(BluetoothActivity.this, "蓝牙绑定失败,请重试", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }

    private String getPrinterName(String name) {
        if (TextUtils.isEmpty(name)) {
            name = "未知设备";
        }
        return name;
    }

    private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent){
                return;
            }
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)){
                return;
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                btStartDiscovery(intent);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btFinishDiscovery(intent);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                btStatusChanged(intent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                btFoundDevice(intent);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                btBondStatusChange(intent);
            } else if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(action)) {
                btPairingRequest(intent);
            }
        }
    };

    private void btStartDiscovery(Intent intent){
        deviceTv.setText("正在搜索蓝牙设备…");
    }

    private void btFinishDiscovery(Intent intent){
        deviceTv.setText("搜索完成");
    }

    private void btStatusChanged(Intent intent){
        init();
    }

    private void btFoundDevice(Intent intent){
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (null != deviceAdapter && device != null) {
            deviceAdapter.addDevices(device);
        }
    }

    private void btBondStatusChange(Intent intent){
        init();
        if (BluetoothUtil.isBondPrinter(BluetoothActivity.this, bluetoothAdapter)){
            // TODO: 2017/4/6
            finish();
        }
    }

    private void btPairingRequest(Intent intent){
        Toasty.info(BluetoothActivity.this, "正在绑定打印机", Toast.LENGTH_SHORT, true).show();
    }

}
