package com.wrydhub.wryd.wrydapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import com.wrydhub.wryd.wrydapp.adapters.HomeListAdapter;
import com.wrydhub.wryd.wrydapp.databinding.ActivityMainBinding;
import com.wrydhub.wryd.wrydapp.models.User;
import com.wrydhub.wryd.wrydapp.ui.home;
import com.wrydhub.wryd.wrydapp.ui.notification;
import com.wrydhub.wryd.wrydapp.ui.profile;
import com.wrydhub.wryd.wrydapp.ui.search;


public class MainActivity extends AppCompatActivity {

    // logging
    private final String TAG = "MainActivity";
    ArrayList<User> userArrayList = new ArrayList<>();
    HomeListAdapter listAdapter;
    ProgressDialog progress;


    // background manager
    private PendingIntent recurringLl24 = null;
    private Intent ll24 = null;
    AlarmManager alarms = null;
    WebSocketClient mWebSocketClient = null;
    Timer timer = null;
    private RemindTask oneSecondTimer = null;

    private String[] autocompleteLocations = new String[] {"bedroom","living room","kitchen","bathroom", "office"};

    String familyName = "iiitdharwad";
    String serverAddress = "https://wryd.live";
    String deviceName  = "note8pro";
    String locationName = "";

    @Override
    protected void onDestroy() {
//        Log.d(TAG, "MainActivity onDestroy()");
//        if (alarms != null) alarms.cancel(recurringLl24);
//        if (timer != null) timer.cancel();
//        if (mWebSocketClient != null) {
//            mWebSocketClient.close();
//        }
//        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancel(0);
//        Intent scanService = new Intent(this, ScanService.class);
//        stopService(scanService);
        super.onDestroy();
    }

    class RemindTask extends TimerTask {
        private Integer counter = 0;

        public void resetCounter() {
            counter = 0;
        }
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counter++;
                    if (mWebSocketClient != null) {
                        if (mWebSocketClient.isClosed()) {
                            connectWebSocket();
                        }
                    }
                    Log.d(TAG, "run: "+ counter + " seconds ago: ");
                }
            });
        }
    }


    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new home());

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home_menu:
                    replaceFragment(new home());
                    break;
                case R.id.notification_menu:
                    replaceFragment(new notification());
                    break;
                case R.id.search_menu:
                    replaceFragment(new search());
                    break;
                case R.id.profile_menu:
                    replaceFragment(new profile());
                    break;
            }
            return true;
        });


//        setContentView(R.layout.activity_main);

        // check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 1);
        }

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);

        String savedUsername = sp.getString("user",null);
        if(savedUsername==null)
        {
            finishAndRemoveTask();
            return;
        }

        // check to see if there are preferences
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);


//        ToggleButton toggleButtonTracking = (ToggleButton) findViewById(R.id.toggleScanType);
//        toggleButtonTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                TextView rssi_msg = (TextView) findViewById(R.id.textOutput);
//                rssi_msg.setText("not running");
//                Log.d(TAG, "toggle set to false");
//                if (alarms != null) alarms.cancel(recurringLl24);
//                android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.cancel(0);
//                if (timer != null) timer.cancel();
//
//                CompoundButton scanButton = (CompoundButton) findViewById(R.id.toggleButton);
//                scanButton.setChecked(false);
//            }
//        });


            boolean allowGPS = false;


            deviceName = savedUsername;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("familyName", "iiitdharwad");
            editor.putString("deviceName", savedUsername);
            editor.putString("serverAddress", "");
//                    editor.putString("locationName", locationName);
            editor.putBoolean("allowGPS",allowGPS);
            editor.commit();

            // 24/7 alarm
            ll24 = new Intent(MainActivity.this, AlarmReceiverLife.class);
            Log.d(TAG, "setting familyName to [" + familyName + "]");
            ll24.putExtra("familyName", familyName);
            ll24.putExtra("deviceName", deviceName);
            ll24.putExtra("serverAddress", serverAddress);
            ll24.putExtra("locationName", locationName);
            ll24.putExtra("allowGPS",allowGPS);
            recurringLl24 = PendingIntent.getBroadcast(MainActivity.this, 0, ll24, PendingIntent.FLAG_CANCEL_CURRENT);
            alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarms.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), 60000, recurringLl24);
            timer = new Timer();
            oneSecondTimer = new RemindTask();
            timer.scheduleAtFixedRate(oneSecondTimer, 1000, 1000);
            connectWebSocket();

            String scanningMessage = "Scanning for " + familyName + "/" + deviceName;
            if (locationName.equals("") == false) {
                scanningMessage += " at " + locationName;
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(scanningMessage)
//                    .setOngoing(true)
                    .setContentIntent(recurringLl24);
            //specifying an action and its category to be triggered once clicked on the notification
            Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);
            resultIntent.setAction("android.intent.action.MAIN");
            resultIntent.addCategory("android.intent.category.LAUNCHER");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(resultPendingIntent);

            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());





    }




    private void connectWebSocket() {
        URI uri;
        try {
            serverAddress = serverAddress.replace("http", "ws");
            uri = new URI(serverAddress + "/ws?family=" + familyName + "&device=" + deviceName);
            Log.d("Websocket", "connect to websocket at " + uri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Websocket", "message: " + message);
                        JSONObject json = null;
                        JSONObject fingerprint = null;
                        JSONObject sensors = null;
                        JSONObject bluetooth = null;
                        JSONObject wifi = null;
                        String deviceName = "";
                        String locationName = "";
                        String familyName = "";
                        try {
                            json = new JSONObject(message);
                        } catch (Exception e) {
                            Log.d("Websocket", "json error: " + e.toString());
                            return;
                        }
                        try {
                            fingerprint = new JSONObject(json.get("sensors").toString());
                            Log.d("Websocket", "fingerprint: " + fingerprint);
                        } catch (Exception e) {
                            Log.d("Websocket", "json error: " + e.toString());
                        }
                        try {
                            sensors = new JSONObject(fingerprint.get("s").toString());
                            deviceName = fingerprint.get("d").toString();
                            familyName = fingerprint.get("f").toString();
                            locationName = fingerprint.get("l").toString();
                            Log.d("Websocket", "sensors: " + sensors);
                        } catch (Exception e) {
                            Log.d("Websocket", "json error: " + e.toString());
                        }
                        try {
                            wifi = new JSONObject(sensors.get("wifi").toString());
                            Log.d("Websocket", "wifi: " + wifi);
                        } catch (Exception e) {
                            Log.d("Websocket", "json error: " + e.toString());
                        }
                        try {
                            bluetooth = new JSONObject(sensors.get("bluetooth").toString());
                            Log.d("Websocket", "bluetooth: " + bluetooth);
                        } catch (Exception e) {
                            Log.d("Websocket", "json error: " + e.toString());
                        }
                        Log.d("Websocket", bluetooth.toString());
                        Integer bluetoothPoints = bluetooth.length();
                        Integer wifiPoints = wifi.length();
                        Long secondsAgo = null;
                        try {
                            secondsAgo = fingerprint.getLong("t");
                        } catch (Exception e) {
                            Log.w("Websocket", e);
                        }

                        if ((System.currentTimeMillis() - secondsAgo)/1000 > 3) {
                            return;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss");
                        Date resultdate = new Date(secondsAgo);
//                        String message = sdf.format(resultdate) + ": " + bluetoothPoints.toString() + " bluetooth and " + wifiPoints.toString() + " wifi points inserted for " + familyName + "/" + deviceName;
                        String message = "1 second ago: added " + bluetoothPoints.toString() + " bluetooth and " + wifiPoints.toString() + " wifi points for " + familyName + "/" + deviceName;
                        oneSecondTimer.resetCounter();
                        if (locationName.equals("") == false) {
                            message += " at " + locationName;
                        }
//                        TextView rssi_msg = (TextView) findViewById(R.id.textOutput);
                        Log.d("Websocket", message);
//                        rssi_msg.setText(message);

                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        TextView rssi_msg = (TextView) findViewById(R.id.textOutput);
//                        rssi_msg.setText("cannot connect to server, fingerprints will not be uploaded");
                    }
                });
            }
        };
        mWebSocketClient.connect();
    }


    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }



}
