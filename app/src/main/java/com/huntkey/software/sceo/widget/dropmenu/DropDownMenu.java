package com.huntkey.software.sceo.widget.dropmenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony.Mms;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.huntkey.software.sceo.R;

import es.dmoral.toasty.Toasty;

public class DropDownMenu extends LinearLayout{

    // Menu 展开的ListView 的 adapter
    private List<MenuListAdapter> MenuAdapters=new ArrayList<MenuListAdapter>();

    // Menu 展开的 list item
    private List<String[]> MenuItems=new ArrayList<String[]>();

    //菜单 上的文字
    private List<TextView> mTvMenuTitles=new ArrayList<TextView>();
    //菜单 的背景布局
    private List<RelativeLayout> mRlMenuBacks=new ArrayList<RelativeLayout>();
    //菜单 的箭头
    private List<ImageView> mIvMenuArrow=new ArrayList<ImageView>();

    private Context context;

    private PopupWindow mPopupWindow;
    // Menu 展开的ListView
    private ListView mMenuList;
    // Menu 展开的ListView 下部的阴影
    private RelativeLayout mRlShadow;

    // 监听器
    private OnMenuSelectedListener MenuSelectedListener;

    // 主Menu的个数
    private int MenuCount;
    // Menu 展开的list 显示数量
    private int ShowCount;

    //选中行数
    private int RowSelected=0;
    //选中列数
    private int ColumnSelected=0;
    
    private int col1;
    private int col2;
    private int col3;
    private int row1;
    private int row2;
    private int row3;
    
    //Menu的字体颜色
    private int MenuTitleTextColor;
    //Menu的字体大小
    private int MenuTitleTextSize;
    //Menu的按下背景
    private int MenuPressedBackColor;
    //Menu的背景
    private int MenuBackColor;
    //Menu list 的字体大小
    private int MenuListTextSize;
    //Menu list 的字体颜色
    private int MenuListTextColor;
    //是否显示选中的对勾
    private boolean showCheck;

    //对勾的图片资源
    private int CheckIcon;
    //向上的箭头图片资源
    private int UpArrow;
    //向下的箭头图片资源
    private int DownArrow;

    public DropDownMenu(Context context) {
        super(context);
        init(context);

    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        MenuCount=2;
        ShowCount=5;
        MenuTitleTextColor=getResources().getColor(R.color.default_menu_text);
        MenuPressedBackColor=getResources().getColor(R.color.background_color);
//        MenuBackColor=getResources().getColor(R.color.default_menu_back);
        MenuTitleTextSize=18;
        showCheck=true;
        CheckIcon=R.drawable.ico_make;
        UpArrow=R.drawable.arrow_up;
        DownArrow=R.drawable.arrow_down;

    }

    // 设置 Menu的item
    public void setMenuItems(List<String[]> menuItems) {
        MenuItems = menuItems;
    }

    // 设置 Menu的数量
    public void setMenuCount(int menuCount) {
        MenuCount = menuCount;
    }

    // 设置 show 数量
    public void setShowCount(int showCount) {
        ShowCount = showCount;
    }

    // 设置 Menu的字体颜色
    public void setMenuTitleTextColor(int menuTitleTextColor) {
        MenuTitleTextColor = menuTitleTextColor;
    }

    // 设置 Menu的字体大小
    public void setMenuTitleTextSize(int menuTitleTextSize) {
        MenuTitleTextSize = menuTitleTextSize;
    }

    //设置Menu的背景色
    public void setMenuBackColor(int menuBackColor) {
        MenuBackColor = menuBackColor;
    }

    //设置Menu的按下背景色
    public void setMenuPressedBackColor(int menuPressedBackColor) {
        MenuPressedBackColor = menuPressedBackColor;
    }

    //设置Menu list的字体颜色
    public void setMenuListTextColor(int menuListTextColor) {
        MenuListTextColor = menuListTextColor;
        for (int i=0;i<MenuAdapters.size();i++){
            MenuAdapters.get(i).setTextColor(MenuListTextColor);
        }
    }
    //设置Menu list的字体大小
    public void setMenuListTextSize(int menuListTextSize) {
        MenuListTextSize = menuListTextSize;
        for (int i=0;i<MenuAdapters.size();i++){
            MenuAdapters.get(i).setTextSize(menuListTextSize);
        }
    }
    //设置是否显示对勾
    public void setShowCheck(boolean showCheck) {
        this.showCheck = showCheck;
    }

    //设置对勾的icon
    public void setCheckIcon(int checkIcon) {
        CheckIcon = checkIcon;
    }

    public void setUpArrow(int upArrow) {
        UpArrow = upArrow;
    }

    public void setDownArrow(int downArrow) {
        DownArrow = downArrow;
    }

    public void setMenuSelectedListener(OnMenuSelectedListener menuSelectedListener) {
        MenuSelectedListener = menuSelectedListener;
    }
    
    public void setMenuSelection1(int ColumnSelected, int RowSelected){
    	this.col1 = ColumnSelected;//行
    	this.row1 = RowSelected;//列
    }
    public void setMenuSelection2(int ColumnSelected, int RowSelected){
    	this.col2 = ColumnSelected;
    	this.row2 = RowSelected;
    }
    public void setMenuSelection3(int ColumnSelected, int RowSelected){
    	this.col3 = ColumnSelected;
    	this.row3 = RowSelected;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPopupWindow==null){
            View popupView =LayoutInflater.from(context).inflate(R.layout.popupwindow_menu, null);
            mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mMenuList=(ListView)popupView.findViewById(R.id.lv_menu);
            mRlShadow=(RelativeLayout)popupView.findViewById(R.id.rl_menu_shadow);

            mRlShadow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mTvMenuTitles.get(col1).setText(MenuItems.get(col1)[row1]);
					MenuAdapters.get(col1).setSelectIndex(row1);
					mTvMenuTitles.get(col2).setText(MenuItems.get(col2)[row2]);
					MenuAdapters.get(col2).setSelectIndex(row2);
					mTvMenuTitles.get(col3).setText(MenuItems.get(col3)[row3]);
					MenuAdapters.get(col3).setSelectIndex(row3);
				}
			}, 1);

            mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPopupWindow.dismiss();
                    RowSelected=position;

                    mTvMenuTitles.get(ColumnSelected).setText(MenuItems.get(ColumnSelected)[RowSelected]);
                    mIvMenuArrow.get(ColumnSelected).setImageResource(DownArrow);
                    MenuAdapters.get(ColumnSelected).setSelectIndex(RowSelected);

                    if(MenuSelectedListener==null)
                        Toasty.warning(context,"MenuSelectedListener is  null",Toast.LENGTH_LONG, true).show();
                    else
                        MenuSelectedListener.onSelected(view,RowSelected, ColumnSelected);
                }
            });

            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    for (int i=0;i<MenuCount;i++){
                        mIvMenuArrow.get(i).setImageResource(DownArrow);
                        mRlMenuBacks.get(i).setBackgroundColor(MenuBackColor);
                    }
                }
            });

            if(MenuItems.size()!=MenuCount){
                Toasty.warning(context,"Menu item is not setted or incorrect",Toast.LENGTH_LONG, true).show();
                return;
            }

            if(MenuAdapters.size()==0){
                for (int i=0;i<MenuCount;i++){
                    MenuListAdapter adapter=new MenuListAdapter(context, MenuItems.get(i));
                    adapter.setShowCheck(showCheck);
                    adapter.setCheckIcon(CheckIcon);
                    MenuAdapters.add(adapter);

                }
            }else if(MenuAdapters.size()!=MenuCount){
                Toasty.warning(context,"If you want set Adapter by yourself,please ensure the number of adpaters equal MenuCount",Toast.LENGTH_LONG, true).show();
                return;
            }
            int width=getWidth();

            for (int i=0;i<MenuCount;i++){
                final RelativeLayout v =(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.menu_item,null,false);
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width/MenuCount, LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(parms);
                TextView tv=(TextView)v.findViewById(R.id.tv_menu_title);
                tv.setTextColor(MenuTitleTextColor);
                tv.setTextSize(MenuTitleTextSize);
                tv.setText(MenuItems.get(i)[0]);
                this.addView(v,i);
                mTvMenuTitles.add(tv);

                RelativeLayout rl=(RelativeLayout)v.findViewById(R.id.rl_menu_head);
                rl.setBackgroundColor(MenuBackColor);
                mRlMenuBacks.add(rl);

                ImageView iv=(ImageView)v.findViewById(R.id.iv_menu_arrow);
                mIvMenuArrow.add(iv);

                final int index=i;
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMenuList.setAdapter(MenuAdapters.get(index));
                        if (MenuAdapters.get(index).getCount()>ShowCount){
                            View childView = MenuAdapters.get(index).getView(0, null, mMenuList);
                            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,childView.getMeasuredHeight()*ShowCount);
                            mMenuList.setLayoutParams(parms);
                        }else{
                            View childView = MenuAdapters.get(index).getView(0, null, mMenuList);
                            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                            mMenuList.setLayoutParams(parms);
                        }
                        ColumnSelected=index;
                        mRlMenuBacks.get(index).setBackgroundColor(MenuPressedBackColor);
                        mIvMenuArrow.get(ColumnSelected).setImageResource(UpArrow);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                            mPopupWindow.showAsDropDown(v);
                        }else {
                            int[] location = new int[2];
                            getLocationOnScreen(location);
                            int y = location[1];
                            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, y + getHeight() + 5);
                        }
                    }
                });
            }
        }
    }


}
