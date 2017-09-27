package com.exam.zzz_other_menu;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.example.n_mart.R;

public class list extends Activity {

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;
	private boolean mWriteMode = false;

	ListView list; // 리스트뷰
	TextView AllPrice; // 총 금액 나타내는 텍스트뷰
	TableLayout tl; // 메뉴버튼에 들어가는 버튼들....
	TextView NowAllPrice; // 현재금액용

	int length = 100; // 배열들 길이
	int menu_button_visible_count = 0; // 메뉴버튼 클릭시 나타나게 또는 사라지게 하기위해서 사용하는 변수
	int info_control = 0; // info실행 조절용

	int allprice = 0; // 총액을 나타내는 변수
	int allprice_cal = 0; // 총액을 계산하기 위해서 사용하는 변수
	int nowallprice = 0; // 현재금액을 나타내는 변수
	int nowallprice_cal = 0; // 현재금액을 계산하기 위해서 사용하는 변수

	MySQLiteHandler handler; // 데이터베이스 사용위한 handler
	Cursor c; // 데이터베이스 사용위한 커서
	Cursor cur; // table4에 쓰는 커서 // nfc찍었을때 찍혔다는 표시위해 저장하는 table용

	int pos = 0; // nfc찍은 후 리스트 재구성시 맨 위로 올라가는것을 찍힌놈이 나타나게 하려는 pos

	String intentmart_name; // nfc찍힌 놈들을 save, save2에 전달하기 위한 nfc찍힌 이름변수
	String intentname; // nfc찍힌 놈들을 save, save2에 전달하기 위한 nfc찍힌 이름변수
	int intentprice = 0; // nfc찍힌 놈들을 save, save2에 전달하기 위한 nfc찍힌 이름변수

	// 커스텀 numberpicker에서 사용하는 변수
	int uprange = 20; // 최대숫자
	int downrange = 0; // 최소숫자
	int values = 1; // 현재숫자

	DataAdapter adapter; // 커스텀 어댑터
	ArrayList<cdata> alist; // 리스트에 뿌리는 정보가 담긴 어레이리스트이자 또다시 추가되는 것 막기 위해 사용하는 어레이리스트
	ArrayList<cdata> alist2; // NFC가 찍힌 물품의 정보가 담긴 어레이리스트이자 또다시 추가되는 것 막기 위해 사용하는 어레이리스트

	static ArrayList<String> itemlist; // 객체의 이름
	static ArrayList<Double> itemlist_x; // 객체의x좌표
	static ArrayList<Double> itemlist_y; // 객체의y좌표
	static ArrayList<Integer> itemstate; // 객체갱신을 위한 임의의 변수

	static int cpzm = 0; // nfc추가할 경우에 사용
	static int cpzm2 = 0; // nfc가 찍혔다는 것을 표시하는 경우에 사용 // 색 변경된 것 다시 변경시키지 않고 있으므로 필요없을 듯???

	String comp_mart_name;
	String comp_mart_name2;

	ArrayList<PriceCompData_small> selectitemlist;
	ArrayList<PriceCompData_small> blist;

	int selectcpzm = 0;

	//터치 위치에 원이 표시되는 사용자정의 뷰 지정
	public TipsView m_tips_view;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.v("list.java", "oncreate!!");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 	 // 액션바 없애기
		setContentView(R.layout.activity_main);

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

			IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

			// create IntentFilter arrays:
			mWriteTagFilters = new IntentFilter[] { tagDetected };
			mReadTagFilters = new IntentFilter[] { ndefDetected, tagDetected };

		}
		AllPrice = (TextView) findViewById(R.id.text2); // 총액가격을 출력하기위한 텍스트뷰 연결
		tl = (TableLayout) findViewById(R.id.tl); // 메뉴버튼 눌렀을 때 나오는 부분
		NowAllPrice = (TextView) findViewById(R.id.text8);

		// 리소스 파일에 정의된 id_view 라는 ID 의 사용자정의뷰를 얻어온다.
		m_tips_view = (TipsView)findViewById(R.id.id_view1);

		tl.setVisibility(View.GONE); // 메뉴버튼과 관련

		handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 핸들러 오픈
		alist = new ArrayList<cdata>(); // 어레이리스트 객체생성
		alist2 = new ArrayList<cdata>(); // 어레이리스트 객체생성
		itemlist = new ArrayList<String>(); // 객체의 이름
		itemlist_x = new ArrayList<Double>(); // 객체의x좌표
		itemlist_y = new ArrayList<Double>(); // 객체의y좌표
		itemstate = new ArrayList<Integer>(); // 객체갱신을 위한 임의의 변수

		selectitemlist = new ArrayList<PriceCompData_small>();
		blist = new ArrayList<PriceCompData_small>();

		Log.v("list.java", "변수초기화 및 각 뷰 연결완료");

		Intent intent = getIntent();

		comp_mart_name = intent.getExtras().get("select_mart_name").toString(); // 처음에 선택한 마트
		comp_mart_name2 = intent.getExtras().get("select_mart_name2").toString(); // 가격비교에서 선택한 마트

		c = handler.select(1);
		c.moveToFirst();

		for(int i=0; i<c.getCount(); i++){
			selectitemlist.add(new PriceCompData_small(c.getString(2).toString(), c.getInt(4)));

			c.moveToNext();
		}

		for(int i=0; i<selectitemlist.size(); i++){
			String a = selectitemlist.get(i).getName();
			if(a.contains("사과")){
				blist.add(new PriceCompData_small("사과", selectitemlist.get(i).getAmount()));
			} else if(a.contains("배")){
				if(a.contains("배추")){
					blist.add(new PriceCompData_small("배추", selectitemlist.get(i).getAmount()));
				} else {
					blist.add(new PriceCompData_small("배", selectitemlist.get(i).getAmount()));
				}
			} else if(a.contains("무")){
				blist.add(new PriceCompData_small("무", selectitemlist.get(i).getAmount()));
			} else if(a.contains("양파")){
				blist.add(new PriceCompData_small("양파", selectitemlist.get(i).getAmount()));
			} else if(a.contains("상추")){
				blist.add(new PriceCompData_small("상추", selectitemlist.get(i).getAmount()));
			} else if(a.contains("오이")){
				blist.add(new PriceCompData_small("오이", selectitemlist.get(i).getAmount()));
			} else if(a.contains("호박")){
				blist.add(new PriceCompData_small("호박", selectitemlist.get(i).getAmount()));
			} else if(a.contains("쇠고기")){
				blist.add(new PriceCompData_small("쇠고기", selectitemlist.get(i).getAmount()));
			} else if(a.contains("돼지고기")){
				blist.add(new PriceCompData_small("돼지고기", selectitemlist.get(i).getAmount()));
			} else if(a.contains("닭고기")){
				blist.add(new PriceCompData_small("닭고기", selectitemlist.get(i).getAmount()));
			} else if(a.contains("달걀")){
				blist.add(new PriceCompData_small("달걀", selectitemlist.get(i).getAmount()));
			} else if(a.contains("조기")){
				blist.add(new PriceCompData_small("조기", selectitemlist.get(i).getAmount()));
			} else if(a.contains("명태") || a.contains("동태")){
				blist.add(new PriceCompData_small("명태", selectitemlist.get(i).getAmount()));
			} else if(a.contains("오징어")){
				blist.add(new PriceCompData_small("오징어", selectitemlist.get(i).getAmount()));
			} else if(a.contains("고등어")){
				blist.add(new PriceCompData_small("고등어", selectitemlist.get(i).getAmount()));
			}
		}

		if(comp_mart_name.equals(comp_mart_name2)){
			c = handler.select(1); // main.java에서 선택한 물품들이 저장된 데이터베이스 모두 조회
			c.moveToFirst(); // 커서의 위치를 처음으로 위치

			// main.java에서 선택한 물품들이 저장된 데이터베이스의 정보를 검사용 배열 temp로 옮기는 부분
			// 동시에 리스트뷰를 구성할 어레이리스트에도 순서대로 이름, 가격, 수량저장
			for (int i = 0; i < c.getCount(); i++) {
				// 리스트뷰를 구성할 어레이리스트에 순서대로 이름, 가격, 수량저장
				alist.add(new cdata(getApplicationContext(), c.getString(2).toString(), c.getInt(3), c.getInt(4)));
				itemlist_x.add(i, c.getDouble(5)); // x좌표
				itemlist_y.add(i, c.getDouble(6)); // y좌표
				itemlist.add(i, c.getString(2).toString()); // 해당하는 물품 이름저장
				itemstate.add(i, c.getInt(7)); // 물품이 찍혔는지 안찍혔는지를 표시하여 지도에 나타내기 위한 변수
				c.moveToNext(); // 커서를 다음으로 이동시킴
				// nfc찍었을때 없는물품이면 추가함수가 발생하고 추가한 후 또 추가하지 않기위해 배열에 저장하기 위해 마지막 인덱스 구하는 부분
			}
		} else {

			handler.delete_table_1_4();

			c = handler.select(5);
			c.moveToFirst();

			for(int i=0; i<c.getCount(); i++){
				if(c.getString(1).toString().equals(comp_mart_name)){
					for(int j=0; j<blist.size(); j++){
						if(c.getString(2).toString().contains(blist.get(j).getName())){
							if(blist.get(j).getName().equals("배추")){
								selectcpzm = 1;
								processadd(comp_mart_name, c.getString(2).toString(), c.getInt(3), blist.get(j).getAmount());
							} else {
								if(c.getString(2).toString().contains("배추") && blist.get(j).getName().equals("배")){

								} else {
									selectcpzm = 1;
									processadd(comp_mart_name, c.getString(2).toString(), c.getInt(3), blist.get(j).getAmount());
								}
							}
						}
					}
				}
				c.moveToNext();
			}
		}

		// 메뉴에서 장바구니삭제시 아이템리스트부분 삭제해야하나 말아야하나 확인용
		for(int i=0; i<itemlist.size(); i++){
			Log.e("아이템리스트", itemlist.get(i));
		}
		
		Log.v("list.java", "전달받은 선택된 물품정보 출력위한 정보저장완료");

		list = (ListView) findViewById(R.id.list); // main에서 선택한 물품이 출력되는 리스트, 커스텀리스트뷰가 이 리스트뷰에 출력됨
		list.setFocusable(false); // 리스트뷰와 버튼이 서로 간섭하지 못하게  선택이 둘다 되도록 안하면 하나만 선택됨

		cur = handler.select(4); // nfc찍힌 물품들 저장되는 데이터베이스 모두 조회
		cur.moveToFirst(); // 커서를 처음으로 위치

		// nfc찍힌 물품들이 저장된 데이터베이스의 정보를 검사용 배열에 저장하는 부분
		if (cur.getCount() != 0) {
			for (int i = 0; i < cur.getCount(); i++) {
				alist2.add(new cdata(getApplicationContext(), cur.getString(1).toString(), cur.getInt(2), cur.getInt(3)));
				cur.moveToNext(); // 커서를 다음으로 이동시킴
			}
		}

		Log.v("list.java", "NFC찍힌 물품정보 관리위한 정보저장완료");

		priceupdata(); // 총액을 나타내는 함수
		priceUpdata2(); // 현재금액을 나타내는 함수
		adapter = new DataAdapter(this, alist); // 커스텀어댑터에 정보출력용 어레이리스트 연결
		adapter.notifyDataSetChanged(); // 리스트의 정보가 변경되었다고 어댑터에게 알림
		list.setAdapter(adapter); // 리스트뷰와 어댑터 연결
		// 리스트뷰 목록을 길게 누르면 삭제다이얼로그가 뜨게 하기위한 구문
		list.setOnItemLongClickListener(new ListViewItemLongClickListener());
		// 모든 물품이 nfc로 찍혔을때 종료다이얼로그가 뜨게하는 함수
		//exit();
		handler.close(); // 데이터베이스 종료

		Log.v("list.java", "oncreate부분 종료");
	}

	// 햅틱
	public void myfunction(){

		Log.v("list.java", "햅틱부분시작");
		m_tips_view.performHapticFeedback(1);
	}

	// 추가할건지 물어보는 다이얼로그 // 예를 선택하면 추가함수로 가고 아니오를 선택하면 그냥 다이얼로그만 종료
	public void add(final String pmart_name, final String pname, final int pprice, final int pamount) {
		Log.v("list.java", "추가할건지 물어보는 다이얼로그");
		cpzm++;
		new AlertDialog.Builder(this) // 다이얼로그 생성문
		.setTitle(R.string.add_singer) // 다이얼로그 제목
		// 다이얼로그 예 버튼
		.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,	int whichButton) {
				Log.v("list.java", "추가다이얼로그의 예를 선택함");
				// 다이얼로그의 예 버튼이 눌리면 동작
				processadd(pmart_name, pname, pprice, pamount); // 데이터베이스에 물품 추가 문
				priceupdata(); // 물품이 추가되면서 가격이 올라가므로 총액변경함수
				priceUpdata2();
				Log.d("add", Integer.toString(nowallprice));
			}
		})
		// 다이얼로그의 아니오 버튼
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			// 다이얼로그의 아니오 버튼이 눌리면 작동
			public void onClick(DialogInterface dialog,	int whichButton) {
				Log.v("list.java", "추가다이얼로그의 아니오를 선택함");
				// ignore, just dismiss
				// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
			}
		}).show(); // 다이얼로그 마지막구문
	}

	// 제거할건지 물어보는 다이얼로그 // 예를 선택하면 제거함수로 가고 아니오를 선택하면 그냥 다이얼로그만 종료
	private void delete(final int rowId) {
		Log.v("list.java", "제거할건지 물어보는 다이얼로그");
		// 리스트뷰가 나타내는 리스트의 position이 0 보다 클때만 작동
		if (rowId >= 0) {
			new AlertDialog.Builder(this) // 다이얼로그 생성문
			.setTitle(R.string.delete_singer) // 다이얼로그 제목
			// 다이얼로그 예 버튼
			.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
				// 다이얼로그의 예 버튼이 눌리면 동작
				public void onClick(DialogInterface dialog,	int whichButton) {
					Log.v("list.java", "제거다이얼로그의 예를 선택함");
					processDelete(rowId); // 전달받은 리스트뷰의 위치를  processDelete 함수에 전달
					priceupdata(); // 물품이 삭제되며 이 때 총액변경용
					priceUpdata2();
				}
			})
			// 다이얼로그의 아니오 버튼
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,	int whichButton) {
					Log.v("list.java", "제거다이얼로그의 아니오를 선택함");
					// ignore, just dismiss
					// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
				}
			}).show(); // 다이얼로그 마지막구문
		}
	}

	// nfc찍었을때 해당 nfc에 들어있는 정보중 이름과 가격과 수량을 표시해주는 구문
	private void info(final String infoname) {

		Log.v("list.java", "NFC찍었을때 정보를 주는 info함수시작");
		Cursor cs = handler.selectinfo(infoname); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장 select(1)
		cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용 // 안쓰면 커서위치는 -1
		values = cs.getInt(4); // 검색한 커서가 위치한 곳의 수량정보 저장
		// 다이얼로그에 출력할 메세지 다이얼로그에서 예 를 선택하면 수량이 증가하게 되고 어느 위치의 리스트가 증가하게 되는지 나타내는 위치정보를 찾기위한 부분
		String str_info = " 이미 " + values + " 개 담으셨습니다. \r\n\r\n 추가하여 담으시겠습니까?"; 
		for (int i = 0; i < length; i++) {
			// 넘어온 정보의 이름과 장보기 리스트의 정보가 일치하게되면 그게 수량을 변경시키고 싶은 위치이므로 그때의 i값을  pos에 저장 후 for문 탈출
			if (infoname.equals(alist.get(i).getName())) {
				pos = i;
				priceUpdata2();
				break;
			}
		}

		// 다이얼로그 출력부분
		new AlertDialog.Builder(this) // 다이얼로그 생성문
		.setTitle(infoname) // 다이얼로그 제목
		.setMessage(str_info) // 다이얼로그 내부 출력문자
		// 다이얼로그 예 버튼
		.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
			// 다이얼로그 예 버튼 선택시 작동
			public void onClick(DialogInterface dialog,	int whichButton) {
				Log.v("list.java", "info다이얼로그의 예를 선택함");
				// values=현재값, downrange=수량변경최소값(0), uprange=수량변경최대값(20)
				if (values >= downrange && values < uprange) {

					values++; // 현재의 values값 1증가
					processUpdata(infoname, values, pos); // 데이터베이스 업데이트를 위해 쓰는 함수(이름,가격,수량순으로 변수배치)
					priceUpdata2();
					list.setSelection(pos - 1); // 수량변경하고 싶은 리스트근처로 이동하는 부분  nfc찍고나면 가장 위로 이동하기 때문에 사용							
				} else if (values > uprange) {
					values = downrange;
					processUpdata(infoname, values, pos);
					// 수량변경하고 싶은 리스트근처로 이동하는 부분, nfc찍고나면 가장 위로 이동하기 때문에 사용
					list.setSelection(pos - 1);}
				priceupdata(); // 총액 변경 함수
				priceUpdata2();
			}
		})
		// 아니오 버튼
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,	int whichButton) {
				Log.v("list.java", "info다이얼로그의 아니오를 클릭함");
				processUpdata(infoname, values, pos); // 데이터베이스 업데이트를 위해 쓰는 함수(이름,가격,수량순으로 변수배치)
				priceUpdata2();
				// ignore, just dismiss
			}
		}).setCancelable(true) // 백키 눌러도 다이얼로그 종료가능 // 아니오 버튼과
		// 동일
		.setIcon(R.drawable.ic_launcher) // 다이얼로그 내부 그림
		.show(); // 실행구문 // 없으면 실행 안됨
	}

	//끝나자 마자 나오는데 조금 시간 조정이 가능한지....
	private void exit(){

		Log.v("list.java", "NFC를 이용하여 모든 물품을 찍었을때 나오는 exit다이얼로그");
		c = handler.select(1);
		cur = handler.select(4);

		// mart4테이블에 아무의미없는거 들어가 있음.. 사용이유는 안쓰면 cur추가가 잘 안돼서
		if(c.getCount() == (cur.getCount()-1)){
			new AlertDialog.Builder(this) // 다이얼로그 생성문
			.setTitle(R.string.delete_singer) // 다이얼로그 제목
			// 다이얼로그 예 버튼
			.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
				// 다이얼로그의 예 버튼이 눌리면 동작
				public void onClick(DialogInterface dialog,	int whichButton) {
					Log.v("list.java", "exit다이얼로그의 예를 선택함");
					//finish();
				}
			})
			// 다이얼로그의 아니오 버튼
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Log.v("list.java", "exit다이얼로그의 아니오를 선택함");
					// ignore, just dismiss
					// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
				}
			}).show(); // 다이얼로그 마지막구문
		}
	}

	// main에서 선택하지 않았던 물품 nfc 찍으면 추가되는 추가함수
	private void processadd(String pmart_name, String pname, int pprice, int pamount) {

		Log.v("list.java", "추가다이얼로그에서 예를 선택하면 넘어와서 실제로 추가시키는 함수시작");
		Cursor cs = handler.selecteach(pname); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장
		cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용, 안쓰면 커서위치는 -1
		double x = cs.getDouble(5); // 검색한 커서가 위치한 곳의 수량정보 저장
		double y = cs.getDouble(6);
		Cursor cs2 = handler.selecteach_martname(pmart_name);
		cs2.moveToFirst();
		double lat = cs2.getDouble(8);
		double lon = cs2.getDouble(9);

		if(selectcpzm==0){
			handler.insert(pmart_name, pname, pprice, pamount, x, y, 1, lat, lon, 1); // main에서 넘어온 물품이 있는 데이터베이스에 정보저장
		} else {
			handler.insert(pmart_name, pname, pprice, pamount, x, y, 0, lat, lon, 1); // main에서 넘어온 물품이 있는 데이터베이스에 정보저장
		}
		// 추가되는 이름, 가격 ,수량을 어레이리스트에 추가하는 부분
		alist.add(new cdata(getApplicationContext(), pname, pprice, pamount)); 

		if(selectcpzm==0){
			// 추가되면 가장 아래에 리스트가 생성되고 추가되는 리스트 따라가는 부분
			list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); 
			adapter.notifyDataSetChanged(); // 리스트 내용이 변경됬다고 알려주는 부분
		}

		itemlist_x.add(x);
		itemlist_y.add(y);
		itemlist.add(pname);
		if(selectcpzm==0){
			itemstate.add(1);
		} else {
			itemstate.add(0);
		}

		Log.v("list.java", "여기서 에러인가??9");

		m_tips_view.invalidate(); // 지도내용이 변경되었으므로 갱신

		if(selectcpzm==0){
			save2();
		} else {
			selectcpzm = 0;
		}

		Log.v("list.java", "추가다이얼로그에서 예를 선택하면 넘어와서 실제로 추가시키는 함수끝");
	}

	// 3초이상 누르거나 수량이 1일때 -버튼 눌렀을때 사용되는 제거함수
	private void processDelete(int rowId) {

		Log.v("list.java", "제거다이얼로그에서 예를 선택하면 넘어와서 실제로 제거시키는 함수시작");
		c = handler.select(1); // 삭제된 정보를 table1에 저장한후 커서를 변경된 것으로 초기화
		c.moveToPosition(rowId); // 지우고자 하는 리스트의 위치를 받아서 커서를 원하는 위치로 이동시기큰 구문
		String[] args = { c.getString(2).toString() };
		//dbhandler에 있는 delete사용법이 이름배열을 변수로 받아 검색하여 제거하기 때문에 받아들을 위치값을 String형으로 변경
		handler.deletetemp(args); // mart4의 내용삭제

		handler.delete(args); // String배열 변경한 위치값을 delete함수로 이동, mart의 내용삭제
		adapter.notifyDataSetChanged(); // 정보가 변경되었다는 것을 알려주는 구문
		alist.remove(rowId);

		for(int i=0; i<alist2.size(); i++){
			if(c.getString(2).toString().equals(alist2.get(i).getName())){
				alist2.remove(i);
			} else{
				Log.d("delete", "실행중???");
			}
		}

		itemlist.remove(rowId);
		itemlist_x.remove(rowId);
		itemlist_y.remove(rowId);
		itemstate.remove(rowId);

		m_tips_view.invalidate(); // 지도 내용이 변경되었으므로 갱신
		Log.v("list.java", "제거다이얼로그에서 예를 선택하면 넘어와서 실제로 제거시키는 함수끝");
	}

	// +,- 버튼 눌렀을 때 수량 변경용 함수
	private void processUpdata(String name, int values, int position) {

		Log.v("list.java", "수량증가감소하는 함수시작");
		handler.update(name, values, 1); // 이름과 수량값을 update함수에 전달
		c = handler.select(1); // 수량이 변경된 정보를 커서에 알려주고 초기화
		handler.update(name, values, 2); // 이름과 수량값을 update함수에 전달
		cur = handler.select(4); // 수량이 변경된 정보를 커서에 알려주고 초기화
		c.moveToPosition(position); // 받아온 위치값을 이용하여 커서를 원하는 위치로 이동시킴
		// 데이터가 변경되었으므로 리스트도 바꿔줘야하는데 어레이리스트의 값변경함수를 사용하기 위해서 만든 cdate 객체
		cdata data = new cdata(getApplicationContext(), c.getString(2).toString(), c.getInt(3), c.getInt(4));
		alist.set(position, data); // 변경된 정보를 변경하려는 위치에 변경하는 구문
		adapter.notifyDataSetChanged(); // 정보가 변경되었다는 것을 알려주는 구문
		Log.v("list.java", "수량증가감소하는 함수끝");
	}

	// 지도에 nfc가 찍혔다는 표시를 위해 네모의 색이 바뀌는데 그때 상태를 이용하므로 그 상태를 변환시키는 함수
	private void processUpdataState(String name, int state, int position) {

		Log.v("list.java", "매장내부지도의 표시가 NFC가 찍혔을때 바뀌는데 그때를 나타내는 함수시작");
		switch(state){
		case 0:
			handler.updatestate(name, 1);
			c = handler.select(1);
			itemstate.set(position, 1);
			break;
		case 1:
			break;
		}
		Log.v("list.java", "매장내부지도의 표시가 NFC가 찍혔을때 바뀌는데 그때를 나타내는 함수끝");
	}

	// 총액 변경용 함수
	private void priceupdata() {

		Log.v("list.java", "총액변경함수 시작");
		c = handler.select(1);
		// processUpdata함수에서 바로 넘어오는 함수이므로 커서는 수량을 변경하고자 하는 데이터베이스를 가리키고 있으며 그 커서를 맨 위로 이동시킴
		c.moveToFirst(); 

		// tabel1에 저장된 정보를 맨 처음부터 읽어서 table1 의 가격과 수랑을 가져와서 서로 곱하여 총액을 나타냄
		for (int i = 0; i < c.getCount(); i++) {

			allprice_cal = c.getInt(3) * c.getInt(4); // 커서가 가리키는 열의 가격과 수량을 곱하여  price2에 저장
			c.moveToNext(); // 커서를 다음열로 이동

			// Allprice는 총액을 나타냄
			allprice = allprice + allprice_cal; // Allprice 와 price를 더한 것을 Allprice에 저장
		}
		AllPrice.setText("" + allprice); // text3에 출력
		allprice = 0; // 0으로 초기화 안하면 바꾸기 전과 더해져서 값이 over되서 나옴
		allprice_cal = 0; // 0으로 초기화 안하면 바꾸기 전과 더해져서 값이 over되서 나옴

		Log.v("list.java", "총액변경함수 끝");
	}

	// 현재금액 출력용
	private void priceUpdata2() {

		Log.v("list.java", "현재금액변경함수 시작");
		c = handler.select(4);
		c.moveToFirst();

		for (int j = 0; j < c.getCount(); j++) {

			nowallprice_cal = c.getInt(2) * c.getInt(3);
			c.moveToNext();

			nowallprice = nowallprice + nowallprice_cal;
		}

		NowAllPrice.setText("" + nowallprice);
		nowallprice = 0;
		nowallprice_cal = 0;

		Log.v("list.java", "현재금액변경함수 끝");
	}


	private class DataAdapter extends ArrayAdapter<cdata> {

		private LayoutInflater mInflater;

		public DataAdapter(Context context, ArrayList<cdata> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			Log.v("list.java", "getview 시작");
			View view = null;
			if (v == null) {
				view = mInflater.inflate(R.layout.list2, null);
			} else {
				view = v;
			}final TextView text1 = (TextView) view.findViewById(R.id.textView1);

			cdata data = this.getItem(position);


			final TextView text2 = (TextView) view.findViewById(R.id.textView2);
			final TextView text3 = (TextView) view.findViewById(R.id.textView3);
			final TextView text4 = (TextView) view.findViewById(R.id.textView4);
			Button upButton = (Button) view.findViewById(R.id.upButton);
			Button downButton = (Button) view.findViewById(R.id.downButton);
			ImageView image = (ImageView) view.findViewById(R.id.imageView1);

			view.setBackgroundResource(R.drawable.back0);


			text1.setText(data.getName());
			text2.setText("" + data.getPrice() + "원");
			text3.setText("" + data.getAmount());

			upButton.setFocusable(false);
			downButton.setFocusable(false);

			upButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.v("list.java", "getview내의 +버튼 눌렀을때 작동함수");
					String a = text1.getText().toString();

					for (int i = 0; i < alist.size(); i++) {
						if (a.equals(alist.get(i).getName())) {
							values = Integer.parseInt(text3.getText().toString());
							break;
						}
					}
					if (values >= downrange && values < uprange) {
						values++;
						processUpdata(a, values, position);
					} else if (values > uprange) {
						values = downrange;
						processUpdata(a, values, position);

					}
					priceupdata();
					priceUpdata2();
					text3.setText("" + values);
				}

			});

			downButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.v("list.java", "getview내의 -버튼 눌렀을때 작동함수");
					String a = text1.getText().toString();

					for (int i = 0; i < alist.size(); i++) {
						if (a.equals(alist.get(i).getName())) {
							values = Integer.parseInt(text3.getText().toString());
							break;
						}
					}

					if (values > downrange && values <= uprange) {

						if (values == 1) {
							delete(position);
						} else {
							values--;
							processUpdata(a, values, position);
						}

					} else if (values < downrange) {
						values = uprange;
						processUpdata(a, values, position);
					}
					priceupdata();
					priceUpdata2();
					text3.setText(values + "");
				}
			});
			// 색 변경 하는 부분
			// 널대신 "" 대입할 것

			Log.v("list.java", "getview내의 리스트의 사진연결부분");

			if (data.getName().contains("사과")) {
				image.setImageResource(R.drawable.apple);
			} else if (data.getName().contains("배")) {
				if (data.getName().contains("배추")) {
					image.setImageResource(R.drawable.bechoo);
				} else{
					image.setImageResource(R.drawable.pear);
				}
			} else if (data.getName().contains("무")) {
				image.setImageResource(R.drawable.moo);
			} else if (data.getName().contains("양파")) {
				image.setImageResource(R.drawable.onion);
			} else if (data.getName().contains("상추")) {
				image.setImageResource(R.drawable.sangchoo);
			} else if (data.getName().contains("오이")) {
				image.setImageResource(R.drawable.oe);
			} else if (data.getName().contains("호박")) {
				image.setImageResource(R.drawable.pumpkin);
			} else if (data.getName().contains("쇠고기")) {
				image.setImageResource(R.drawable.beef);
			} else if (data.getName().contains("돼지고기")) {
				image.setImageResource(R.drawable.pork);
			} else if (data.getName().contains("닭고기")) {
				image.setImageResource(R.drawable.chicken);
			} else if (data.getName().contains("달걀")) {
				image.setImageResource(R.drawable.egg);
			} else if (data.getName().contains("조기")) {
				image.setImageResource(R.drawable.jogi);
			} else if (data.getName().contains("명태")) {
				image.setImageResource(R.drawable.myungtae);
			} else if (data.getName().contains("동태")) {
				// 그림 바꿔야 하나???
				image.setImageResource(R.drawable.myungtae);
			} else if (data.getName().contains("오징어")) {
				image.setImageResource(R.drawable.ojing);
			} else if (data.getName().contains("고등어")) {
				image.setImageResource(R.drawable.gofish);
			}

			Log.v("list.java", "getview내의 NFC찍었을때 리스트 각각의 색 변경부분");


			// 색변경하는 코드가 문제있음
			// 찍힌게 있으면 PASS 없으면 색변경시키는 걸로 변경해야함

			for (int j = 0; j < alist2.size(); j++) {
				if (data != null) {
					String dlatl;
					if (c.getCount() == 0) {
						dlatl = intentname;
					} else if (isNull(alist2.get(j).getName())) {
						dlatl = "";
					} else {
						dlatl = alist2.get(j).getName();
					}

					if (dlatl.equals(data.getName())) {
						view.setBackgroundColor(Color.CYAN);
						notifyDataSetChanged();
					} else if (isNull(dlatl)) {
						break;
					} else {
					}
				}
			}

			int oneprice = data.getPrice() * data.getAmount();
			text4.setText("" + oneprice + "원");
			oneprice = 0;

			Log.v("list.java", "getview 종료");
			return view;
		}
	}

	// 리스트의 목록중 하나를 롱클릭했을 시 삭제다이얼로그가 뜨게하는 부분
	private class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			delete(position);
			return false;
		}
	}

	// 문자열이 비어있는지 알려주는 함수
	public boolean isNull(String value) {
		if (value == null || value.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	// 단말기 메뉴, 홈, 뒤로가기 버튼 이벤트 관리부분
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BACK:
				{
					Intent intent = new Intent(getApplicationContext(), SelectMenu.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
//					finish();
					// 단말기의 BACK버튼
					return true;
				}
			case KeyEvent.KEYCODE_MENU:
				// 단말기의 메뉴버튼
				if (menu_button_visible_count == 0) {
					tl.setVisibility(View.VISIBLE);
					menu_button_visible_count++;
				} else {
					tl.setVisibility(View.GONE);
					menu_button_visible_count = 0;
				}
				return true;
				/*	case KeyEvent.KEYCODE_HOME:
				// 단말기의 HOME버튼 -> 동작안함
				return true;*/
			}
		}
		return super.dispatchKeyEvent(event);
	}

	// 이 아래부터는 NFC관련 함수

	// 포어그라운드 디스패치 활성화
	@Override
	protected void onResume()
	{
		super.onResume();

		if(mNfcAdapter!=null){
			Log.d(TAG, "onResume: " + getIntent());

			if (getIntent().getAction() != null)
			{
				if (getIntent().getAction().equals(
						NfcAdapter.ACTION_NDEF_DISCOVERED))
				{
					NdefMessage[] msgs = getNdefMessagesFromIntent(getIntent());
					NdefRecord record = msgs[0].getRecords()[0];
					byte[] payload = record.getPayload();
					setTextFieldValues(new String(payload));
				}
			}
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


	@Override
	protected void onNewIntent(Intent intent)
	{
		if(mNfcAdapter!=null){
			Log.d(TAG, "onNewIntent: " + intent);

			if (!mWriteMode)
			{
				// Currently in tag READING mode
				if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
				{
					NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
					String payload = new String(msgs[0].getRecords()[0].getPayload());
					setTextFieldValues(payload);
				} else if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))
				{
					Toast.makeText(this,
							"This NFC tag currently has no inventory NDEF data.",
							Toast.LENGTH_LONG).show();
				}
			} else
			{
				Log.d("list.java", "mWriteMode 입니다");
			}
		}
	}

	// READING MODE METHODS

	NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
		NdefMessage[] msgs = null;
		if(mNfcAdapter!=null){
			// Parse the intent
			String action = intent.getAction();
			if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)
					|| action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
			{
				//EXTRA_NDEF_MESSAGES를 이용하여 데이터를 parcels로 얻어올수있다
				Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
				// 데이터가 비어있지 않다면 parcelable 객체 내부를 순회하면서 새로운 ndefmessage객체를 만들수 있다
				if (rawMsgs != null) {
					msgs = new NdefMessage[rawMsgs.length];
					for (int i = 0; i < rawMsgs.length; i++) {
						msgs[i] = (NdefMessage) rawMsgs[i];
					}
				} else {
					// Unknown tag type
					byte[] empty = new byte[] {};
					NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
					NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
					msgs = new NdefMessage[] { msg };
				}
			} else {
				Log.e(TAG, "Unknown intent.");
				finish();
			}
			return msgs;
		} else {
			return msgs;
		}
	}

	private void setTextFieldValues(String jsonString)
	{
		if(mNfcAdapter!=null){
			JSONObject inventory = null;
			String mart_name2 = "";
			String name2 = "";
			String price2 = "";
			try
			{
				inventory = new JSONObject(jsonString);
				mart_name2 = inventory.getString("mart_name");
				name2 = inventory.getString("name");
				price2 = inventory.getString("ram");
			} catch (JSONException e)
			{
				Log.e(TAG, "Couldn't parse JSON: ", e);
			}

			intentmart_name = mart_name2;
			intentname = name2;
			intentprice = Integer.parseInt(price2);
			m_tips_view.invalidate();
			save();
		}
	}

	// 중복추가 방지용
	public void save(){

		if(intentmart_name.equals(comp_mart_name)){
			Log.d("save", "처음부분");
			if(alist.size()==0){
				Log.d("save", "size=0");
				add(intentmart_name, intentname, intentprice, 1);
			} else{
				for(int i=0; i<alist.size(); i++){
					if(intentname.equals(alist.get(i).getName())){
						Log.d("alist", "같은거 실행됨");
						save2();
						break;
					} else if(i==(alist.size()-1)){
						add(intentmart_name, intentname, intentprice, 1);
						break;
					} else{
						Log.d("if, else if pass", "alist");
					}
				}
			}
		} else {
			Toast.makeText(this, "NFC에 저장된 것과 실제가 다릅니다  \n " +
					"NFC에 저장 되어 있는 마트이름 : " + intentmart_name +
					"\n 선택된 마트이름 : " + comp_mart_name, Toast.LENGTH_SHORT).show();
		}
	}


	//NFC 중복추가 방지용
	public void save2(){
		Log.e("save2", "1");
		//j가 0이면 널값이기 때문에 계속 제대로 돌아가지 않음 // 처음에 널때문에 else if실행되고 
		if(alist2.size()==0){

			Cursor cs = handler.selectinfo(intentname); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장
			cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용 // 안쓰면 커서위치는 -1
			double x = cs.getDouble(5); // 검색한 커서가 위치한 곳의 수량정보 저장
			double y = cs.getDouble(6);
			int state = cs.getInt(7);
			processUpdataState(intentname, state, cpzm2);
			state = 1;
			handler.insert("", intentname, intentprice, 1, x, y, state, 0, 0, 4); // nfc를 처음찍었을때 찍혔다고 데이터베이스에 저장
			alist2.add(new cdata(getApplicationContext(), intentname, intentprice, 1)); 

			priceUpdata2();
			adapter.notifyDataSetChanged();
		} else{
			for (int j = 0; j < alist2.size(); j++) {
				if (intentname.equals(alist2.get(j).getName())) {
					info(intentname);
					// 이미 nfc한번 찍었다면 temp2배열에 이름 저장되어있고 아무것도 안하고 for문 탈출
					break;
				} else if(j==(alist2.size()-1)){
					c = handler.select(1);
					c.moveToFirst();

					for(int k=0; k<c.getCount(); k++) {
						if(intentname.equals(c.getString(2).toString())){
							for(int l=0; l<itemlist.size(); l++){
								if(intentname.equals(itemlist.get(l))){
									cpzm2 = l;
									break;
								}
							}
							Log.e("save2", "5");
							if(cpzm==0) {
								Log.d("cpzm=0일 때", "start");
								Cursor cs = handler.selectinfo(intentname); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장
								cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용 // 안쓰면 커서위치는 -1
								double x = cs.getDouble(5); // 검색한 커서가 위치한 곳의 수량정보 저장
								double y = cs.getDouble(6);
								int nfcamount = cs.getInt(4); // 검색한 커서가 위치한 곳의 수량정보 저장
								int state = cs.getInt(7);
								processUpdataState(intentname, state, cpzm2);
								state = itemstate.get(cpzm2);
								handler.insert("", intentname, intentprice, nfcamount, x, y, state, 0, 0, 4); // nfc를 처음찍었을때 찍혔다고 데이터베이스에 저장
								alist2.add(new cdata(getApplicationContext(), intentname, intentprice, nfcamount)); 
								info_control = 1;
								break;
							} else {
								Log.d("cpzm=0아닐 때", "start");
								Cursor cs = handler.selecteach(intentname); // 넘어온 이름정보를 데이터베이스 검색하여 커서에 저장
								cs.moveToFirst(); // 검색한 커서 사용하기 위해서 사용 // 안쓰면 커서위치는 -1
								double x = cs.getDouble(5); // 검색한 커서가 위치한 곳의 수량정보 저장
								double y = cs.getDouble(6);
								int state = cs.getInt(7);
								processUpdataState(intentname, state, cpzm2);
								state = itemstate.get(cpzm2);

								handler.insert("", intentname, intentprice, 1, x, y, state, 0, 0, 4); // nfc를 처음찍었을때 찍혔다고 데이터베이스에 저장
								alist2.add(new cdata(getApplicationContext(), intentname, intentprice, 1)); 
								info_control = 1;
								break;
							}
						} else{
							c.moveToNext();
						}
					}
				} else{
					Log.d("if, else if pass", "alist2");
				}

				if(info_control==1){
					info_control = 0;
					priceUpdata2();
					adapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}
}

class cdata {
	private String name;
	private int price;
	private int amount;

	public cdata(Context context, String pname, int pprice, int pamount) {
		name = pname;
		price = pprice;
		amount = pamount;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public int getAmount() {
		return amount;
	}
}