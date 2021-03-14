package com.example.myproject2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LockSetting extends AppCompatActivity {

    protected CheckBox ch_finger;
    private FingerprintManager fingerprintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_setting);

        // ch_password = findViewById(R.id.ch_password);
        ch_finger = findViewById(R.id.ch_finger);

        // data 유지
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk = pref.getBoolean("ch_finger", false);
        ch_finger.setChecked(chk);

        makeLock();

    }

    public void onResume() {

        super.onResume();
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Boolean chk = pref.getBoolean("ch_finger", false);
        ch_finger.setChecked(chk);
    }


    public void onStop() {
        super.onStop();

        // data 유지
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // editor.putBoolean("ch_password", ch_password.isChecked());
        editor.putBoolean("ch_finger", ch_finger.isChecked());

        editor.commit();
    }



    public void makeLock() {


        ch_finger.setOnClickListener(new CheckBox.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(LockSetting.this,
                        R.style.AlertDialog);

                mDialog.setMessage("휴대폰에 등록된 지문으로 잠금을 설정하시겠습니까?")
                        .setTitle("지문 설정")
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ch_finger.setChecked(false);
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int which) {
                                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                                if (!fingerprintManager.isHardwareDetected()) {
                                    Toast.makeText(getApplicationContext(),"지문을 사용할 수 없는 디바이스 입니다.",Toast.LENGTH_LONG).show();
                                }
                                else if (!fingerprintManager.hasEnrolledFingerprints()) {
                                    Toast.makeText(getApplicationContext(),"등록된 지문이 없습니다.",Toast.LENGTH_LONG).show();
                                }
                                else {


                                    ch_finger.setChecked(true);
                                }
                            }
                        })
                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록

                        .show();

            }
        });
    }
}
