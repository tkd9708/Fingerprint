package com.example.myproject2;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BackgroundService extends android.accessibilityservice.AccessibilityService {
    private static final String TAG = "BackgroundService";
    private String LastAccessibilityPackage;
    private ArrayList<pkgTime> pkgTime_array;
    private static int OK_sign;
    private String[] oneUI = {"SM-N971N", "SM-N976N", "SM-G970N", "SM-G973N", "SM-G975N", "SM-G977N", "SM-J737S", "SM-A505N",
        "SM-G981N", "SM-G981U", "SM-G986N", "SM-G986U", "SM-G988N", "SM-G988U", "SM-N981", "SM-N986", "SM-A105N", "SM-A805N","SM-A908N",
        "SM-A915F", "SM-A315N", "SM-A516N", "SM-A716S"};


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return Service.START_STICKY;
        else processCommand(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent){
        if (intent.getParcelableArrayListExtra("pkgArray") != null){
            pkgTime_array =  intent.getParcelableArrayListExtra("pkgArray");
        }

        OK_sign = intent.getIntExtra("OK_sign", 5);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event == null){
                return;
            }
            if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                return;
            }

            CharSequence pkgCs = event.getPackageName();
            if (pkgCs == null || pkgCs.length() == 0) return;
            String pkg = pkgCs.toString();

            if (pkgTime_array.size() != 0){
                for (int i = 0; i < pkgTime_array.size(); i++){
                    Log.d(TAG, pkgTime_array.get(i).pkgName + " " + pkgTime_array.get(i).s_dateTime);
                }
                Log.d(TAG,"------");
            }

            long now = System.currentTimeMillis();
            Date date_now = new Date(now);

            // ????????? package??? touch??? package ??????
            if (pkgTime_array != null){
                for (int j = 0; j < pkgTime_array.size(); j++) {

                    if (pkg.equals(pkgTime_array.get(j).pkgName)){

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM.dd hh:mm");
                        try {
                            Date dateTime = transFormat.parse(pkgTime_array.get(j).s_dateTime);

                            if (dateTime.after(date_now)) {
                                Toast.makeText(this.getApplicationContext(), event.getPackageName() + "?????? ?????????????????????", Toast.LENGTH_LONG);
                                gotoHome();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // ???????????? ??????
        try {
            // one ui ?????????
            for(int i=0; i<oneUI.length; i++){
                if(Build.MODEL.equals(oneUI[i]))
                    return;
            }

            if (event == null) {
                return;
            }

            //????????? ????????? ?????????
            if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    return;
                }
            }

            CharSequence pkgCs = event.getPackageName();
            if (pkgCs == null || pkgCs.length() == 0) return;
            String pkg = pkgCs.toString();

            //????????? ?????????, ????????? ??????
            if (!TextUtils.equals(LastAccessibilityPackage, pkg)){

                //????????? ????????? ??????
                if (event.isFullScreen()) {
                    if (pkg.startsWith("com.google.android.inputmethod")) return;
                } else {
                    AccessibilityNodeInfo source = event.getSource(); // ????????? ??? ?????? ?????? ?????? ?????? null
                    if (source == null) return;
                    int count = source.getChildCount();
                    if (count == 0) return;
                    if ("com.android.systemui".equals(pkg)) return;

                    for (int i = 0; i < count; ++i) {
                        if (source.getChild(i) == null) return;
                    }
                }
                LastAccessibilityPackage = pkg;

                if (!pkg.equals("com.sec.android.app.launcher")){
                    if (OK_sign < 5){
                        OK_sign++;
                        Log.e("ok_sign", String.valueOf(OK_sign));
                        return;
                    }

                    // ???????????? ??????
                    final Core core = new Core() {
                        @Override
                        public void onSuccess(FingerprintManager.CryptoObject cryptoObject) {
                            Log.d(TAG, "Success");

                            // ?????? ?????????
                            startService(new Intent(getApplicationContext(), ViewService.class));
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            Log.d(TAG, "Fail : " + code + " " + msg);
                        }
                    };
                    core.init(getApplicationContext());

                    // ???????????? ?????????????????? app ?????????
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            core.cancel();
                        }
                    }, 15000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void gotoHome(){
        Log.e("gotoHome", "gotoHome");

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS // ?????? ??????????????? ????????? ?????? ??????
                | Intent.FLAG_ACTIVITY_FORWARD_RESULT // result??? ??????
                | Intent.FLAG_ACTIVITY_NEW_TASK // ????????? Task??? ???????????? ??? Task ?????? Activity??? ??????
                | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP // onActivityResult()??? ????????? ????????? ??? ?????? ??????????????? ???????????????
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); //?????????????????? ?????? Task??? ?????????????????? ????????? ???
        startActivity(intent);
    }

    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_SELECTED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
        info.notificationTimeout = 100; // millisecond

        setServiceInfo(info);
    }


    @Override
    public void onInterrupt() {
        Log.e("TEST", "OnInterrupt");
    }
}
