//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                              _oo0oo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             0\  =  /0
//                           ___/`---‘\___
//                        .' \\\|     |// '.
//                       / \\\|||  :  |||// \\
//                      / _ ||||| -:- |||||- \\
//                      | |  \\\\  -  /// |   |
//                      | \_|  ''\---/''  |_/ |
//                      \  .-\__  '-'  __/-.  /
//                    ___'. .'  /--.--\  '. .'___
//                 ."" '<  '.___\_<|>_/___.' >'  "".
//                | | : '-  \'.;'\ _ /';.'/ - ' : | |
//                \  \ '_.   \_ __\ /__ _/   .-' /  /
//            ====='-.____'.___ \_____/___.-'____.-'=====
//                              '=---='
//
//
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                         佛祖保佑                 永无BUG
package com.huntkey.software.sceo.base;

import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.huntkey.software.sceo.widget.ErrorView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * 判断网络连接
	 * @return
	 */
	public boolean hasNetWork(){
		return NetWorkUtil.networkCanUse(getActivity());
	}
	
	/**
	 * 显示errorView(错误视图、空视图、没有网络视图)
	 * @param view
	 * @param drawable
	 * @param title
	 * @param content
	 */
	public void showErrorView(ErrorView view, Drawable drawable, String title, String content){
		view.setVisibility(View.VISIBLE);
		view.setImage(drawable);
		view.setTitle(title);
		view.setSubtitle(content);
	}
	
	/**
	 * 隐藏errorView
	 * @param view
	 */
	public void hideErrorView(ErrorView view){
		view.setVisibility(View.GONE);
	}
	
}
