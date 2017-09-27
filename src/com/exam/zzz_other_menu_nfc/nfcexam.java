package com.exam.zzz_other_menu_nfc;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.exam.zzz_other_menu.*;
import com.exam.zzz_other_menu_mysql.*;
import com.example.n_mart.*;
import com.nhn.android.mapviewer.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.*;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.*;


public class nfcexam extends Activity {
	
	private static final String TAG = "nfcinventory_simple";

	MySQLiteHandler handler; // 데이터베이스 사용위한 handler
	Cursor c; // 데이터베이스 사용위한 커서
	int each_or_all = 0; // nfc입력시에 하나씩 할지 전부다 할지 정하기 위해 사용하는 변수
	int db_check = 0; // updata_all 사용시 한번만 검사하게 하기 위해 사용하는 변수
	int c_getcount = 0; // table5의 총 갯수 저장하여 updata_all 정지시키기 위해 사용하는 변수

	// NFC-related variables
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	private boolean mWriteMode = false;

	// UI elements
	EditText mRAM;
	Button mWriter_all;
	AlertDialog mWriteTagDialog;

	Spinner spin_MartName;
	Spinner spin_Name;
	Spinner spin_Price;

	ArrayList<String> spinnerlist_MartName;

	ArrayList<String> Lotte_seoul;
	ArrayList<String> Lotte_gangbyeon;
	ArrayList<String> Lotte_geumcheon;
	ArrayList<String> Home_jamsil;
	ArrayList<String> Home_gangdong;
	ArrayList<String> Home_banghak;
	ArrayList<String> Home_dongdaemun;
	ArrayList<String> Home_myenmok;
	ArrayList<String> Home_yongdungpo;
	ArrayList<String> Home_syhung;
	ArrayList<String> E_seongsu;
	ArrayList<String> E_eunpyeong;
	ArrayList<String> E_mokdong;
	ArrayList<String> E_yougsan;
	ArrayList<String> E_chunggye;
	ArrayList<String> E_myeongil;
	ArrayList<String> E_sindorim;
	ArrayList<String> E_changdong;
	ArrayList<String> E_jayang;
	ArrayList<String> E_sangbong;
	ArrayList<String> E_mia;
	ArrayList<String> E_gayang;
	ArrayList<String> E_yeouido;
	ArrayList<String> E_wangsimni;
	
	ArrayList<String> Lotte_seoul_Price;
	ArrayList<String> Lotte_gangbyeon_Price;
	ArrayList<String> Lotte_geumcheon_Price;
	ArrayList<String> Home_jamsil_Price;
	ArrayList<String> Home_gangdong_Price;
	ArrayList<String> Home_banghak_Price;
	ArrayList<String> Home_dongdaemun_Price;
	ArrayList<String> Home_myenmok_Price;
	ArrayList<String> Home_yongdungpo_Price;
	ArrayList<String> Home_syhung_Price;
	ArrayList<String> E_seongsu_Price;
	ArrayList<String> E_eunpyeong_Price;
	ArrayList<String> E_mokdong_Price;
	ArrayList<String> E_yougsan_Price;
	ArrayList<String> E_chunggye_Price;
	ArrayList<String> E_myeongil_Price;
	ArrayList<String> E_sindorim_Price;
	ArrayList<String> E_changdong_Price;
	ArrayList<String> E_jayang_Price;
	ArrayList<String> E_sangbong_Price;
	ArrayList<String> E_mia_Price;
	ArrayList<String> E_gayang_Price;
	ArrayList<String> E_yeouido_Price;
	ArrayList<String> E_wangsimni_Price;

	ArrayAdapter<String> spin_MartName_adapter;
	ArrayAdapter<String> spin_Name_adapter;
	ArrayAdapter<String> spin_Price_adapter;

	String MartName_temp = "";
	String Name_temp = "";
	String select_MartName = "";
	String select_Name = "";
	String select_Price = "";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_main);

		handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 핸들러 오픈

		findViewById(R.id.write_tag).setOnClickListener(mTagWriter);
		mRAM = ((EditText) findViewById(R.id.computer_ram));
		mWriter_all = (Button) findViewById(R.id.write_tag_all);
		spin_MartName = (Spinner) findViewById(R.id.spin_MartName);
		spin_Name = (Spinner) findViewById(R.id.spin_Name);
	//	spin_Price = (Spinner) findViewById(R.id.spin_Price);

		spinnerlist_MartName = new ArrayList<String>();

		Lotte_seoul = new ArrayList<String>();
		Lotte_gangbyeon = new ArrayList<String>();
		Lotte_geumcheon = new ArrayList<String>();
		Home_jamsil = new ArrayList<String>();
		Home_gangdong = new ArrayList<String>();
		Home_banghak = new ArrayList<String>();
		Home_dongdaemun = new ArrayList<String>();
		Home_myenmok = new ArrayList<String>();
		Home_yongdungpo = new ArrayList<String>();
		Home_syhung = new ArrayList<String>();
		E_seongsu = new ArrayList<String>();
		E_eunpyeong = new ArrayList<String>();
		E_mokdong = new ArrayList<String>();
		E_yougsan = new ArrayList<String>();
		E_chunggye = new ArrayList<String>();
		E_myeongil = new ArrayList<String>();
		E_sindorim = new ArrayList<String>();
		E_changdong = new ArrayList<String>();
		E_jayang = new ArrayList<String>();
		E_sangbong = new ArrayList<String>();
		E_mia = new ArrayList<String>();
		E_gayang = new ArrayList<String>();
		E_yeouido = new ArrayList<String>();
		E_wangsimni = new ArrayList<String>();

		
/*		Lotte_seoul_Price = new ArrayList<String>();
		Lotte_gangbyeon_Price = new ArrayList<String>();
		Lotte_geumcheon_Price = new ArrayList<String>();
		Home_jamsil_Price = new ArrayList<String>();
		Home_gangdong_Price = new ArrayList<String>();
		Home_banghak_Price = new ArrayList<String>();
		Home_dongdaemun_Price = new ArrayList<String>();
		Home_myenmok_Price = new ArrayList<String>();
		Home_yongdungpo_Price = new ArrayList<String>();
		Home_syhung_Price = new ArrayList<String>();
		E_seongsu_Price = new ArrayList<String>();
		E_eunpyeong_Price = new ArrayList<String>();
		E_mokdong_Price = new ArrayList<String>();
		E_yougsan_Price = new ArrayList<String>();
		E_chunggye_Price = new ArrayList<String>();
		E_myeongil_Price = new ArrayList<String>();
		E_sindorim_Price = new ArrayList<String>();
		E_changdong_Price = new ArrayList<String>();
		E_jayang_Price = new ArrayList<String>();
		E_sangbong_Price = new ArrayList<String>();
		E_mia_Price = new ArrayList<String>();
		E_gayang_Price = new ArrayList<String>();
		E_yeouido_Price = new ArrayList<String>();
		E_wangsimni_Price = new ArrayList<String>();*/

		c = handler.select(5);
		c.moveToFirst();

		for(int i=0; i<c.getCount(); i++){
			if(MartName_temp.equals(c.getString(1).toString())){
				Log.d("nfcexam.java", "같으므로 pass");
			} else {
				spinnerlist_MartName.add(c.getString(1).toString());
				MartName_temp = c.getString(1).toString();
			}

			if(c.getString(1).toString().equals("롯데마트 서울역점")){
				Lotte_seoul.add(c.getString(2).toString());
			//	Lotte_seoul_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("롯데마트 강변점")){
				Lotte_gangbyeon.add(c.getString(2).toString());
			//	Lotte_gangbyeon_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("롯데마트 금천점")){
				Lotte_geumcheon.add(c.getString(2).toString());
			//	Lotte_geumcheon_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 잠실점")){
				Home_jamsil.add(c.getString(2).toString());
			//	Home_jamsil_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 강동점")){
				Home_gangdong.add(c.getString(2).toString());
			//	Home_gangdong_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 방학점")){
				Home_banghak.add(c.getString(2).toString());
			//	Home_banghak_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 동대문점")){
				Home_dongdaemun.add(c.getString(2).toString());
			//	Home_dongdaemun_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 면목점")){
				Home_myenmok.add(c.getString(2).toString());
			//	Home_myenmok_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 영등포점")){
				Home_yongdungpo.add(c.getString(2).toString());
			//	Home_yongdungpo_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("홈플러스 시흥점")){
				Home_syhung.add(c.getString(2).toString());
			//	Home_syhung_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 성수점")){
				E_seongsu.add(c.getString(2).toString());
			//	E_seongsu_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 은평점")){
				E_eunpyeong.add(c.getString(2).toString());
			//	E_eunpyeong_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 목동점")){
				E_mokdong.add(c.getString(2).toString());
			//	E_mokdong_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 용산점")){
				E_yougsan.add(c.getString(2).toString());
			//	E_yougsan_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 청계점")){
				E_chunggye.add(c.getString(2).toString());
			//	E_chunggye_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 명일점")){
				E_myeongil.add(c.getString(2).toString());
			//	E_myeongil_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 신도림점")){
				E_sindorim.add(c.getString(2).toString());
			//	E_sindorim_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 창동점")){
				E_changdong.add(c.getString(2).toString());
			//	E_changdong_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 자양점")){
				E_jayang.add(c.getString(2).toString());
			//	E_jayang_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 상봉점")){
				E_sangbong.add(c.getString(2).toString());
			//	E_sangbong_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 미아점")){
				E_mia.add(c.getString(2).toString());
			//	E_mia_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 가양점")){
				E_gayang.add(c.getString(2).toString());
			//	E_gayang_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 여의도점")){
				E_yeouido.add(c.getString(2).toString());
			//	E_yeouido_Price.add(Integer.toString(c.getInt(3)));
			} else if(c.getString(1).toString().equals("이마트 왕십리점")){
				E_wangsimni.add(c.getString(2).toString());
			//	E_wangsimni_Price.add(Integer.toString(c.getInt(3)));
			}else {
				Log.d("nfcexam.java", "일치하는 이름이 없습니다");
			}

			c.moveToNext();
		}

		// 캐쉬된 NfcAdapter 인스턴스를 가져온다
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null)
		{
			Toast.makeText(this, "Your device does not support NFC. Cannot run demo.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// check if NFC is enabled
		checkNfcEnabled();

		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try
		{
			ndefDetected.addDataType("application/root.gast.playground.nfc");
		} catch (MalformedMimeTypeException e)
		{
			throw new RuntimeException("Could not add MIME type.", e);
		}

		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mReadTagFilters = new IntentFilter[] { ndefDetected, tagDetected };

		handler.close();

		spin_MartName_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerlist_MartName);
		spin_MartName_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_MartName.setAdapter(spin_MartName_adapter);

		spin_MartName.setOnItemSelectedListener(new MyOnItemSelectedListener_MartName());
	}

	public class MyOnItemSelectedListener_MartName implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
			Toast.makeText(getApplicationContext(), "선택한 것은 " + parent.getItemAtPosition(position),
					Toast.LENGTH_SHORT).show();
			
			select_MartName = (String) parent.getItemAtPosition(position);

			switch(position){
			case 0:
				NameSpinner(Lotte_seoul);
				break;
			case 1:
				NameSpinner(Lotte_gangbyeon);
				break;
			case 2:
				NameSpinner(Lotte_geumcheon);
				break;
			case 3:
				NameSpinner(Home_jamsil);
				break;
			case 4:
				NameSpinner(Home_gangdong);
				break;
			case 5:
				NameSpinner(Home_banghak);
				break;
			case 6:
				NameSpinner(Home_dongdaemun);
				break;
			case 7:
				NameSpinner(Home_myenmok);
				break;
			case 8:
				NameSpinner(Home_yongdungpo);
				break;
			case 9:
				NameSpinner(Home_syhung);
				break;
			case 10:
				NameSpinner(E_seongsu);
				break;
			case 11:
				NameSpinner(E_eunpyeong);
				break;
			case 12:
				NameSpinner(E_mokdong);
				break;
			case 13:
				NameSpinner(E_yougsan);
				break;
			case 14:
				NameSpinner(E_chunggye);
				break;
			case 15:
				NameSpinner(E_myeongil);
				break;
			case 16:
				NameSpinner(E_sindorim);
				break;
			case 17:
				NameSpinner(E_changdong);
				break;
			case 18:
				NameSpinner(E_jayang);
				break;
			case 19:
				NameSpinner(E_sangbong);
				break;
			case 20:
				NameSpinner(E_mia);
				break;
			case 21:
				NameSpinner(E_gayang);
				break;
			case 22:
				NameSpinner(E_yeouido);
				break;
			case 23:
				NameSpinner(E_wangsimni);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?>arg0){
			//Do nothing
		}
	}

	private void NameSpinner(ArrayList<String> alist){
		spin_Name_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, alist);
		spin_Name_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_Name.setAdapter(spin_Name_adapter);
		
		spin_Name.setOnItemSelectedListener(new MyOnItemSelectedListener_Name());
	}
	
	public class MyOnItemSelectedListener_Name implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
			Toast.makeText(getApplicationContext(), "선택한 것은 " + parent.getItemAtPosition(position),
					Toast.LENGTH_SHORT).show();
			
			select_Name = (String) parent.getItemAtPosition(position);
	/*		
			switch(position){
			case 0:
				PriceSpinner(Lotte_seoul_Price);
				break;
			case 1:
				PriceSpinner(Lotte_gangbyeon_Price);
				break;
			case 2:
				PriceSpinner(Lotte_geumcheon_Price);
				break;
			case 3:
				PriceSpinner(Home_jamsil_Price);
				break;
			case 4:
				PriceSpinner(Home_gangdong_Price);
				break;
			case 5:
				PriceSpinner(Home_banghak_Price);
				break;
			case 6:
				PriceSpinner(Home_dongdaemun_Price);
				break;
			case 7:
				PriceSpinner(Home_myenmok_Price);
				break;
			case 8:
				PriceSpinner(Home_yongdungpo_Price);
				break;
			case 9:
				PriceSpinner(Home_syhung_Price);
				break;
			case 10:
				PriceSpinner(E_seongsu_Price);
				break;
			case 11:
				PriceSpinner(E_eunpyeong_Price);
				break;
			case 12:
				PriceSpinner(E_mokdong_Price);
				break;
			case 13:
				PriceSpinner(E_yougsan_Price);
				break;
			case 14:
				PriceSpinner(E_chunggye_Price);
				break;
			case 15:
				PriceSpinner(E_myeongil_Price);
				break;
			case 16:
				PriceSpinner(E_sindorim_Price);
				break;
			case 17:
				PriceSpinner(E_changdong_Price);
				break;
			case 18:
				PriceSpinner(E_jayang_Price);
				break;
			case 19:
				PriceSpinner(E_sangbong_Price);
				break;
			case 20:
				PriceSpinner(E_mia_Price);
				break;
			case 21:
				PriceSpinner(E_gayang_Price);
				break;
			case 22:
				PriceSpinner(E_yeouido_Price);
				break;
			case 23:
				PriceSpinner(E_wangsimni_Price);
				break;
			}*/
		}

		@Override
		public void onNothingSelected(AdapterView<?>arg0){
			//Do nothing
		}
	}
	/*
	private void PriceSpinner(ArrayList<String> alist){
		spin_Price_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, alist);
		spin_Price_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_Price.setAdapter(spin_Price_adapter);
		
		spin_Price.setOnItemSelectedListener(new MyOnItemSelectedListener_Price());
	}
	
	public class MyOnItemSelectedListener_Price implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
			Toast.makeText(getApplicationContext(), "선택한 것은 " + parent.getItemAtPosition(position),
					Toast.LENGTH_SHORT).show();
			
			select_Price = (String) parent.getItemAtPosition(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?>arg0){
			//Do nothing
		}
	}*/
	
	// 포어그라운드 디스패치 활성화
	@Override
	protected void onResume()
	{
		Log.d("nfcexam.java", "onResume()작동");
		super.onResume();

		checkNfcEnabled();

		Log.d(TAG, "onResume: " + getIntent());

		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadTagFilters, null);
	}

	// 포어그라운드 디스패치 비활성화
	@Override
	protected void onPause()
	{
		Log.d("nfcexma.java", "onPause()작동");
		super.onPause();
		Log.d(TAG, "onPause: " + getIntent());
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.d(TAG, "onNewIntent: " + intent);
		Log.d("nfcexam.java", "onNewIntent 작동");

		// Currently in tag WRITING mode
		if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))
		{
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			writeTag(createNdefFromJson(), detectedTag);
			mWriteTagDialog.cancel();
		}
	}

	// WRITING MODE METHODS 

	private View.OnClickListener mTagWriter = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			Log.d("nfcexam.java", "updata버튼 클릭");
			enableTagWriteMode();

			AlertDialog.Builder builder = new AlertDialog.Builder(nfcexam.this)
			.setTitle(getString(R.string.ready_to_write))
			.setMessage(getString(R.string.ready_to_write_instructions))
			.setCancelable(true)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				@Override
				public void onCancel(DialogInterface dialog)
				{
					enableTagReadMode();
				}
			});

			each_or_all = 1;

			mWriteTagDialog = builder.create();
			mWriteTagDialog.show();

		}
	};

	// 읽기 기능을 위하여 json객체를 만든 다음 ndef 레코드의 페이로드로 부터 읽어 들인 값을 바이트 배열 형식으로 변환한다.
	private NdefMessage createNdefFromJson()
	{
		Log.d("nfcexam", "createNdefFromJson()작동");
		c = handler.select(5);
		c.moveToFirst();

		// get the values from the form's text fields:

		Editable ramField = mRAM.getText();
		for(int i=0; i<c.getCount(); i++){
			if(c.getString(1).toString().contains(select_MartName)){
				for(int j=0; j<c.getCount(); j++){
					if(c.getString(2).toString().equals(select_Name)){
						mRAM.setText(Integer.toString(c.getInt(3)));
						ramField = mRAM.getText();

						break;
					} else {
						c.moveToNext();
					}
				}
				break;
			}
			else{
				c.moveToNext();
			}
		}

		// create a JSON object out of the values:
		JSONObject computerSpecs = new JSONObject();
		try
		{
			computerSpecs.put("mart_name", select_MartName);
			computerSpecs.put("name", select_Name);
			computerSpecs.put("ram", ramField);
		} catch (JSONException e)
		{
			Log.e(TAG, "Could not create JSON: ", e);
		}

		String mimeType = "application/root.gast.playground.nfc";
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
		String data = computerSpecs.toString();
		byte[] dataBytes = data.getBytes(Charset.forName("UTF-8"));
		byte[] id = new byte[0];
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, dataBytes);
		NdefMessage m = new NdefMessage(new NdefRecord[] { record });

		return m;
	}

	private void enableTagWriteMode()
	{
		mWriteMode = true;
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
	}

	private void enableTagReadMode()
	{
		mWriteMode = false;
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mReadTagFilters, null);
	}

	boolean writeTag(NdefMessage message, Tag tag)
	{
		int size = message.toByteArray().length;

		try
		{
			Ndef ndef = Ndef.get(tag);
			if (ndef != null)
			{
				ndef.connect();

				if (!ndef.isWritable())
				{
					Toast.makeText(this,
							"Cannot write to this tag. This tag is read-only.",
							Toast.LENGTH_LONG).show();
					return false;
				}
				if (ndef.getMaxSize() < size)
				{
					Toast.makeText(
							this,
							"Cannot write to this tag. Message size (" + size
							+ " bytes) exceeds this tag's capacity of "
							+ ndef.getMaxSize() + " bytes.",
							Toast.LENGTH_LONG).show();
					return false;
				}

				ndef.writeNdefMessage(message);
				Toast.makeText(this,
						"A pre-formatted tag was successfully updated.",
						Toast.LENGTH_LONG).show();
				return true;
			} else
			{
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null)
				{
					try
					{
						format.connect();
						format.format(message);
						Toast.makeText(
								this,
								"This tag was successfully formatted and updated.",
								Toast.LENGTH_LONG).show();
						return true;
					} catch (IOException e)
					{
						Toast.makeText(
								this,
								"Cannot write to this tag due to I/O Exception.",
								Toast.LENGTH_LONG).show();
						return false;
					}
				} else
				{
					Toast.makeText(
							this,
							"Cannot write to this tag. This tag does not support NDEF.",
							Toast.LENGTH_LONG).show();
					return false;
				}
			}
		} catch (Exception e)
		{
			Toast.makeText(this,
					"Cannot write to this tag due to an Exception.",
					Toast.LENGTH_LONG).show();
		}

		return false;
	}

	/*
	 * **** HELPER METHODS ****
	 */
	// 만약 nfc가 꺼져있다면 사용자에게 nfc기능을 활성화 하는 방법을 표시하는 부분
	private void checkNfcEnabled()
	{
		Boolean nfcEnabled = mNfcAdapter.isEnabled();
		if (!nfcEnabled) 
		{
			new AlertDialog.Builder(nfcexam.this)
			.setTitle(getString(R.string.warning_nfc_is_off))
			.setMessage(getString(R.string.turn_on_nfc))
			.setCancelable(false)
			.setPositiveButton("Update Settings",
					new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			}).create().show();
		}
	}
}

class spinner_data{
	private String MartName;
	private String Name;
	private String Price;

	public spinner_data(String pMartName, String pName, String pPrice){
		MartName = pMartName;
		Name = pName;
		Price = pPrice;
	}

	public String getMartName(){
		return MartName;
	}

	public String getName(){
		return Name;
	}

	public String getPrice(){
		return Price;
	}
}