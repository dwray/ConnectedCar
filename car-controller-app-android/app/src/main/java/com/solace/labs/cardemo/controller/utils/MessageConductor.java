/*******************************************************************************
 * Copyright (c) 2014-2016 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Mike Robertson - initial contribution
 *    Aldo Eisma - light can now be controlled with toggle, on and off
 *    David Wray - updated for Solace CarDemo
 *******************************************************************************/
package com.solace.labs.cardemo.controller.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.solace.labs.cardemo.controller.IoTStarterApplication;
import com.solace.labs.cardemo.controller.R;
import com.solace.labs.cardemo.controller.activities.MapsActivity;
import com.solace.labs.cardemo.controller.activities.ProfilesActivity;
import com.solace.labs.cardemo.controller.fragments.IoTPagerFragment;
import com.solace.labs.cardemo.controller.fragments.LogPagerFragment;
import com.solace.labs.cardemo.controller.fragments.LoginPagerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Steer incoming MQTT messages to the proper activities based on their content.
 */
public class MessageConductor {

    private final static String TAG = MessageConductor.class.getName();
    private static MessageConductor instance;
    private final Context context;
    private final IoTStarterApplication app;

    private MessageConductor(Context context) {
        this.context = context;
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    public static MessageConductor getInstance(Context context) {
        if (instance == null) {
            instance = new MessageConductor(context);
        }
        return instance;
    }

    /**
     * Steer incoming MQTT messages to the proper activities based on their content.
     *
     * @param payload The log of the MQTT message.
     * @param topic The topic the MQTT message was received on.
     * @throws JSONException If the message contains invalid JSON.
     */
    public void steerMessage(String payload, String topic) throws JSONException {
        Log.d(TAG, ".steerMessage() entered");
        JSONObject top = new JSONObject(payload);
        JSONObject d = top.getJSONObject("d");

        if (topic.contains(Constants.TEXT_EVENT)) {
            int unreadCount = app.getUnreadCount();
            String messageText = d.getString("text");
            app.setUnreadCount(++unreadCount);

            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] Received text:
            // <message text>
            Date date = new Date();
            String logMessage = "[" + new Timestamp(date.getTime()) + "] Received Text:\n";
            app.getMessageLog().add(logMessage + messageText);

            // Send intent to LOG fragment to mark list data invalidated
            String runningActivity = app.getCurrentRunningActivity();
            //if (runningActivity != null && runningActivity.equals(LogPagerFragment.class.getName())) {
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
            context.sendBroadcast(actionIntent);
            //}

            // Send intent to current active fragment / activity to update Unread message count
            // Skip sending intent if active tab is LOG
            // TODO: 'current activity' code needs fixing.
            Intent unreadIntent;
            if (runningActivity.equals(LogPagerFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
            } else if (runningActivity.equals(LoginPagerFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
            } else if (runningActivity.equals(IoTPagerFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            } else if (runningActivity.equals(ProfilesActivity.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_PROFILES);
            } else {
                return;
            }

            if (messageText != null) {
                unreadIntent.putExtra(Constants.INTENT_DATA, Constants.UNREAD_EVENT);
                context.sendBroadcast(unreadIntent);
            }
        } else if (topic.contains(Constants.CRASH_EVENT) || topic.contains(Constants.TRACKING_EVENT)) {
            // save payload in an arrayList
            boolean isACrash = false;
            if (topic.contains(Constants.CRASH_EVENT)) {
                app.setCrashed(true);
                isACrash = true;
            }
            int unreadCount = app.getUnreadCount();
            double accel_x = d.getDouble("acceleration_x");
            double accel_y = d.getDouble("acceleration_y");
            double accel_z = d.getDouble("acceleration_z");
            double lat = d.getDouble("latitude");
            double lng = d.getDouble("longitude");

            app.setUnreadCount(++unreadCount);

            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] Received alert:
            // <message text>
            Date date = new Date();
            String logMessage;
            if (isACrash) {
                logMessage = "[" + new Timestamp(date.getTime()) + "] Received Crash Notification:\n";
            } else {
                logMessage = "[" + new Timestamp(date.getTime()) + "] Received Tracking Event:\n";
            }
            Log.d(TAG,logMessage + d.toString());

            String runningActivity = app.getCurrentRunningActivity();

            if (runningActivity != null) {
                app.setCarLocation(lat, lng);

                if (isACrash) {
                    app.setAccelData(new float[]{(float) accel_x, (float) accel_y, (float) accel_z});
                    Uri crashSound = Uri.parse("android.resource://" + app.getPackageName() + "/raw/crash");

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.drawable.ic_icon) // notification icon
                            .setContentTitle("Crash Alert!") // title for notification
                            .setContentText("Select to show crash location!") // message for notification
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setAutoCancel(true) // clear notification after click
                            .setSound(crashSound)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); //to show content in lock screen
                    Intent mapIntent = new Intent(context, MapsActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
                    Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                    actionIntent.putExtra(Constants.INTENT_DATA, Constants.CRASH_EVENT);
                    context.sendBroadcast(actionIntent);
                }

                Intent trackingIntent = new Intent((Constants.APP_ID + Constants.INTENT_TRACKING));
                trackingIntent.putExtra(Constants.INTENT_DATA, Constants.TRACKING_EVENT);
                context.sendBroadcast(trackingIntent);
            }
        }
    }
}
