/**
 * 지도 
 */

package com.example.n_mart;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.Context;
import android.content.DialogInterface;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.database.Cursor;

import android.nfc.NfcAdapter;

import android.os.Bundle;

import android.util.Log;

import android.view.Window;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;

import com.exam.zzz_other_menu.Main;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;


public class MainActivity extends NMapActivity implements OnMapStateChangeListener, OnStateChangeListener, LocationListener{

	private static final String TAG = "nfcinventory_simple";

	// nfc에서 사용
	NfcAdapter mNfcAdapter; // 실제 nfc하드웨어와의 다리 역할을 담당 // nfc설정이 체크되어 있는지 점검 가능 // nfc태그에서 ndef데이터를 읽어 오거나 반대로 값을 쓸 때 사용 가능
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mReadTagFilters;
	IntentFilter[] mWriteTagFilters;

	public static final String API_KEY = "df41e81814d5b62b088e616c54e1ef7b";	// API_KEY

	MySQLiteHandler handler;
	Cursor c;

	NMapView mMapView = null;	// 네이버 맵 객체
	NMapController mMapController = null;	// 맵 컨트롤러
	LinearLayout MapContainer;	// 맵을 추가할 레이아웃

	NMapViewerResourceProvider mMapViewerResourceProvider = null;	// 오버레이의 리소스를 제공하기 위한 객체
	NMapOverlayManager mOverlayManager;	// 오버레이 관리자

	String select_mart_name;

	// 현재위치
	public LocationManager locManager;		
	public Location myLocation = null;
	public double latPoint, lngPoint;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 비활성
		setContentView(R.layout.activity_main_map);

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



		MapContainer = (LinearLayout) findViewById(R.id.MapContainer);	// 네이버 지도를 넣기 위한 LinearLayout 컴포넌트
		mMapView = new NMapView(this);	// 네이버 지도 객체 생성
		mMapController = mMapView.getMapController();	// 지도 객체로부터 컨트롤러 추출
		mMapView.setApiKey(API_KEY);	// 네이버 지도 객체에 API_KEY 지정
		MapContainer.addView(mMapView);		// 생성된 네이버 지도 객체를 LinearLayout에 추가시킨다
		mMapView.setClickable(true);	// 지도를 터치할 수 있도록 옵션 활성화
		mMapView.setBuiltInZoomControls(true, null);	// 확대,축소를 위한 줌 컨트롤러 표시 옵션 활성화
		mMapView.setOnMapStateChangeListener(this);		// 지도에 대한 상태 변경 이벤트 연결


		//FUNCTION02(); 파싱
		loc01();
		FUNCTION03();

	}

	public void loc01(){		// 현재위치 찾기.
		// LocationListener 핸들
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// GPS로 부터 위치 정보를 업데이트 요청
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, this);  // 0초에 바로 받아옴 10초(1000)하면 10초뒤에 받아옴?
		// 기지국으로부터 위치 정보를 업데이트 요청
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, this);	// 100미터 거리 이동하면 업데이트

		//	getGeoLocation();
	}
	public void getGeoLocation() {

		if(myLocation != null) {
			latPoint = myLocation.getLatitude();
			lngPoint = myLocation.getLongitude();

			Log.d("didididididi", ""+latPoint+" "+lngPoint);
		}

		Log.d("실제들어가는좌표",""+lngPoint+"  "+latPoint);
		NGeoPoint mGeoPoint = new NGeoPoint(lngPoint,latPoint);
		mMapController.setMapCenter(mGeoPoint, 11);

		FUNCTION01();
	}

	public void FUNCTION01(){	// 마커 띄우기
		Log.e("tag", "FUNCTION01 시작");

		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);	// 오버레이 리소스 관리객체 할당
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);	// 오버레이 관리자 추가

		int markerId = NMapPOIflagType.PIN;		// 오버레이에 표시하기 위한 마커 이미지의 id값 생성

		// 표시할 위치 데이터를 지정한다. -- 마지막 인자가 오버레이를 인식하기 위한 id 값
		NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
		poiData.beginPOIdata(100);

		for(int i=0; i<mart_address.size(); i++){
			String name = mart_name.get(i);
			double la = latitude.get(i);
			double lo = longitude.get(i);
			poiData.addPOIitem(lo, la, name, markerId, 0);
		}

		/*for(int i=0; i<row_count; i++){
			String name = mart_name_lot.get(i);
			double la = latitude_lot.get(i);
			double lo = longitude_lot.get(i);
			poiData.addPOIitem(lo, la, name, markerId, 0);
		}*/

		/*poiData.addPOIitem(127.0630205, 37.5091300, "위치1", markerId, 0);
		poiData.addPOIitem(127.061, 37.51, "위치2", markerId, 0);*/

		/*NMapPOIitem item = poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerId, 0); // 빨갱화살표
			item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
		NMapPOIitem item2 = poiData.addPOIitem(127.061, 37.51, "Pizza 777-111", markerId, 0); // 빨갱화살표
			item2.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);*/

		Log.d("펑션1마커","현재좌표"+lngPoint+" "+latPoint);
		poiData.addPOIitem(lngPoint, latPoint, "현재", markerId, 0);


		poiData.endPOIdata();


		// 위치 데이터를 사용하여 오버레이 생성
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

		//poiDataOverlay.showAllPOIdata(0);	// id값이 0으로 지정된 모든 오버레이가 표시되고 있는 위치로 지도의 중심과 Zoom 재설정
		Log.e("tag", "FUNCTION01 끝");

		poiDataOverlay.setOnStateChangeListener(this);	// 아이템 선택 상태 변경, 말풍선 선택 되는 경우를 위한 이벤트 리스너 등록
	}

	/*
	public void FUNCTION02(){	// 파싱 클래스 호출 함수.
		//Log.e("tag","FUCNTION02 시작");
		XmlWork xmlWork = new XmlWork();
		xmlWork.execute("http://openapi.seoul.go.kr:8088/6b616e673530323632313333/xml/ListMarketInfoServer/1/30/%EB%A1%AF%EB%8D%B0%EB%A7%88%ED%8A%B8");
		//Log.e("tag","FUCNTION02 끝");

	}
	 */

	public ArrayList<Double> latitude = new ArrayList<Double>(); // 좌표 위도
	public ArrayList<Double> longitude = new ArrayList<Double>(); // 좌표 경도
	public ArrayList<String> mart_address = new ArrayList<String>(); // 주소
	public ArrayList<String> mart_name = new ArrayList<String>(); // 마트이름
	public ArrayList<String> mart_mobile = new ArrayList<String>(); // 웹주소

	public void FUNCTION03(){

		mart_mobile.add(0, "http://company.lottemart.com/branch/bc/main.do?brnchCd=0100012");
		mart_mobile.add(1, "http://company.lottemart.com/branch/bc/main.do?brnchCd=0100001");
		mart_mobile.add(2, "http://company.lottemart.com/branch/vm/main.do?brnchCd=0400006r");
		mart_mobile.add(3, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(4, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(5, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(6, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(7, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(8, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(9, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(10, "http://corporate.homeplus.co.kr/store/hypermarket.aspx");
		mart_mobile.add(11, "http://store.emart.com/branch/list.do");
		mart_mobile.add(12, "http://store.emart.com/branch/list.do");
		mart_mobile.add(13, "http://store.emart.com/branch/list.do");
		mart_mobile.add(14, "http://store.emart.com/branch/list.do");
		mart_mobile.add(15, "http://store.emart.com/branch/list.do");
		mart_mobile.add(16, "http://store.emart.com/branch/list.do");
		mart_mobile.add(17, "http://store.emart.com/branch/list.do");
		mart_mobile.add(18, "http://store.emart.com/branch/list.do");
		mart_mobile.add(19, "http://store.emart.com/branch/list.do");
		mart_mobile.add(20, "http://store.emart.com/branch/list.do");
		mart_mobile.add(21, "http://store.emart.com/branch/list.dor");
		mart_mobile.add(22, "http://store.emart.com/branch/list.do");
		mart_mobile.add(23, "http://store.emart.com/branch/list.do");
		mart_mobile.add(24, "http://store.emart.com/branch/list.do");

		mart_mobile.add(25, "https://www.nhhanaro.co.kr/utong/club/index.jsp");
		mart_mobile.add(26, "https://www.nhhanaro.co.kr/utong/club/index.jsp");
		mart_mobile.add(27, "https://www.nhhanaro.co.kr/utong/club/index.jsp");
		mart_mobile.add(28, "https://www.nhhanaro.co.kr/utong/club/index.jsp");
		mart_mobile.add(29, "http://www.shinsegae.com/store/main.jsp?storeCode=D02");
		mart_mobile.add(30, "http://www.shinsegae.com/store/main.jsp?storeCode=D01");
		mart_mobile.add(31, "http://www.ehyundai.com/newPortal/DP/DP000000_V.do?branchCd=B00141000");
		mart_mobile.add(32, "http://www.ehyundai.com/newPortal/DP/DP000000_V.do?branchCd=B00127000");
		mart_mobile.add(33, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=006");
		mart_mobile.add(34, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=005");
		mart_mobile.add(35, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=007");
		mart_mobile.add(36, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=024");
		mart_mobile.add(37, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=001");
		mart_mobile.add(38, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=003");
		mart_mobile.add(39, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=002");
		mart_mobile.add(40, "http://store.lotteshopping.com/handler/Main-Start?subBrchCd=004");


		mart_name.add(0, "롯데마트 서울역점");
		mart_name.add(1, "롯데마트 강변점");
		mart_name.add(2, "롯데마트 금천점");
		mart_name.add(3, "홈플러스 잠실점");
		mart_name.add(4, "홈플러스 강동점");
		mart_name.add(5, "홈플러스 방학점");
		mart_name.add(6, "홈플러스 동대문점");
		mart_name.add(7, "홈플러스 면목점");
		mart_name.add(8, "홈플러스 영등포점");
		mart_name.add(9, "홈플러스 독산점");
		mart_name.add(10, "홈플러스 시흥점");

		mart_name.add(11, "이마트 성수점");
		mart_name.add(12, "이마트 은평점");
		mart_name.add(13, "이마트 목동점");
		mart_name.add(14, "이마트 용산점");
		mart_name.add(15, "이마트 청계점");
		mart_name.add(16, "이마트 명일점");
		mart_name.add(17, "이마트 신도림점");
		mart_name.add(18, "이마트 창동점");
		mart_name.add(19, "이마트 자양점");
		mart_name.add(20, "이마트 상봉점");
		mart_name.add(21, "이마트 미아점");
		mart_name.add(22, "이마트 가양점");
		mart_name.add(23, "이마트 여의도점");
		mart_name.add(24, "이마트 왕십리점");

		mart_name.add(25, "하나로마트 목동점");
		mart_name.add(26, "하나로마트 미아점");
		mart_name.add(27, "하나로마트 양재점");
		mart_name.add(28, "하나로마트 용산점");

		mart_name.add(29, "신세계백화점 강남점");
		mart_name.add(30, "신세계백화점 본점");

		mart_name.add(31, "현대백화점 미아점");
		mart_name.add(32, "현대백화점 신촌점");

		mart_name.add(33, "롯데백화점 강남점");
		mart_name.add(34, "롯데백화점 관악점");
		mart_name.add(35, "롯데백화점 노원점");
		mart_name.add(36, "롯데백화점 미아점");
		mart_name.add(37, "롯데백화점 본점");
		mart_name.add(38, "롯데백화점 영등포점");
		mart_name.add(39, "롯데백화점 잠실점");
		mart_name.add(40, "롯데백화점 청량리점");

		mart_address.add(0, "서울특별시 중구 봉래동2가 122-11");
		mart_address.add(1, "서울특별시 광진구 구의동 546-4");
		mart_address.add(2, "서울특별시 금천구 독산동 295-10");
		mart_address.add(3, "서울특별시 송파구 신천동 7-12");
		mart_address.add(4, "서울특별시 강동구 천호동 42");
		mart_address.add(5, "서울특별시 도봉구 방학동 707-7");
		mart_address.add(6, "서울특별시 동대문구 용두동 33-1");
		mart_address.add(7, "서울특별시 중랑구 면목동 168-2");
		mart_address.add(8, "서울특별시 영등포구 문래동3가 55-3");
		mart_address.add(9, "서울특별시 금천구 독산동 1038-28");
		mart_address.add(10, "서울특별시 금천구 시흥동 837-38");
		mart_address.add(11, "서울특별시 성동구 성수동2가 333-16");
		mart_address.add(12, "서울특별시 은평구 응암동 90-1");
		mart_address.add(13, "서울특별시 양천구 오목로 299");
		mart_address.add(14, "서울특별시 용산구 한강로3가 40-999");
		mart_address.add(15, "서울특별시 중구 황학동 2545");
		mart_address.add(16, "서울특별시 강동구 명일동 46-4");
		mart_address.add(17, "서울특별시 구로구 구로동 3-25");
		mart_address.add(18, "서울특별시 도봉구 창동 135-26");
		mart_address.add(19, "서울특별시 광진구 자양동 227-7");
		mart_address.add(20, "서울특별시 중랑구 망우동 506-1");
		mart_address.add(21, "서울특별시 성북구 길음동 25-2");
		mart_address.add(22, "서울특별시 강서구 가양동 449-19");
		mart_address.add(23, "서울특별시 영등포구 여의도동 47");
		mart_address.add(24, "서울특별시 성동구 성수동2가 333-16");

		mart_address.add(25, "서울특별시 중랑구 묵동 188-10");
		mart_address.add(26, "서울특별시 강북구 미아동 318-5 성북프라자 1층");
		mart_address.add(27, "서울특별시 서초구 양재동 230");
		mart_address.add(28, "서울특별시 용산구 한강로2가 15-19 ");
		mart_address.add(29, "서울특별시 서초구 반포동 19-3");
		mart_address.add(30, "서울특별시 중구 충무로1가 52-5");
		mart_address.add(31, "서울특별시 성북구 길음동 20-1");
		mart_address.add(32, "서울특별시 서대문구 창천동 30-33");
		mart_address.add(33, "서울특별시 강남구 대치동 937");
		mart_address.add(34, "서울특별시 관악구 봉천동 729-22 ");
		mart_address.add(35, "서울특별시 노원구 상계2동 713");
		mart_address.add(36, "서울특별시 강북구 미아동 70-6");
		mart_address.add(37, "서울특별시 중구 소공동 1");
		mart_address.add(38, "서울특별시 영등포구 영등포동 618-496");
		mart_address.add(39, "서울특별시 송파구 잠실동 40-1");
		mart_address.add(40, "서울특별시 동대문구 전농동 591-53");

		longitude.add(0, 126.9695336); latitude.add(0, 37.555042);
		longitude.add(1, 127.0957088); latitude.add(1, 37.5349372);
		longitude.add(2, 126.8956692); latitude.add(2, 37.4704705);
		longitude.add(3, 127.1029989); latitude.add(3, 37.5162275);
		longitude.add(4, 127.1422439); latitude.add(4, 37.5456582);
		longitude.add(5, 127.0435994); latitude.add(5, 37.6648183);
		longitude.add(6, 127.0387578); latitude.add(6, 37.5745108);
		longitude.add(7, 127.0798833); latitude.add(7, 37.5808178);
		longitude.add(8, 126.8959156); latitude.add(8, 37.5181712);
		longitude.add(9, 126.9023778); latitude.add(9, 37.4657313);
		longitude.add(10, 126.9062253); latitude.add(10, 37.4523405);
		longitude.add(11, 127.0531674); latitude.add(11, 37.5399389);
		longitude.add(12, 126.9202568); latitude.add(12, 37.6003893);
		longitude.add(13, 126.8703272); latitude.add(13, 37.5257763);
		longitude.add(14, 126.9640447); latitude.add(14, 37.5288539);
		longitude.add(15, 127.0213284); latitude.add(15, 37.5708733);
		longitude.add(16, 127.1557517); latitude.add(16, 37.5546516);
		longitude.add(17, 126.8902185); latitude.add(17, 37.5070431);
		longitude.add(18, 127.046933); latitude.add(18, 37.6516821);
		longitude.add(19, 127.0726183); latitude.add(19, 37.5376127);
		longitude.add(20, 127.0935903); latitude.add(20, 37.596438);
		longitude.add(21, 127.0296051); latitude.add(21, 37.6108648);
		longitude.add(22, 126.8622978); latitude.add(22, 37.5578966);
		longitude.add(23, 126.9261297); latitude.add(23, 37.5181371);
		longitude.add(24, 127.0531674); latitude.add(24, 37.5399389);

		longitude.add(25, 127.0789505); latitude.add(25, 37.6057062);
		longitude.add(26, 127.0264632); latitude.add(26, 37.6215289);
		longitude.add(27, 127.0434134); latitude.add(27, 37.4629917);
		longitude.add(28, 126.9647209); latitude.add(28, 37.5331353);
		longitude.add(29, 127.0030692); latitude.add(29, 37.5041299);
		longitude.add(30, 126.9809889); latitude.add(30, 37.5609008);
		longitude.add(31, 127.0287626); latitude.add(31, 37.6083223);
		longitude.add(32, 126.9357977); latitude.add(32, 37.5560736);
		longitude.add(33, 127.0532731); latitude.add(33, 37.4969255);
		longitude.add(34, 126.9249911); latitude.add(34, 37.4905756);
		longitude.add(35, 127.0684857); latitude.add(35, 37.6587754);
		longitude.add(36, 127.0305098); latitude.add(36, 37.6145502);
		longitude.add(37, 126.981369); latitude.add(37, 37.5649903);
		longitude.add(38, 126.9066823); latitude.add(38, 37.5154709);
		longitude.add(39, 127.0980274); latitude.add(39, 37.5112348);
		longitude.add(40, 127.0479007); latitude.add(40, 37.5792928);

	}


	/**
	 * 지도 애니메이션 상태 변경 시 호출된다.
	 * animType : ANIMATION_TYPE_PAN or ANIMATION_TYPE_ZOOM
	 * animState : ANIMATION_STATE_STARTED or ANIMATION_STATE_FINISHED
	 */
	@Override
	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * 지도 중심 변경 시 호출되며 변경된 중심 좌표가 파라미터로 전달된다.
	 */
	@Override
	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 지도가 초기화된 후 호출된다.
	 * 정상적으로 초기화되면 errorInfo 객체는 null이 전달되며, 초기화 실패시 errorInfo객체에 에러 원인이 전달된다.
	 * 이 이벤트 핸들러에는 두개의 매서드가 전달 됨. 첫 째는 이벤트가 발생한 NMapView객체 자신이고 두번째는 에러 정보를 담고 있는 객체.
	 */
	@Override
	public void onMapInitHandler(NMapView mapview, NMapError errorInfo) {
		// TODO Auto-generated method stub
		if(errorInfo == null){ // success
			mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);
		} else { // fail
			android.util.Log.e("NMAP", "onMapInitHandler: error=" + errorInfo.toString());
		}
	}

	/**
	 * 지도 레벨 변경 시 호출되며 변경된 지도 레벨이 파라미터로 전달된다.
	 */
	@Override
	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public final String[] items = new String[] {"매장선택", "정보"};
	int index_1 = 0;
	/**
	 * 말풍선 터치피 이벤트
	 */
	@Override
	public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, final NMapPOIitem item) {  //final
		// TODO Auto-generated method stub
		//Toast.makeText(this, "OnCalloutClick: " + item.getTitle(), Toast.LENGTH_SHORT).show();

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		//dialog.setIcon(R.drawable.ic_launcher);
		if(item.getTitle().equals("현재"))
			return;
		
		dialog.setTitle(item.getTitle());
		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {//which==0 (매장선택) , which==1 ( 정보)
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "items : "+items[which], Toast.LENGTH_SHORT).show();

				if(which == 0){ // 매장선택 버튼 

					handler = MySQLiteHandler.open(getApplicationContext()); // 데이터베이스 핸들러

					for(int i=0; i < mart_name.size(); i++){
						if(item.getTitle().equals(mart_name.get(i))){
							Toast.makeText(MainActivity.this, item.getTitle() + " " + i, Toast.LENGTH_SHORT).show();
							index_1 = i;
							break;
						}else{
							//	Toast.makeText(MainActivity.this, "false!", Toast.LENGTH_SHORT).show();
						}
					}
					//Toast.makeText(MainActivity.this, "인덱스"+index_1, Toast.LENGTH_SHORT).show();


					c = handler.select(1);
					c.moveToFirst();

					if(c.getCount()!=0){

						Log.d("asdjlfjasklfjasdlfjsjfs", c.getString(1).toString());

						select_mart_name = c.getString(1).toString();

						Builder dlg = new AlertDialog.Builder(MainActivity.this);
						dlg.setTitle("선택")
						.setMessage(c.getString(1).toString() + " 에서 이전에 장 본 물품이 남아있습니다.\n" +
								"이번에 선택한 매장에서 장을 보시려면 예\n" +
								"이전에 선택한 매장에서 장을보시려면 아니오를 클릭하세요")
								.setPositiveButton(R.string.ok,	new DialogInterface.OnClickListener() {
									// 다이얼로그의 예 버튼이 눌리면 동작
									@Override
									public void onClick(DialogInterface dialog,	int whichButton) {
										handler.delete_table_1_4();

										Intent intent2 = new Intent(getApplicationContext(), Main.class);
										intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
										intent2.putExtra("select_mart_name", select_mart_name);
										startActivity(intent2);
										finish();
									}
								})
								// 다이얼로그의 아니오 버튼
								.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int whichButton) {
										// ignore, just dismiss
										// 눌렀을때 단순종료만 되며 다른 동작을 주고싶다면 여기에 추가시키면 됨

										Intent intent3 = new Intent(getApplicationContext(), Main.class);
										intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
										intent3.putExtra("select_mart_name", select_mart_name);
										Log.d("셀렉트메뉴에서 마트인텐트", select_mart_name);
										startActivity(intent3);
										finish();

									}
								}).show(); // 다이얼로그 마지막구문

					} else{
						Intent intent4 = new Intent(getApplicationContext(), Main.class);
						intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent4.putExtra("select_mart_name", mart_name.get(index_1));
						startActivity(intent4);
						finish();
					}

					handler.close();
				}else{ // 정보 버튼
					//Toast.makeText(MainActivity.this, "items : " + items[1], Toast.LENGTH_SHORT).show();

					for(int i=0; i < mart_name.size(); i++){
						if(item.getTitle().equals(mart_name.get(i))){
							Toast.makeText(MainActivity.this, item.getTitle() + " " + i, Toast.LENGTH_SHORT).show();
							index_1 = i;
							break;
						}else{
							//	Toast.makeText(MainActivity.this, "false!", Toast.LENGTH_SHORT).show();
						}
					}
					//Toast.makeText(MainActivity.this, "인덱스"+index_1, Toast.LENGTH_SHORT).show();

					Intent intent_1 = new Intent(MainActivity.this, Map_info.class);
					intent_1.putExtra("mobile_address", mart_mobile.get(index_1));
					intent_1.putExtra("mobile_name", mart_name.get(index_1));
					startActivity(intent_1);
					finish();
				}
			}
		});
		dialog.setNeutralButton("닫기", null);
		dialog.show();
	}

	/**
	 * 오버레이 선택 -> 다른 오버레이 선택 됐을 때 이벤트. 현재 좌표값 로그 호출
	 */
	@Override
	public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
		// TODO Auto-generated method stub
		if (item != null){
			Log.i("LOG_TAG", "onFocusChanged: " + item.toString());
		} else {
			Log.i("LOG_TAG", "onFocusChanged: " );
		}
	}

	public ArrayList<Double> latitude_lot = new ArrayList<Double>(); // 좌표 위도
	public ArrayList<Double> longitude_lot = new ArrayList<Double>(); // 좌표 경도
	public ArrayList<String> mart_address_lot = new ArrayList<String>(); // 주소
	public ArrayList<String> mart_name_lot = new ArrayList<String>(); // 마트이름
	public int row_count;


	public void CHECK01(){
		for(int i=0; i<row_count; i++){
			Log.e("CHECK01 마트이름", i +" "+ mart_name_lot.get(i));
			Log.e("CHECK01 주소", i +" "+ mart_address_lot.get(i));
			Log.e("CHECK01 위도/경도", i +" "+ latitude_lot.get(i)+"/"+longitude_lot.get(i));
		}
	}
	public void CHECK02(){
		for(int i=0; i<row_count; i++){
			Log.e("CHECK02", i +" "+ mart_address_lot.get(i));
		}
	}
	/**
	 *	주소를 받아와서 지오코딩을 하기 위한 클래스
	 *
	 *
	 * 파싱
	 */
	/*	public class XmlWork extends AsyncTask<String, Void, Document> {

		Document doc = null;


		GetAddress getAddress = new GetAddress();

		@Override
		public Document doInBackground(String... urls) {
			URL url;
			try {
				url = new URL(urls[0]);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); // XML문서 빌더 객체를 생성
				doc = db.parse(new InputSource(url.openStream())); // XML문서를 파싱한다.
				doc.getDocumentElement().normalize();
			} catch (Exception e) {
				Log.e("XmlWork", "Parsing Error");
				// Toast.makeText(getBaseContext(), "Parsing Error",
				// Toast.LENGTH_SHORT).show();
			}
			return doc;
		}

		@Override
		public void onPostExecute(Document doc) {
			Log.e("tag","xmlwork 시작");

			String s = "";
			String ss = "";

			NodeList nodeList = doc.getElementsByTagName("row"); 

			for(int i = 0; i< nodeList.getLength(); i++){

				s += "["+i+"]" ;
				Node node = nodeList.item(i); //data엘리먼트 노드    첫번째<row>
				Element fstElmnt = (Element) node;


				NodeList nameList  = fstElmnt.getElementsByTagName("M_MART_NAME"); //
				Element nameElement = (Element) nameList.item(0);
				nameList = nameElement.getChildNodes();
				s += "마트이름=" + ((Node) nameList.item(0)).getNodeValue() +" \n";



				NodeList m_name = fstElmnt.getElementsByTagName("M_MART_NAME");
				ss = m_name.item(0).getChildNodes().item(0).getNodeValue();
				mart_name_lot.add(i, ss);	// 마트 이름을 저장한다.



				NodeList m_address = fstElmnt.getElementsByTagName("M_MART_ADDR");
				ss = m_address.item(0).getChildNodes().item(0).getNodeValue();
				mart_address_lot.add(i, ss);	// 마트 주소를 저장한다.

				//	Log.e("지오코딩 전 ss", ss);
				latitude_lot.add(i, getAddress.getAddress_to_la(ss)); 	// 주소를 위도로 변환하여 저장.
				longitude_lot.add(i, getAddress.getAddress_to_lo(ss));  // 주소를 경도로 변환하여 저장. 
				//	Log.e("지오코딩후 좌표", latitude_lot.get(i).toString());

				row_count = nodeList.getLength();
			}

			loc01();
			//FUNCTION01();	// 마커표시 부분
			CHECK01();
			super.onPostExecute(doc);
		}
	}// end inner class - GetXMLTask


	 */

	// 현재위치
	@Override
	public void onLocationChanged(Location location) {	// 함수 호출
		// TODO Auto-generated method stub
		myLocation = location;
		getGeoLocation();
	}



	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

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
