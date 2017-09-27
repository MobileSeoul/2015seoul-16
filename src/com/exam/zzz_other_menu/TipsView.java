package com.exam.zzz_other_menu;

import java.util.ArrayList;

import android.content.res.Resources;
import android.content.Context;

import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap;

import android.util.AttributeSet;
import android.util.Log;

import android.view.View;
import android.view.MotionEvent;

import com.example.n_mart.R;

//View 클래스를 상속받은 사용자 정의 뷰
public class TipsView extends View
{
	public Paint m_paint = null;	 // 원을 그릴 때 사용할 속성 paint
	public float m_x = -1, m_y = -1;	 // 원을 그릴 좌표를 저장할 변수
	Paint paint = new Paint();

	private ArrayList<bitdata> image;
	private ArrayList<bitdata> image2;

	public TipsView(Context c, AttributeSet attrs){	    // XML 리소스 파일로 뷰가 생성되면 호출되는 생성자
		super(c, attrs);
		Log.e("hjs", "create TipsView by XML");

		m_paint = new Paint();		// 빨강색 속성을 가진 Paint 객체 생성
		m_paint.setAntiAlias(true);
		m_paint.setColor(Color.RED);

		image = new ArrayList<bitdata>();
		image2 = new ArrayList<bitdata>();
		
		Resources r = c.getResources();

		image.add(new bitdata("사과", BitmapFactory.decodeResource(r, R.drawable.apple_notbuy)));
		image.add(new bitdata("배", BitmapFactory.decodeResource(r, R.drawable.pear_notbuy)));
		image.add(new bitdata("배추", BitmapFactory.decodeResource(r, R.drawable.bechoo_notbuy)));
		image.add(new bitdata("무", BitmapFactory.decodeResource(r, R.drawable.moo_notbuy)));
		image.add(new bitdata("양파", BitmapFactory.decodeResource(r, R.drawable.onion_notbuy)));
		image.add(new bitdata("상추", BitmapFactory.decodeResource(r, R.drawable.sangchoo_notbuy)));
		image.add(new bitdata("오이", BitmapFactory.decodeResource(r, R.drawable.oe_notbuy)));
		image.add(new bitdata("호박", BitmapFactory.decodeResource(r, R.drawable.pumpkin_notbuy)));
		image.add(new bitdata("쇠고기", BitmapFactory.decodeResource(r, R.drawable.beef_notbuy)));
		image.add(new bitdata("돼지고기", BitmapFactory.decodeResource(r, R.drawable.pork_notbuy)));
		image.add(new bitdata("닭고기", BitmapFactory.decodeResource(r, R.drawable.chicken_notbuy)));
		image.add(new bitdata("달걀", BitmapFactory.decodeResource(r, R.drawable.egg_notbuy)));
		image.add(new bitdata("조기", BitmapFactory.decodeResource(r, R.drawable.jogi_notbuy)));
		image.add(new bitdata("명태", BitmapFactory.decodeResource(r, R.drawable.myungtae_notbuy)));
		image.add(new bitdata("동태", BitmapFactory.decodeResource(r, R.drawable.myungtae_notbuy)));
		image.add(new bitdata("오징어", BitmapFactory.decodeResource(r, R.drawable.ojing_notbuy)));
		image.add(new bitdata("고등어", BitmapFactory.decodeResource(r, R.drawable.gofish_notbuy)));

		image2.add(new bitdata("사과", BitmapFactory.decodeResource(r, R.drawable.apple_buy)));
		image2.add(new bitdata("배", BitmapFactory.decodeResource(r, R.drawable.pear_buy)));
		image2.add(new bitdata("배추", BitmapFactory.decodeResource(r, R.drawable.bechoo_buy)));
		image2.add(new bitdata("무", BitmapFactory.decodeResource(r, R.drawable.moo_buy)));
		image2.add(new bitdata("양파", BitmapFactory.decodeResource(r, R.drawable.onion_buy)));
		image2.add(new bitdata("상추", BitmapFactory.decodeResource(r, R.drawable.sangchoo_buy)));
		image2.add(new bitdata("오이", BitmapFactory.decodeResource(r, R.drawable.oe_buy)));
		image2.add(new bitdata("호박", BitmapFactory.decodeResource(r, R.drawable.pumpkin_buy)));
		image2.add(new bitdata("쇠고기", BitmapFactory.decodeResource(r, R.drawable.beef_buy)));
		image2.add(new bitdata("돼지고기", BitmapFactory.decodeResource(r, R.drawable.pork_buy)));
		image2.add(new bitdata("닭고기", BitmapFactory.decodeResource(r, R.drawable.chicken_buy)));
		image2.add(new bitdata("달걀", BitmapFactory.decodeResource(r, R.drawable.egg_buy)));
		image2.add(new bitdata("조기", BitmapFactory.decodeResource(r, R.drawable.jogi_buy)));
		image2.add(new bitdata("명태", BitmapFactory.decodeResource(r, R.drawable.myungtae_buy)));
		image2.add(new bitdata("동태", BitmapFactory.decodeResource(r, R.drawable.myungtae_buy)));
		image2.add(new bitdata("오징어", BitmapFactory.decodeResource(r, R.drawable.ojing_buy)));
		image2.add(new bitdata("고등어", BitmapFactory.decodeResource(r, R.drawable.gofish_buy)));
	}
	
	public void onDraw(Canvas canvas) {		// 뷰에 그림그리는 행위를 담당하는 메소드	
		Log.e("TipsView","onDraw() IN");

		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);

		Log.e("TipsView","onDraw() IN2");
/*		// 터치 행위가 발생한 경우 해당 위치에 원을 그린다.
		if(m_x > 0 && m_y > 0) {
			// (x - 5, y - 5) 를 시작으로 지름이 10인 원을 그린다.
			canvas.drawCircle(m_x - 5, m_y - 5, 10, m_paint);
		}*/

		// 기본 셋팅
		Log.e("TipsView","onDraw() IN3");
		for(int i=0; i<list.itemstate.size(); i++){
			if(list.itemstate.get(i) == 0){
				double x = list.itemlist_x.get(i);
				double y = list.itemlist_y.get(i);
				String name = list.itemlist.get(i);
				DRAW(canvas, name, x, y);
			}
		}
		Log.e("TipsView","onDraw() IN9");
		for(int i=0; i<list.itemstate.size(); i++){
			if(list.itemstate.get(i) == 1){
				double x = list.itemlist_x.get(i);
				double y = list.itemlist_y.get(i);
				String name = list.itemlist.get(i);
				DRAW2(canvas, name, x, y);
			}
		}

		Log.e("TipsView","onDraw() OUT");
	}

	public void DRAW(Canvas canvas, String name, double x, double y){

		Log.e("TipsView_DRAW","IN");

		for(int i=0; i<image.size(); i++){
			if(name.contains(image.get(i).getName())){
				if(image.get(i).getName().contains("배추")){
					canvas.drawBitmap(image.get(i).getBitmap(), (float) x, (float) y, null);
				} else {
					canvas.drawBitmap(image.get(i).getBitmap(), (float) x, (float) y, null);
				}
			}
		}
	}
	public void DRAW2(Canvas canvas, String name, double x, double y){

		Log.e("TipsView_DRAW2","IN");

		for(int i=0; i<image2.size(); i++){
			if(name.contains(image2.get(i).getName())){
				if(image2.get(i).getName().contains("배추")){
					canvas.drawBitmap(image2.get(i).getBitmap(), (float) x, (float) y, null);
				} else {
					canvas.drawBitmap(image2.get(i).getBitmap(), (float) x, (float) y, null);
				}
			}
		}
	}

	public void DRAW3(Canvas canvas, int n){ // 격자 만들기 함수. n은 격자간격
		Log.e("TipsView_DRAW3","IN");
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		for(int i=0; i<600; i+=n){
			canvas.drawLine(0, i, 500, i, paint);
			canvas.drawLine(i, 0, i, 400, paint);
		}
	}
	
	// 터치 이벤트를 처리하는 콜백 메소드
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("터치이벤트","IN");
		// 상위 클래스인 View 클래스에 발생한 이벤트를 전달한다.
		super.onTouchEvent(event);

		// 어떤 이벤트가 발생하였는지에 따라 처리가 달라진다.
		switch (event.getAction()) {
		// DOWN 이나 MOVE 가 발생한 경우
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			// 터치가 발생한 X, Y 의 각 좌표를 얻는다.
			m_x = event.getX();
			m_y = event.getY();
			Log.e("hjs", Float.toString(m_x) + "  " + Float.toString(m_y));
			Log.e("hjs", "onTouchEvent");
			// 뷰를 갱신한다.
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			// UP 이 발생한 경우 그리기 좌표에 -1 을 저장하고 뷰를 갱신한다.
			m_y = m_x = -1; 
			invalidate();
			break;

		}
		// true 를 반환하여 더이상의 이벤트 처리가 이루어지지 않도록 이벤트 처리가 완료한다.
		return true;
	}
}

class bitdata{

	private String name;
	private Bitmap bit;

	public bitdata(String pname, Bitmap pbit){
		this.name = pname;
		this.bit = pbit;
	}

	public String getName(){
		return name;
	}

	public Bitmap getBitmap(){
		return bit;
	}
}