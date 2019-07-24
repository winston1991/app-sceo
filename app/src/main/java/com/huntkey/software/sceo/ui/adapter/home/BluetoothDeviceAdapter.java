package com.huntkey.software.sceo.ui.adapter.home;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.utils.SceoUtil;

import java.util.ArrayList;

/**
 * Created by chenl on 2017/4/5.
 */

public class BluetoothDeviceAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> devices;
    private LayoutInflater inflater;
    private String connectedDeviceAddress;

    public BluetoothDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices){
        this.inflater = LayoutInflater.from(context);
        this.devices = null==devices ? new ArrayList<BluetoothDevice>() : devices;
        connectedDeviceAddress = SceoUtil.getDefaultBluetoothDeviceAddress(context);
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<BluetoothDevice> devices) {
        if (null == devices){
            devices = new ArrayList<>();
        }
        this.devices = devices;
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        if (null != this.devices){
            this.devices = sortByBond(this.devices);
        }
        super.notifyDataSetChanged();
    }

    private ArrayList<BluetoothDevice> sortByBond(ArrayList<BluetoothDevice> devices){
        if (null == devices){
            return null;
        }
        if (devices.size() < 2){
            return devices;
        }
        ArrayList<BluetoothDevice> bondDevices = new ArrayList<>();
        ArrayList<BluetoothDevice> unbondDevice = new ArrayList<>();
        int size = devices.size();
        for (int i = 0; i < size; i++){
            BluetoothDevice bluetoothDevice = devices.get(i);
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                bondDevices.add(bluetoothDevice);
            }else {
                unbondDevice.add(bluetoothDevice);
            }
        }
        devices.clear();
        devices.addAll(bondDevices);
        devices.addAll(unbondDevice);
        bondDevices.clear();
        bondDevices = null;
        unbondDevice.clear();
        unbondDevice = null;
        return devices;
    }

    public void setConnectedDeviceAddress(String connectedDeviceAddress) {
        this.connectedDeviceAddress = connectedDeviceAddress;
    }

    public void addDevices(ArrayList<BluetoothDevice> devices){
        if (null == devices){
            return;
        }
        for (BluetoothDevice bluetoothDevice : devices){
            addDevices(bluetoothDevice);
        }
    }

    public void addDevices(BluetoothDevice device){
        if (null == device){
            return;
        }
        if (!this.devices.contains(device)){
            this.devices.add(device);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else {
            convertView = inflater.inflate(R.layout.item_bluetooth, parent, false);
            holder = new ViewHolder();
            if (null != convertView){
                convertView.setTag(holder);
            }
        }
        holder.name = (TextView) convertView.findViewById(R.id.txt_adapter_bt_name);
        holder.address = (TextView) convertView.findViewById(R.id.txt_adapter_bt_address);
        holder.bond = (TextView) convertView.findViewById(R.id.btn_adapter_bt_has_bond);

        BluetoothDevice bluetoothDevice = devices.get(position);
        String name = bluetoothDevice.getName() == null ? "未知设备" : bluetoothDevice.getName();
        if (TextUtils.isEmpty(name)){
            name = "未知设备";
        }
        holder.name.setText(name);
        String address = bluetoothDevice.getAddress() == null ? "未知地址" : bluetoothDevice.getAddress();
        if (TextUtils.isEmpty(address)){
            address = "未知地址";
        }
        holder.address.setText(address);

        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
            if (address.equals(connectedDeviceAddress)){
                holder.bond.setText("已连接");
            }else {
                holder.bond.setText("已配对");
            }
        }else {
            holder.bond.setText("未配对");
        }

        return convertView;
    }

    static class ViewHolder{
        TextView name;
        TextView address;
        TextView bond;
    }
}
