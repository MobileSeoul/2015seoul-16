package com.exam.zzz_other_menu_mysql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.*;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	public MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {

		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("db_test", "oncreate");

		String sql = "create table select_item ( " + "_id integer primary key autoincrement ," +
				" mart_name text , name text , price integer , amount integer , x double , y double , state integer , lat double , lon double)";
		db.execSQL(sql);

	/*	String sql2 = "create table small_item_info ( " + "_id integer primary key autoincrement ," +
				" name text , price integer)";
		db.execSQL(sql2);*/

		String sql3 = "create table large_item_info ( " + "_id integer primary key autoincrement ," +
				" name text)";
		db.execSQL(sql3);

		String sql4 = "create table nfc_tag_item ( " + "_id integer primary key autoincrement ," +
				" name text , price integer , amount integer , x double , y double , state integer)";
		db.execSQL(sql4);

		String sql5 = "create table small_mart_info ( " + "_id integer primary key autoincrement ," +
				" mart_name text , name text , price integer , amount integer , x double , y double , state integer , lat integer , lon integer)";
		db.execSQL(sql5);

		String sql6 = "create table large_mart_info ( " + "_id integer primary key autoincrement ," +
				" name text)";
		db.execSQL(sql6);
		
		String sql7 = "create table small_mart_info2 ( " + "_id integer primary key autoincrement ," +
				" name text , bookmarker integer)";
		db.execSQL(sql7);
		
		String sql8 = "create table info_length ( " + "_id integer primary key autoincrement ," +
				" length integer , item_length integer)";
		db.execSQL(sql8);
	/*	
		String sql9 = "create table date_data ( " + "_id integer primary key autoincrement ," +
				" year text , month text , date text)";
		db.execSQL(sql9);
		*/
		String sql9 = "create table date_data ( " + "_id integer primary key autoincrement ," +
				" year integer , month integer , date integer , hour integer , minute integer , second integer)";
		db.execSQL(sql9);
		
		String sql10 = "create table intent_mart_name ( " + "_id integer primary key autoincrement ," +
				" name text)";
		db.execSQL(sql10);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String sql = "drop table if exists select_mart";
		db.execSQL(sql);

	/*	String sql2 = "drop table if exists small_item_info";
		db.execSQL(sql2);*/

		String sql3 = "drop table if exists large_item_info";
		db.execSQL(sql3);

		String sql4 = "drop table if exists nfc_tag_item";
		db.execSQL(sql4);

		String sql5 = "drop table if exists small_mart_info";
		db.execSQL(sql5);

		String sql6 = "drop table if exists large_mart_info";
		db.execSQL(sql6);
		
		String sql7 = "drop table if exists small_mart_info2";
		db.execSQL(sql7);

		String sql8 = "drop table if exists info_length";
		db.execSQL(sql8);
		
		String sql9 = "drop table if exists date_data";
		db.execSQL(sql9);
		
		String sql10 = "drop table if exists date_data";
		db.execSQL(sql10);
		
		onCreate(db);
	}
}
