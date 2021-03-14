package com.example.myproject2;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends BaseActivity
{

    PackageManager packageManager;
    ListView apkList;
    private ArrayList listItem;
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";
    private static final String SETTINGS_PLAYER_JSONN = "settings_item_jsonn";
    private static final String SETTINGS_PLAYER_JSONNN = "settings_item_jsonnn";
    public final String PREFERENCE = "main_preference";
    public final String PREFERENCE2 = "main_preference2";
    private ArrayList<String> checkedPkg;
    private ImageButton ImBtn;
    private ArrayList<pkgTime> pkgTime_array;
    private String mPkgName;
    private String mDateTime;
    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM.dd hh:mm");
    long now = System.currentTimeMillis();
    final Date date_now = new Date(now);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 접근성 확인
        requestPermission();
        if(!checkAccessibilityPermissions()) {
            setAccessibilityPermissions();
        }
        onCheckComplete();

        listItem = new ArrayList();
        checkedPkg = new ArrayList<>();
        pkgTime_array = new ArrayList<pkgTime>();
        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();

        // system 어플 필터링
        for(PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
            if(!b) {
                packageList1.add(pi);
            }
        }
        apkList.setAdapter(new ApkAdapter(this, packageList1, packageManager));
        apkList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public void onResume() {

        super.onResume();

        onCheckComplete();
    }


    public void onStop(){
        super.onStop();

        // 데이터 저장
        setArrayPref(SETTINGS_PLAYER_JSON, listItem);
        setArrayPref(SETTINGS_PLAYER_JSONN, checkedPkg);
        setPkgTimePref(SETTINGS_PLAYER_JSONNN, pkgTime_array);


    }

    @Override
    public void onCheckComplete() {
        initPref(); // json 저장 데이터 가져오기
        initListener(); // 서브메뉴

        // 시간이 설정되서 받아왔으면
        if (getIntent().getStringExtra("dateTime") != null){
            if (getIntent().getStringExtra("pkgName").length() != 0){
                mPkgName = getIntent().getStringExtra("pkgName");
                mDateTime = getIntent().getStringExtra("dateTime");

                // 중복 저장 필터링
                boolean ox = false;
                if (pkgTime_array != null){
                    for (int i = 0; i < pkgTime_array.size(); i++){
                        if ((pkgTime_array.get(i).pkgName).equals(mPkgName)){
                            ox = true;
                        }
                    }
                }
                if(pkgTime_array == null){
                    pkgTime_array = new ArrayList<pkgTime>();
                }
                if (!ox){
                    pkgTime_array.add(new pkgTime(mPkgName, mDateTime));
                }
            }
        }
        if(pkgTime_array != null){
            setAlarm();
        }

    }


    public void initPref(){
        if(getArrayPref(SETTINGS_PLAYER_JSON) != null){
            listItem = getArrayPref(SETTINGS_PLAYER_JSON);
        }

        if (getArrayPref(SETTINGS_PLAYER_JSONN) != null){
            checkedPkg = getArrayPref(SETTINGS_PLAYER_JSONN);
        }


        if(getPkgTimePref(SETTINGS_PLAYER_JSONNN) != null){
            pkgTime_array = getPkgTimePref(SETTINGS_PLAYER_JSONNN);

        }


    }

    public void initListener() {

        ImBtn = findViewById(R.id.button);
        apkList = findViewById(R.id.applist);

        ImBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.popup, p.getMenu());

                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {

                        if (item.getItemId() == R.id.item1){
                            Intent intent = new Intent(getApplicationContext(),LockSetting.class);
                            startActivity(intent);
                        }
                        else {
                            alarmOverlaySetting();
                        }

                        return false;
                    }
                });
                p.show();
            }
        });
    }

    public void alarmOverlaySetting(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("잠금 해제 알림 설정");
        builder.setMessage("설정 시간에 앱의 잠금이 풀릴 시, 알림창을 띄우겠습니까?");

        builder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        check_OverlayPermission();
                        Toast.makeText(getApplicationContext(),"알림 설정",Toast.LENGTH_LONG).show();
                    }
                });

        builder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        unCheck_OverlayPermission();
                        Toast.makeText(getApplicationContext(),"알림 설정 취소.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }


    public void setAlarm(){
        for (int i = 0; i < pkgTime_array.size(); i++){
            try {
                Date date = transFormat.parse(pkgTime_array.get(i).s_dateTime);
                if (date.after(date_now)){
                    AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
                    intent.putExtra("pkgName", pkgTime_array.get(i).pkgName);

                    PendingIntent sender = PendingIntent.getBroadcast(this, (int)date.getTime(),
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender); // date.getTime() >> 여러개 알람 설정을 위해 다른 index 설정
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


    private void setArrayPref(String key, ArrayList values) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = prefs.edit();
    JSONArray a = new JSONArray();

    for (int i = 0; i < values.size(); i++) {
        a.put(values.get(i));
    }

    if (!values.isEmpty()) {
        editor.putString(key, a.toString());
    }
    else {
        editor.putString(key, null);
    }

    editor.apply();
}

    private ArrayList getArrayPref(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = prefs.getString(key, null);
        ArrayList arrayList = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String data = a.optString(i);
                    arrayList.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    private void setPkgTimePref(String key, ArrayList<pkgTime> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("pkgName", values.get(i).pkgName);
                jsonObject.put("s_dateTime", values.get(i).s_dateTime);
                a.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        }
        else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    private ArrayList<pkgTime> getPkgTimePref(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = prefs.getString(key, null);

        if (json != null) {
            try {
                ArrayList<pkgTime> arrayList = new ArrayList<pkgTime>();
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    JSONObject jsonObject = a.getJSONObject(i);
                    String pkgName = jsonObject.getString("pkgName");
                    String s_dateTime = jsonObject.getString("s_dateTime");

                    pkgTime mPkgTime = new pkgTime(pkgName, s_dateTime);

                    arrayList.add(mPkgTime);
                }

                return arrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private boolean isSystemPackage(PackageInfo pkgInfo) {
        if ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
            return true;
        else
            return false;
    }

    public class ApkAdapter extends BaseAdapter {
        List<PackageInfo> packageList;
        Activity context;
        PackageManager packageManager;

        public ApkAdapter(Activity context, List<PackageInfo> packageList, PackageManager packageManager) {
            super();
            this.context = context;
            this.packageList = packageList;
            this.packageManager = packageManager;
        }

        private class ViewHolder {
            TextView apkName;
            TextView setTime;
            CheckBox apkCheck;
            String packageName;
        }

        public int getCount() {
            return packageList.size();
        }
        public Object getItem(int position) {
            return packageList.get(position);
        }
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = context.getLayoutInflater();
            final int checkBoxPosition = position;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.apklist_item, null);
                holder = new ViewHolder();

                holder.apkName = convertView.findViewById(R.id.appname);
                holder.setTime = convertView.findViewById(R.id.setTime);
                holder.apkCheck = convertView.findViewById(R.id.app_check);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // package List
            final PackageInfo packageInfo = (PackageInfo) getItem(position);
            Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
            String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
            holder.packageName = packageInfo.packageName;

            appIcon.setBounds(0, 0, 80, 80);
            holder.apkName.setCompoundDrawables(appIcon, null, null, null);
            holder.apkName.setCompoundDrawablePadding(50);
            holder.apkName.setText(appName);

            if (holder.apkCheck != null) {
                // 체크박스의 상태 변화를 체크
                holder.apkCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) { // 체크를 할 때
                            for (int i = 0; i < listItem.size(); i++) {
                                if (Integer.parseInt(String.valueOf(listItem.get(i))) == checkBoxPosition) {
                                   return;
                                }
                            }

                            // 중복 저장 필터링
                            boolean ox = false;
                            if(pkgTime_array != null){
                                for (int i = 0; i < pkgTime_array.size(); i ++){
                                    if (holder.packageName.equals(pkgTime_array.get(i).pkgName)){
                                        ox = true;
                                        break;
                                    }
                                }
                            }

                            // 새로운 체크일 시, 시간설정
                            if(!ox){
                                Intent intent = new Intent(getApplicationContext(), SelectTime.class);
                                intent.putExtra("pkgName", holder.packageName);
                                startActivity(intent);
                            }
                            finish();
                        }
                        else { // 체크가 해제될 때
                            for (int i =0; i < listItem.size(); i++) {
                                if (Integer.parseInt(String.valueOf(listItem.get(i))) == checkBoxPosition) {
                                    boolean ox = false;
                                    for (int j = 0; j < pkgTime_array.size(); j++){
                                        // if (checkedPkg.get(j).contentEquals(holder.apkName.getText())){
                                        if (pkgTime_array.get(j).pkgName.equals(holder.packageName)){
                                            try {
                                                Date dateTime = transFormat.parse(pkgTime_array.get(j).s_dateTime);

                                                // 시간이 지났으면 array에서 삭제
                                                if (date_now.after(dateTime)){
                                                    pkgTime_array.remove(j);
                                                    ox = true;
                                                }
                                                else { // 시간이 지나지 않았으면 check 유지
                                                    holder.apkCheck.setChecked(true);
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if (ox) {
                                        listItem.remove(i);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });

                for (int i = 0; i < pkgTime_array.size(); i ++){
                    if (holder.packageName.equals(pkgTime_array.get(i).pkgName)){
                        boolean ox = false;
                        for (int j = 0; j < listItem.size(); j++){
                            if (Integer.parseInt(String.valueOf(listItem.get(j))) == checkBoxPosition){
                                ox = true;
                            }
                        }
                        if (!ox){
                            listItem.add(checkBoxPosition);
                        }
                    }
                }

                boolean isChecked = false; // 체크된 아이템인지 판단
                for (int i = 0; i < listItem.size(); i++) {
                    // 만약 체크되었던 아이템이라면 check
                    if (Integer.parseInt(String.valueOf(listItem.get(i))) == checkBoxPosition) {
                        boolean ox = false;
                        if( pkgTime_array != null){
                            for (int j = 0; j < pkgTime_array.size(); j++){
                                if (pkgTime_array.get(j).pkgName.equals(holder.packageName)) {
                                    try {
                                        Date dateTime = transFormat.parse(pkgTime_array.get(j).s_dateTime);
                                        // 설정시간 지났으면 삭제
                                        if(date_now.after(dateTime)){
                                            pkgTime_array.remove(j);
                                            holder.apkCheck.setChecked(false);
                                            holder.setTime.setText(" ");
                                            ox = true;
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (!ox){ // 시간이 아직 안지났으면 체크 유지
                            for (int j = 0; j < pkgTime_array.size(); j++){
                                if (pkgTime_array.get(j).pkgName.equals(holder.packageName)) {

                                    holder.apkCheck.setChecked(true);
                                    if (pkgTime_array.size() != 0){
                                        holder.setTime.setText(pkgTime_array.get(j).s_dateTime);
                                        isChecked = true;
                                    }
                                }
                            }


                        }
                        else {
                            listItem.remove(i);
                        }
                        break;
                    }
                }

                // 아니라면 체크 안함
                if (!isChecked) {
                    holder.apkCheck.setChecked(false);
                    holder.setTime.setText(" ");
                }

                // Background service로 체크된 pkgTime 전송
                Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                intent.putParcelableArrayListExtra("pkgArray", pkgTime_array);
                startService(intent);

                if (pkgTime_array.size() != 0){
                    for (int j = 0; j < pkgTime_array.size(); j++){
                        Log.e("TEST pkg", pkgTime_array.get(j).pkgName + " "
                                + pkgTime_array.get(j).s_dateTime);
                    }
                    Log.d("TEST","------");
                }
            }
            return convertView;
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}

