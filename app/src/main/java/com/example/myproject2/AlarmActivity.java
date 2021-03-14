package com.example.myproject2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

public class AlarmActivity extends Service {

    private WindowManager wm;
    private View mView;
    private String pkgName;
    private String appName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return Service.START_STICKY;
        else processCommand(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent){

        pkgName =  intent.getStringExtra("pkgName");
        PackageManager packageManager = this.getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications( 0 );

        for (int i = 0; i < appList.size(); i++) {
            if (pkgName.equals(appList.get(i).packageName)){
                appName = (String) appList.get(i).loadLabel(packageManager);
            }
        }

        final LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int layout_params;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_params = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layout_params = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layout_params,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |WindowManager.LayoutParams.FLAG_DIM_BEHIND // 창 뒤에 흐리게
                        |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT);

        mView = inflate.inflate(R.layout.view_alarm, null);
        TextView btn_close = mView.findViewById(R.id.view_yes);
        TextView tv_pkgName = mView.findViewById(R.id.alarm_pkgName);
        tv_pkgName.setText(appName);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(AlarmActivity.this, AlarmActivity.class));
            }
        });
        wm.addView(mView, params);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(wm != null) {
            if(mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }



}
