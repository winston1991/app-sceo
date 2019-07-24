package com.huntkey.software.sceo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.FormMenuItem;

import java.util.List;

/**
 * Created by chenl on 2018/7/18.
 */

public class FormMenuAdapter extends BaseAdapter {

    private Context context;
    private List<FormMenuItem> data;

    public FormMenuAdapter(Context context, List<FormMenuItem> formMenuItems) {
        this.context = context;
        this.data = formMenuItems;
    }

    public void updateList(List<FormMenuItem> formMenuItems) {
        this.data = formMenuItems;
        notifyDataSetChanged();
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.form_menu_item, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.fmenu_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FormMenuItem details = data.get(position);

        viewHolder.title.setText(details.getKmam_desc());
        return convertView;
    }

    private class ViewHolder{
        TextView title;
    }
}
