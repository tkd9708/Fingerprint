package com.example.myproject2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

// 지문인식 잠금화면
public class Lock extends LockSetting {

    private String TAG = "Lock";
    private LinearLayout linearLayout;
    private TextView tv_finger;
    private ImageView ic_finger;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_login);

        // 지문인식 설정 유무
        ch_finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                Boolean chk = pref.getBoolean("ch_finger", false);
                ch_finger.setChecked(chk);
            }
        });

        if(!ch_finger.isChecked()){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            finish();
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                mInit();
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                pInit();
            }
        }



    }

    public void onResume() {
        super.onResume();

        if(!ch_finger.isChecked()){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            finish();
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                mInit();
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                pInit();
            }
        }



    }

    public void mInit(){
        tv_finger = findViewById(R.id.tv_finger);
        ic_finger = findViewById(R.id.ic_finger);
        linearLayout = findViewById(R.id.layout2);
        linearLayout.setVisibility(View.GONE);

        tv_finger.setText("손가락을 홈버튼에 올리세요.");

        Core core = new Core() {

            @Override
            public void onSuccess(FingerprintManager.CryptoObject cryptoObject) {
                tv_finger.setText("앱 접근 허용");
                Log.e(TAG, "앱 접근 허용");

                tv_finger.setTextColor(ContextCompat.getColor(Lock.this, R.color.colorPrimaryDark));
                ic_finger.setImageResource(R.drawable.ic_done);

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                tv_finger.setText(msg);
                Log.e(TAG, msg);
                tv_finger.setTextColor(ContextCompat.getColor(Lock.this, R.color.colorAccent));
            }
        };
        core.init(getApplicationContext());
    }

    public void pInit(){
        tv_finger = findViewById(R.id.tv_finger);
        ic_finger = findViewById(R.id.ic_finger);
        linearLayout = findViewById(R.id.layout2);
        linearLayout.setVisibility(View.GONE);
        ic_finger.setVisibility(View.GONE);
        tv_finger.setText("손가락을 홈버튼에 올리세요.");

        BioCore bioCore = new BioCore() {
            @Override
            public void onSuccess(BiometricPrompt.CryptoObject cryptoObject) {
                tv_finger.setText("앱 접근 허용");
                Log.e(TAG, "앱 접근 허용");

                tv_finger.setTextColor(ContextCompat.getColor(Lock.this, R.color.colorPrimaryDark));
                ic_finger.setImageResource(R.drawable.ic_done);
                ic_finger.setVisibility(View.VISIBLE);

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFail(int code, String msg) {
                tv_finger.setText(msg);
                Log.e(TAG, msg);
                tv_finger.setTextColor(ContextCompat.getColor(Lock.this, R.color.colorAccent));
            }
        };
        bioCore.init(getApplicationContext());
    }
}
