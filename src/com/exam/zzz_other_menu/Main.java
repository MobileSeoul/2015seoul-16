package com.exam.zzz_other_menu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import android.database.Cursor;

import android.nfc.NfcAdapter;

import android.os.Bundle;

import android.util.Log;

import android.view.Window;
import android.view.View;
//import android.view.KeyEvent;

import android.widget.Button;
import android.widget.ExpandableListView;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.example.n_mart.R;

public class Main extends Activity {

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	MySQLiteHandler handler; // 데이터베이스 사용위한 핸들러 생성
	Cursor c; // 원하는 데이터베이스 테이블 사용위한 커서

	ExpandableListView lv; // 익스펜더블 리스트뷰 생성
	ArrayList<ArrayList<Model>> childList; // 소그룹에 연결할 리스트 생성
	ArrayList<GM> groups; // 대그룹에 연결할 리스트 생성
	Button b1; // 버튼생성
	Adapter adapter; // 연결어댑터 생성

	int length = 100;
	String temp[] = new String[length];

	int fruit = 0;
	int farm = 0;
	int animal = 0;
	int fish = 0;
	int item_length = 0;
	int item_size = 0;

	String mart_name;

	public void onCreate(Bundle savedInstanceState)
	{
		Log.w("Main.java", "oncreate!!");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	 // 액션바 없애기
		setContentView(R.layout.main_exlist);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null){
		} else {
			mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
			try	{
				ndefDetected.addDataType("application/root.gast.playground.nfc");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Could not add MIME type.", e);
			}
		}
		
		Intent intent = getIntent();
		mart_name = intent.getExtras().get("select_mart_name").toString();

		Log.w("Main.java", "Handler 사용위해 생성");
		handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 사용위한 open 구문

		Log.w("Main.java", "1번 테이블을 불러옵니다");
		c = handler.select(1);
		c.moveToFirst();

		for(int i=0; i<length; i++){
			temp[i]="";
		}

		for(int i=0; i<c.getCount(); i++){
			temp[i] = c.getString(2).toString();
			c.moveToNext();
		}

		lv = (ExpandableListView) findViewById(R.id.expandableListView1); // 익스펜더블 리스트뷰 생성
		groups = new ArrayList<GM>(); // 대그룹에 연결할 리스트 생성

		Log.w("Main.java", "3번 테이블을 불러옵니다");
		c = handler.select(3);
		c.moveToFirst();

		if(c.getCount()!=0){ // 데이터베이스에 정보가 있어야만 실행되도록 만듬
			Log.w("Main.java", "물품 대그룹정보 저장 시작");
			for(int i=0; i<c.getCount(); i++) {
				GM g = new GM(); // 대그룹리스트에 정보객체
				g.setName(c.getString(1).toString()); // 대그룹리스트에 정보저장->그룹이름
				groups.add(g); // 대그룹어레이리스트에 저장

				c.moveToNext();
			}
			Log.w("Main.java", "물품 대그룹정보 저장 끝");
		}

		Log.w("Main.java", "물품 소그룹정보 저장하기 위한 준비");
		childList = new ArrayList<ArrayList<Model>>(); // 소그룹리스트 생성
		ArrayList<Model> list = new ArrayList<Model>(); // 소그룹리스트에 들어가는 정보리스트 생성
		Model m = new Model(); // 소그룹정보리스트 정보객체

		Log.w("Main.java", "8번테이블을 불러옵니다");
		c = handler.select(8);
		c.moveToFirst();

		item_length = c.getInt(2);
		item_size = c.getInt(1);

		Log.w("Main.java", "5번테이블을 불러옵니다");
		c = handler.select(5);
		c.moveToFirst();

		if(c.getCount()!=0){ // 데이터베이스에 정보가 있어야만 실행되도록 만듬

			Log.w("Main.java", "물품소그룹정보 저장준비 시작");
			for(int i=0; i<item_size; i++){
				if(c.getString(1).toString().equals(mart_name)){
					for(int j=0; j<item_length; j++) {
						if((c.getString(2).toString().contains("사과"))||
								(c.getString(2).toString().contains("배"))){
							if(c.getString(2).toString().contains("배추")){
								c.moveToNext();
								farm++;
							}
							else {
								c.moveToNext(); // 커서를 다음으로 이동시킴
								fruit++;
							}
						}
						else if((c.getString(2).toString().contains("배추"))||
								(c.getString(2).toString().contains("무"))||
								(c.getString(2).toString().contains("양파"))||
								(c.getString(2).toString().contains("상추"))||
								(c.getString(2).toString().contains("오이"))||
								(c.getString(2).toString().contains("호박"))){
							c.moveToNext(); // 커서를 다음으로 이동시킴
							farm++;
						}
						else if((c.getString(2).toString().contains("쇠고기"))||
								(c.getString(2).toString().contains("돼지고기"))||
								(c.getString(2).toString().contains("닭고기"))||
								(c.getString(2).toString().contains("달걀"))){
							c.moveToNext(); // 커서를 다음으로 이동시킴
							animal++;
						}
						else if((c.getString(2).toString().contains("조기"))||
								(c.getString(2).toString().contains("동태"))||
								(c.getString(2).toString().contains("명태"))||
								(c.getString(2).toString().contains("오징어"))||
								(c.getString(2).toString().contains("고등어"))){
							c.moveToNext(); // 커서를 다음으로 이동시킴
							fish++;
						}
						else{
							Log.i("Main.java", "잘 나왔는지 확인해봐요~~");
							break;
						}
					}
					break;
				}
				else{
					c.moveToNext();
				}
			}
			Log.w("Main.java", "물품 소그룹정보 저장준비 끝");
		}

		if(c.getCount()!=0) {
			Log.i("Main.java", "물품 소그룹정보를 저장하는 부분 시작");
			c = handler.select(5);
			c.moveToFirst();

			for(int i=0; i<item_size; i++){
				if(c.getString(1).toString().equals(mart_name)){
					for(int j=0; j<fruit; j++){
						m.setItemName(c.getString(2).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
						m.setPrice(c.getInt(3));
						m.setAmount(0);
						m.setLat(c.getDouble(8));
						m.setLon(c.getDouble(9));
						list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
						m = new Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
						c.moveToNext(); // 커서를 다음으로 이동시킴
					}

					childList.add(list); // 소그룹리스트에 추가
					list = new ArrayList<Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

					for(int j=0; j<farm; j++){
						m.setItemName(c.getString(2).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
						m.setPrice(c.getInt(3));
						m.setAmount(0);
						m.setLat(c.getDouble(8));
						m.setLon(c.getDouble(9));
						list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
						m = new Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
						c.moveToNext(); // 커서를 다음으로 이동시킴
					}

					childList.add(list); // 소그룹리스트에 추가
					list = new ArrayList<Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

					for(int j=0; j<animal; j++){
						m.setItemName(c.getString(2).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
						m.setPrice(c.getInt(3));
						m.setAmount(0);
						m.setLat(c.getDouble(8));
						m.setLon(c.getDouble(9));
						list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
						m = new Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
						c.moveToNext(); // 커서를 다음으로 이동시킴
					}

					childList.add(list); // 소그룹리스트에 추가
					list = new ArrayList<Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...


					for(int j=0; j<fish; j++){
						m.setItemName(c.getString(2).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
						m.setPrice(c.getInt(3));
						m.setAmount(0);
						m.setLat(c.getDouble(8));
						m.setLon(c.getDouble(9));
						list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
						m = new Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
						c.moveToNext(); // 커서를 다음으로 이동시킴
					}

					childList.add(list); // 소그룹리스트에 추가
					list = new ArrayList<Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

					break;
				}
				else{
					c.moveToNext();
				}
			}
			Log.i("Main.java", "매장 소그룹정보를 저장하는 부분 끝");
		}
		
		c = handler.select(7);
		c.moveToFirst();
		
		for(int i=0; i<c.getCount(); i++){
			Log.d("7번테이블검사~~", c.getString(1).toString() + " " + Integer.toString(c.getInt(2)));
			c.moveToNext();
		}

		handler.close(); // 데이터베이스 사용 종료

		adapter = new Adapter(getApplicationContext(), groups, childList); // 커스텀 익스펜더블 리스트뷰에 연결
		adapter.notifyDataSetChanged();
		lv.setAdapter(adapter); // 어댑터와 익스팬더블 리스트뷰 연결
		b1 = (Button) findViewById(R.id.button1); // 내가 선택한 물품들을 출력해주는 list.java파일로 보내는 인텐트 구문 실행 버튼

		// 버튼을 클릭했을 때의 이벤트 실행을 위한 구문
		b1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0)
			{
				ArrayList<ArrayList<Model>> list = adapter.getAllList(); // 대그룹리스트 객체에 그동안 저장한 정보를 저장
				int size = list.size(); // 대그룹리스트의 길이 계산
				for (int i = 0; i < size; i++)
				{
					int subSize = list.get(i).size(); // 대그룹리스트 내의 소그룹리스트의 길이계산
					ArrayList<Model> subList = new ArrayList<Model>(); // 소그룹리스트 생성
					subList = list.get(i); // 소그룹리스트에 정보 저장
					for (int j = 0; j < subSize; j++)
					{
						if (subList.get(j).getAmount()!=0) {
							for(int k=0; k<length; k++) {
								if(temp[k].equals(subList.get(j).getItemName())) {
									Log.i("Main.java", "이미 있는 물품 입니다");
									break;
								}
								else if(isNull(temp[k])) {
							//		Log.i("Main.java", "추가되는 물품 입니다");

									Cursor cs = handler.selecteach(subList.get(j).getItemName()); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장
									cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용 // 안쓰면 커서위치는 -1
									double x = cs.getDouble(5); // 검색한 커서가 위치한 곳의 수량정보 저장
									double y = cs.getDouble(6);
									handler.insert(mart_name, subList.get(j).getItemName(), subList.get(j).getPrice(), subList.get(j).getAmount(), x, y, 0, subList.get(j).getLat(), subList.get(j).getLon(), 1); // 데이터베이스 테이블 mart로의 추가
									handler.close(); // 데이터베이스 사용 종료
									break;
								}
								else {
									Log.d("if, else if pass", "temp");
								}
							}
						}
					}
				}

				Intent intent = new Intent(getApplicationContext(), PriceComp.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
				intent.putExtra("select_mart_name", mart_name);
				startActivity(intent); // 인텐트 시작
			//	finish();
			}
		});
	}
/*	
	// 단말기 메뉴, 홈, 뒤로가기 버튼 이벤트 관리부분
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BACK:
				finish();
				// 단말기의 BACK버튼
				return true;
			case KeyEvent.KEYCODE_MENU:
				// 단말기의 메뉴버튼
				return true;
					case KeyEvent.KEYCODE_HOME:
				// 단말기의 HOME버튼 -> 동작안함
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}*/

	public boolean isNull(String value){
		if(value==null || value.trim().length()==0){
			return true;
		} else {
			return false;
		}
	}

	// 이 아래부터는 NFC관련 함수

	// 포어그라운드 디스패치 활성화
	@Override
	protected void onResume() {
		super.onResume();
		if(mNfcAdapter!=null){
			mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadTagFilters, null);
		}
	}

	// 포어그라운드 디스패치 비활성화
	@Override
	protected void onPause()
	{
		super.onPause();
		if(mNfcAdapter!=null){
			Log.d(TAG, "onPause: " + getIntent());

			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
}