package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormSecDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FormAdapter extends BaseAdapter {
    private Context context;
    private List<FormSecDetails> data;
    private OnNameClickListener mOnNameClickListener;

    public FormAdapter(Context context, List<FormSecDetails> details) {
        this.context = context;
        this.data = details;
    }

    public void updateList(List<FormSecDetails> details) {
        this.data = details;
        notifyDataSetChanged();
    }

    public void setOnNameClickListener(OnNameClickListener onNameClickListener) {
        this.mOnNameClickListener = onNameClickListener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.form_item, null);
            viewHolder.nameLayout = convertView.findViewById(R.id.nameLayout);
            viewHolder.name = convertView.findViewById(R.id.form_item_name);
            viewHolder.depart = convertView.findViewById(R.id.form_item_depart);
            viewHolder.imageView = convertView.findViewById(R.id.form_item_iv);
            viewHolder.theNew = convertView.findViewById(R.id.form_item_new);
            viewHolder.theMonth = convertView.findViewById(R.id.form_item_month);
            viewHolder.theYear = convertView.findViewById(R.id.form_item_year);
            viewHolder.theWeek = convertView.findViewById(R.id.form_item_week);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final FormSecDetails details = data.get(position);

        String ppif_name = details.getPpif_name();
        if (!"0".equals(details.getPpif_ct())) {
            ppif_name += "(" + details.getPpif_ct() + ")";
        }
        viewHolder.name.setText(ppif_name);
        viewHolder.depart.setText(details.getCc_full());

        if (!details.getSt().equals("0")) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
        }

        setView(details.getPpid_yesterday(), viewHolder.theNew);
        setView(details.getFfl_cur(), viewHolder.theMonth);
        setView(details.getFfl_sum(), viewHolder.theYear);
        //setView(details.getHb_ffl_cur(), viewHolder.theYear);//环比
        setView(details.getPpid_week(), viewHolder.theWeek);

        viewHolder.nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNameClickListener.onNameClick(details.getPpif_id());
            }
        });

        return convertView;
    }

    private void setView(String str, TextView textView) {
        if (str.length() > 8 && str.contains(".")) {
            str = str.substring(0, str.indexOf("."));
        }
        textView.setText(str);
        if (str.length() > 8) {
            textView.setTextSize(12);
        } else {
            textView.setTextSize(14);
        }
    }

    private class ViewHolder {
        LinearLayout nameLayout;
        TextView name;//名字
        TextView depart;//部门
        TextView theNew;//昨日
        TextView theMonth;//当月
        TextView theYear;//财年
        ImageView imageView;//加号
        TextView theWeek;//当周
    }

    public interface OnNameClickListener {
        void onNameClick(String ppif_id);
    }
}
