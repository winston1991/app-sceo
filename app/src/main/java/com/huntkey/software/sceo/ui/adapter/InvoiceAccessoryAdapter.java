package com.huntkey.software.sceo.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.utils.AndroidLifecycleUtils;

/**
 * Created by chenl on 2017/7/21.
 */

public class InvoiceAccessoryAdapter extends RecyclerView.Adapter<InvoiceAccessoryAdapter.PhotoViewHolder> {

    private Context context;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private LayoutInflater inflater;

    public final static int TYPE_ADD = 1;
    public final static int TYPE_PHOTO = 2;

    public final static int MAX = 9;

    public InvoiceAccessoryAdapter(Context context, ArrayList<String> photoPaths){
        this.context = context;
        this.photoPaths = photoPaths;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType){
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.item_invoice_accessory_add, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(me.iwf.photopicker.R.layout.__picker_item_photo, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO){
            Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

            boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

            if (canLoadImage){
                Glide.with(context)
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.ic_placeholder_pic)
                        .error(R.drawable.ic_placeholder_pic)
                        .into(holder.ivPhoto);
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = photoPaths.size() + 1;
        if (count > MAX){
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(me.iwf.photopicker.R.id.iv_photo);
            vSelected = itemView.findViewById(me.iwf.photopicker.R.id.v_selected);
            if (vSelected != null) {
                vSelected.setVisibility(View.GONE);
            }
        }
    }
}
