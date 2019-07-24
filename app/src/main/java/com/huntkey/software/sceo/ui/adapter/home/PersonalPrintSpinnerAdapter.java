package com.huntkey.software.sceo.ui.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.PrinterItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/12.
 */

public class PersonalPrintSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<PrinterItem> data;
    private LayoutInflater mLayoutInflater = null;

    public PersonalPrintSpinnerAdapter(Context context, List<PrinterItem> items){
        this.context = context;
        this.data = items;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_ebc_spinner, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_ebc_spinner_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrinterItem item = data.get(position);
        viewHolder.textView.setText(item.getPsvr_no());

        return convertView;
    }

    private class ViewHolder{
        TextView textView;
    }
}
