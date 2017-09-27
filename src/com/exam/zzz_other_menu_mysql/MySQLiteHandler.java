package com.exam.zzz_other_menu_mysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MySQLiteHandler {

	MySQLiteOpenHelper helper;
	SQLiteDatabase db;

	public MySQLiteHandler(Context ctx) {

		helper = new MySQLiteOpenHelper(ctx, "mart.sqlite", null, 1);
	}
	public static MySQLiteHandler open(Context ctx) {
		return new MySQLiteHandler(ctx);
	}

	public void close() {
		helper.close();
	}

	public void insert(String mart_name, String name, int price, int amount, double x, double y, int state, double lat, double lon, int type) {

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		switch(type){
		case 1:
			values.put("mart_name", mart_name);
			values.put("name", name);
			values.put("price", price);
			values.put("amount", amount);
			values.put("x", x);
			values.put("y", y);
			values.put("state", state);
			values.put("lat", lat);
			values.put("lon", lon);

			db.insert("select_item", null, values);
			break;
			/*	case 2:
			db.insert("small_item_info", null, values);
			break;*/
		case 3:
			values.put("name", name);

			db.insert("large_item_info", null, values);
			break;
		case 4:
			values.put("name", name);
			values.put("price", price);
			values.put("amount", amount);
			values.put("x", x);
			values.put("y", y);
			values.put("state", state);

			db.insert("nfc_tag_item", null, values);
			break;
		case 5:

			values.put("mart_name", mart_name);
			values.put("name", name);
			values.put("price", price);
			values.put("amount", amount);
			values.put("x", x);
			values.put("y", y);
			values.put("state", state);
			values.put("lat", lat);
			values.put("lon", lon);

			db.insert("small_mart_info", null, values);
			break;
		case 6:
			values.put("name", mart_name);

			db.insert("large_mart_info", null, values);
			break;
		}
	}
	
	public void insert_bookmarker(String name, int bookmarker){
		
		db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put("name", name);
		values.put("bookmarker", bookmarker);

		db.insert("small_mart_info2", null, values);
	}
	
	public void insert_intentmart(String name){
		
		db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put("name", name);

		db.insert("intent_mart_name", null, values);
	}

	public void insert_length(int length, int item_length){

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("length", length);
		values.put("item_length", item_length);

		db.insert("info_length", null, values);
	}

	public void insert_date(int year, int month, int date, int hour, int minute, int second){

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("year", year);
		values.put("month", month);
		values.put("date", date);
		values.put("hour", hour);
		values.put("minute", minute);
		values.put("second", second);

		db.insert("date_data", null, values);
	}

	public void update(String name, int amount, int type) {

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("amount", amount);

		switch(type){
		case 1:
			db.update("select_item", values, "name = ?", new String[]{ name });
			break;
		case 2:
			db.update("nfc_tag_item", values, "name = ?", new String[]{ name });
			break;
		}

	}

	public void update(int year, int month, int date, int hour, int minute, int second) {

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("year", year);
		values.put("month", month);
		values.put("date", date);
		values.put("hour", hour);
		values.put("minute", minute);
		values.put("second", second);

		db.update("date_data", values, null, null);
	}

	public void updatestate(String name, int state){

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("state", state);

		db.update("select_item", values, "name = ?", new String[]{ name });
	}
	
	public void updatebookmarker(String name, int bookmarker){

		db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("bookmarker", bookmarker);

		db.update("small_mart_info2", values, "name = ?", new String[]{ name });
	}

	public void delete(String[] name) {
		db = helper.getWritableDatabase();

		db.delete("select_item", "NAME=?", name);
	}

	public void deletetemp(String[] name) {

		db = helper.getWritableDatabase();

		db.delete("nfc_tag_item", "NAME=?", name);
	}

	public void deleteAll() {

		db = helper.getWritableDatabase();

		db.delete("select_item", null, null);
		//	db.delete("small_item_info", null, null);
		db.delete("large_item_info", null, null);
		db.delete("nfc_tag_item", null, null);
		db.delete("small_mart_info", null, null);
		db.delete("large_mart_info", null, null);
		db.delete("small_mart_info2", null, null);
		db.delete("info_length", null, null);
		db.delete("date_data", null, null);
	}
	
	public void delete_info() {

		db = helper.getWritableDatabase();

		//	db.delete("small_item_info", null, null);
		db.delete("large_item_info", null, null);
		db.delete("small_mart_info", null, null);
		db.delete("large_mart_info", null, null);
		db.delete("small_mart_info2", null, null);
		db.delete("info_length", null, null);
	}

	public void delete_table_1_4(){

		db = helper.getWritableDatabase();

		db.delete("select_item", null, null);
		db.delete("nfc_tag_item", null, null);
	}
	
	public void delete_table_9(){

		db = helper.getWritableDatabase();

		db.delete("date_data", null, null);
	}
	
	public void delete_table_10(){

		db = helper.getWritableDatabase();

		db.delete("intent_mart_name", null, null);
	}

	// Main.java에서 사용
	public Cursor selecteach(String name) {
		db = helper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM small_mart_info WHERE name LIKE '" + name + "'", null);
		return c;
	}

	public Cursor selectinfo(String name) {
		db = helper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM select_item WHERE name LIKE '" + name + "'", null);
		return c;
	}
	
	public Cursor selectbookmarker(String name) {
		db = helper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM small_mart_info2 WHERE name LIKE '" + name + "'", null);
		return c;
	}
	
	public Cursor selecteach_martname(String mart_name) {
		db = helper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM small_mart_info WHERE mart_name LIKE '" + mart_name + "'", null);
		return c;
	}

	public Cursor select(int type) {

		db = helper.getReadableDatabase();

		Cursor c1 = null;
	//	Cursor c2 = null;
		Cursor c3 = null;
		Cursor c4 = null;
		Cursor c5 = null;
		Cursor c6 = null;
		Cursor c7 = null;
		Cursor c8 = null;
		Cursor c9 = null;
		Cursor c10 = null;
		Cursor c = null;

		switch(type){
		case 1:
			c1 = db.query("select_item", null, null, null, null, null, null);
			c = c1;
			break;
			/*case 2:
			c2 = db.query("small_item_info", null, null, null, null, null, null);
			c = c2;
			break;*/
		case 3:
			c3 = db.query("large_item_info", null, null, null, null, null, null);
			c = c3;
			break;
		case 4:
			c4 = db.query("nfc_tag_item", null, null, null, null, null, null);
			c = c4;
			break;
		case 5:
			c5 = db.query("small_mart_info", null, null, null, null, null, null);
			c = c5;
			break;
		case 6:
			c6 = db.query("large_mart_info", null, null, null, null, null, null);
			c = c6;
			break;
		case 7:
			c7 = db.query("small_mart_info2", null, null, null, null, null, null);
			c = c7;
			break;
		case 8:
			c8 = db.query("info_length", null, null, null, null, null, null);
			c = c8;
			break;
		case 9:
			c9 = db.query("date_data", null, null, null, null, null, null);
			c = c9;
			break;
		case 10:
			c10 = db.query("intent_mart_name", null, null, null, null, null, null);
			c = c10;
			break;
		}
		return c;
	}
}