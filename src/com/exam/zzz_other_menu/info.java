package com.exam.zzz_other_menu;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;

import android.content.Intent;

import android.database.Cursor;

import android.os.Bundle;

import android.util.Log;

import android.widget.Toast;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;

// 어플 메인이 뜨기 전 익스펜더블 리스트뷰에 항목들을 저장하기 위한 액티비티
public class info extends Activity {

	MySQLiteHandler handler; // 데이터베이스 사용하기 위한 handler
	Cursor c; // 데이터베이스 사용위해 테이블을 찾아줄 커서

	ArrayList<Info_save_class> item_list; // Parsing_data.java에서 넘어오는 어레이리스트를 저장할 어레이리스트

	int item_breakpoint = 0;
	int item_node_length = 0;
	String temp = ""; // 7번테이블에 저장시 매장이름이 중복되는 것 방지하기 위해서 사용

	// 날짜 저장하기 위한 변수
	int year;
	int month;
	int date;
	int hour;
	int minute;
	int second;
	
	double x[]; // x좌표
	double y[]; // y좌표

	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("info.java", "onCreate!!");
		super.onCreate(savedInstanceState);

		item_list = new ArrayList<Info_save_class>();

		// 물품 x좌표
		x = new double[]
				{258.0, 329.0,
				189.0, 132.0, 7.0, 190.0, 66.0, 7.0,
				262.0, 335.0, 338.0, 415.0,
				7.0, 7.0, 72.0, 62.0};

		// 물품 y좌표
		y = new double[]
				{110.0, 110.0,
				110.0, 110.0, 100.0, 43.0, 43.0, 43.0,
				515.0, 515.0, 580.0, 515.0,
				278.0, 475.0, 375.0, 580.0};

		Log.d("info.java", "인텐트가 잘 되나 봅시다");
		Intent intent2 = getIntent();
		if(intent2!=null) {

			Log.d("info.java", "인텐트가 잘 됩니다");
			item_list = (ArrayList<Info_save_class>)getIntent().getSerializableExtra("item_list");
			item_breakpoint = intent2.getIntExtra("length", 0);
			item_node_length = intent2.getIntExtra("item_length", 0);
		}

		handler = MySQLiteHandler.open(getApplicationContext());

		Log.d("info.java", "여기서부터 정보를 저장합니다");

		Calendar cal = Calendar.getInstance();

		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		date = cal.get(Calendar.DATE);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		second = cal.get(Calendar.SECOND);
/*
		Log.d("확인", Integer.toString(year));
		Log.d("확인", Integer.toString(month));
		Log.d("확인", Integer.toString(date));
		Log.d("확인", Integer.toString(hour));
		Log.d("확인", Integer.toString(minute));
		Log.d("확인", Integer.toString(second));
*/
		c = handler.select(9);
		c.moveToFirst();

		if(c.getCount()==0){
			handler.insert_date(year, month, date, hour, minute, second);

			Table_3();
			Table_5();
			Table_6();
			Table_7();
			Table_8();
		} else {
			if(c.getInt(1)==year && c.getInt(2)==month && c.getInt(3)==date/* && c.getInt(4)==hour && c.getInt(5)==minute*/){
				Log.d("같아서 그냥 넘어갑니다", "1");
			} else {
				handler.update(year, month, date, hour, minute, second);

				handler.delete_info();

				Toast.makeText(getApplicationContext(), "오늘 처음 실행하셨으므로 정보를 다시 씁니다", Toast.LENGTH_SHORT).show();

				Table_3();
				Table_5();
				Table_6();
				Table_7();
				Table_8();
			}
		}

		handler.close();

		Log.d("info.java", "다음 액티비티로 넘어가는 인텐트부분입니다");

		Intent intent = new Intent(getApplicationContext(), SelectMenu.class); // 데이터베이스에 저장후 Main액티비티로 전환
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 재사용하기 위함
		startActivity(intent); // 인텐트 시작
		finish();
	}

	public void Table_3(){
		c = handler.select(3);
		c.moveToFirst();

		if(c.getCount()==0) {

			String[] Name = new String[]{"과일","농산물","축산물","수산물"};

			Log.d("info.java", "물품대그룹을 저장합니다");
			for(int i=0; i<Name.length; i++) {
				handler.insert("", Name[i], 0, 0, 0, 0, 0, 0, 0, 3);
			}
		}
	}

	public void Table_5(){
		c = handler.select(5);
		c.moveToFirst();

		int l = 0;
		
		if(c.getCount()==0){

			Log.d("info.java", "물품소그룹을 저장합니다");
			for(int k=0; k<item_breakpoint; k++) {
				handler.insert(item_list.get(k).getMartName(), item_list.get(k).getName(), item_list.get(k).getPrice(), 1, x[l], y[l], 0, item_list.get(k).getLat(), item_list.get(k).getLon(), 5); // 위의 항목들을 데이터베이스에 저장
				
				if(l==15){
					l=0;
				} else {
					l++;
				}
			}
		}
	}

	public void Table_6(){

		String[] large_mart_name = new String[]{"롯데마트","이마트","홈플러스","하나로마트","신세계백화점","현대백화점","롯데백화점"};

		c = handler.select(6);
		c.moveToFirst();

		if(c.getCount()==0){

			Log.d("info.java", "매장대그룹을 저장합니다");
			for(int i=0; i<large_mart_name.length; i++) {
				handler.insert(large_mart_name[i], "", 0, 0, 0, 0, 0, 0, 0, 6);
			}
		}
	}

	public void Table_7(){
		c = handler.select(7);
		c.moveToFirst();

		if(c.getCount()==0){
			Log.d("info.java", "매장소그룹을 저장합니다");
			
			for(int i=0; i<item_breakpoint; i++) {
				if(temp.equals(item_list.get(i).getMartName())){
					Log.d("info.java", "소그룹저장시 중복방지용");
				}
				else{
					handler.insert_bookmarker(item_list.get(i).getMartName(), 0);
					temp = item_list.get(i).getMartName();
				}
			}
		}
	}

	public void Table_8(){
		c = handler.select(8);
		c.moveToFirst();

		if(c.getCount()==0){
			handler.insert_length(item_breakpoint, item_node_length);
		}
	}
}