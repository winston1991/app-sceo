package com.huntkey.software.sceo.ui.activity.invoicedetails;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.adapter.InvoiceAccessoryAdapter;
import com.huntkey.software.sceo.utils.LogUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by chenl on 2017/7/21.
 */

public class InvoiceAccessoryActivity extends KnifeBaseActivity {

    @BindView(R.id.invoice_accessory_title)
    BackTitle title;
    @BindView(R.id.invoice_accessory_recyclerview)
    RecyclerView recyclerView;

    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private InvoiceAccessoryAdapter photoAdapter;
    private String mid;
    private LoadingDialog loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_invoice_accessory;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initTitle();

        mid = getIntent().getStringExtra("mid");
        loadingDialog = new LoadingDialog(this);

        photoAdapter = new InvoiceAccessoryAdapter(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (photoAdapter.getItemViewType(position) == InvoiceAccessoryAdapter.TYPE_ADD){
                    PhotoPicker.builder()
                            .setPhotoCount(InvoiceAccessoryAdapter.MAX)
                            .setShowCamera(true)
                            .setPreviewEnabled(true)
                            .setShowGif(false)
                            .setSelected(selectedPhotos)
                            .start(InvoiceAccessoryActivity.this);
                }else {
                    PhotoPreview.builder()
                            .setPhotos(selectedPhotos)
                            .setCurrentItem(position)
                            .start(InvoiceAccessoryActivity.this);
                }
            }
        }));
    }

    private void initTitle() {
        title.setBackTitle("上传照片");
        title.setConfirmText("上传");
        title.setConfirmBtnVisible();
        title.setBackBtnClickLis(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setConfirmClicklis(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        uploadMultiFile();
                    }
                }, R.string.perssion_PICK_PHOTO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
    }

    private void uploadMultiFile() {
        if (selectedPhotos.size() > 0){
            for (int i = 0; i < selectedPhotos.size(); i++){
                File file = new File(selectedPhotos.get(i));
                final int finalI = i;
                Luban.with(InvoiceAccessoryActivity.this)
                        .load(file)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file1) {
                                OkGo.post(Conf.SERVICE_URL + "EADSys/csp/EADSys.dll?page=EAD12AC&pcmd=uploadIFile&charset=utf8")
                                        .tag(this)
                                        .params("sessionkey", SceoUtil.getSessionKey(InvoiceAccessoryActivity.this))
                                        .params("mid", mid)
                                        .params("file", file1)
                                        .params("imgname", file1.getName())
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onSuccess(String s, Call call, Response response) {
                                                if (finalI == selectedPhotos.size() - 1){
                                                    loadingDialog.dismiss();
                                                    Toasty.info(InvoiceAccessoryActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                                    InvoiceAccessoryActivity.this.finish();
                                                }
                                            }

                                            @Override
                                            public void onBefore(BaseRequest request) {
                                                super.onBefore(request);
                                                if (finalI == 0){
                                                    loadingDialog.show();
                                                }
                                            }

                                            @Override
                                            public void onError(Call call, Response response, Exception e) {
                                                super.onError(call, response, e);
                                                Toasty.error(InvoiceAccessoryActivity.this, "上传图片出错", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toasty.error(InvoiceAccessoryActivity.this, "压缩图片出错", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .launch();
            }
        }else {
            Toasty.warning(InvoiceAccessoryActivity.this, "请先选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)){
            List<String> photos = null;
            if (data != null){
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null){
                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    private static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{

        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
