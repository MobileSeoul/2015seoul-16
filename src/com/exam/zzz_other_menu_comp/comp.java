package com.exam.zzz_other_menu_comp;

import com.exam.zzz_other_menu.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class comp extends Activity {

	String name = "»ç°ú";
	static int a = 1;
	static int price1 = 600;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent(this, list.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("name", name);
		intent.putExtra("val", a);
		intent.putExtra("price2", price1);
		startActivity(intent);
		finish();
	}
}