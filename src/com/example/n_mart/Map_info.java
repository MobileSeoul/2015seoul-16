package com.example.n_mart;

import com.exam.zzz_other_menu.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class Map_info extends Activity {


	int i = 0;

	@Override
	public void onBackPressed() { // �빖�뱶�룿 �뮘濡� 踰꾪듉
		// TODO Auto-generated method stub
		//super.onBackPressed(); // 二쇱꽍泥섎━�븞�븯硫� �븞�뱶濡쒖씠�뱶 湲곕낯湲곕뒫 �맖. 

		if(i==1){
			i=0;
			super.onBackPressed();
		}else{
			if(mWebView.canGoBack()){
				mWebView.goBack();
			}
		}


		//Toast.makeText(this, "痍⑥냼踰꾪듉", Toast.LENGTH_SHORT).show();
		if(mWebView.canGoBack()){
			mWebView.goBack();
		}
	}

	WebView mWebView;
	Button btn_1, btn_back, btn_forward, btn_2;

	String mart_name;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // �봽濡쒓렇�옒�뒪諛�
		//	requestWindowFeature(Window.FEATURE_NO_TITLE); // �븸�뀡諛� 鍮꾪솢�꽦

		setContentView(R.layout.activity_map_info);

		btn_1 = (Button)findViewById(R.id.btn_1);
		btn_back = (Button)findViewById(R.id.btnback);
		btn_forward = (Button)findViewById(R.id.btnforward);
		btn_2 = (Button)findViewById(R.id.btn_2);

		mWebView = (WebView)findViewById(R.id.webview);
		mWebView.setWebViewClient(new HelloWebViewClient());

		WebSettings set = mWebView.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);

		Intent intent_1 = getIntent();
		String url_1 = intent_1.getExtras().getString("mobile_address").toString();
		mart_name = intent_1.getExtras().getString("mobile_name").toString();
		Toast.makeText(this, url_1, Toast.LENGTH_SHORT).show();

		//String url = "http://www.naver.com";
		if(url_1 != null && url_1.length() > 0){
			mWebView.loadUrl(url_1); 
		}else{
			String data = "<html><body><center><h2> EoN^^</h2> <br>";
			mWebView.loadData(data, "text/html", null);
		} 

		btn_1.setOnClickListener(new OnClickListener() { // �젙蹂� �럹�씠吏��뿉�꽌 踰꾪듉 �겢由� �떆

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(Map_info.this, "�빞�샇!", Toast.LENGTH_LONG).show();

				Intent intent = new Intent(getApplicationContext(), Main.class); // 占썲볼 占쏙옙占� 占쏙옙占쏙옙占쌍댐옙 list.java占쏙옙占쏙옙 占쏙옙占쏙옙트 占쏙옙占쏙옙
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 占쏙옙占쏙옙트 占쏙옙 占쏙옙티占쏙옙티 占쏙옙占쏙옙 占쏙옙占쏙옙
				intent.putExtra("select_mart_name", mart_name);
				startActivity(intent); // 占쏙옙占쏙옙트 占쏙옙占쏙옙
				finish(); // 占쏙옙占쏙옙트 占쏙옙 占쏙옙티占쏙옙티 占쏙옙占쏙옙

			}
		});

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mWebView.canGoBack()){
					mWebView.goBack();
				}
			}
		});

		btn_forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mWebView.canGoForward()){
					mWebView.goForward();
				}

			}
		});

		btn_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(Map_info.this, "지도로 돌아갑니다", Toast.LENGTH_SHORT).show();

				i = 1;
//				onBackPressed();
				Intent intent = new Intent(getApplicationContext(), MainActivity.class); // 占썲볼 占쏙옙占� 占쏙옙占쏙옙占쌍댐옙 list.java占쏙옙占쏙옙 占쏙옙占쏙옙트 占쏙옙占쏙옙
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP); // 占쏙옙占쏙옙트 占쏙옙 占쏙옙티占쏙옙티 占쏙옙占쏙옙 占쏙옙占쏙옙
				startActivity(intent); // 占쏙옙占쏙옙트 占쏙옙占쏙옙
				finish(); // 占쏙옙占쏙옙트 占쏙옙 占쏙옙티占쏙옙티 占쏙옙占쏙옙

			}
		});

		/*@Override
		public boolean onCreateOptionsMenu(Menu menu){
			getMenuInflater().inflate(R.menu.activity_main, menu);
			return true;
		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event){
			if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
				mWebView.goBack();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}*/
	}



	private class HelloWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}     

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon){
			super.onPageStarted(view, url, favicon);

			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onPageFinished(WebView view, String url){

			setProgressBarIndeterminateVisibility(false);
		}
	}
}





