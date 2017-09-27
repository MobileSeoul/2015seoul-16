package com.exam.zzz_other_menu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActionBar;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.content.Context;
import android.content.IntentFilter.MalformedMimeTypeException;

import android.database.Cursor;

import android.nfc.NfcAdapter;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;

import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView;
import android.widget.Toast;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.example.n_mart.R;

public class PriceComp extends Activity {

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	MySQLiteHandler handler;
	Cursor c;
	Cursor cur;
	Cursor d;

	ListView list;

	ArrayList<PriceCompData_small> alist;
	ArrayList<PriceCompData_small> blist;
	ArrayList<PriceCompData> clist;
	ArrayList<PriceCompData> dlist;
	ArrayList<Integer> pricelist;
	ArrayList<String> mart_name;

	ArrayList<Integer> applelist;
	ArrayList<Integer> pearlist;
	ArrayList<Integer> boochoolist;
	ArrayList<Integer> moolist;
	ArrayList<Integer> onionlist;
	ArrayList<Integer> sangchoolist;
	ArrayList<Integer> oelist;
	ArrayList<Integer> pumpkinlist;
	ArrayList<Integer> beeflist;
	ArrayList<Integer> porklist;
	ArrayList<Integer> chickenlist;
	ArrayList<Integer> egglist;
	ArrayList<Integer> jogilist;
	ArrayList<Integer> myungtaelist;
	ArrayList<Integer> ojinglist;
	ArrayList<Integer> gofishlst;

	ArrayAdapter<String> Adapter;
	DataAdapter adapter;


	public ArrayList<String> dl = new ArrayList<String>();
	Parsing_data pd;

	int Lotte_seoul = 0, Lotte_gangbyeon = 0, Lotte_geumcheon = 0;
	int Home_gangdong = 0, Home_banghak = 0, Home_dongdaemun = 0, Home_myenmok = 0, Home_yongdungpo = 0, Home_syhung = 0, Home_jamsil = 0;
	int E_seongsu = 0, E_eunpyeong = 0, E_mokdong = 0, E_yougsan = 0, E_chunggye = 0, E_myeongil = 0, E_sindorim = 0, E_changdong = 0, E_jayang = 0, E_sangbong = 0, E_mia = 0, E_gayang = 0, E_yeouido = 0, E_wangsimni = 0;
	int Hanaro_mokdong = 0, Hanaro_mia = 0, Hanaro_yangjae = 0, Hanaro_yongsan = 0;
	int Sin_gangnam = 0, Sin_bon = 0;
	int HD_mia = 0, HD_sinchon = 0;
	int LD_gangnam = 0, LD_khwanak = 0, LD_nowon = 0, LD_mia = 0, LD_bon = 0, LD_yongdungpo = 0, LD_jamsil = 0, LD_chungryangri = 0;

	String mart_name_intent = "";
	String price_mart = "";
	String price_mart_name = "";
	int comp_price1;

	public void onCreate(Bundle savedInstanceState) {

		Log.e("PriceComp.java", "1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.price_comp);

		ActionBar actionbar = getActionBar();
		actionbar.hide();

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null)
		{/*
			Log.v("list.java", "NFC준비 안됨");
			Toast.makeText(this,
					"Your device does not support NFC. Cannot run demo.",
					Toast.LENGTH_LONG).show();
			finish();
			return;*/
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




		pd = new Parsing_data();


		Intent intent = getIntent();
		mart_name_intent = intent.getExtras().get("select_mart_name").toString();
		Log.d("mart_name_intent", mart_name_intent);

		Log.e("PriceComp.java", "2");
		list = (ListView) findViewById(R.id.list1);

		Log.e("PriceComp.java", "3");
		handler = MySQLiteHandler.open(getApplicationContext());

		Log.e("PriceComp.java", "4");
		alist = new ArrayList<PriceCompData_small>();
		blist = new ArrayList<PriceCompData_small>();
		clist = new ArrayList<PriceCompData>();
		dlist = new ArrayList<PriceCompData>();
		pricelist = new ArrayList<Integer>();
		mart_name = new ArrayList<String>();

		applelist = new ArrayList<Integer>();
		pearlist = new ArrayList<Integer>();
		boochoolist = new ArrayList<Integer>();
		moolist = new ArrayList<Integer>();
		onionlist = new ArrayList<Integer>();
		sangchoolist = new ArrayList<Integer>();
		oelist = new ArrayList<Integer>();
		pumpkinlist = new ArrayList<Integer>();
		beeflist = new ArrayList<Integer>();
		porklist = new ArrayList<Integer>();
		chickenlist = new ArrayList<Integer>();
		egglist = new ArrayList<Integer>();
		jogilist = new ArrayList<Integer>();
		myungtaelist = new ArrayList<Integer>();
		ojinglist = new ArrayList<Integer>();
		gofishlst = new ArrayList<Integer>();
		/*
		View header = getLayoutInflater().inflate(R.layout.comp_header,null,false);
		TextView pricemart = (TextView) header.findViewById(R.id.Price_mart_name1);
		TextView pricemart_name = (TextView) header.findViewById(R.id.Price_mart_name2);
		TextView price_all = (TextView) header.findViewById(R.id.Price_all);
		RelativeLayout rl = (RelativeLayout) header.findViewById(R.id.rl);*/

		//	list.addHeaderView(header);

		RelativeLayout header_rellay = (RelativeLayout) findViewById(R.id.headlist1);
		ImageView header_img = (ImageView) findViewById(R.id.header_image);
		TextView header_mart_name = (TextView) findViewById(R.id.header_mart_name);
		TextView header_small_name = (TextView) findViewById(R.id.header_small_name);
		TextView header_allprice = (TextView) findViewById(R.id.header_allprice);


		for(int i=1; i<41; i++){
			applelist.add((1+((i-1)*16)));
			pearlist.add((2+((i-1)*16)));
			boochoolist.add((3+((i-1)*16)));
			moolist.add((4+((i-1)*16)));
			onionlist.add((5+((i-1)*16)));
			sangchoolist.add((6+((i-1)*16)));
			oelist.add((7+((i-1)*16)));
			pumpkinlist.add((8+((i-1)*16)));
			beeflist.add((9+((i-1)*16)));
			porklist.add((10+((i-1)*16)));
			chickenlist.add((11+((i-1)*16)));
			egglist.add((12+((i-1)*16)));
			jogilist.add((13+((i-1)*16)));
			myungtaelist.add((14+((i-1)*16)));
			ojinglist.add((15+((i-1)*16)));
			gofishlst.add((16+((i-1)*16)));
			// list.get(i-1)인거 명심하기
		}

		c = handler.select(1);
		c.moveToFirst();

		for(int i=0; i<c.getCount(); i++){
			Log.d("데이터베이스 숫자", Integer.toString(c.getInt(0)));
			alist.add(new PriceCompData_small(c.getString(2).toString(), c.getInt(4)));

			c.moveToNext();
		}

		Log.d("여긴가??", "1");
		for(int i=0; i<alist.size(); i++){
			String a = alist.get(i).getName();
			if(a.contains("사과")){
				blist.add(new PriceCompData_small("사과", alist.get(i).getAmount()));
			} else if(a.contains("배")){
				if(a.contains("배추")){
					blist.add(new PriceCompData_small("배추", alist.get(i).getAmount()));
				} else {
					blist.add(new PriceCompData_small("배", alist.get(i).getAmount()));
				}
			} else if(a.contains("무")){
				blist.add(new PriceCompData_small("무", alist.get(i).getAmount()));
			} else if(a.contains("양파")){
				blist.add(new PriceCompData_small("양파", alist.get(i).getAmount()));
			} else if(a.contains("상추")){
				blist.add(new PriceCompData_small("상추", alist.get(i).getAmount()));
			} else if(a.contains("오이")){
				blist.add(new PriceCompData_small("오이", alist.get(i).getAmount()));
			} else if(a.contains("호박")){
				blist.add(new PriceCompData_small("호박", alist.get(i).getAmount()));
			} else if(a.contains("쇠고기")){
				blist.add(new PriceCompData_small("쇠고기", alist.get(i).getAmount()));
			} else if(a.contains("돼지고기")){
				blist.add(new PriceCompData_small("돼지고기", alist.get(i).getAmount()));
			} else if(a.contains("닭고기")){
				blist.add(new PriceCompData_small("닭고기", alist.get(i).getAmount()));
			} else if(a.contains("달걀")){
				blist.add(new PriceCompData_small("달걀", alist.get(i).getAmount()));
			} else if(a.contains("조기")){
				blist.add(new PriceCompData_small("조기", alist.get(i).getAmount()));
			} else if(a.contains("명태") || a.contains("동태")){
				blist.add(new PriceCompData_small("명태", alist.get(i).getAmount()));
			} else if(a.contains("오징어")){
				blist.add(new PriceCompData_small("오징어", alist.get(i).getAmount()));
			} else if(a.contains("고등어")){
				blist.add(new PriceCompData_small("고등어", alist.get(i).getAmount()));
			}
		}

		Log.d("여긴가??", "2");
		c = handler.select(5);
		c.moveToFirst();

		for(int i=0; i<c.getCount(); i++){
			Log.d("adsfsadfasd", c.getString(1).toString() + "   " + Integer.toString(c.getInt(3)));
		}

		for(int i=0; i<blist.size(); i++){
			if(blist.get(i).getName().equals("사과")){
				for(int j=0; j<applelist.size(); j++){
					c.moveToPosition((applelist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("배")){
				for(int j=0; j<pearlist.size(); j++){
					c.moveToPosition((pearlist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("배추")){
				for(int j=0; j<boochoolist.size(); j++){
					c.moveToPosition((boochoolist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("무")){
				for(int j=0; j<moolist.size(); j++){
					c.moveToPosition((moolist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("양파")){
				for(int j=0; j<onionlist.size(); j++){
					c.moveToPosition((onionlist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("상추")){
				for(int j=0; j<sangchoolist.size(); j++){
					c.moveToPosition((sangchoolist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("오이")){
				for(int j=0; j<oelist.size(); j++){
					c.moveToPosition((oelist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("호박")){
				for(int j=0; j<pumpkinlist.size(); j++){
					c.moveToPosition((pumpkinlist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("쇠고기")){
				for(int j=0; j<beeflist.size(); j++){
					c.moveToPosition((beeflist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("돼지고기")){
				for(int j=0; j<porklist.size(); j++){
					c.moveToPosition((porklist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("닭고기")){
				for(int j=0; j<chickenlist.size(); j++){
					c.moveToPosition((chickenlist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("달걀")){
				for(int j=0; j<egglist.size(); j++){
					c.moveToPosition((egglist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("조기")){
				for(int j=0; j<jogilist.size(); j++){
					c.moveToPosition((jogilist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("명태")){
				for(int j=0; j<myungtaelist.size(); j++){
					c.moveToPosition((myungtaelist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("오징어")){
				for(int j=0; j<ojinglist.size(); j++){
					c.moveToPosition((ojinglist.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			} else if(blist.get(i).getName().equals("고등어")){
				for(int j=0; j<gofishlst.size(); j++){
					c.moveToPosition((gofishlst.get(j)-1));
					clist.add(new PriceCompData(c.getString(1).toString(), c.getString(2).toString(), c.getInt(3), blist.get(i).getAmount()));
				}
			}
		}

		Log.d("여긴가??", "3");
		for(int i=0; i<clist.size(); i++){
			if(clist.get(i).getMartName().equals("롯데마트 강변점")){
				Log.e("값을 구해 보아요~~", Integer.toString(clist.get(i).getPrice()) + "  " + Integer.toString(clist.get(i).getAmount()));
				Lotte_gangbyeon = Lotte_gangbyeon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데마트 금천점")){
				Lotte_geumcheon = Lotte_geumcheon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데마트 서울역점")){
				Lotte_seoul = Lotte_seoul + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 가양점")){
				E_gayang = E_gayang + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 명일점")){
				E_myeongil = E_myeongil + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 목동점")){
				E_mokdong = E_mokdong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 미아점")){
				E_mia = E_mia + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 상봉점")){
				E_sangbong= E_sangbong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 성수점")){
				E_seongsu = E_seongsu + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 신도림점")){
				E_sindorim = E_sindorim + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 여의도점")){
				E_yeouido = E_yeouido + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 왕십리점")){
				E_wangsimni = E_wangsimni + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 용산점")){
				E_yougsan = E_yougsan + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 은평점")){
				E_eunpyeong = E_eunpyeong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 자양점")){
				E_jayang = E_jayang + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 창동점")){
				E_changdong = E_changdong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("이마트 청계점")){
				E_chunggye = E_chunggye + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 강동점")){
				Home_gangdong = Home_gangdong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 동대문점")){
				Home_dongdaemun = Home_dongdaemun + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 면목점")){
				Home_myenmok = Home_myenmok + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 방학점")){
				Home_banghak = Home_banghak + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 시흥점")){
				Home_syhung = Home_syhung + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 영등포점")){
				Home_yongdungpo = Home_yongdungpo + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("홈플러스 잠실점")){
				Home_jamsil = Home_jamsil + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("하나로마트 목동점")){
				Hanaro_mokdong = Hanaro_mokdong + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("하나로마트 미아점")){
				Hanaro_mia = Hanaro_mia + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("하나로마트 양재점")){
				Hanaro_yangjae = Hanaro_yangjae + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("하나로마트 용산점")){
				Hanaro_yongsan = Hanaro_yongsan + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("신세계백화점 강남점")){
				Sin_gangnam = Sin_gangnam + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("신세계백화점 본점")){
				Sin_bon = Sin_bon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("현대백화점 미아점")){
				HD_mia = HD_mia + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("현대백화점 신촌점")){
				HD_sinchon = HD_sinchon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 강남점")){
				LD_gangnam = LD_gangnam + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 관악점")){
				LD_khwanak = LD_khwanak + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 노원점")){
				LD_nowon = LD_nowon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 미아점")){
				LD_mia = LD_mia + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 본점")){
				LD_bon = LD_bon + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 영등포점")){
				LD_yongdungpo = LD_yongdungpo + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 잠실점")){
				LD_jamsil = LD_jamsil + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else if(clist.get(i).getMartName().equals("롯데백화점 청량리점")){
				LD_chungryangri = LD_chungryangri + (clist.get(i).getPrice()*clist.get(i).getAmount());
			} else {
			}
		}

		Log.d("여긴가??", "4");
		pricelist.add(0, Lotte_gangbyeon);
		pricelist.add(1, Lotte_geumcheon);
		pricelist.add(2, Lotte_seoul);

		pricelist.add(3, E_gayang);
		pricelist.add(4, E_myeongil);
		pricelist.add(5, E_mokdong);
		pricelist.add(6, E_mia);
		pricelist.add(7, E_sangbong);
		pricelist.add(8, E_seongsu);
		pricelist.add(9, E_sindorim);
		pricelist.add(10, E_yeouido);
		pricelist.add(11, E_wangsimni);
		pricelist.add(12, E_yougsan);
		pricelist.add(13, E_eunpyeong);
		pricelist.add(14, E_jayang);
		pricelist.add(15, E_changdong);		
		pricelist.add(16, E_chunggye);		

		pricelist.add(17, Home_gangdong);		
		pricelist.add(18, Home_dongdaemun);
		pricelist.add(19, Home_myenmok);
		pricelist.add(20, Home_banghak);
		pricelist.add(21, Home_syhung);
		pricelist.add(22, Home_yongdungpo);		
		pricelist.add(23, Home_jamsil);

		pricelist.add(24, Hanaro_mokdong);
		pricelist.add(25, Hanaro_mia);
		pricelist.add(26, Hanaro_yangjae);
		pricelist.add(27, Hanaro_yongsan);

		pricelist.add(28, Sin_gangnam);
		pricelist.add(29, Sin_bon);

		pricelist.add(30, HD_mia);
		pricelist.add(31, HD_sinchon);

		pricelist.add(32, LD_gangnam);
		pricelist.add(33, LD_khwanak);
		pricelist.add(34, LD_nowon);
		pricelist.add(35, LD_mia);
		pricelist.add(36, LD_bon);
		pricelist.add(37, LD_yongdungpo);
		pricelist.add(38, LD_jamsil);
		pricelist.add(39, LD_chungryangri);








		mart_name.add(0, "롯데마트 강변점"); 
		mart_name.add(1, "롯데마트 금천점");
		mart_name.add(2, "롯데마트 서울역점");

		mart_name.add(3, "이마트 가양점"); 
		mart_name.add(4, "이마트 명일점"); 
		mart_name.add(5, "이마트 목동점"); 
		mart_name.add(6, "이마트 미아점");
		mart_name.add(7, "이마트 상봉점"); 
		mart_name.add(8, "이마트 성수점"); 
		mart_name.add(9, "이마트 신도림점");
		mart_name.add(10, "이마트 여의도점");
		mart_name.add(11, "이마트 왕십리점");
		mart_name.add(12, "이마트 용산점"); 
		mart_name.add(13, "이마트 은평점"); 
		mart_name.add(14, "이마트 자양점"); 
		mart_name.add(15, "이마트 창동점"); 
		mart_name.add(16, "이마트 청계점");		

		mart_name.add(17, "홈플러스 강동점"); 		 
		mart_name.add(18, "홈플러스 동대문점");
		mart_name.add(19, "홈플러스 면목점");
		mart_name.add(20, "홈플러스 방학점");
		mart_name.add(21, "홈플러스 시흥점");
		mart_name.add(22, "홈플러스 영등포점");
		mart_name.add(23, "홈플러스 잠실점"); 

		mart_name.add(24, "하나로마트 목동점");
		mart_name.add(25, "하나로마트 미아점");
		mart_name.add(26, "하나로마트 양재점");
		mart_name.add(27, "하나로마트 용산점");

		mart_name.add(28, "신세계백화점 강남점");
		mart_name.add(29, "신세계백화점 본점");

		mart_name.add(30, "현대백화점 미아점");
		mart_name.add(31, "현대백화점 신촌점");

		mart_name.add(32, "롯데백화점 강남점");
		mart_name.add(33, "롯데백화점 관악점");
		mart_name.add(34, "롯데백화점 노원점");
		mart_name.add(35, "롯데백화점 미아점");
		mart_name.add(36, "롯데백화점 본점");
		mart_name.add(37, "롯데백화점 영등포점");
		mart_name.add(38, "롯데백화점 잠실점");
		mart_name.add(39, "롯데백화점 청량리점");	

		Log.d("여긴가??", "1");
		for(int i=0; i<mart_name.size(); i++){
			dlist.add(new PriceCompData(mart_name.get(i), "", pricelist.get(i), 0));
		}

		for(int i=0; i<dlist.size(); i++){
			Log.d("확인용", dlist.get(i).getMartName() + " " + dlist.get(i).getPrice());
		}

		for(int i=0; i<dlist.size()-1; i++){
			if(dlist.get(i).getPrice()>dlist.get(i+1).getPrice()){

				Log.d("dlist", dlist.get(i).getMartName() + "   " + Integer.toString(dlist.get(i).getPrice()));
				Log.d("dlist", dlist.get(i+1).getMartName() + "   " + Integer.toString(dlist.get(i+1).getPrice()));

				PriceCompData pcd = new PriceCompData(dlist.get(i+1).getMartName(), "", dlist.get(i+1).getPrice(), 0);
				PriceCompData pcd2 = new PriceCompData(dlist.get(i).getMartName(), "", dlist.get(i).getPrice(), 0);
				dlist.set(i, pcd);
				dlist.set(i+1, pcd2);

				Log.d("dlist", dlist.get(i).getMartName() + "   " + Integer.toString(dlist.get(i).getPrice()));
				Log.d("dlist", dlist.get(i+1).getMartName() + "   " + Integer.toString(dlist.get(i+1).getPrice()));


				i = -1;
			} else {
				Log.d("비교중", "같네요~");
			}
		}

		c = handler.select(1);
		c.moveToFirst();


		if(c.getCount()!=0){

			if(c.getString(1).toString().equals("롯데마트 강변점")){

				header_img.setBackgroundResource(R.drawable.l_gangbyncomp);
				header_mart_name.setText("롯데마트");
				header_small_name.setText("강변점");
				header_allprice.setText("" + pricelist.get(0));




			}else if(c.getString(1).toString().equals("롯데마트 금천점")){
				header_img.setBackgroundResource(R.drawable.l_keumchoncomp);
				header_mart_name.setText("롯데마트");
				header_small_name.setText("금천점");
				header_allprice.setText("" + pricelist.get(1));
			}else if(c.getString(1).toString().equals("롯데마트 서울역점")){
				header_img.setBackgroundResource(R.drawable.l_seoulstationcomp);
				header_mart_name.setText("롯데마트");
				header_small_name.setText("서울역점");
				header_allprice.setText("" + pricelist.get(2));
			}else if(c.getString(1).toString().equals("이마트 가양점")){
				header_img.setBackgroundResource(R.drawable.e_gayangcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("가양점");
				header_allprice.setText("" + pricelist.get(3));
			}else if(c.getString(1).toString().equals("이마트 명일점")){
				header_img.setBackgroundResource(R.drawable.e_myungilcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("명일점");
				header_allprice.setText("" + pricelist.get(4));
			}else if(c.getString(1).toString().equals("이마트 목동점")){
				header_img.setBackgroundResource(R.drawable.e_mokdongcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("목동점");
				header_allprice.setText("" + pricelist.get(5));
			}else if(c.getString(1).toString().equals("이마트 미아점")){
				header_img.setBackgroundResource(R.drawable.e_miacomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("미아점");
				header_allprice.setText("" + pricelist.get(6));
			}else if(c.getString(1).toString().equals("이마트 상봉점")){
				header_img.setBackgroundResource(R.drawable.e_sangbongcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("상봉점");
				header_allprice.setText("" + pricelist.get(7));
			}else if(c.getString(1).toString().equals("이마트 성수점")){
				header_img.setBackgroundResource(R.drawable.e_sungsoocomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("성수점");
				header_allprice.setText("" + pricelist.get(8));
			}else if(c.getString(1).toString().equals("이마트 신도림점")){
				header_img.setBackgroundResource(R.drawable.e_sindorimcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("신도림점");
				header_allprice.setText("" + pricelist.get(9));
			}else if(c.getString(1).toString().equals("이마트 여의도점")){
				header_img.setBackgroundResource(R.drawable.e_yuidocomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("여의도점");
				header_allprice.setText("" + pricelist.get(10));
			}else if(c.getString(1).toString().equals("이마트 왕십리점")){
				header_img.setBackgroundResource(R.drawable.e_wangsiricomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("왕십리점");
				header_allprice.setText("" + pricelist.get(11));
			}else if(c.getString(1).toString().equals("이마트 용산점")){
				header_img.setBackgroundResource(R.drawable.e_yongsancomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("용산점");
				header_allprice.setText("" + pricelist.get(12));
			}else if(c.getString(1).toString().equals("이마트 은평점")){
				header_img.setBackgroundResource(R.drawable.e_eunpyungcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("은평점");
				header_allprice.setText("" + pricelist.get(13));
			}else if(c.getString(1).toString().equals("이마트 자양점")){
				header_img.setBackgroundResource(R.drawable.e_jayangcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("자양점");
				header_allprice.setText("" + pricelist.get(14));
			}else if(c.getString(1).toString().equals("이마트 창동점")){
				header_img.setBackgroundResource(R.drawable.e_changdongcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("창동점");
				header_allprice.setText("" + pricelist.get(15));
			}else if(c.getString(1).toString().equals("이마트 청계점")){
				header_img.setBackgroundResource(R.drawable.e_changdongcomp);
				header_mart_name.setText("이마트");
				header_small_name.setText("청계점");
				header_allprice.setText("" + pricelist.get(16));
			}else if(c.getString(1).toString().equals("홈플러스 강동점")){
				header_img.setBackgroundResource(R.drawable.h_gangdongcomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("강동점");
				header_allprice.setText("" + pricelist.get(17));
			}else if(c.getString(1).toString().equals("홈플러스 동대문점")){
				header_img.setBackgroundResource(R.drawable.h_dongdaemooncomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("동대문점");
				header_allprice.setText("" + pricelist.get(18));
			}else if(c.getString(1).toString().equals("홈플러스 면목점")){
				header_img.setBackgroundResource(R.drawable.h_myunmokcomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("면목점");
				header_allprice.setText("" + pricelist.get(19));
			}else if(c.getString(1).toString().equals("홈플러스 방학점")){
				header_img.setBackgroundResource(R.drawable.h_banghakcomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("방학점");
				header_allprice.setText("" + pricelist.get(20));
			}else if(c.getString(1).toString().equals("홈플러스 시흥점")){
				header_img.setBackgroundResource(R.drawable.h_siheungcomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("시흥점");
				header_allprice.setText("" + pricelist.get(21));
			}else if(c.getString(1).toString().equals("홈플러스 영등포점")){
				header_img.setBackgroundResource(R.drawable.h_youngdeungpocomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("영등포점");
				header_allprice.setText("" + pricelist.get(22));
			}else if(c.getString(1).toString().equals("홈플러스 잠실점")){
				header_img.setBackgroundResource(R.drawable.h_jamsilcomp);
				header_mart_name.setText("홈플러스");
				header_small_name.setText("잠실점");
				header_allprice.setText("" + pricelist.get(23));
			}else if(c.getString(1).toString().equals("하나로마트 목동점")){
				header_img.setBackgroundResource(R.drawable.n_mokdongcomp);
				header_mart_name.setText("하나로마트");
				header_small_name.setText("목동점");
				header_allprice.setText("" + pricelist.get(24));
			}else if(c.getString(1).toString().equals("하나로마트 미아점")){
				header_img.setBackgroundResource(R.drawable.n_miacomp);
				header_mart_name.setText("하나로마트");
				header_small_name.setText("미아점");
				header_allprice.setText("" + pricelist.get(25));
			}else if(c.getString(1).toString().equals("하나로마트 양재점")){
				header_img.setBackgroundResource(R.drawable.n_yangjaecomp);
				header_mart_name.setText("하나로마트");
				header_small_name.setText("양재점");
				header_allprice.setText("" + pricelist.get(26));
			}else if(c.getString(1).toString().equals("하나로마트 용산점")){
				header_img.setBackgroundResource(R.drawable.n_yongsancomp);
				header_mart_name.setText("하나로마트");
				header_small_name.setText("용산점");
				header_allprice.setText("" + pricelist.get(27));
			}else if(c.getString(1).toString().equals("신세계백화점 강남점")){
				header_img.setBackgroundResource(R.drawable.s_gangnamcomp);
				header_mart_name.setText("신세계백화점");
				header_small_name.setText("강남점");
				header_allprice.setText("" + pricelist.get(28));
			}else if(c.getString(1).toString().equals("신세계백화점 본점")){
				header_img.setBackgroundResource(R.drawable.s_boncomp);
				header_mart_name.setText("신세계백화점");
				header_small_name.setText("본점");
				header_allprice.setText("" + pricelist.get(29));
			}else if(c.getString(1).toString().equals("현대백화점 미아점")){
				header_img.setBackgroundResource(R.drawable.hd_miacomp);
				header_mart_name.setText("현대백화점");
				header_small_name.setText("미아점");
				header_allprice.setText("" + pricelist.get(30));
			}else if(c.getString(1).toString().equals("현대백화점 신촌점")){
				header_img.setBackgroundResource(R.drawable.hd_sinchoncomp);
				header_mart_name.setText("현대백화점");
				header_small_name.setText("신촌점");
				header_allprice.setText("" + pricelist.get(31));
			}else if(c.getString(1).toString().equals("롯데백화점 강남점")){
				header_img.setBackgroundResource(R.drawable.ld_gangnamcomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("강남점");
				header_allprice.setText("" + pricelist.get(32));
			}else if(c.getString(1).toString().equals("롯데백화점 관악점")){
				header_img.setBackgroundResource(R.drawable.ld_khwanakcomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("관악점");
				header_allprice.setText("" + pricelist.get(33));
			}else if(c.getString(1).toString().equals("롯데백화점 노원점")){
				header_img.setBackgroundResource(R.drawable.ld_nowoncomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("노원점");
				header_allprice.setText("" + pricelist.get(34));
			}else if(c.getString(1).toString().equals("롯데백화점 미아점")){
				header_img.setBackgroundResource(R.drawable.ld_miacomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("미아점");
				header_allprice.setText("" + pricelist.get(35));
			}else if(c.getString(1).toString().equals("롯데백화점 본점")){
				header_img.setBackgroundResource(R.drawable.ld_boncomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("본점");
				header_allprice.setText("" + pricelist.get(36));
			}else if(c.getString(1).toString().equals("롯데백화점 영등포점")){
				header_img.setBackgroundResource(R.drawable.ld_youngdeungpocomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("영등포점");
				header_allprice.setText("" + pricelist.get(37));
			}else if(c.getString(1).toString().equals("롯데백화점 잠실점")){
				header_img.setBackgroundResource(R.drawable.ld_jamsilcomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("잠실점");
				header_allprice.setText("" + pricelist.get(38));
			}else if(c.getString(1).toString().equals("롯데백화점 청량리점")){
				header_img.setBackgroundResource(R.drawable.ld_chungryangricomp);
				header_mart_name.setText("롯데백화점");
				header_small_name.setText("청량리점");
				header_allprice.setText("" + pricelist.get(39));
			}
		}
		comp_price1 = Integer.parseInt(header_allprice.getText().toString());

		Log.e("PriceComp.java", "9");

		price_mart = header_mart_name.getText().toString();
		price_mart_name = header_small_name.getText().toString();

		c = handler.select(1);
		c.moveToFirst();

		d = handler.select(5);
		d.moveToFirst();

		//		for(int i=0; i<dlist.size(); i++){
		//
		//			if(dlist.get(i).getMartName().equals(price_mart + " " + price_mart_name)){
		//				dlist.remove(i);
		//			}
		//		}

		Log.e("장점인 로그", Integer.toString(pd.latitude.size()));
		Log.e("장점인 로그2", Integer.toString(pd.longitude.size()));


		for(int j = pd.latitude.size()-1; j>-1; j--){


			//			Double f= Math.sqrt(Math.pow(pd.latitude.get(j)-c.getDouble(8),2) + Math.pow(pd.longitude.get(j)-c.getDouble(9),2));
			//			Log.d("위도경도랄랄라",""+f);

			if(Math.sqrt( Math.pow(pd.latitude.get(j)- c.getDouble(8),2)  + 
					Math.pow(pd.longitude.get(j)- c.getDouble(9),2)	)>0.042591996){
				for(int i = 0; i<dlist.size();i++){
					if(dlist.get(i).getMartName() == mart_name.get(j).toString()){
						dlist.remove(i);
					}
				}
			}	
		}

		/*for(int i=0; i<mart_name.size(); i++){
			Log.e("마트네임확인", ""+ mart_name.get(i).toString());
		}*/

		//		for(int i=0; i<dlist.size(); i++){
		//			if(dlist.get(i).getMartName() == mart_name.get(3).toString()){
		//				Log.e("디리스트3", ""+i+"  "+ dlist.get(i).getMartName());
		//				dlist.remove(i);
		//			}
		//			
		//		}


		/*	dlist.remove(3);
		for(int i=0; i< dlist.size(); i++){

			Log.e("디리스트 삭제확인", ""+i+"  "+ dlist.get(i).getMartName());
		}*/


		for(int i=0; i<dlist.size(); i++){

			if(dlist.get(i).getMartName().equals(price_mart + " " + price_mart_name)){
				dlist.remove(i);
			}
		}


		adapter = new DataAdapter(this, dlist); // 커스텀어댑터에 정보출력용 어레이리스트 연결
		adapter.notifyDataSetChanged(); // 리스트의 정보가 변경되었다고 어댑터에게 알림
		list.setAdapter(adapter); // 리스트뷰와 어댑터 연결

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

				Builder dlg = new AlertDialog.Builder(PriceComp.this);
				dlg.setTitle("선택")
				.setMessage(adapter.getItem(position).getMartName() + " 를 선택 하셨습니다 맞으면 확인 아니면 취소를 선택하세요")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						Log.e("포지션확인", Integer.toString(position));
						Intent intent = new Intent(getApplicationContext(), list.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
						intent.putExtra("select_mart_name", adapter.getItem(position).getMartName());
						intent.putExtra("select_mart_name2", mart_name_intent);
						startActivity(intent); // 인텐트 시작
					//	finish();

					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
		});

		header_rellay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Builder dlg = new AlertDialog.Builder(PriceComp.this);
				dlg.setTitle("선택")
				.setMessage(price_mart + " " + price_mart_name + " 를 선택 하셨습니다 맞으면 확인 아니면 취소를 선택하세요")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

//						Toast.makeText(getApplicationContext(), "싫어싫어~~~", Toast.LENGTH_SHORT).show();

						Intent intent2 = new Intent(getApplicationContext(), list.class); // 장볼 목록 보여주는 list.java로의 인텐트 구문
						intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 인텐트 시 액티비티 재사용 구문
						intent2.putExtra("select_mart_name", price_mart + " " + price_mart_name);
						intent2.putExtra("select_mart_name2", mart_name_intent);
						startActivity(intent2); // 인텐트 시작
					//	finish();
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
		});


		handler.close();
		Log.e("PriceComp.java", "10");
	}

	private class DataAdapter extends ArrayAdapter<PriceCompData> {

		private LayoutInflater mInflater;

		public DataAdapter(Context context, ArrayList<PriceCompData> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			Log.e("getView", "1");
			View view = null;
			if (v == null) {
				view = mInflater.inflate(R.layout.comp_list, null);
			} else {
				view = v;
			}

			Log.e("getView", "2");
			PriceCompData pcd = this.getItem(position);
			//			adapter = new DataAdapter(this, dlist);
			Log.e("getView", "3");
			TextView MartNameText = (TextView) view.findViewById(R.id.marttext);
			TextView PriceText = (TextView) view.findViewById(R.id.pricetext);
			TextView Smallname = (TextView) view.findViewById(R.id.smallname);
			ImageView comp_arrow = (ImageView) view.findViewById(R.id.comp_arrow);

			ImageView mart_picture = (ImageView) view.findViewById(R.id.Comp_mart5);



			if(pcd.getMartName().equals("롯데마트 강변점")){



				MartNameText.setText("롯데마트");
				Smallname.setText("강변점");
				PriceText.setText("" + pcd.getPrice());
				mart_picture.setBackgroundResource(R.drawable.l_gangbyun);

				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데마트 금천점")){
				MartNameText.setText("롯데마트");
				Smallname.setText("금천점");
				PriceText.setText("" + pcd.getPrice());
				mart_picture.setBackgroundResource(R.drawable.l_keumchon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데마트 서울역점")){
				MartNameText.setText("롯데마트");
				Smallname.setText("서울역점");
				PriceText.setText("" + pcd.getPrice());	
				mart_picture.setBackgroundResource(R.drawable.l_seoulstation);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 가양점")){
				MartNameText.setText("이마트");
				Smallname.setText("가양점");
				mart_picture.setBackgroundResource(R.drawable.e_gayang);
				PriceText.setText("" + pcd.getPrice());		
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 명일점")){
				MartNameText.setText("이마트");
				Smallname.setText("명일점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_myungil);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 목동점")){
				MartNameText.setText("이마트");
				Smallname.setText("목동점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_mokdong);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 미아점")){
				MartNameText.setText("이마트");
				Smallname.setText("미아점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_mia);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 상봉점")){
				MartNameText.setText("이마트");
				Smallname.setText("상봉점");
				PriceText.setText("" + pcd.getPrice());	
				mart_picture.setBackgroundResource(R.drawable.e_sangbong);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 성수점")){
				MartNameText.setText("이마트");
				Smallname.setText("성수점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_sungsoo);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 신도림점")){
				MartNameText.setText("이마트");
				Smallname.setText("신도림점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_sindorim);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 여의도점")){
				MartNameText.setText("이마트");
				Smallname.setText("여의도점");
				PriceText.setText("" + pcd.getPrice());		
				mart_picture.setBackgroundResource(R.drawable.e_yuido);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 왕십리점")){
				MartNameText.setText("이마트");
				Smallname.setText("왕십리점");
				PriceText.setText("" + pcd.getPrice());				
				mart_picture.setBackgroundResource(R.drawable.e_wangsiri);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 용산점")){
				MartNameText.setText("이마트");
				Smallname.setText("용산점");
				mart_picture.setBackgroundResource(R.drawable.e_yongsan);
				PriceText.setText("" + pcd.getPrice());		
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 은평점")){
				MartNameText.setText("이마트");
				Smallname.setText("은평점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.e_eunpyung);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 자양점")){
				MartNameText.setText("이마트");
				Smallname.setText("자양점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.e_jayang);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 창동점")){
				MartNameText.setText("이마트");
				Smallname.setText("창동점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.e_changdong);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("이마트 청계점")){
				MartNameText.setText("이마트");
				Smallname.setText("청계점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.e_chungye);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 강동점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("강동점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_gangdong);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 동대문점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("동대문점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_dongdaemoon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 면목점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("면목점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_myunmok);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 방학점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("방학점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_banghak);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 시흥점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("시흥점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_siheung);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 영등포점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("영등포점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_youngdeungpo);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("홈플러스 잠실점")){
				MartNameText.setText("홈플러스");
				Smallname.setText("잠실점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.h_jamsil);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("하나로마트 목동점")){
				MartNameText.setText("하나로마트");
				Smallname.setText("목동점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.n_mokdong);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("하나로마트 미아점")){
				MartNameText.setText("하나로마트");
				Smallname.setText("미아점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.n_mia);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("하나로마트 양재점")){
				MartNameText.setText("하나로마트");
				Smallname.setText("양재점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.n_yangjae);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("하나로마트 용산점")){
				MartNameText.setText("하나로마트");
				Smallname.setText("용산점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.n_yongsan);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("신세계백화점 강남점")){
				MartNameText.setText("신세계백화점");
				Smallname.setText("강남점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.s_gangnam);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("신세계백화점 본점")){
				MartNameText.setText("신세계백화점");
				Smallname.setText("본점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.s_bon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("현대백화점 미아점")){
				MartNameText.setText("현대백화점");
				Smallname.setText("미아점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.hd_mia);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("현대백화점 신촌점")){
				MartNameText.setText("현대백화점");
				Smallname.setText("신촌점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.hd_sinchon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 강남점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("강남점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_gangnam);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 관악점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("관악점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_khwanak);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 노원점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("노원점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_nowon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 미아점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("미아점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_mia);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 본점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("본점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_bon);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 영등포점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("영등포점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_youngdeungpo);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 잠실점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("잠실점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_jamsil);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			} else if(pcd.getMartName().equals("롯데백화점 청량리점")){
				MartNameText.setText("롯데백화점");
				Smallname.setText("청량리점");
				PriceText.setText("" + pcd.getPrice());			
				mart_picture.setBackgroundResource(R.drawable.ld_chungryangri);
				if(comp_price1>pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_down);
				}else if(comp_price1<pcd.getPrice()){
					comp_arrow.setBackgroundResource(R.drawable.comp_up);
				}else{
					comp_arrow.setBackgroundResource(R.drawable.comp_equal);
				}
			}



			Log.e("getView", "5");
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

class PriceCompData_small{

	private String Name;
	private int Amount;

	public PriceCompData_small(String pName, int pAmount){

		this.Name = pName;
		this.Amount = pAmount;
	}

	public String getName(){
		return Name;
	}

	public int getAmount(){
		return Amount;
	}
}

class PriceCompData{

	private String MartName;
	private String Name;
	private int Price;
	private int Amount;

	public PriceCompData(String pMartName, String pName, int pPrice, int pAmount){

		this.MartName = pMartName;
		this.Name = pName;
		this.Price = pPrice;
		this.Amount = pAmount;
	}

	public String getMartName(){
		return MartName;
	}

	public String getName(){
		return Name;
	}

	public int getPrice(){
		return Price;
	}

	public int getAmount(){
		return Amount;
	}
}

