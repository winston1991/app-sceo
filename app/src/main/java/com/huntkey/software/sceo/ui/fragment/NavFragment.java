package com.huntkey.software.sceo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.bean.FormSecData;
import com.huntkey.software.sceo.bean.FormSecDetails;
import com.huntkey.software.sceo.bean.FormSecInfo;
import com.huntkey.software.sceo.bean.eventbus.EventBusDepart;
import com.huntkey.software.sceo.bean.eventbus.EventBusDepartHasSub;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.activity.MainActivity;
import com.huntkey.software.sceo.ui.activity.forms.ChartActivity;
import com.huntkey.software.sceo.ui.activity.forms.DepartmentActivity;
import com.huntkey.software.sceo.ui.activity.forms.FormActivity;
import com.huntkey.software.sceo.ui.activity.forms.FormEditActivity;
import com.huntkey.software.sceo.ui.adapter.FormAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.MainTitle_v2;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class NavFragment extends BaseFragment {

    @ViewInject(R.id.salelist_error)
    ErrorView errorView;
    @ViewInject(R.id.form_title)
    MainTitle_v2 title;
    @ViewInject(R.id.form_ptrListview)
    PullToRefreshListView listView;

    private String fYear;
    private String fMonth;
    private String code;
    private List<FormSecDetails> formSecList;
    private int currentPage = 1;
    private String pageSize = "20";
    private int totalRow;
    private FormAdapter adapter;
    private LoadingDialog loadingDialog;

    private String ccCode;
    private String ccName;
    private String isHasSub;
    private String theTitle;
    private String issys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav, null);
        ViewUtils.inject(this, view);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBusUtil.getInstence().register(this);
        }

        fYear = "";
        fMonth = "";

        loadingDialog = new LoadingDialog(getContext());

        ccCode = SceoUtil.shareGetString(getContext(), SceoUtil.getAcctid(getContext()) + SceoUtil.SHARE_FORM_CCCODE);
        ccName = SceoUtil.shareGetString(getContext(), SceoUtil.getAcctid(getContext()) + SceoUtil.SHARE_FORM_CCNAME);
        isHasSub = SceoUtil.shareGetString(getContext(), SceoUtil.getAcctid(getContext()) + SceoUtil.SHARE_FORM_IS_INCLUDE_SUB);

        if (getActivity() instanceof MainActivity) {
            title.hideBack();
            queryInitParams();
        }

        return view;
    }

    public void setParams(String code, String theTitle, String issys) {
        this.code = code;
        this.theTitle = theTitle;
        this.issys = issys;

        initTitleAndErrorView();
        initView();
    }

    private void initTitleAndErrorView() {
        title.setMainTitle(theTitle);
        title.setMainTitleColor(getResources().getColor(R.color.white));
        title.setTitleSettingClickLis(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FormEditActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("name", theTitle);
                startActivity(intent);
            }
        });
        if ("1".equals(issys)) {
            title.setTitleSettingVisible(View.GONE);
        } else {
            title.setTitleSettingVisible(View.VISIBLE);
        }
        title.setTitleDepartClickLis(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                startActivity(intent);
            }
        });
        if (ccName != null && !"".equals(ccName)) {
            title.setTitleDepartText(ccName);
        } else {
            title.setTitleDepartText("起始部门");
        }

        errorView.setOnRetryListener(new ErrorView.RetryListener() {

            @Override
            public void onRetry() {
                hideErrorView(errorView);
                getData(true, ccCode, true);
            }
        });
    }

    private void initView() {
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(false, true).setPullLabel("加载更多");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开开始加载更多");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");

        listView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
        listView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开开始刷新");
        listView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        //刷新/加载更多
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (listView.isHeaderShown()) {//刷新
                    currentPage = 1;
                    getData(true, ccCode, false);
                } else if (listView.isFooterShown()) {//加载
                    ++currentPage;
                    getData(false, ccCode, false);
                }
            }
        });

        getData(true, ccCode, true);
    }

    private void getData(final boolean isRefresh, String upperCode, final boolean isLoadingShow) {
        hideErrorView(errorView);

        if (!hasNetWork()) {
            showErrorView(errorView,
                    getResources().getDrawable(R.drawable.ic_content_manager_wifi),
                    "网络请求失败",
                    "请打开您的数据连接并重试");
        } else {
            doNetWork(isRefresh, upperCode, isLoadingShow);
        }
    }

    /**
     * @param isRefresh 为true表示刷新
     * @param upperCode 请求的部门  没选择部门的时候传空字符串
     */
    private void doNetWork(final boolean isRefresh, String upperCode, final boolean isLoadingShow) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getContext()));
        params.addBodyParameter("fyear", fYear);
        params.addBodyParameter("fmonth", fMonth);
        params.addBodyParameter("kmas_code", code);
        params.addBodyParameter("st", "1");
        params.addBodyParameter("cc_level", "");//第一次传空字符串
        params.addBodyParameter("ppif_id", "");
        params.addBodyParameter("hasright", "");
        params.addBodyParameter("lvl", "");
        params.addBodyParameter("pageno", currentPage + "");
        params.addBodyParameter("pagesize", pageSize + "");
        params.addBodyParameter("upper_code", upperCode);
        params.addBodyParameter("isHasSub", isHasSub);//包含下级传1，不包含传0
        HttpUtils http = new HttpUtils();
        http.configSoTimeout(30 * 1000);
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryData",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        listView.onRefreshComplete();
                        loadingDialog.dismiss();
                        showErrorView(errorView,
                                getResources().getDrawable(R.drawable.ic_content_manager_sync),
                                "请求失败",
                                "请求数据失败，请重试");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        listView.onRefreshComplete();
                        loadingDialog.dismiss();

                        FormSecData data = SceoUtil.parseJson(responseInfo.result, FormSecData.class);
                        if (data.getStatus() == 0) {
                            totalRow = data.getData().getPage().getRowcount();
                            FormSecInfo info = data.getData();
                            try {
                                if (isRefresh) {
                                    title.setTitleTime(info.getUpdatetime().substring(5));
                                }
                            } catch (Exception e) {
                                title.setTitleTime("");
                                e.printStackTrace();
                            }

                            if (isRefresh) {
                                if (formSecList != null) {
                                    formSecList.clear();
                                }
                                formSecList = data.getData().getList();
                            } else {
                                formSecList.addAll(data.getData().getList());
                            }

                            if (adapter == null) {
                                adapter = new FormAdapter(getContext(), formSecList);
                                adapter.setOnNameClickListener(new FormAdapter.OnNameClickListener() {
                                    @Override
                                    public void onNameClick(String ppif_id) {
                                        queryPPINav(ppif_id);
                                    }
                                });
                                listView.setAdapter(adapter);
                            } else {
                                adapter.updateList(formSecList);
                                adapter.notifyDataSetChanged();
                            }

                            if (adapter.getCount() < totalRow) {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }

                            final ArrayList<String> xvalue = new ArrayList<>();
                            xvalue.add(info.getM1());
                            xvalue.add(info.getM2());
                            xvalue.add(info.getM3());
                            xvalue.add(info.getM4());
                            xvalue.add(info.getM5());
                            xvalue.add(info.getM6());
                            xvalue.add(info.getM7());
                            xvalue.add(info.getM8());
                            xvalue.add(info.getM9());
                            xvalue.add(info.getM10());
                            xvalue.add(info.getM11());
                            xvalue.add(info.getM12());

                            setOnClickListener(listView, xvalue, info.getM13());

                            if (formSecList.size() == 0) {
                                showErrorView(errorView,
                                        getResources().getDrawable(R.drawable.ic_content_manager_visible),
                                        "这里是空的",
                                        "请更改条件或点击再试一次");
                            }
                        } else if (data.getStatus() == 88) {
                            Toasty.error(getContext(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(getContext());
                            SceoApplication.getInstance().exit();
                        } else {
                            if (formSecList != null && formSecList.size() > 0) {
                                formSecList.clear();
                                adapter.notifyDataSetChanged();
                            }
                            showErrorView(errorView,
                                    getResources().getDrawable(R.drawable.ic_content_manager_visible),
                                    "这里是空的",
                                    "您还没有数据，请添加数据并重试");

                            Toasty.error(getContext(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (isLoadingShow) {
                            loadingDialog.show();
                        } else {
                            listView.setRefreshing();
                        }
                    }
                });
    }

    private void setOnClickListener(PullToRefreshListView listView, final ArrayList<String> xvalue, final String s) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChartActivity.class);
                intent.putExtra("formSecList", formSecList.get(position - 1));
                intent.putStringArrayListExtra("xvalue", xvalue);
                intent.putExtra("fyear", fYear);
                intent.putExtra("fmonth", fMonth);
                intent.putExtra("kmas_code", code);
                intent.putExtra("length", s);
                intent.putExtra("ccCode", formSecList.get(position - 1).getCc_code());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SceoUtil.getFormEditChange(getContext())) {
            getData(true, ccCode, false);
            SceoUtil.setFormEditChange(getContext(), false);
        }
    }

    @Subscribe
    public void onEventMainThread(EventBusDepart depart) {
        ccCode = depart.getEbCode();
        title.setTitleDepartText(depart.getEbName());
        getData(true, ccCode, false);
    }

    @Subscribe
    public void onEventMainThread(EventBusDepartHasSub hasSub) {
        isHasSub = hasSub.getShouldRefresh();
        getData(true, ccCode, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBusUtil.getInstence().unregister(this);
        }
    }

    // 处理首页显示，不传入参数（默认查找IVC导航，如果没有，再找即时快报）
    private void queryInitParams() {
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                queryInitParams();
            }
        });
        if (!hasNetWork()) {
            showErrorView(errorView,
                    getResources().getDrawable(R.drawable.ic_content_manager_wifi),
                    "网络请求失败",
                    "请打开您的数据连接并重试");
        } else {
            RequestParams params = new RequestParams();
            params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getContext()));
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("GB2312");
            http.send(HttpRequest.HttpMethod.POST,
                    Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=GetIvcNav",
                    params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            loadingDialog.dismiss();
                            InitParam data = SceoUtil.parseJson(responseInfo.result, InitParam.class);

                            if (data != null && data.getRows() != null && data.getRows().size() > 0) {
                                final InitParam.Item first = data.getRows().get(0);
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).setFormTxt(first.getDesc());
                                }
                                setParams(first.getCode(), first.getDesc(), String.valueOf(first.getIssys()));
                                initTitleAndErrorView();
                                initView();
                            } else {
                                showErrorView(errorView,
                                        getResources().getDrawable(R.drawable.ic_content_manager_visible),
                                        "无数据",
                                        "没有IVC导航，也没有即时快报");
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            loadingDialog.dismiss();
                            showErrorView(errorView,
                                    getResources().getDrawable(R.drawable.ic_content_manager_sync),
                                    "请求失败",
                                    "请求数据失败，请重试");
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            loadingDialog.show();
                        }
                    });
        }
    }

    // 查询指标导航
    private void queryPPINav(String ppif_id) {
        if (!hasNetWork()) {
            Toasty.error(getContext(), "请打开您的数据连接并重试", Toast.LENGTH_SHORT, true).show();
        } else {
            RequestParams params = new RequestParams();
            params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getContext()));
            params.addBodyParameter("ppif_id", ppif_id);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("GB2312");
            http.send(HttpRequest.HttpMethod.POST,
                    Conf.SERVICE_URL + "EKPSysV2/csp/EKPSys.dll?page=EKP20AA&pcmd=GetPPIMgrAly",
                    params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            loadingDialog.dismiss();
                            PpiNav data = SceoUtil.parseJson(responseInfo.result, PpiNav.class);

                            if (data != null && data.getRows() != null && data.getRows().size() > 0) {
                                final PpiNav.Item first = data.getRows().get(0);

                                // 打开FormActivity
                                Intent intent = new Intent(getActivity(), FormActivity.class);
                                intent.putExtra("code", first.getKmam_code());
                                intent.putExtra("title", first.getKmam_desc());
                                intent.putExtra("issys", "0");
                                startActivity(intent);
                            } else {
                                Toasty.error(getContext(), "没找到对应的指标导航", Toast.LENGTH_SHORT, true).show();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            loadingDialog.dismiss();
                            Toasty.error(getContext(), "查询指标导航失败，请重试", Toast.LENGTH_SHORT, true).show();
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            loadingDialog.show();
                        }
                    });
        }
    }

    // 初始化参数对象
    static class InitParam {
        private int Total;
        private List<Item> Rows;

        public int getTotal() {
            return Total;
        }

        public void setTotal(int total) {
            Total = total;
        }

        public List<Item> getRows() {
            return Rows;
        }

        public void setRows(List<Item> rows) {
            Rows = rows;
        }

        static class Item {
            private String code;
            private String desc;
            private int issys;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getIssys() {
                return issys;
            }

            public void setIssys(int issys) {
                this.issys = issys;
            }
        }
    }

    static class PpiNav {
        private int Total;
        private List<Item> Rows;

        public int getTotal() {
            return Total;
        }

        public void setTotal(int total) {
            Total = total;
        }

        public List<Item> getRows() {
            return Rows;
        }

        public void setRows(List<Item> rows) {
            Rows = rows;
        }

        static class Item {
            private String kmam_code;
            private String kmam_desc;

            public String getKmam_code() {
                return kmam_code;
            }

            public void setKmam_code(String kmam_code) {
                this.kmam_code = kmam_code;
            }

            public String getKmam_desc() {
                return kmam_desc;
            }

            public void setKmam_desc(String kmam_desc) {
                this.kmam_desc = kmam_desc;
            }
        }
    }
}
