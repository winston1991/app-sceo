package com.huntkey.software.sceo.ui.activity.attence;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.AttenceCardMachineData;
import com.huntkey.software.sceo.entity.AttenceCardMachineItem;
import com.huntkey.software.sceo.entity.AttenceInfoData;
import com.huntkey.software.sceo.entity.AttenceInfoItem;
import com.huntkey.software.sceo.entity.AttenceSaveInfoData;
import com.huntkey.software.sceo.entity.AttenceTimeData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.adapter.AttenceAdapter;
import com.huntkey.software.sceo.utils.EmulatorCheckUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/9/20.
 */

public class AttenceActivity extends KnifeBaseActivity {

    @BindView(R.id.attence_toolbar)
    Toolbar toolbar;
    @BindView(R.id.attence_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.attence_standardtime)
    TextView standardTimeTv;//标准时间
    @BindView(R.id.attence_judgeloc)
    TextView judgeLocTv;//判断当前位置是否在有效范围内

    private LocationServer locationServer;
    private TextView timeTv;
    private AttenceAdapter adapter;
    private LoadingDialog loadingDialog;

    private String addvNos;//有效卡机组合成的字符串
    private List<AttenceCardMachineItem> cardMachineList = new ArrayList<>();
    private List<AttenceInfoItem> cardInfoList = new ArrayList<>();
    private boolean canPunchCard = false;
    private String currentLocation;
    private String currentAddvno;

    private String[] reminderStrs = {"努力 奋斗", "辛苦了", "天行健 君子以自强不息", "水激石则鸣 人激志则宏", "不登高山 不知天之大",
                            "不临深谷 不知地之厚", "绳锯木断 水滴石穿", "志于道 据于德", "天道酬勤", "厚积薄发"};

    @Override
    protected int getContentViewId() {
        return R.layout.activity_attence_v2;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "移动考勤");

        if (EmulatorCheckUtil.CheckDeviceIDS(this) || EmulatorCheckUtil.CheckEmulatorBuild(this)
                || EmulatorCheckUtil.CheckImsiIDS(this) || EmulatorCheckUtil.CheckPhoneNumber(this)
                || EmulatorCheckUtil.CheckQEmuDriverFile() || EmulatorCheckUtil.checkBlueStacksFiles()
                || EmulatorCheckUtil.readCpuInfo()) {
            Toasty.warning(AttenceActivity.this, "当前功能不支持在模拟器上运行", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog = new LoadingDialog(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();

        getCardMachines();
    }

    /**
     * 获取标准时间和有效卡机
     */
    private void getStarandTime() {
        OkGo.get(Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPQueryTime")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("itime", TimeUtil.getCurrentDay())
                .params("emp", SceoUtil.getEmpCode(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AttenceTimeData data = SceoUtil.parseJson(s, AttenceTimeData.class);
                        if (data.getStatus() == 0){
                            standardTimeTv.setText("标准时间：" + data.getData().getEmp_time());
                            addvNos = data.getData().getAddv_no();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AttenceActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        getCardInfo();
                    }
                });
    }

    /**
     * 获取所有卡机
     */
    private void getCardMachines(){
        OkGo.get(Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPQueryAddr")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AttenceCardMachineData data = SceoUtil.parseJson(s, AttenceCardMachineData.class);
                        if (data.getStatus() == 0){
                            cardMachineList = data.getData();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AttenceActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (!loadingDialog.isShowing()){
                            loadingDialog.show();
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        getStarandTime();
                    }
                });
    }

    /**
     * 获取打卡信息
     */
    private void getCardInfo(){
        OkGo.get(Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPQuery")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("emp", SceoUtil.getEmpCode(this))
                .params("itime", TimeUtil.getCurrentDay())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AttenceInfoData data = SceoUtil.parseJson(s, AttenceInfoData.class);
                        if (data.getStatus() == 0){
                            for (AttenceInfoItem item : data.getData()){
                                if (!TextUtils.isEmpty(item.getLocation())){
                                    cardInfoList.add(item);
                                }
                            }
                            adapter.setNewData(cardInfoList);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AttenceActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        if (loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 打卡
     */
    private void saveCardInfo(){
        if (!canPunchCard){
            showMsgDialog(judgeLocTv.getText().toString().replace("提示：", ""));
            return;
        }
        OkGo.get(Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPSave&charset=utf8")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("emp", SceoUtil.getEmpCode(this))
                .params("locate", currentLocation)
                .params("mac", currentAddvno)
                .params("marker", "0")
                .params("host", android.os.Build.MODEL)
                .params("hostmac", SceoUtil.getDeviceId(AttenceActivity.this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AttenceSaveInfoData data = SceoUtil.parseJson(s, AttenceSaveInfoData.class);
                        if (data.getStatus() == 0){
                            AttenceInfoItem item = new AttenceInfoItem();
                            item.setEmp_id(SceoUtil.getEmpCode(AttenceActivity.this));
                            item.setEmp_time(data.getData().getCard_time());
                            item.setLocation(currentLocation);
                            adapter.addData(item);
                            showSuccessDialog("打卡成功");
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AttenceActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AttenceActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        loadingDialog.show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        loadingDialog.dismiss();
                    }
                });
    }

    private void initAdapter() {
        adapter = new AttenceAdapter(R.layout.item_attence, cardInfoList);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        View footerView = getFooterView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCardInfo();
            }
        });
        adapter.addFooterView(footerView);

        recyclerView.setAdapter(adapter);
    }

    private View getFooterView(View.OnClickListener listener){
        View view = getLayoutInflater().inflate(R.layout.view_attence_circle, (ViewGroup) recyclerView.getParent(), false);
        RelativeLayout punchBtn = (RelativeLayout) view.findViewById(R.id.a_circle_btn);
        timeTv = (TextView) view.findViewById(R.id.a_circle_time);
        new TimeThread().start();
        punchBtn.setOnClickListener(listener);
        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                                doLocation();
                            }
                        }, R.string.perssion_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onStop() {
        if (locationServer != null) {
            locationServer.unregisterListener(mListener); //注销掉监听
            locationServer.stop(); //停止定位服务
        }
        super.onStop();
    }

    private void doLocation() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用
        locationServer = ((SceoApplication) getApplication()).locationServer;
        locationServer.registerListener(mListener);
        //注册监听
        locationServer.setLocationOption(locationServer.getDefaultLocationClientOption());
        locationServer.start();// 定位SDK
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (cardMachineList.size() == 0){
                judgeLocTv.setText("提示：当前无有效移动打卡点");
                judgeLocTv.setTextColor(getResources().getColor(R.color.form_red));
                return;
            }
            if (!TextUtils.isEmpty(addvNos) && !addvNos.contains("M")){
                judgeLocTv.setText("提示：您没有设置移动打卡");
                judgeLocTv.setTextColor(getResources().getColor(R.color.form_red));
                return;
            }
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                for (AttenceCardMachineItem item : cardMachineList){
                    if (isInCircle(createLatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLontitude())),
                            Integer.parseInt(item.getCircleRadius()),
                            createLatLng(location.getLatitude(), location.getLongitude()))){
                        currentLocation = location.getAddrStr() + location.getLocationDescribe();
                        currentAddvno = item.getAddv_no();
                        if (TextUtils.isEmpty(addvNos)){
                            judgeLocTv.setText("提示：当前位置处于有效打卡范围内");
                            judgeLocTv.setTextColor(getResources().getColor(R.color.text_color_deep_weak));
                            canPunchCard = true;
                        }else {
                            if (addvNos.contains(item.getAddv_no())){
                                judgeLocTv.setText("提示：当前位置处于有效打卡范围内");
                                judgeLocTv.setTextColor(getResources().getColor(R.color.text_color_deep_weak));
                                canPunchCard = true;
                            }else {
                                judgeLocTv.setText("提示：您没有设置当前移动打卡点");
                                judgeLocTv.setTextColor(getResources().getColor(R.color.form_red));
                                canPunchCard = false;
                            }
                        }
                        return;
                    }else {
                        judgeLocTv.setText("提示：当前位置不处于有效打卡范围内");
                        judgeLocTv.setTextColor(getResources().getColor(R.color.form_red));
                        canPunchCard = false;
                    }
                }
            }else {
                Toasty.warning(AttenceActivity.this, "定位出错", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     *
     * @param lat 纬度 39.915291
     * @param lon 经度 116.403857
     * @return
     */
    private LatLng createLatLng(double lat, double lon){
        return new LatLng(lat, lon);
    }

    /**
     * 判断点pt是否在以pCenter为中心点，radius为半径的圆内
     * @param pCenter
     * @param radius
     * @param pt
     * @return
     */
    private boolean isInCircle(LatLng pCenter, int radius, LatLng pt){
        return SpatialRelationUtil.isCircleContainsPoint(pCenter, radius, pt);
    }

    private void showMsgDialog(String msg){
        new SweetAlertDialog(AttenceActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("提示")
                .setContentText(msg)
                .setConfirmText("确定")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private void showSuccessDialog(String msg){
        Random random = new Random();
        int result = random.nextInt(10);
        new SweetAlertDialog(AttenceActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(msg)
                .setContentText(reminderStrs[result])
                .setConfirmText("确定")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    timeTv.setText(TimeUtil.getCurrentTime()); //更新时间
                    break;
                default:
                    break;
            }
        }
    };
}
