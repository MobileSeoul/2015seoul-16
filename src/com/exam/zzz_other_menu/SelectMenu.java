package com.exam.zzz_other_menu;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.exam.zzz_other_menu_nfc.nfcexam;
import com.example.developer_info.develope_activity;
import com.example.n_mart.MainActivity;
import com.example.n_mart.R;

public class SelectMenu extends Activity implements View.OnClickListener{

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	AlertDialog mWriteTagDialog;

	MySQLiteHandler handler; // 데이터베이스 사용위한 handler
	Cursor c;

	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmenu);
		Log.e("SelectMenu.java", "onCreate!!");

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
		} else {

			// check if NFC is enabled
			checkNfcEnabled();

			mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
			try	{
				ndefDetected.addDataType("application/root.gast.playground.nfc");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Could not add MIME type.", e);
			}
		}

		ActionBar actionbar = getActionBar();
		actionbar.hide();



		cManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		Button button1 = (Button) findViewById(R.id.map);
		Button button2 = (Button) findViewById(R.id.listmap);
		Button button3 = (Button) findViewById(R.id.delete);
		Button button4 = (Button) findViewById(R.id.help);
		Button button5 = (Button) findViewById(R.id.info);

		handler = MySQLiteHandler.open(getApplicationContext());

		c = handler.select(5);
		c.moveToFirst();

		if(c.getCount()==0 || ((!mobile.isConnected() && !wifi.isConnected()))){
			Builder dlg = new AlertDialog.Builder(SelectMenu.this);
			dlg.setTitle("선택")
			.setMessage("인터넷 연결이 되어있지 않아 마트정보가 제대로 저장되지 않았거나 \n" +
					"마트정보 업데이트가 제대로 되지 않았습니다." +
					"인터넷 연결 후 다시 실행하여 주세요")
					// 다이얼로그의 아니오 버튼
					.setNegativeButton("확인", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							// ignore, just dismiss
							// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
						}
					}).show(); // 다이얼로그 마지막구문
		} else {
			button1.setOnClickListener(this);
			button2.setOnClickListener(this);
			button3.setOnClickListener(this);
			button4.setOnClickListener(this);
			button5.setOnClickListener(this);
		}
		handler.close();

	}

	public void onClick(View v) {

		switch(v.getId()) {
		case R.id.map:
			Log.e("SelectMenu.java", "지도버튼을 눌렀습니다");
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.listmap:
			Log.e("SelectMenu.java", "매장선택을 눌렀습니다");

			handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 핸들러

			c = handler.select(1);
			c.moveToFirst();

			if(c.getCount()!=0){

				Builder dlg = new AlertDialog.Builder(SelectMenu.this);
				dlg.setTitle("선택")
				.setMessage(c.getString(1).toString() + " 에서 이전에 장 본 물품이 남아있습니다.\n" +
						"다른매장에서 장을 보시려면 예\n" +
						"같은매장에서 장을보시려면 아니오를 클릭하세요")
						.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
							// 다이얼로그의 예 버튼이 눌리면 동작
							@Override
							public void onClick(DialogInterface dialog,	int whichButton) {
								handler.delete_table_1_4();

								Intent intent2 = new Intent(getApplicationContext(), Select_mart.class);
								intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent2);
							}
						})
						// 다이얼로그의 아니오 버튼
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								// ignore, just dismiss
								// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨
								handler.delete_table_1_4();

								Intent intent3 = new Intent(getApplicationContext(), Main.class);
								intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent3.putExtra("select_mart_name", c.getString(1).toString());
								Log.d("셀렉트메뉴에서 마트인텐트", c.getString(1).toString());
								startActivity(intent3);

							}
						}).show(); // 다이얼로그 마지막구문

				handler.close();

			} else{
				Intent intent4 = new Intent(getApplicationContext(), Select_mart.class);
				intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent4);
				break;
			}

			break;
		case R.id.info:
			//Intent intent5 = new Intent(this, develope_activity.class);
			Intent intent5 = new Intent(this, nfcexam.class);
			intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent5);
			break;
		case R.id.help:
			Intent intent6 = new Intent(this, Bookmark.class);
			intent6.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent6);
			break;
		case R.id.delete:
			Log.e("SelectMenu.java", "삭제버튼을 눌렀습니다");
			handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 핸들러
			handler.delete_table_1_4();
			//handler.deleteAll();
			handler.close(); // 데이터베이스 종료
			Toast.makeText(getApplicationContext(), "장바구니가 삭제되었습니다", Toast.LENGTH_SHORT).show();
//			// 틀자마자 종료를 누르면 에러나는데 그 이유는 itemlist등이 list.java에 붙어있는데 이때는 리스트가 형성 안되있음
//			if(list.itemlist.size()==0){
//				for(int i=0; i<list.itemlist_x.size(); i++){
//					list.itemlist_x.remove(i);
//				}
//				for(int i=0; i<list.itemlist_y.size(); i++){
//					list.itemlist_y.remove(i);
//				}
//				for(int i=0; i<list.itemlist.size(); i++) {
//					list.itemlist.remove(i);
//				}
//				for(int i=0; i<list.itemstate.size(); i++) {
//					list.itemstate.remove(i);
//				}
//				Log.d("close", "3");
//			}
//			Log.d("close", "4");
			//finish();
		}
	}

	// 이 아래부터는 NFC관련 함수

	// 포어그라운드 디스패치 활성화
	@Override
	protected void onResume() {
		super.onResume();
		if(mNfcAdapter!=null){
			Log.d(TAG, "onResume: " + getIntent());

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

	// 만약 nfc가 꺼져있다면 사용자에게 nfc기능을 활성화 하는 방법을 표시하는 부분
	private void checkNfcEnabled()
	{
		Boolean nfcEnabled = mNfcAdapter.isEnabled();
		if (!nfcEnabled) {
			new AlertDialog.Builder(SelectMenu.this)
			.setTitle(getString(R.string.warning_nfc_is_off))
			.setMessage(getString(R.string.NFC_NO_SETTING))
			.setCancelable(false)
			.setPositiveButton("Update Settings", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)	{
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			}).create().show();
		} else {
			Toast.makeText(getApplicationContext(), "NFC설정 되었습니다", Toast.LENGTH_SHORT).show();
		}
	}
}
