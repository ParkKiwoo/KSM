package com.hardcopy.btchat.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.hardcopy.btchat.DialogActivity;
import com.hardcopy.btchat.GpsInfo;
import com.hardcopy.btchat.MainPage;

import java.util.List;

public class KSMService extends Service implements SensorEventListener {

    public static boolean sosSend = false;

    boolean lflag = true, rflag = true;
    private SensorManager sm;
    private Sensor s;

    // 서비스 생성시 1번만 실행
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "Service 가 실행되었습니다.", Toast.LENGTH_SHORT).show();
        sosSend = false;
        // 센서객체를 얻어오기 위해서는 센서메니저를 통해서만 가능하다
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // 방향센서
    }


    // 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
    public int onStartCommand(Intent intent, int flags, int startId) {
        sm.registerListener(this,        // 콜백 받을 리스너
                s,            // 콜백 원하는 센서
                sm.SENSOR_DELAY_NORMAL); // 지연시간

        return START_STICKY;

    }




    // 서비스가 종료될때 실행
    public void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(this);
        Toast.makeText(getBaseContext(), "Service 가 종료되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onSensorChanged(SensorEvent event) {
        // 센서값이 변경되었을 때 호출되는 콜백 메서드
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 방향센서값이 변경된거라면

            if(sosSend){
                MainPage.startBtn.setEnabled(true);
                MainPage.stopBtn.setEnabled(false);
                MainPage.connectSW.setEnabled(false);
                MainPage.ledSW.setEnabled(false);
                MainPage.sosSW.setEnabled(false);
                MainPage.gpsSW.setEnabled(false);
                MainPage.weatherSW.setEnabled(false);
                stopSelf();
            }

            else if (MainPage.messageFlag && (event.values[2] > 70 || event.values[2] < -70)) {
                MainPage.messageFlag = false;
                Log.e("sos", event.values[2]+"");
                Log.e("flag", MainPage.messageFlag+"");
                // 4은 양쪽 LED
                Context context = getApplicationContext();
                Intent intent = new Intent(context, DialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            else if (event.values[2] < -35 && event.values[2] >= -55 && rflag && MainPage.messageFlag) {
                Log.e("right", event.values[2] + "");
                MainPage.mService.sendMessageToRemote("2"); //2는 오른쪽 LED
                rflag = false;
            }

            else if (event.values[2] > 35 && event.values[2] <= 55 && lflag && MainPage.messageFlag) {
                Log.e("left", event.values[2] + "");
                MainPage.mService.sendMessageToRemote("1"); //1은 왼쪽 LED
                lflag = false;
            }

            else if(event.values[2] <= 35 && event.values[2] >= -35 && MainPage.messageFlag){
                Log.e("stop", event.values[2]+"");
                MainPage.mService.sendMessageToRemote("3");
                rflag = true;
                lflag = true;
            }

            else{
                MainPage.mService.sendMessageToRemote("5");
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서의 정확도가 변경되었을 때 호출되는 콜백 메서드
    }

}

