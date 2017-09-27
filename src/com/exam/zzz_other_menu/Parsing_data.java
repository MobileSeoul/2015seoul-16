package com.exam.zzz_other_menu;

import java.net.URL;

import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.database.Cursor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask;

import android.util.Log;

import android.view.Window;

import android.widget.Toast;

import com.exam.zzz_other_menu_mysql.MySQLiteHandler;
import com.example.n_mart.R;

public class Parsing_data extends Activity {

	public ArrayList<String> mart_name; // 마트이름 한글
	public ArrayList<String> mart_name_url; // 마트이름 URL
	static public ArrayList<Double> latitude; // 마트 위도
	static public ArrayList<Double> longitude; // 마트 경도

	public ArrayList<Info_save_class> item_list; // 이름과 가격을 저장하는 ArrayList
	//Info_save_class item; // 사용자정의클래스

	int l=0; // 소스코드에서 사용
	int m=0; // 작동하는지 안하는지 확인용

	Document doc = null;
	//LinearLayout layout = null;

	MySQLiteHandler handler; // 데이터베이스 사용하기 위한 handler
	Cursor c; // 데이터베이스 사용위해 테이블을 찾아줄 커서

	// 날짜를 저장해서 파싱할지 말지 정하는 것
	int year;
	int month;
	int date;
	int hour;
	int minute;
	int second;

	// 인터넷 연결이 되어있는지 확인하기 위한 변수
	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 삭제
		setContentView(R.layout.parsing_data);

		Log.v("Parsing_data.java","onCreate!!"); 
		
		cManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // 3g 또는 LTE 연결 되어있는지 확인하는 변수
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // WIFI 연결 되어있는지 확인하는 변수
		
		mart_name = new ArrayList<String>(); // 마트이름 한글
		mart_name_url = new ArrayList<String>(); // 마트이름 URL
		latitude = new ArrayList<Double>(); // 마트 위도
		longitude = new ArrayList<Double>(); // 마트 경도

		// 파싱이 되지 않을때도 로딩화면 보여주기 위한 핸들러
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {

			@Override
			public void run() {

				add_mart_name(); // 한글 마트 이름 ADD
				add_mart_name_url(); // URL 마트 이름 ADD
				add_mart_lat(); // 마트 위도
				add_mart_lon(); // 마트 경도

				item_list = new ArrayList<Info_save_class>(); // item_list 초기화
				//item = new Info_save_class(null, null, 0, 0, 0); // item 객체 생성 및 초기화

				handler = MySQLiteHandler.open(getApplicationContext());

				Calendar cal = Calendar.getInstance();

				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH);
				date = cal.get(Calendar.DATE);
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND);

				c = handler.select(9);
				c.moveToFirst();

				if((mobile.isConnected() || wifi.isConnected()) && (c.getCount()==0 || (c.getInt(1)!=year || c.getInt(2)!=month || c.getInt(3)!=date/* || c.getInt(4)!=hour || c.getInt(5)!=minute*/))){
					Log.v("Parsing_data.java", "XML파싱 하는 부분입니다");
					for(int i=0; i<40; i++){
						FUNCTION();
						m++;
					}
				}
				else {
					Log.v("Parsing_data.java", "XML파싱 건너 뛰어버림....?");
					Intent intent = new Intent(getApplicationContext(), SelectMenu.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			}
		}, 3000);
	}
	
	public void add_mart_lat(){
		Log.v("Parsing_data.java", "마트의 위도를 저장하는 함수입니다");
		
		latitude.add(0, 37.5349372); // 롯데마트 강변점
		latitude.add(1, 37.4704705); // 롯데마트 금천점
		latitude.add(2, 37.555042);  // 롯데마트 서울역점
		
		latitude.add(3, 37.5578966); // 이마트 가양점
		latitude.add(4, 37.5546516); // 이마트 명일점
		latitude.add(5, 37.5257763); // 이마트 목동점
		latitude.add(6, 37.6108648); // 이마트 미아점
		latitude.add(7, 37.596438); // 이마트 상봉점
		latitude.add(8, 37.5399389); // 이마트 성수점
		latitude.add(9, 37.5070431); // 이마트 신도림점
		latitude.add(10, 37.5181371); // 이마트 여의도점
		latitude.add(11, 37.5399389); // 이마트 왕십리점
		latitude.add(12, 37.5288539); // 이마트 용산점
		latitude.add(13, 37.6003893); // 이마트 은평점
		latitude.add(14, 37.5376127); // 이마트 자양점
		latitude.add(15, 37.6516821); // 이마트 창동점
		latitude.add(16, 37.5708733); // 이마트 청계점
		
		latitude.add(17, 37.5456582); // 홈플러스 강동점
		latitude.add(18, 37.5745108); // 홈플러스 동대문점
		latitude.add(19, 37.5808178); // 홈플러스 면목점
		latitude.add(20, 37.6648183); // 홈플러스 방학점
		latitude.add(21, 37.4523405); // 홈플러스 시흥점
		latitude.add(22, 37.5181712); // 홈플러스 영등포점
		latitude.add(23, 37.5162275); // 홈플러스 잠실점
		
		latitude.add(24, 37.6057062); // 하나로마트 목동점
		latitude.add(25, 37.6215289); // 하나로마트 미아점
		latitude.add(26, 37.4629917); // 하나로마트 양재점
		latitude.add(27, 37.5331353); // 하나로마트 용산점

		latitude.add(28, 37.5041299); // 신세계백화점 강남점
		latitude.add(29, 37.5609008); // 신세계백화점 본점

		latitude.add(30, 37.6083223); // 현대백화점 미아점
		latitude.add(31, 37.5560736); // 현대백화점 신촌점

		latitude.add(32, 37.4969255); // 롯데백화점 강남점
		latitude.add(33, 37.4905756); // 롯데백화점 관악점
		latitude.add(34, 37.6587754); // 롯데백화점 노원점
		latitude.add(35, 37.6145502); // 롯데백화점 미아점
		latitude.add(36, 37.5649903); // 롯데백화점 본점
		latitude.add(37, 37.5154709); // 롯데백화점 영등포점
		latitude.add(38, 37.5112348); // 롯데백화점 잠실점
		latitude.add(39, 37.5792928); // 롯데백화점 청량리점
	}
	
	public void add_mart_lon(){
		Log.v("Parsing_data.java", "마트의 경도를 저장하는 함수입니다");
		
		longitude.add(0, 127.0957088); // 롯데마트 강변점
		longitude.add(1, 126.8956692); // 롯데마트 금천점
		longitude.add(2, 126.9695336);  // 롯데마트 서울역점
		
		longitude.add(3, 126.8622978); // 이마트 가양점
		longitude.add(4, 127.1557517); // 이마트 명일점
		longitude.add(5, 126.8703272); // 이마트 목동점
		longitude.add(6, 127.0296051); // 이마트 미아점
		longitude.add(7, 127.0935903); // 이마트 상봉점
		longitude.add(8, 127.0531674); // 이마트 성수점
		longitude.add(9, 126.8902185); // 이마트 신도림점
		longitude.add(10, 126.9261297); // 이마트 여의도점
		longitude.add(11, 127.0531674); // 이마트 왕십리점
		longitude.add(12, 126.9640447); // 이마트 용산점
		longitude.add(13, 126.9202568); // 이마트 은평점
		longitude.add(14, 127.0726183); // 이마트 자양점
		longitude.add(15, 127.046933); // 이마트 창동점
		longitude.add(16, 127.0213284); // 이마트 청계점
		
		longitude.add(17, 127.1422439); // 홈플러스 강동점
		longitude.add(18, 127.0387578); // 홈플러스 동대문점
		longitude.add(19, 127.0798833); // 홈플러스 면목점
		longitude.add(20, 127.0435994); // 홈플러스 방학점
		longitude.add(21, 126.9062253); // 홈플러스 시흥점
		longitude.add(22, 126.8959156); // 홈플러스 영등포점
		longitude.add(23, 127.1029989); // 홈플러스 잠실점
		
		longitude.add(24, 127.0789505); // 하나로마트 목동점
		longitude.add(25, 127.0264632); // 하나로마트 미아점
		longitude.add(26, 127.0434134); // 하나로마트 양재점
		longitude.add(27, 126.9647209); // 하나로마트 용산점

		longitude.add(28, 127.0030692); // 신세계백화점 강남점
		longitude.add(29, 126.9809889); // 신세계백화점 본점

		longitude.add(30, 127.0287626); // 현대백화점 미아점
		longitude.add(31, 126.9357977); // 현대백화점 신촌점

		longitude.add(32, 127.0532731); // 롯데백화점 강남점
		longitude.add(33, 126.9249911); // 롯데백화점 관악점
		longitude.add(34, 127.0684857); // 롯데백화점 노원점
		longitude.add(35, 127.0305098); // 롯데백화점 미아점
		longitude.add(36, 126.981369); // 롯데백화점 본점
		longitude.add(37, 126.9066823); // 롯데백화점 영등포점
		longitude.add(38, 127.0980274); // 롯데백화점 잠실점
		longitude.add(39, 127.0479007); // 롯데백화점 청량리점
	}

	public void add_mart_name(){
		Log.v("Parsing_data.java", "마트이름을 한글로 저장하는 함수입니다");
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
	}

	public void add_mart_name_url(){
		Log.v("Parsing_data.java", "마트이름을 URL로 저장하는 부분입니다");
		mart_name_url.add(0, "%EB%A1%AF%EB%8D%B0%EB%A7%88%ED%8A%B8%EA%B0%95%EB%B3%80%EC%A0%90");
		mart_name_url.add(1, "%EB%A1%AF%EB%8D%B0%EB%A7%88%ED%8A%B8%EA%B8%88%EC%B2%9C%EC%A0%90");
		mart_name_url.add(2, "%EB%A1%AF%EB%8D%B0%EB%A7%88%ED%8A%B8%EC%84%9C%EC%9A%B8%EC%97%AD%EC%A0%90");

		mart_name_url.add(3, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EA%B0%80%EC%96%91%EC%A0%90");
		mart_name_url.add(4, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EB%AA%85%EC%9D%BC%EC%A0%90");
		mart_name_url.add(5, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EB%AA%A9%EB%8F%99%EC%A0%90");
		mart_name_url.add(6, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EB%AF%B8%EC%95%84%EC%A0%90");
		mart_name_url.add(7, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%83%81%EB%B4%89%EC%A0%90");
		mart_name_url.add(8, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%84%B1%EC%88%98%EC%A0%90");
		mart_name_url.add(9, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%8B%A0%EB%8F%84%EB%A6%BC%EC%A0%90");
		mart_name_url.add(10, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%97%AC%EC%9D%98%EB%8F%84%EC%A0%90");
		mart_name_url.add(11, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%99%95%EC%8B%AD%EB%A6%AC%EC%A0%90");
		mart_name_url.add(12, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EB%AA%A9%EB%8F%99%EC%A0%90");      
		mart_name_url.add(13, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%9D%80%ED%8F%89%EC%A0%90");
		mart_name_url.add(14, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%9E%90%EC%96%91%EC%A0%90");
		mart_name_url.add(15, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%B0%BD%EB%8F%99%EC%A0%90");
		mart_name_url.add(16, "%EC%9D%B4%EB%A7%88%ED%8A%B8%EC%B2%AD%EA%B3%84%EC%A0%90");

		mart_name_url.add(17, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EA%B0%95%EB%8F%99%EC%A0%90");
		mart_name_url.add(18, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EB%8F%99%EB%8C%80%EB%AC%B8%EC%A0%90");
		mart_name_url.add(19, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EB%A9%B4%EB%AA%A9%EC%A0%90");
		mart_name_url.add(20, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EB%B0%A9%ED%95%99%EC%A0%90");
		mart_name_url.add(21, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EC%8B%9C%ED%9D%A5%EC%A0%90");
		mart_name_url.add(22, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EC%98%81%EB%93%B1%ED%8F%AC%EC%A0%90");
		mart_name_url.add(23, "%ED%99%88%ED%94%8C%EB%9F%AC%EC%8A%A4%EC%9E%A0%EC%8B%A4%EC%A0%90");   

		mart_name_url.add(24, "%ED%96%89%EB%B3%B5%ED%95%9C%EC%84%B8%EC%83%81%EB%AA%A9%EB%8F%99%EC%A0%90%28%ED%95%98%EB%82%98%EB%A1%9C%EB%A7%88%ED%8A%B8%29");
		mart_name_url.add(25, "%ED%95%98%EB%82%98%EB%A1%9C%ED%81%B4%EB%9F%BD%EB%AF%B8%EC%95%84%EC%A0%90");
		mart_name_url.add(26, "%ED%95%98%EB%82%98%EB%A1%9C%ED%81%B4%EB%9F%BD%EC%96%91%EC%9E%AC%EC%A0%90");
		mart_name_url.add(27, "%EB%86%8D%ED%98%91%ED%95%98%EB%82%98%EB%A1%9C%EB%A7%88%ED%8A%B8%EC%9A%A9%EC%82%B0%EC%A0%90");

		mart_name_url.add(28, "%EC%8B%A0%EC%84%B8%EA%B3%84%EB%B0%B1%ED%99%94%EC%A0%90%EA%B0%95%EB%82%A8%EC%A0%90");
		mart_name_url.add(29, "%EC%8B%A0%EC%84%B8%EA%B3%84%EB%B0%B1%ED%99%94%EC%A0%90");

		mart_name_url.add(30, "%ED%98%84%EB%8C%80%EB%B0%B1%ED%99%94%EC%A0%90%EB%AF%B8%EC%95%84%EC%A0%90");
		mart_name_url.add(31, "%ED%98%84%EB%8C%80%EB%B0%B1%ED%99%94%EC%A0%90%EC%8B%A0%EC%B4%8C%EC%A0%90");
		
		mart_name_url.add(32, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EA%B0%95%EB%82%A8%EC%A0%90");
		mart_name_url.add(33, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EA%B4%80%EC%95%85%EC%A0%90");
		mart_name_url.add(34, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EB%85%B8%EC%9B%90%EC%A0%90");
		mart_name_url.add(35, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EB%AF%B8%EC%95%84%EC%A0%90");
		mart_name_url.add(36, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90");
		mart_name_url.add(37, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EC%98%81%EB%93%B1%ED%8F%AC%EC%A0%90");
		mart_name_url.add(38, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EC%9E%A0%EC%8B%A4%EC%A0%90");
		mart_name_url.add(39, "%EB%A1%AF%EB%8D%B0%EB%B0%B1%ED%99%94%EC%A0%90%EC%B2%AD%EB%9F%89%EB%A6%AC%EC%A0%90");
}

	public void FUNCTION(){
		Log.v("Parsing_data.java", "XML파싱하도록 요청하는 부분입니다");

		GetXMLTask task = new GetXMLTask();
		task.execute("http://openapi.seoul.go.kr:8088/6b616e673530323632313333/xml/ListNecessariesPricesService/1/16/" +
				mart_name_url.get(m));
	}

	//private inner class extending AsyncTask
	private class GetXMLTask extends AsyncTask<String, Void, Document>{   

		@Override
		protected Document doInBackground(String... urls) {
			Log.v("Parsing_data.java", "이곳에서 파싱이 일어납니다");
			URL url;
			try {
				url = new URL(urls[0]);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
				doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
				doc.getDocumentElement().normalize();

			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
			}
			return doc;
		}

		@Override
		protected void onPostExecute(Document doc) {

			Log.v("Parsing_data.java", "여기서 파싱 결과를 변수에 저장하고 다음 액티비티로 넘겨주는 부분입니다");

			String s = "";
			String ss = "";
			String sss = "";

			NodeList nodeList = doc.getElementsByTagName("row"); 

			//int count = 0;

			for(int i = 0; i< nodeList.getLength(); i++){

				s += "["+i+"]" ;
				Node node = nodeList.item(i); //data엘리먼트 노드    첫번째<row>
				Element fstElmnt = (Element) node;

				NodeList nameList  = fstElmnt.getElementsByTagName("M_NAME"); //
				Element nameElement = (Element) nameList.item(0);
				nameList = nameElement.getChildNodes();
				s += "마트이름=" + ((Node) nameList.item(0)).getNodeValue() +" \n";

				NodeList i_name = fstElmnt.getElementsByTagName("A_NAME");
				ss = i_name.item(0).getChildNodes().item(0).getNodeValue();

				NodeList i_price = fstElmnt.getElementsByTagName("A_PRICE");
				sss = i_price.item(0).getChildNodes().item(0).getNodeValue();

			//	Log.e("파싱숫자검사전", "l="+Integer.toString(l));
			//	Log.e("마트이름검사", mart_name.get(l));
				item_list.add(new Info_save_class(mart_name.get(l), ss, Integer.parseInt(sss), latitude.get(l), longitude.get(l)));
			//	Log.e("파싱데이터검사", ss);
			//	Log.e("파싱숫자검사", Integer.toString(count));
				//count++;
			}

			l++;
		//	Log.e("파싱숫자검사후", "l="+Integer.toString(l));

			Log.e("asdfjkladsfjldksafjkldsajfdlka", toString().valueOf(item_list.size()) + " " + toString().valueOf(mart_name.size()*nodeList.getLength()));
			if(item_list.size()==((mart_name.size())*nodeList.getLength())) {

				Log.v("Pasing_data.java", "다음 액티비티로 넘어갑니다");
				Log.v("인텐트때의 숫자m", Integer.toString(m));
				Log.v("인텐트때의 숫자node", Integer.toString(nodeList.getLength()));
				Intent intent = new Intent(getApplicationContext(), info.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("item_list", item_list);
				intent.putExtra("length", item_list.size());
				intent.putExtra("item_length", nodeList.getLength());
				startActivity(intent);
				finish();
			}
			super.onPostExecute(doc);
		}
	}//end inner class - GetXMLTask
}