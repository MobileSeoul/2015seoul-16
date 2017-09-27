package com.exam.zzz_other_menu_comp;

import com.exam.zzz_other_menu.*;

import android.app.*;
import android.content.*;
import android.os.*;

public class comp7 extends Activity {

	String name = "치토스";
	static int a = 1;
	static int price1 = 10000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀바 삭제

		Intent intent = new Intent(this, list.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("name", name);
		intent.putExtra("val", a);
		intent.putExtra("price2", price1);
		startActivity(intent);
		finish();
	}
}