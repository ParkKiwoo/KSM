package com.hardcopy.btchat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import java.util.logging.Handler;


public class Orientation extends Activity implements SensorEventListener {

    private TextView tv;
    private SensorManager sm;
    private Sensor s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orientation);

        tv = (TextView)findViewById(R.id.textView1);

        // 센서객체를 얻어오기 위해서는 센서메니저를 통해서만 가능하다
        sm = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // 방향센서
    } // end of onCreate

    @Override
    protected void onResume() { // 화면에 보이기 직전에 센서자원 획득
        super.onResume();
        // 센서의 값이 변경되었을 때 콜백 받기위한 리스너를 등록한다
        sm.registerListener(this,        // 콜백 받을 리스너
                s,            // 콜백 원하는 센서
                sm.SENSOR_DELAY_UI); // 지연시간

    }
    @Override
    protected void onPause() { // 화면을 빠져나가면 즉시 센서자원 반납해야함!!
        super.onPause();
        sm.unregisterListener(this); // 반납할 센서
    }

    public void onSensorChanged(SensorEvent event) {
        // 센서값이 변경되었을 때 호출되는 콜백 메서드
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 방향센서값이 변경된거라면
            String str = "현재방향센서값 \n\n"
                    +"\n방위각: "+event.values[0]
                    +"\n피치 : "+event.values[1]
                    +"\n롤 : "+event.values[2];
            tv.setText(str);

            if(event.values[2]>45){
                tv.setText(str+"\n좌회전~~~~~~");

            }
            if(event.values[2]<-45){
                tv.setText(str+"\n우회전~~~~~~");
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서의 정확도가 변경되었을 때 호출되는 콜백 메서드
    }

} // end of class
