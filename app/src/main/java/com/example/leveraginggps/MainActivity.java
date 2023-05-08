package com.example.leveraginggps;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.leveraginggps.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
  private static final int PERMISSIONS_REQUEST_CODE = 100;

  ActivityMainBinding binding;
  GpsTracker gpsTracker;


  ActivityResultLauncher<Intent> activityLauncher;
  private final String[] requiredPermission = {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.INTERNET,
      Manifest.permission.FOREGROUND_SERVICE,
      Manifest.permission.POST_NOTIFICATIONS
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_main);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


    activityLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            ActivityCompat.requestPermissions(MainActivity.this, requiredPermission,
                PERMISSIONS_REQUEST_CODE);
          }
        }
    );

    // 권한 체크
    if(!checkRunTimePermission()) {
      ActivityCompat.requestPermissions(MainActivity.this, requiredPermission,
          PERMISSIONS_REQUEST_CODE);

      return;
    }


//    gpsTracker = new GpsTracker(this);
    Intent startIntent = new Intent(this, GpsRecorder.class);
    startService(startIntent);

    binding.show.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    binding.clear.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    binding.stop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent stopIntent = new Intent(MainActivity.this, GpsRecorder.class);
        stopService(stopIntent);
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    Intent stopIntent = new Intent(MainActivity.this, GpsRecorder.class);
    stopService(stopIntent);
  }

  private boolean checkRunTimePermission() {
    // 런타임 퍼미션 처리
    int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.ACCESS_COARSE_LOCATION);
    int hasInternetPermission = ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.INTERNET);
    int hasForegroundPermission = ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.FOREGROUND_SERVICE);
    int hasNotificationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
        Manifest.permission.POST_NOTIFICATIONS);

    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasInternetPermission == PackageManager.PERMISSION_GRANTED &&
        hasForegroundPermission == PackageManager.PERMISSION_GRANTED &&
        hasNotificationPermission == PackageManager.PERMISSION_GRANTED) {
      // 이미 퍼미션을 가지고 있다면
      // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
      return true;
    } else {
      // 퍼미션이 없다면

      if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ||
          ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
          ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET) ||
          ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE) ||
          ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)) {
        // 사용자가 퍼미션을 거부한 적이 있는 경우
        //showPermissionSettingAlert();

        return false;
      } else {
        // 사용자가 퍼미션을 거부한 적이 없는 경우


        return false;
      }
    }
  }

  public GpsTracker getGpsTracker() { return gpsTracker; }
}