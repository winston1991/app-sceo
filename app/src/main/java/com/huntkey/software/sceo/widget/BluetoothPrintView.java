package com.huntkey.software.sceo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.utils.QRCodeUtil;
import com.huntkey.software.sceo.utils.SceoUtil;

import java.io.File;
import java.math.BigDecimal;

import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/5/25.
 */

public class BluetoothPrintView extends FrameLayout {

    private TextView pnTv;
    private TextView dcTv;
    private TextView vdTv;
    private TextView snTv;
    private TextView qtyTv;
    private TextView idTv;
    private ImageView qrIv;
    private Context context;
    private Activity activity;
    private TextView splNameTv;

    public BluetoothPrintView(Context context, AttributeSet attrs, Activity activity) {
        super(context, attrs);
        this.context = context;
        this.activity = activity;
        LayoutInflater.from(context).inflate(R.layout.view_bluetooth_print, this);
        pnTv = (TextView) findViewById(R.id.view_btp_pn);
        dcTv = (TextView) findViewById(R.id.view_btp_dc);
        vdTv = (TextView) findViewById(R.id.view_btp_vd);
        snTv = (TextView) findViewById(R.id.view_btp_sn);
        qtyTv = (TextView) findViewById(R.id.view_btp_qty);
        idTv = (TextView) findViewById(R.id.view_btp_id);
        qrIv = (ImageView) findViewById(R.id.view_btp_qr);
        splNameTv = (TextView) findViewById(R.id.view_btp_splname);
    }

    public void setPnTv(String pn){
        pnTv.setText("P/N: " + pn);
    }

    public void setDcTv(String dc){
        dcTv.setText("DC: " + dc);
    }

    public void setVdTv(String vd){
        vdTv.setText("VD: " + vd);
    }

    public void setSnTv(String sn){
        snTv.setText("SN: " + sn);
    }

    public void setQtyTv(String qty){
        qtyTv.setText("QTY: " + qty);
    }

    public void setIdTv(String id){
        idTv.setText("ID: " + id);
    }

    public void setSplNameTv(String splName) {
        if (TextUtils.isEmpty(splName)){
            splNameTv.setVisibility(GONE);
        }else {
            splNameTv.setVisibility(VISIBLE);
        }
        splNameTv.setText(splName);
    }

    public void qrMaker(final String lot) {
        final String filePath = getFileRoot(context) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";
        final String tmpLot;
        try {   // 防止生成的二维码中出现科学计数法
            String[] tmp = lot.split(",");
            if (tmp[2].contains("e")){
                BigDecimal bd = new BigDecimal(tmp[2]);
                tmpLot = tmp[0] + "," + tmp[1] + "," + bd.toPlainString();
            }else {
                tmpLot = lot;
            }
        }catch (Exception e){
            Toasty.error(activity, "二维码生成错误", Toast.LENGTH_SHORT).show();
            return;
        }

        //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(
                        tmpLot,
                        SceoUtil.dp2px(context, 300),
                        SceoUtil.dp2px(context, 300),
                        null,
                        filePath);

                if (success) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrIv.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        }).start();
    }

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }
}
