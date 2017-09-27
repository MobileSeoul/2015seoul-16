package com.exam.zzz_other_menu;

import java.util.*;

import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.*;
import android.nfc.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;

import com.exam.zzz_other_menu_mysql.*;
import com.example.n_mart.*;

public class Select_mart extends Activity {

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	MySQLiteHandler handler;
	Cursor c;
	ExpandableListView Select_mart;

	ArrayList<ArrayList<Select_mart_Model>> childList; // 소그룹에 연결할 리스트 생성
	ArrayList<Select_mart_GM> groups; // 대그룹에 연결할 리스트 생성
	Select_mart_Adapter adapter; // 연결어댑터 생성

	String[] temp;
	int Lotte = 0;
	int Home = 0;
	int E_m = 0;
	int Hanaro = 0;
	int Sin = 0;
	int Lotte_dep = 0;
	int H_dep = 0;

	String MartName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	 // 액션바 없애기
		setContentView(R.layout.select_mart);

		Log.i("Select_mart.java", "oncreate!!");

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
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
		Select_mart = (ExpandableListView) findViewById(R.id.mart_expandableListView2); // 마트명이 나타날 layout

		Select_mart.setFocusable(false);

		Log.i("Select_mart.java", "handler를 생성합니다");
		handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 사용위한 open 구문

		Log.i("Select_mart.java", "6번테이블을 불러옵니다");
		c = handler.select(6); // info클래스를 불러야할지 말아야 할지 정하는 getCount를 위해서 있음
		c.moveToFirst(); // 없으면 getCount 실행 안됨

		groups = new ArrayList<Select_mart_GM>(); // 대그룹에 연결할 리스트 생성

		Log.i("Select_mart.java", "매장대그룹 정보를 화면에 출력하기 위해 사용하는 부분입니다");
		if(c.getCount()!=0){ // 데이터베이스에 정보가 있어야만 실행되도록 만듬
			Log.i("Select_mart.java", "매장대그룹 정보 저장 시작");
			for(int i=0; i<c.getCount(); i++) // i의 조건을 일반적으로 만들어야 함... // 소그룹들의 갯수가 항상 맞는 것은 아닐듯...?? // 어떤거는 5개 어떤거는 7개 이럴지도...
			{
				Select_mart_GM g = new Select_mart_GM(); // 대그룹리스트에 정보객체

				g.setName(c.getString(1).toString()); // 대그룹리스트에 정보저장->그룹이름
				groups.add(g); // 대그룹어레이리스트에 저장

				c.moveToNext();
			}
			Log.i("Select_mart.java", "매장대그룹 정보 저장 끝");
		}

		Log.i("Select_mart.java", "매장소그룹 정보를 화면에 출력하기 위해 사용하는 부분입니다");
		childList = new ArrayList<ArrayList<Select_mart_Model>>(); // 소그룹리스트 생성
		ArrayList<Select_mart_Model> list = new ArrayList<Select_mart_Model>(); // 소그룹리스트에 들어가는 정보리스트 생성
		Select_mart_Model m = new Select_mart_Model(); // 소그룹정보리스트 정보객체

		Log.i("Select_mart.java", "7번 테이블을 불러옵니다");
		c = handler.select(7); // 데이터베이스 테이블 mart2의 내용을 전부 검색함
		c.moveToFirst(); // 없으면 아래 getCount 실행 안됨

		Log.i("Select_mart.java", "각 마트가 몇번씩 들어갔는지 확인하기 위해서 사용하는 부분입니다");
		if(c.getCount()!=0){ // 데이터베이스에 정보가 있어야만 실행되도록 만듬

			for(int i = 0; i<c.getCount(); i++) // i의 조건을 일반적으로 만들어야 함... // 소그룹들의 갯수가 항상 맞는 것은 아닐듯...?? // 어떤거는 5개 어떤거는 7개 이럴지도...
			{
				if((c.getString(1).toString().contains("롯데마트"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					Lotte++;
				}
				else if((c.getString(1).toString().contains("홈플러스"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					Home++;
				}
				else if((c.getString(1).toString().contains("이마트"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					E_m++;
				}
				else if((c.getString(1).toString().contains("하나로마트"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					Hanaro++;
				}
				else if((c.getString(1).toString().contains("신세계백화점"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					Sin++;
				}
				else if((c.getString(1).toString().contains("롯데백화점"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					Lotte_dep++;
				}
				else if((c.getString(1).toString().contains("현대백화점"))){
					c.moveToNext(); // 커서를 다음으로 이동시킴
					H_dep++;
				}
				else{
					Log.i("Select_mart.java", "잘 나왔는지 확인해봐요~~");
				}
			}
		}

		if(c.getCount()!=0) {
			Log.i("Select_mart.java", "매장 소그룹정보를 저장하는 부분 시작");
			c = handler.select(7);
			c.moveToFirst();

			for(int j=0; j<Lotte; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<E_m; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<Home; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<Hanaro; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<Sin; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<H_dep; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			for(int j=0; j<Lotte_dep; j++){
				m.setMartName(c.getString(1).toString()); // 소그룹리스트에 들어가는 정보리스트에 출력되는 이름
				m.setBookMarker(c.getInt(2));
				list.add(m); // 소그룹리스트에 들어가는 정보리스트에 추가
				m = new Select_mart_Model(); // 새로운 소그룹정보객체를 생성 // 안하면 계속 덮어써져서 맨 마지막 데이터만 쭉 출력됨
				c.moveToNext(); // 커서를 다음으로 이동시킴
			}

			childList.add(list); // 소그룹리스트에 추가
			list = new ArrayList<Select_mart_Model>(); // 새로운 소그룹 정보리스트를 생성 // 확인은 안해봤지만 안하면 계속 덮어써지거나 에러날듯...

			Log.i("Select_mart.java", "매장 소그룹정보를 저장하는 부분 끝");
		}

		adapter = new Select_mart_Adapter(getApplicationContext(), groups, childList); // 커스텀 익스펜더블 리스트뷰에 연결
		adapter.notifyDataSetChanged();

		Select_mart.setAdapter(adapter); // 어댑터와 익스팬더블 리스트뷰 연결

		Log.i("Select_mart.java", "매장을 선택했을때 뜨는 다이얼로그를 실행합니다");

		Select_mart.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				final Select_mart_Model model = adapter.getChild(groupPosition, childPosition);

				Builder dlg = new AlertDialog.Builder(Select_mart.this);
				dlg.setTitle("선택")
				.setMessage(model.getMartName() + " 를 선택 하셨습니다 맞으면 확인 아니면 취소를 선택하세요")
				.setNeutralButton("바로시작", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), list.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
						intent.putExtra("select_mart_name", model.getMartName());
						intent.putExtra("select_mart_name2", model.getMartName());
						startActivity(intent); // 인텐트 시작
						finish(); // 인텐트 후 액티비티 종료
					}
				})
				.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
					// 다이얼로그의 예 버튼이 눌리면 동작

					@Override
					public void onClick(DialogInterface dialog,	int whichButton) {
						Intent intent2 = new Intent(getApplicationContext(), Main.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
						intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
						intent2.putExtra("select_mart_name", model.getMartName());
						startActivity(intent2); // 인텐트 시작
						finish(); // 인텐트 후 액티비티 종료
					}
				})
				// 다이얼로그의 아니오 버튼
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// ignore, just dismiss
						// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
					}
				}).show(); // 다이얼로그 마지막구문

				return false;
			}
		});
		handler.close(); // 데이터베이스 사용 종료
	}

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
	protected void onPause() {
		super.onPause();
		if(mNfcAdapter!=null){
			Log.d(TAG, "onPause: " + getIntent());

			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
}