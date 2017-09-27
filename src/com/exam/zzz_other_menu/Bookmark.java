package com.exam.zzz_other_menu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ActionBar;

import android.content.Intent;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.IntentFilter;
import android.content.Context;

import android.database.Cursor;

import android.nfc.NfcAdapter;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.example.n_mart.R;

public class Bookmark extends Activity {

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 및 nfc설정이 체크되어 있는지 점검 가능 및 nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	ListView list;
	ArrayList<button_Data> alist;

	DataAdapter adapter;

	MySQLiteHandler handler;
	Cursor c;

	Button btn;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		ActionBar actionbar = getActionBar();
		actionbar.hide();

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


		handler = MySQLiteHandler.open(getApplicationContext());

		alist = new ArrayList<button_Data>();

		c = handler.select(7);
		c.moveToFirst();

		for(int i=0; i<c.getCount(); i++){
			if(c.getInt(2)==1){
				alist.add(new button_Data(c.getString(1).toString(), c.getInt(2)));
			}
			c.moveToNext();
		}

		adapter = new DataAdapter(this, alist);

		list = (ListView) findViewById(R.id.l1);
		list.setAdapter(adapter);

		handler.close();

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), Main.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
				intent.putExtra("select_mart_name", adapter.getItem(position).getBook_mart());
				startActivity(intent); // 인텐트 시작
				finish();
			}
		});
	}

	private class DataAdapter extends ArrayAdapter<button_Data> {

		private LayoutInflater mInflater;

		public DataAdapter(Context context, ArrayList<button_Data> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			View view = null;
			if (v == null) {
				view = mInflater.inflate(R.layout.book_child, null);
			} else {
				view = v;
			}

			final button_Data BD = this.getItem(position);

			TextView Book_martname = (TextView) view.findViewById(R.id.Book_mart_childname);
			TextView Book_smallname = (TextView) view.findViewById(R.id.Book_smallname);
			ImageView Book_martpicture = (ImageView) view.findViewById(R.id.Book_martpic);

			btn = (Button) view.findViewById(R.id.Select_mart_button2);
			btn.setFocusable(false);

			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					c = handler.selectbookmarker(BD.getBook_mart());
					c.moveToFirst();

					if(c.getInt(2)==0){
						handler.updatebookmarker(BD.getBook_mart(), 1);
						BD.setBook_num(1);
						notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
					} else {
						handler.updatebookmarker(BD.getBook_mart(), 0);
						BD.setBook_num(0);
						notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), "즐겨찾기에서 해체되었습니다.", Toast.LENGTH_SHORT).show();
					}
				}
			});

			if(BD.getBook_num()==0){
				btn.setBackgroundResource(R.drawable.book_no);
				notifyDataSetChanged();
			} else {
				btn.setBackgroundResource(R.drawable.book_yes);
				notifyDataSetChanged();
			}

			c = handler.select(7);
			c.moveToFirst();

			for(int i=0;i<c.getCount();i++){

				if(BD.getBook_mart().equals("롯데마트 강변점")){
					Book_martpicture.setBackgroundResource(R.drawable.l_gangbyun);
					Book_martname.setText("롯데마트");
					Book_smallname.setText("강변점");
				}else if(BD.getBook_mart().equals("롯데마트 금천점")){
					Book_martpicture.setBackgroundResource(R.drawable.l_keumchon);
					Book_martname.setText("롯데마트");
					Book_smallname.setText("금천점");
				}else if(BD.getBook_mart().equals("롯데마트 서울역점")){
					Book_martpicture.setBackgroundResource(R.drawable.l_seoulstation);
					Book_martname.setText("롯데마트");
					Book_smallname.setText("서울역점");
				}else if(BD.getBook_mart().equals("이마트 가양점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_gayang);
					Book_martname.setText("이마트");
					Book_smallname.setText("가양점");
				}else if(BD.getBook_mart().equals("이마트 명일점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_myungil);
					Book_martname.setText("이마트");
					Book_smallname.setText("명일점");
				}else if(BD.getBook_mart().equals("이마트 목동점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_mokdong);
					Book_martname.setText("이마트");
					Book_smallname.setText("목동점");
				}else if(BD.getBook_mart().equals("이마트 미아점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_mia);
					Book_martname.setText("이마트");
					Book_smallname.setText("미아점");
				}else if(BD.getBook_mart().equals("이마트 상봉점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_sangbong);
					Book_martname.setText("이마트");
					Book_smallname.setText("상봉점");
				}else if(BD.getBook_mart().equals("이마트 성수점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_sungsoo);
					Book_martname.setText("이마트");
					Book_smallname.setText("성수점");
				}else if(BD.getBook_mart().equals("이마트 신도림점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_sindorim);
					Book_martname.setText("이마트");
					Book_smallname.setText("신도림점");
				}else if(BD.getBook_mart().equals("이마트 여의도점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_yuido);
					Book_martname.setText("이마트");
					Book_smallname.setText("여의도점");
				}else if(BD.getBook_mart().equals("이마트 왕십리점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_wangsiri);
					Book_martname.setText("이마트");
					Book_smallname.setText("왕십리점");
				}else if(BD.getBook_mart().equals("이마트 용산점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_yongsan);
					Book_martname.setText("이마트");
					Book_smallname.setText("용산점");
				}else if(BD.getBook_mart().equals("이마트 은평점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_eunpyung);
					Book_martname.setText("이마트");
					Book_smallname.setText("은평점");
				}else if(BD.getBook_mart().equals("이마트 자양점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_jayang);
					Book_martname.setText("이마트");
					Book_smallname.setText("자양점");
				}else if(BD.getBook_mart().equals("이마트 창동점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_changdong);
					Book_martname.setText("이마트");
					Book_smallname.setText("창동점");
				}else if(BD.getBook_mart().equals("이마트 청계점")){
					Book_martpicture.setBackgroundResource(R.drawable.e_changdong);
					Book_martname.setText("이마트");
					Book_smallname.setText("청계점");
				}else if(BD.getBook_mart().equals("홈플러스 강동점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_gangdong);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("강동점");
				}else if(BD.getBook_mart().equals("홈플러스 동대문점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_dongdaemoon);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("동대문점");
				}else if(BD.getBook_mart().equals("홈플러스 면목점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_myunmok);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("면목점");
				}else if(BD.getBook_mart().equals("홈플러스 방학점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_banghak);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("방학점");
				}else if(BD.getBook_mart().equals("홈플러스 시흥점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_siheung);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("시흥점");
				}else if(BD.getBook_mart().equals("홈플러스 영등포점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_youngdeungpo);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("영등포점");
				}else if(BD.getBook_mart().equals("홈플러스 잠실점")){
					Book_martpicture.setBackgroundResource(R.drawable.h_jamsil);
					Book_martname.setText("홈플러스");
					Book_smallname.setText("잠실점");
				}else if(BD.getBook_mart().equals("하나로마트 목동점")){
					Book_martpicture.setBackgroundResource(R.drawable.n_mokdong);
					Book_martname.setText("하나로마트");
					Book_smallname.setText("목동점");
				}else if(BD.getBook_mart().equals("하나로마트 미아점")){
					Book_martpicture.setBackgroundResource(R.drawable.n_mia);
					Book_martname.setText("하나로마트");
					Book_smallname.setText("미아점");
				}else if(BD.getBook_mart().equals("하나로마트 양재점")){
					Book_martpicture.setBackgroundResource(R.drawable.n_yangjae);
					Book_martname.setText("하나로마트");
					Book_smallname.setText("양재점");
				}else if(BD.getBook_mart().equals("하나로마트 용산점")){
					Book_martpicture.setBackgroundResource(R.drawable.n_yongsan);
					Book_martname.setText("하나로마트");
					Book_smallname.setText("용산점");
				}else if(BD.getBook_mart().equals("신세계백화점 강남점")){
					Book_martpicture.setBackgroundResource(R.drawable.s_gangnam);
					Book_martname.setText("신세계백화점");
					Book_smallname.setText("강남점");
				}else if(BD.getBook_mart().equals("신세계백화점 본점")){
					Book_martpicture.setBackgroundResource(R.drawable.s_bon);
					Book_martname.setText("신세계백화점");
					Book_smallname.setText("본점");
				}else if(BD.getBook_mart().equals("현대백화점 미아점")){
					Book_martpicture.setBackgroundResource(R.drawable.hd_mia);
					Book_martname.setText("현대백화점");
					Book_smallname.setText("미아점");
				}else if(BD.getBook_mart().equals("현대백화점 신촌점")){
					Book_martpicture.setBackgroundResource(R.drawable.hd_sinchon);
					Book_martname.setText("현대백화점");
					Book_smallname.setText("신촌점");
				}else if(BD.getBook_mart().equals("롯데백화점 강남점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_gangnam);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("강남점");
				}else if(BD.getBook_mart().equals("롯데백화점 관악점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_khwanak);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("관악점");
				}else if(BD.getBook_mart().equals("롯데백화점 노원점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_nowon);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("노원점");
				}else if(BD.getBook_mart().equals("롯데백화점 미아점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_mia);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("미아점");
				}else if(BD.getBook_mart().equals("롯데백화점 본점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_bon);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("본점");
				}else if(BD.getBook_mart().equals("롯데백화점 영등포점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_youngdeungpo);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("영등포점");
				}else if(BD.getBook_mart().equals("롯데백화점 잠실점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_jamsil);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("잠실점");
				}else if(BD.getBook_mart().equals("롯데백화점 청량리점")){
					Book_martpicture.setBackgroundResource(R.drawable.ld_chungryangri);
					Book_martname.setText("롯데백화점");
					Book_smallname.setText("청량리점");
					Log.d("1","청량리가찍혓다");
				}
				c.moveToNext();
			}
			handler.close();
			return view;
		}
	}

	// 이 아래부터는 NFC관련 함수

	// 포어그라운드 디스패치 활성화
	@Override
	protected void onResume()
	{
		super.onResume();

		if(mNfcAdapter!=null){
			Log.d(TAG, "onResume: " + getIntent());

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

class button_Data {
	private String Book_mart_name;
	private int Book_mart_num;


	public button_Data(String Book_mart_name, int Book_mart_num){
		this.Book_mart_name=Book_mart_name;
		this.Book_mart_num=Book_mart_num;
	}

	public void setBook_mart(String Book_mart_name){
		this.Book_mart_name=Book_mart_name;
	}

	public void setBook_num(int Book_mart_num){
		this.Book_mart_num=Book_mart_num;
	}

	public String getBook_mart(){
		return Book_mart_name;
	}

	public int getBook_num(){
		return Book_mart_num;
	}

}