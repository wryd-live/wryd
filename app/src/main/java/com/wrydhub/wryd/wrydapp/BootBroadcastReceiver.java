package com.wrydhub.wryd.wrydapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.wrydhub.wryd.wrydapp.utils.keysConfig;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BootBroadcastReceiver extends BroadcastReceiver {


    private final String TAG = "MainActivity";


    private PendingIntent recurringLl24 = null;
    private Intent ll24 = null;
    AlarmManager alarms = null;
    WebSocketClient mWebSocketClient = null;
    Timer timer = null;
    private RemindTask oneSecondTimer = null;

    private String[] autocompleteLocations = new String[] {"bedroom","living room","kitchen","bathroom", "office"};

    String familyName = "iiitdharwad";
    String serverAddress = keysConfig.wrydLocationServerURL;
    String deviceName  = "note8pro";
    String locationName = "";

    boolean allowGPS = false;




    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            // Do your work related to alarm manager

            SharedPreferences sp = context.getSharedPreferences("login",context.MODE_PRIVATE);

            String savedUsername = sp.getString("user",null);
            if(savedUsername==null)
            {
                return;
            }

            deviceName = savedUsername;


            ll24 = new Intent(context, AlarmReceiverLife.class);
            Log.d(TAG, "setting familyName to [" + familyName + "]");
            ll24.putExtra("familyName", familyName);
            ll24.putExtra("deviceName", deviceName);
            ll24.putExtra("serverAddress", serverAddress);
            ll24.putExtra("locationName", locationName);
            ll24.putExtra("allowGPS",allowGPS);
            recurringLl24 = PendingIntent.getBroadcast(context, 0, ll24, PendingIntent.FLAG_CANCEL_CURRENT);
            alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarms.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), 60000, recurringLl24);
            timer = new Timer();
            oneSecondTimer = new RemindTask();
            timer.scheduleAtFixedRate(oneSecondTimer, 1000, 1000);
            connectWebSocket();

            String scanningMessage = "Scanning for " + familyName + "/" + deviceName;
            if (locationName.equals("") == false) {
                scanningMessage += " at " + locationName;
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(scanningMessage)
//                    .setOngoing(true)
                    .setContentIntent(recurringLl24);
            //specifying an action and its category to be triggered once clicked on the notification
            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.setAction("android.intent.action.MAIN");
            resultIntent.addCategory("android.intent.category.LAUNCHER");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(resultPendingIntent);

            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }


    class RemindTask extends TimerTask {
        private Integer counter = 0;

        public void resetCounter() {
            counter = 0;
        }
        public void run() {
            new Thread(new Runnable() {
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
                new Thread(new Runnable() {
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
                        Log.d("Websocket", message);

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
            }
        };
        mWebSocketClient.connect();
    }

}


