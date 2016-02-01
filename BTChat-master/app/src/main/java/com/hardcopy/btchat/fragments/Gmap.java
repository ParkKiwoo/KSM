package com.hardcopy.btchat.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hardcopy.btchat.MainPage;
import com.hardcopy.btchat.R;


public class Gmap extends FragmentActivity  implements OnMyLocationChangeListener {

    GoogleMap mGoogleMap;
    LatLng loc = new LatLng(37.57778,126.979187); // 위치 좌표 설정
    CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(10).build();
    MarkerOptions marker = new MarkerOptions().position(loc); // 구글맵에 기본마커 표시
    Marker m;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex);

        mGoogleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        // 화면에 구글맵 표시
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // 지정위치로 이동
        mGoogleMap.addMarker(marker); // 지정위치에 마커 추가


        // Enabling MyLocation Layer of Google Map
        mGoogleMap.setMyLocationEnabled(true);
        // Setting event handler for location change
        mGoogleMap.setOnMyLocationChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMyLocationChange(Location location) {
        // TODO Auto-generated method stub
        // 현재 위도
        double latitude = location.getLatitude();

        // 현재 경도
        double longitude = location.getLongitude();

        // latLng변수에 현재 위도, 경도를 저장
        loc = new LatLng(latitude, longitude);

        // 현재 위치로 구글맵 이동
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // 확대 및 축소(Zoom)
        // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));



        //마커,타이틀, 스니핏 표시
        if(m!=null){
            m.remove(); //기존마커지우기
        }
        cp = new CameraPosition.Builder().target((loc)).zoom(19).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        marker=new MarkerOptions().position(loc).title("현재위치");
        m = mGoogleMap.addMarker(marker);


    }



}