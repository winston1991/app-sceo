package com.huntkey.software.sceo.ui.activity.forms;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.DepartmentData;
import com.huntkey.software.sceo.bean.DepartmentDetails;
import com.huntkey.software.sceo.bean.eventbus.EventBusDepart;
import com.huntkey.software.sceo.bean.eventbus.EventBusDepartHasSub;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.DepartmentAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.huntkey.software.sceo.widget.SwitchButton;
import com.huntkey.software.sceo.widget.TreeViewD;
import com.huntkey.software.sceo.widget.TreeViewD.ParentLevelItemClickListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

/**
 * 选择部门
 * @author chenliang3
 *
 */
public class DepartmentActivity extends BaseActivity{

	@ViewInject(R.id.depart_tree)
	TreeViewD treeList;
	@ViewInject(R.id.depart_title_back)
	RelativeLayout titleBackBtn;
	@ViewInject(R.id.depart_title_confirm)
	RelativeLayout titleConfirmBtn;
	@ViewInject(R.id.depart_include)
	SwitchButton switchButton;
	
	private DbUtils db;
	private LoadingDialog loadingDialog;
	private List<DepartmentDetails> departmentDetails = new ArrayList<>();
	private List<String> ccData = new ArrayList<>();
	public static String HIGHESTID = "Z00000";
	public static String HIGHESTPARENTID = "Z00001";
	private static Activity activity;
	private String isHasSub;//是否包含下级字段
	
	public static Activity getInstence(){
		return activity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_department);
		ViewUtils.inject(this);
		
		activity = this;
		//针对不同公司存储不同的数据库
		db = DbUtils.create(this, "departmentdetails" + SceoUtil.getAcctid(this));
		
		initTitle();
		initSwitchBtn();
		initView();
		
		initControl();
	}
	
	private void initView() {
		String tmpCurrentYMD = TimeUtil.getCurrentYMD();
		String keepedYmd = SceoUtil.getCurrentYMD(DepartmentActivity.this);
		if (!tmpCurrentYMD.equals(keepedYmd)) {
			try {
				db.deleteAll(DepartmentDetails.class);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		
		loadingDialog = new LoadingDialog(this);
		
		initMainData();
		treeList.setParentLevelItemClickCallBack(pItemClickCallBack);
	}
	
	//父节点点击事件监听
	ParentLevelItemClickListener pItemClickCallBack = new ParentLevelItemClickListener() {
		
		@Override
		public void onParentItemClick(int position, DepartmentAdapter adapter, View convertView) {
			DepartmentDetails details = (DepartmentDetails) adapter.getItem(position);
			if (details.getChildCount() > 0) {//有子节点才去加载
				getData(details.getCc_level(), details.getCc_code(), position);
			}
		}
	};
	
	private void initControl(){
		String tmpCClevel = SceoUtil.shareGetString(DepartmentActivity.this, 
				SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_CCLEVEL);
		for(int i = 0; i < tmpCClevel.length()/2-1; i++) {
			String strCClevel = tmpCClevel.substring(0, (i+1)*2);
			
			for(int j = 0; j < treeList.getCount(); j++){
				DepartmentDetails departmentDetails = (DepartmentDetails)treeList.getItem(j);
				if(departmentDetails.getCc_level().equals(strCClevel)){
					getDataFromDb(strCClevel,departmentDetails.getCc_code() , j);
					treeList.setSelection(j);
					break;
				}				
			}
		}				
	}
	
	/**
	 * 自动展开的时候需要将父节点设置为展开状态（未设置为展开数据会重复）
	 * @param ccLevel
	 * @param ccCode
	 * @param position
	 */
	private List<DepartmentDetails> dbData = new ArrayList<>();
	private void getDataFromDb(String ccLevel, final String ccCode, final int position){
		try {
			dbData = db.findAll(Selector.from(DepartmentDetails.class)
					.where("parentId", "=", ccCode));
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		if (dbData != null && dbData.size() > 0) {
			treeList.updateData2(DepartmentActivity.this, dbData, position, ccCode);
		}
	}
	
	/**
	 * 获取主节点数据
	 */
	List<DepartmentDetails> mainDataFromDb;
	private void initMainData() {
		String currentYMD = TimeUtil.getCurrentYMD();
		//存储当前年月日，部门数据在数据库中只保存一天，进入该界面的时候判断，如果日期不同则清空数据库
		SceoUtil.setCurrentYMD(DepartmentActivity.this, currentYMD);
		
		final List<DepartmentDetails> tmpList = new ArrayList<>();
		
		try {
			mainDataFromDb = db.findAll(Selector.from(DepartmentDetails.class)
					.where("parentId", "=", HIGHESTPARENTID).or("parentId", "=", HIGHESTID));
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		
		if (mainDataFromDb != null && mainDataFromDb.size() > 0) {
			treeList.initData(DepartmentActivity.this, mainDataFromDb);
		}else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(DepartmentActivity.this));
			params.addBodyParameter("cc_level", "");
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryDept",
					params, 
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					loadingDialog.dismiss();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					loadingDialog.dismiss();
					
					DepartmentData data = SceoUtil.parseJson(responseInfo.result, DepartmentData.class);
					if (data.getStatus() == 0) {
						List<DepartmentDetails> details = data.getData();
						for (int i = 0; i < details.size(); i++) {
							int tmpLevel = details.get(i).getCc_level().length();
							if (tmpLevel == 0) {									
								details.get(i).setHasParent(false);
								details.get(i).setFold(true);
								details.get(i).setParentId(HIGHESTPARENTID);
							}else {
								details.get(i).setHasParent(true);
								details.get(i).setFold(false);
								details.get(i).setParentId(HIGHESTID);
							}
							details.get(i).setHasChild(true);
							details.get(i).setLevel(tmpLevel);
							departmentDetails.add(details.get(i));
							tmpList.add(details.get(i));
						}
						treeList.initData(DepartmentActivity.this, departmentDetails);
						
						//保存到数据库
						try {
							db.saveAll(tmpList);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}else {
						Toasty.error(DepartmentActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
					}
				}
				
				@Override
				public void onStart() {
					super.onStart();
					loadingDialog.show();
				}
			});
		}
		
	}
	
	/**
	 * 加载父节点下子节点数据
	 * @param ccLevel 后台返回的level
	 * @param ccCode 父节点id
	 * @param position 位置
	 */
	private List<DepartmentDetails> dataFromDb = new ArrayList<>();
	private void getData(String ccLevel, final String ccCode, final int position){
		final List<DepartmentDetails> tmpList = new ArrayList<>();
		
		try {
			dataFromDb = db.findAll(Selector.from(DepartmentDetails.class)
					.where("parentId", "=", ccCode));
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		if (dataFromDb != null && dataFromDb.size() > 0) {
			treeList.updateData(DepartmentActivity.this, dataFromDb, position, ccCode);
		}else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(DepartmentActivity.this));
			params.addBodyParameter("cc_level", ccLevel);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryDept",
					params, 
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					loadingDialog.dismiss();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					loadingDialog.dismiss();
					
					DepartmentData data = SceoUtil.parseJson(responseInfo.result, DepartmentData.class);
					if (data.getStatus() == 0) {
						List<DepartmentDetails> details = data.getData();
						for (int i = 0; i < details.size(); i++) {
							int tmpLevel = details.get(i).getCc_level().length();
							
							details.get(i).setHasParent(true);
							details.get(i).setLevel(tmpLevel);
							details.get(i).setFold(false);
							details.get(i).setParentId(ccCode);
							if (details.get(i).getChildCount() > 0) {
								details.get(i).setHasChild(true);
							}else {
								details.get(i).setHasChild(false);
							}
							departmentDetails.add(details.get(i));
							tmpList.add(details.get(i));
						}
						treeList.updateData(DepartmentActivity.this, departmentDetails, position, ccCode);
						
						//保存到数据库
						try {
							db.saveAll(tmpList);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}else {
						Toasty.error(DepartmentActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
					}
				}
				
				@Override
				public void onStart() {
					super.onStart();
					loadingDialog.show();
				}
			});
		}
	}
	
	private void initTitle() {
		isHasSub = SceoUtil.shareGetString(DepartmentActivity.this, 
				SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_IS_INCLUDE_SUB);
		titleBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleConfirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ccData = treeList.getCcData();
				if (ccData.get(0) != null && ccData.get(1) != null) {
					SceoUtil.shareSet(DepartmentActivity.this, 
							SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_CCCODE, ccData.get(0));
					SceoUtil.shareSet(DepartmentActivity.this, 
							SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_CCNAME, ccData.get(1));
					SceoUtil.shareSet(DepartmentActivity.this, 
							SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_CCLEVEL, ccData.get(2));
					
					EventBusDepart depart = new EventBusDepart();
					depart.setEbCode(ccData.get(0));
					depart.setEbName(ccData.get(1));
					EventBusUtil.getInstence().post(depart);
				}
				
				finish();
			}
		});
	}
	
	private void initSwitchBtn(){
		if (isHasSub.equals("1")) {
			switchButton.setChecked(true);
		}else {
			switchButton.setChecked(false);
		}
		switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if (isChecked) {					
					SceoUtil.shareSet(DepartmentActivity.this, 
							SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_IS_INCLUDE_SUB, "1");
				}else {
					SceoUtil.shareSet(DepartmentActivity.this, 
							SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_IS_INCLUDE_SUB, "0");
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//如果更改了是否包含下级的状态，则通知列表界面刷新
		String tmp = SceoUtil.shareGetString(DepartmentActivity.this, 
				SceoUtil.getAcctid(DepartmentActivity.this)+SceoUtil.SHARE_FORM_IS_INCLUDE_SUB);
		if (!isHasSub.equals(tmp)) {
			EventBusDepartHasSub hasSub = new EventBusDepartHasSub();
			hasSub.setShouldRefresh(tmp);
			EventBusUtil.getInstence().post(hasSub);
		}
	}
	
	/**
	 * 重写finish方法，压入栈不销毁该activity
	 * activity设置为singleInstance模式
	 */
//	@Override
//	public void finish() {
//		moveTaskToBack(true);
//	}
	
}
