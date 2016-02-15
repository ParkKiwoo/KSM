package com.hardcopy.btchat;

import android.app.Activity;
import android.app.TabActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import com.hardcopy.btchat.fragments.ExampleFragment;
import com.hardcopy.btchat.fragments.FragmentAdapter;
import com.hardcopy.btchat.fragments.Gmap;
import com.hardcopy.btchat.service.BTCTemplateService;
import com.hardcopy.btchat.service.KSMService;
import com.hardcopy.btchat.utils.Constants;
import com.hardcopy.btchat.utils.Logs;

import java.util.Timer;


@SuppressWarnings("deprecation")
public class MainPage extends TabActivity {

    boolean flag = true;
    public static Switch connectSW, ledSW, sosSW, gpsSW, weatherSW;
    public static Button startBtn;
    public static Button stopBtn;
    WebView web;
    Intent service;
    public static boolean messageFlag = true;

    private static final String TAG = "MainPage";
    public static BTCTemplateService mService;
    private ActivityHandler mActivityHandler;
    private Timer mRefreshTimer = null;

    private ImageView mImageBT = null;
    private TextView mTextStatus = null;

    private FragmentManager mFragmentManager;
    private FragmentAdapter mSectionsPagerAdapter;

    public void start(){
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
        connectSW.setEnabled(false);
        ledSW.setEnabled(false);
        sosSW.setEnabled(false);
        gpsSW.setEnabled(false);
        weatherSW.setEnabled(false);
        service = new Intent(getApplicationContext(), KSMService.class);
        startService(service);
//        Intent intent = new Intent(this, Orientation.class);
//        startActivity(intent);
    }

    public void stop(){
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
            mRefreshTimer = null;
        }
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        connectSW.setEnabled(true);
        ledSW.setEnabled(true);
        sosSW.setEnabled(true);
        gpsSW.setEnabled(true);
        weatherSW.setEnabled(true);
        service= new Intent(getApplicationContext(), KSMService.class);
        stopService(service);
    }
    void bluetooth() {
        Intent intent = new Intent(getApplicationContext(),Bluetooth.class);
        startActivity(intent);
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        Intent service= new Intent(getApplicationContext(), KSMService.class);
        stopService(service);

        mActivityHandler = new ActivityHandler();

        mImageBT = (ImageView) findViewById(R.id.status_title);
        mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
        mTextStatus = (TextView) findViewById(R.id.status_text);
        mTextStatus.setText(getResources().getString(R.string.bt_state_init));

        connectSW = (Switch) findViewById(R.id.switch1);
        connectSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectSW.isChecked()) {
                    doStartService();
                }else {
                    if(mRefreshTimer != null) {
                        mRefreshTimer.cancel();
                        mRefreshTimer = null;
                    }
                }
            }
        });
        ledSW = (Switch) findViewById(R.id.switch2);
        ledSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        sosSW = (Switch) findViewById(R.id.switch3);
        sosSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sosSW.isChecked()) {
                }
            }
        });
        gpsSW = (Switch) findViewById(R.id.switch4);
        gpsSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsSW.isChecked()) {
                    Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    //Intent intent1 = new Intent(getApplicationContext(), Gmap.class);
                    //startActivity(intent1);
                }
            }
        });
        weatherSW = (Switch) findViewById(R.id.switch5);
        weatherSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        startBtn= (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                start();
               // Intent intent = new Intent(getApplicationContext(), Orientation.class);
               // startActivity(intent);
            }
        });
        stopBtn= (Button) findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });


        web=(WebView)findViewById(R.id.webView1);
        web.setWebViewClient(new Web());

        WebSettings webSet = web.getSettings();
        webSet.setBuiltInZoomControls(true);
        web.loadUrl("http://naver.com");

        TabHost tabHost = getTabHost();

        TabSpec tabSpec1 = tabHost.newTabSpec("option").setIndicator("주행 옵션");
        tabSpec1.setContent(R.id.tab1);
        tabHost.addTab(tabSpec1);

        TabSpec tabSpec2 = tabHost.newTabSpec("ksm").setIndicator("KSM NEWS");
        tabSpec2.setContent(R.id.tab2);
        tabHost.addTab(tabSpec2);

        tabHost.setCurrentTab(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan();
                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Logs.e(TAG, "BT is not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
                break;
        }	// End of switch(requestCode)
    }

    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BTCTemplateService.class));
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here. Do not initialize while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private void initialize() {
        Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

        // Load activity reports and display
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
        }

        // Use below timer if you want scheduled job
        //mRefreshTimer = new Timer();
        //mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
    }

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_init));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_invisible));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_away));
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            mTextStatus.setText(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName);
                            mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    mTextStatus.setText(getResources().getString(R.string.bt_state_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    mTextStatus.setText(getResources().getString(R.string.bt_cmd_sending_error));
                    mImageBT.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_busy));
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }
}

