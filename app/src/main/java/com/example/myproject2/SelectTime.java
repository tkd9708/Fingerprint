package com.example.myproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SelectTime extends AppCompatActivity {

    private String pkgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepicker);
        pkgName = getIntent().getStringExtra("pkgName");
        final TimePicker picker = findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        // 앞서 설정한 값, 없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE); // more_private : 자신 앱에서만 사용
        long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis());
        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        // 이전 설정값으로 TimePicker 초기화
        final Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());
        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        if (Build.VERSION.SDK_INT >= 23 ){
            picker.setHour(pre_hour);
            picker.setMinute(pre_minute);
        }
        else{
            picker.setCurrentHour(pre_hour);
            picker.setCurrentMinute(pre_minute);
        }
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                show();

            }

        });
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("해당 앱을 잠그겠습니까?");
        builder.setMessage("※ 주의 ※");
        builder.setMessage("'yes' 버튼을 누르시면 해당 앱은 설정하신 시간까지 절대 실행되지 않습니다.");
        builder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final TimePicker picker = findViewById(R.id.timePicker);
                        picker.setIs24HourView(true);
                        int hour_24, minute;
                        if (Build.VERSION.SDK_INT >= 23 ){
                            hour_24 = picker.getHour();
                            minute = picker.getMinute();
                        }
                        else{
                            hour_24 = picker.getCurrentHour();
                            minute = picker.getCurrentMinute();
                        }

                        // 지정된 시간으로 알람 시간 설정
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1);
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Date currentDateTime = calendar.getTime();
                        if (hour_24 == 0){
                            String date_text = new SimpleDateFormat("yyyy-MM.dd 00:mm", Locale.getDefault()).format(currentDateTime);
                            Toast.makeText(getApplicationContext(),date_text + "까지 해당 앱이 실행되지 않습니다.", Toast.LENGTH_SHORT).show();
                            intent.putExtra("dateTime", date_text);
                        }
                        else{
                            String date_text = new SimpleDateFormat("yyyy-MM.dd kk:mm", Locale.getDefault()).format(currentDateTime);
                            Toast.makeText(getApplicationContext(),date_text + "까지 해당 앱이 실행되지 않습니다.", Toast.LENGTH_SHORT).show();
                            intent.putExtra("dateTime", date_text);
                        }

                        //  Preference에 설정한 값 저장
                        SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                        editor.putLong("nextNotifyTime", (long)calendar.getTimeInMillis());
                        editor.apply();

                        intent.putExtra("pkgName", pkgName);
                        startActivity(intent);
                        finish();
                    }
                });

        builder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.show();
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}