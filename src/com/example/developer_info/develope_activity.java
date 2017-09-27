package com.example.developer_info;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.n_mart.R;

public class develope_activity extends Activity{
	
	TextView txtView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.develope_main);
		Log.i("MainActivity","onCreate");
		
		txtView = (TextView) findViewById(R.id.txtview);
		
	}
}
