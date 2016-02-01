package com.hardcopy.btchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DialogActivity extends Activity {

    LocationManager locationManager;
    int cnt=0;
    boolean flag= true;
    float latitude;
    float longitude;
    private TextView tv;
    private Button mConfirm;
    private Handler mHandler;
    private Runnable mRunnable;

            @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.alert);
                setContent();

    }

    @Override

    protected void onResume() {

        super.onResume();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        1000, 1, mLocationListener);
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 20000);

        }

    private void setContent() {
        tv= (TextView) findViewById(R.id.txtView);
        mConfirm = (Button) findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i("test", "onDstory()");
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
        locationManager.removeUpdates(mLocationListener);
    }
    private final LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

//여기서 위치값이 갱신되면 이벤트가 발생한다.

//값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {

//Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.

                latitude = (float) location.getLatitude();         //위도
                longitude = (float) location.getLongitude();       //경도

                final SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("01056113129", null,
                        "바이크 사고입니다." +
                                "http://maps.google.com/?q="+latitude+","+longitude, null, null);
               finish();

            }

            else {
//Network 위치제공자에 의한 위치변화

//Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            }
        }

        public void onProviderDisabled(String provider) {

        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };





}
