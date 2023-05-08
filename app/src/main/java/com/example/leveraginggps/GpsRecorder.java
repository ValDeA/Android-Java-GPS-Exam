package com.example.leveraginggps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GpsRecorder extends Service {
  private String CHANNEL_ID = "gpsNotification";

  Timer recorderTimer;
  Notification.Builder builder;

  GpsTracker gpsTracker;
  double lastLatitude, lastLongitude;

  // Room DB
  RoomDB database;
  List<MainData> dataList = new ArrayList<>();

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    NotificationManager manager = getSystemService(NotificationManager.class);

    NotificationChannel channel = new NotificationChannel(
        CHANNEL_ID,
        "Foreground Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT
    );
    manager.createNotificationChannel(channel);

    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,
        0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

    builder = new Notification.Builder(this, CHANNEL_ID)
        .setContentTitle("Foreground Service")
        .setContentText("GPS Recorder Working")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(pendingIntent)
//        .setShowWhen(true)
        .setAutoCancel(true)
        .setChannelId(CHANNEL_ID);

//    manager.notify(1, notification);
    startForeground(1, builder.build());

    Log.d("Main", "Foreground Service Start");

    gpsTracker = new GpsTracker(this);
    database = RoomDB.getInstance(this);
    dataList = database.mainDao().getAll();
    lastLatitude = -1; lastLongitude = -1;


    recorderTimer = new Timer();
    TimerTask recorderTask = new TimerTask() {
      @Override
      public void run() {
        recodeGPS();
//        database.mainDao().reset(database.mainDao().getAll());
      }
    };
    recorderTimer.scheduleAtFixedRate(recorderTask, 0, 1000 * 60 * 15);


    List<MainData> list = database.mainDao().getAll();
    for (int i=0; i<list.size(); i++) {
      MainData mainData = list.get(i);

      Log.d("Main", mainData.getId() + " : Lat " + mainData.getLatitude() + ", Long " + mainData.getLongitude() + ", cnt " + mainData.getCount());
    }


    return START_STICKY;
//    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d("Main", "Service Destroy");

    try {
      recorderTimer.cancel();
      Log.d("Main", "Timer is Cancel");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public void recodeGPS() {
    double latitude = gpsTracker.getLatitude(); // 위도
    double longitude = gpsTracker.getLongitude();   //경도

    List<MainData> findList = database.mainDao().find(latitude, longitude);
    if(findList.size() != 0 || (lastLatitude == latitude && lastLongitude == longitude)) {
      int id = findList.get(0).getId();
      int count = findList.get(0).getCount() + 1;

      database.mainDao().update(id, latitude, longitude, count);
    } else {
      MainData data = new MainData();
      data.setLatitude(latitude);
      data.setLongitude(longitude);
      data.setCount(1);

      database.mainDao().insert(data);
    }

    lastLatitude = latitude;
    lastLongitude = longitude;
  }
  public void resetGPS() {
    dataList.clear();
    dataList.addAll(database.mainDao().getAll());

    database.mainDao().reset(dataList);
  }
}
