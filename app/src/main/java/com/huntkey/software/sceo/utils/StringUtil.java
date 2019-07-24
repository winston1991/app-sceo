package com.huntkey.software.sceo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 字符串处理类
 * @author chenliang3
 *
 */
public class StringUtil {

	/**
	 * 判断字符串是否为空或null
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return str == null || str.trim().length() == 0;
	}
	
	/**
	 * 字符串处理
	 */
	public static ArrayList<String> getImgPaths(String paths){
		if (paths == null || "".equals(paths)) {
			return null;
		}
		ArrayList<String> list = new ArrayList<String>();
		String strs[] = paths.trim().split(";");
		for (int i = 0; i < strs.length; i++) {
			list.add(strs[i]);
		}
		
		return list;
	}
	
    public static boolean isNull(Object o) {
        return o == null ? true : false;
    }

    public static boolean isNull(List<?> list) {
        return list == null || list.size() == 0 ? true : false;
    }

    public static boolean isNull(String str) {
        return TextUtils.isEmpty(str) ? true : false;
    }
    
    public static boolean isFlagContain(int sourceFlag, int compareFlag) {
        return (sourceFlag & compareFlag) == compareFlag;
    }
    
    /**
     * string转float
     * @param str
     * @return
     */
    public static float string2float(String str){
    	try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return 0f;
		}
    }
	
}
