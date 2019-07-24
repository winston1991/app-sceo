package com.huntkey.software.sceo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库
 * @author chenliang3
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	//记录用户搜索记录的表名
	public static final String T_INPUT_KEYWORD = "input_keyword";
	
	//记录用户搜索待审单据记录的表名
	public static final String I_INPUT_KEYWORD = "invoice_input";
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * 创建表
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建记录用户搜索记录的表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + T_INPUT_KEYWORD + "("
				+ "_id integer primary key autoincrement,value varchar,createDate long)");
		
		//创建记录用户搜索待审单据记录的表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + I_INPUT_KEYWORD + "("
				+ "_id integer primary key autoincrement,value varchar,createDate long)");
	}

	/**
	 * 更新表
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + T_INPUT_KEYWORD);
		db.execSQL("DROP TABLE IF EXISTS " + I_INPUT_KEYWORD);
		onCreate(db);
	}
	
	/**
	 * 更新列
	 */
	public void updateColumn(SQLiteDatabase db, String oldColumn, 
			String newColumn, String typeColumn){
		try {
			db.execSQL("ALTER TABLE " + T_INPUT_KEYWORD + "CHANGE" + oldColumn + 
					" " + newColumn + " " + typeColumn);
			db.execSQL("ALTER TABLE " + I_INPUT_KEYWORD + "CHANGE" + oldColumn + 
					" " + newColumn + " " + typeColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
