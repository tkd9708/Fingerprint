package com.example.myproject2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String pkgName = intent.getStringExtra("pkgName");
        intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("pkgName", pkgName);
        context.startService(intent);
    }

}
