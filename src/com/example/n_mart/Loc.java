package com.example.n_mart;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class Loc extends Activity implements LocationListener{

	public LocationManager locManager;
	public Location myLocation = null;
	public double latPoint, lngPoint;

	
	
	public void Start_(){

		// LocationListener 핸들
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// GPS로 부터 위치 정보를 업데이트 요청
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		// 기지국으로부터 위치 정보를 업데이트 요청
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
	}
	
	 public void getGeoLocation() {
		 
		 
		   if(myLocation != null) {
		    latPoint = myLocation.getLatitude();
		    lngPoint = myLocation.getLongitude();
		    
		    Log.d("didididididi", ""+latPoint+" "+lngPoint);
		   }
		   
		  }

	@Override
	public void onLocationChanged(Location location) {
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

}
