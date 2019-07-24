package com.huntkey.software.sceo.ui.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.ExternalBarConversionAItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExterBarConverSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<ExternalBarConversionAItem> data;
    private LayoutInflater mLayoutInflater = null;

    public ExterBarConverSpinnerAdapter(Context context, List<ExternalBarConversionAItem> list){
        this.context = context;
        this.data = list;
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
        ExterBarConverSpinnerAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ExterBarConverSpinnerAdapter.ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_ebc_spinner, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_ebc_spinner_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ExterBarConverSpinnerAdapter.ViewHolder) convertView.getTag();
        }

        ExternalBarConversionAItem item = data.get(position);

        viewHolder.textView.setText("项" + item.getLstd_line() + "：" + item.getLstd_part());

        return convertView;
    }

    private class ViewHolder{
        TextView textView;
    }
}
