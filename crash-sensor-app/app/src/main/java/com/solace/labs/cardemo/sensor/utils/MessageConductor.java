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
package com.solace.labs.cardemo.sensor.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.solace.labs.cardemo.sensor.IoTStarterApplication;
import com.solace.labs.cardemo.sensor.R;
import com.solace.labs.cardemo.sensor.activities.ProfilesActivity;
import com.solace.labs.cardemo.sensor.fragments.IoTPagerFragment;
import com.solace.labs.cardemo.sensor.fragments.LogPagerFragment;
import com.solace.labs.cardemo.sensor.fragments.LoginPagerFragment;
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
    private MediaPlayer playerHorn;
    private MediaPlayer playerStart;


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

        if (topic.contains(Constants.COLOR_COMMAND)) {
            Log.d(TAG, "Color Event");
            int r = d.getInt("r");
            int g = d.getInt("g");
            int b = d.getInt("b");
            // alpha value received is 0.0 < a < 1.0 but Color.argb expects 0 < a < 255
            int alpha = (int)(d.getDouble("alpha")*255.0);
            if ((r > 255 || r < 0) ||
                    (g > 255 || g < 0) ||
                    (b > 255 || b < 0) ||
                    (alpha > 255 || alpha < 0)) {
                return;
            }

            app.setColor(Color.argb(alpha, r, g, b));

            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.COLOR_COMMAND);
            context.sendBroadcast(actionIntent);

        } else if (topic.contains(Constants.LIGHT_COMMAND)) {
            Log.d(TAG, "Light Event");
            // Set light on or off, or toggle light otherwise.
            String light = d.optString("light");
            Boolean newState;
            if ("on".equals(light)) {
                newState = true;
            } else if ("off".equals(light)) {
                newState = false;
            } else {
                newState = null;
            }
            app.handleLightMessage(newState);
        } else if (topic.contains(Constants.TEXT_EVENT)) {
            int unreadCount = app.getUnreadCount();
            String messageText = d.getString("text");
            app.setUnreadCount(++unreadCount);

            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] Received text:
            // <message text>
            Date date = new Date();
            String logMessage = "["+new Timestamp(date.getTime())+"] Received Text:\n";
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
        } else if (topic.contains(Constants.ALERT_EVENT)) {
            // save payload in an arrayList
            int unreadCount = app.getUnreadCount();
            String messageText = d.getString("text");
            app.setUnreadCount(++unreadCount);

            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] Received alert:
            // <message text>
            Date date = new Date();
            String logMessage = "["+new Timestamp(date.getTime())+"] Received Alert:\n";
            app.getMessageLog().add(logMessage + messageText);

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null) {
                Uri crashSound =  Uri.parse("android.resource://"+app.getPackageName()+"/raw/crash");

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.drawable.ic_icon) // notification icon
                        .setContentTitle("Notification!") // title for notification
                        .setContentText("Hello world") // message for notification
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setAutoCancel(true) // clear notification after click
                        .setSound(crashSound)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//to show content in lock screen
                Intent alertIntent = new Intent(context, LogPagerFragment.class);
                PendingIntent pi = PendingIntent.getActivity(context,0,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pi);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());

            }

        } else if (topic.contains(Constants.ENGINE_COMMAND)) {
            // Play appropriate sound
            int unreadCount = app.getUnreadCount();
            app.setUnreadCount(++unreadCount);

            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] Received alert:
            // <message text>

            Date date = new Date();
            String logMessage = "[" + new Timestamp(date.getTime()) + "] Received Sound Event:\n";
            app.getMessageLog().add(logMessage + Constants.ENGINE_COMMAND);

            playerStart = MediaPlayer.create(context, R.raw.startengine);
            playerStart.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release(); // finish current activity
                    mp = null;
                }
            });

            playerStart.start();

        } else if (topic.contains(Constants.HORN_COMMAND)) {
                // Play appropriate sound
                int unreadCount = app.getUnreadCount();
                app.setUnreadCount(++unreadCount);

                // Log message with the following format:
                // [yyyy-mm-dd hh:mm:ss.S] Received alert:
                // <message text>

                Date date = new Date();
                String logMessage = "[" + new Timestamp(date.getTime()) + "] Received Sound Event:\n";
                app.getMessageLog().add(logMessage + Constants.HORN_COMMAND);

                playerHorn = MediaPlayer.create(context, R.raw.carhorn);
                playerHorn.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release(); // finish current activity
                        mp = null;
                    }
                });

                playerHorn.start();
        } else if (topic.contains(Constants.LOCK_COMMAND)) {
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.LOCK_COMMAND);
            String lockState = d.optString("lock");

            actionIntent.putExtra(Constants.LOCK_STATE, lockState);
            context.sendBroadcast(actionIntent);
        }
    }
}
