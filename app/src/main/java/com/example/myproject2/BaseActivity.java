package com.example.myproject2;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int CHECK_PERMISSION = 9923;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermission = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
                listPermission.add(Manifest.permission.USE_FINGERPRINT);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED)
                    listPermission.add(Manifest.permission.USE_BIOMETRIC);
            }

            if (listPermission.size() > 0) {
                String[] arrayPermissions = new String[listPermission.size()];
                arrayPermissions = listPermission.toArray(arrayPermissions);

                ActivityCompat.requestPermissions(this, arrayPermissions, CHECK_PERMISSION);
            } else
                onCheckComplete();
        } else
            onCheckComplete();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        if (requestCode == CHECK_PERMISSION) {
            for (int granted : grantResults) {
                if (granted != PackageManager.PERMISSION_GRANTED)
                    isAllGranted = false;
            }

            if (isAllGranted)
                onCheckComplete();
            else
                finish();
        }
    }

    public boolean checkAccessibilityPermissions() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        // ?????? ????????? ????????? ?????? ???????????? ????????????
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);

        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            // ????????? ????????? ?????? ?????? ????????? ????????? ????????? ????????? ????????? ???????????? ????????? ????????? ????????? ????????? ??????
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())) {
                return true;
            }
        }
        return false;
    }



    // ????????? ?????????????????? ???????????? ??????
    public void setAccessibilityPermissions() {
        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("????????? ?????? ??????");
        gsDialog.setMessage("???????????? ??? ?????? ????????? ????????? ????????? ????????? ????????? ?????????");
        gsDialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                return;
            }
        }).create().show();
    }

    public void check_OverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void unCheck_OverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // ????????? ?????? ????????? ????????? ??????

            }
        }
    }

    public abstract void onCheckComplete();
}


