package com.solace.labs.cardemo.controller.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by davidwray on 02/02/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent actionIntent = new Intent(Constants.APP_ID + Constants.SWITCH_TO_MAP_EVENT);
        context.sendBroadcast(actionIntent);
    }
}
