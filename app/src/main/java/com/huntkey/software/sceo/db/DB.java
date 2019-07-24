package com.huntkey.software.sceo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author chenliang3
 *
 */
public class DB {

	//数据库名称
	private static String DB_NAME = "sceoapp.db";
	//数据库版本
	private static int DB_VERSION = 1;
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	
	public DB(Context context){
		dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
		database = dbHelper.getWritableDatabase();
	}
	
	public void Close(){
		database.close();
		dbHelper.close();
	}
	
	/**
	 * 获取曾经输入过的文本
	 * @param keyword
	 * @return
	 * 只获取前10条
	 */
	public List<String> getInputTexts(String keyword){
		List<String> list = new ArrayList<String>(10);
		String sql = "SELECT _id,value FROM input_keyword WHERE value LIKE ? ORDER BY createDate DESC";
		try {
			Cursor cursor = database.rawQuery(sql, new String[]{"%"+keyword+"%"});
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && cursor.getString(1) != null) {
				list.add(cursor.getString(1));
				cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 保存搜索记录
	 * @param value
	 */
	public void saveInputText(String value){
		//不能为空
		if (null == value) {
			return;
		}
		
		//去掉前后空白后不能为空字符串
		value = value.trim();
		if ("".equals(value)) {
			return;
		}
		
		//首先检查该字符是否已经录入
		String checkSql = "SELECT _id FROM input_keyword WHERE value = ?";
		Cursor cursor = database.rawQuery(checkSql, new String[]{value});
		
		//如果已经录入，则不再处理
		if (cursor.moveToFirst()) {
			return;
		}
		//录入该文本
		ContentValues values = new ContentValues();
		values.put("value", value);
		values.put("createDate", System.currentTimeMillis());
		database.insert(DBHelper.T_INPUT_KEYWORD, "_id", values);
	}
	
	/**
	 * 获取待审单据曾经输入的文本
	 * 只获取前10条
	 */
	public List<String> getInvoiceInputs(String keyword){
		List<String> list = new ArrayList<String>(10);
		String sql = "SELECT _id,value FROM invoice_input WHERE value LIKE ? ORDER BY createDate DESC";
		try {
			Cursor cursor = database.rawQuery(sql, new String[]{"%"+keyword+"%"});
			cursor.moveToFirst();
			while (!cursor.isAfterLast() && cursor.getString(1) != null) {
				list.add(cursor.getString(1));
				cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 保存待审单据搜索记录
	 */
	public void saveInvoiceInputs(String value){
		//不能为空
		if (null == value) {
			return;
		}
		
		//去掉前后空白后不能为空字符串
		value = value.trim();
		if ("".equals(value)) {
			return;
		}
		
		//首先检查该字符是否已经录入
		String checkSql = "SELECT _id FROM invoice_input WHERE value = ?";
		Cursor cursor = database.rawQuery(checkSql, new String[]{value});
		
		//如果已经录入，则不再处理
		if (cursor.moveToFirst()) {
			return;
		}
		//录入该文本
		ContentValues values = new ContentValues();
		values.put("value", value);
		values.put("createDate", System.currentTimeMillis());
		database.insert(DBHelper.I_INPUT_KEYWORD, "_id", values);
	}
	
	
	
	public static void deleteDBDatas(Context context){
		context.deleteDatabase(DB_NAME);
	}
	
}
