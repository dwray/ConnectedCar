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
 *    Aldo Eisma - add bearing and speed to acceleration message
 *    David Wray - updated for Solace CarDemo
 *******************************************************************************/
package com.solace.labs.cardemo.sensor.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.solace.labs.cardemo.sensor.IoTStarterApplication;
import com.solace.labs.cardemo.sensor.iot.IoTClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

/**
 * This class implements the SensorEventListener interface. When the application creates the MQTT
 * connection, it registers listeners for the accelerometer and magnetometer sensors.
 * Output from these sensors is used to publish accel event messages.
 */
public class DeviceSensor implements SensorEventListener {
    private final String TAG = DeviceSensor.class.getName();
    private static DeviceSensor instance;
    private final IoTStarterApplication app;
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetometer;
    private final Context context;
    private final int minCrashInterval_ms = 1000;
    private Timer crashTimer;
    private Timer locationTimer;
    private long tripId;
    private boolean isEnabled = false;
    private long lastCrash = System.currentTimeMillis();

    private DeviceSensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        app = (IoTStarterApplication) context.getApplicationContext();
    }

    /**
     * @param context The application context for the object.
     * @return The MqttHandler object for the application.
     */
    public static DeviceSensor getInstance(Context context) {
        if (instance == null) {
            Log.i(DeviceSensor.class.getName(), "Creating new DeviceSensor");
            instance = new DeviceSensor(context);
        }
        return instance;
    }

    /**
     * Register the listeners for the sensors the application is interested in.
     */
    public void enableSensor() {
        Log.i(TAG, ".enableSensor() entered");
        if (!isEnabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
            tripId = System.currentTimeMillis()/ 1000;
            crashTimer = new Timer();
            locationTimer = new Timer();
            // crashTimer at 5 ms
            crashTimer.scheduleAtFixedRate(new SendSensorTimerTask(), 5, 5);
            // locationTimer at 5 sec
            locationTimer.scheduleAtFixedRate(new SendLocationTimerTask(), 1000, 5000);
            isEnabled = true;
        }
    }

    /**
     * Disable the listeners.
     */
    public void disableSensor() {
        Log.d(TAG, ".disableSensor() entered");
        if (crashTimer != null && isEnabled) {
            crashTimer.cancel();
            locationTimer.cancel();
            sensorManager.unregisterListener(this);
            isEnabled = false;
        }
    }

    // Values used for accelerometer, magnetometer, orientation sensor data
    private float[] G = new float[3]; // gravity x,y,z
    private float[] M = new float[3]; // geomagnetic field x,y,z
    private final float[] R = new float[9]; // rotation matrix
    private final float[] I = new float[9]; // inclination matrix
    private float[] O = new float[3]; // orientation azimuth, pitch, roll
    private float yaw;

    /**
     * Callback for processing data from the registered sensors. Accelerometer and magnetometer
     * data are used together to get orientation data.
     *
     * @param sensorEvent The event containing the sensor data values.
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.v(TAG, "onSensorChanged() entered");
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.v(TAG, "Accelerometer -- x: " + sensorEvent.values[0] + " y: "
                    + sensorEvent.values[1] + " z: " + sensorEvent.values[2]);
            G = sensorEvent.values;

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.v(TAG, "Magnetometer -- x: " + sensorEvent.values[0] + " y: "
                    + sensorEvent.values[1] + " z: " + sensorEvent.values[2]);
            M = sensorEvent.values;
        }
        if (G != null && M != null) {
            if (SensorManager.getRotationMatrix(R, I, G, M)) {
                float[] previousO = O.clone();
                O = SensorManager.getOrientation(R, O);
                yaw = O[0] - previousO[0];
                Log.v(TAG, "Orientation: azimuth: " + O[0] + " pitch: " + O[1] + " roll: " + O[2] + " yaw: " + yaw);
            }
        }
    }

    /**
     * Callback for the SensorEventListener interface. Unused.
     *
     * @param sensor The sensor that changed.
     * @param i The change in accuracy for the sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged() entered");
    }

    /**
     * Timer task for sending accel data on 1ms intervals
     */
    private class SendSensorTimerTask extends TimerTask {

        private void logToPage(String message){
            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] message
            Date date = new Date();
            String logMessage = "["+new Timestamp(date.getTime())+"]:"+message;
            app.getMessageLog().add(logMessage);
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
            context.sendBroadcast(actionIntent);
        }


        /**
         * Publish a crash event message.
         */
        @Override
        public void run() {
            Log.v(TAG, "SendSensorTimerTask.run() entered");

            float thisG[] = G.clone();

            double lon = 0.0;
            double lat = 0.0;
            float heading = 0.0f;
            float speed = 0.0f;

            float crashpos = 33.0f;
            String g = app.getgLevel();
            try {
                float tempG = Float.parseFloat(g);
                crashpos = tempG;
            } catch (Exception e) {
                // ignore parse exception
            }
            float crashneg = crashpos * -1.0F;
            if (app.getCurrentLocation() != null) {
                lon = app.getCurrentLocation().getLongitude();
                lat = app.getCurrentLocation().getLatitude();
                heading = app.getCurrentLocation().getBearing();
                speed = app.getCurrentLocation().getSpeed() * 3.6f;
            }
            // G Value to detect a crash set in the Login page
            if ((thisG[0] > crashpos) || (thisG[1] > crashpos) || (thisG[2] > crashpos) || (thisG[0] < crashneg) || (thisG[1] < crashneg) || (thisG[2] < crashneg)) {
                // this is to stop sending 10s of crash messages during a single crash, we'll only send a crash message once every <minCrashInterval_ms> milliseconds.
                if ((System.currentTimeMillis() - lastCrash) < minCrashInterval_ms) {
                    return;
                }
                lastCrash = System.currentTimeMillis();
                String messageData = MessageFactory.getAccelMessage(thisG, O, yaw, lon, lat, heading, speed, tripId);
                Log.d(TAG,"CRASH DETECTED!");

                try {
                    // create ActionListener to handle message published results
                    MyIoTActionListener listener = new MyIoTActionListener(context, Constants.ActionStateStatus.PUBLISH);
                    IoTClient iotClient = IoTClient.getInstance(context);

                    iotClient.publishEvent(Constants.CRASH_EVENT, "json", messageData, 0, false, listener);
                    iotClient.publishEvent(Constants.GLEVEL,"text","Send SMS to "+iotClient.getGLevel(), 0, false, listener);

/* not sending SMS any more
                    SmsManager sms = SmsManager.getDefault();
                    try {
                        sms.sendTextMessage(iotClient.getGLevel(), null, "Help! I've been in an accident!", null, null);
                    } catch (Exception e) {
                        // eat it, we're not that worried about SMS, probably invalid number
                    }
*/
                    if (app.getDisplayActivity() != null) {
                        app.getDisplayActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(app.getDisplayActivity(), "Emergency Alert Sent.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    int count = app.getPublishCount();
                    app.setPublishCount(++count);

                    //String runningActivity = app.getCurrentRunningActivity();
                    //if (runningActivity != null && runningActivity.equals(IoTPagerFragment.class.getName())) {
                    Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                    actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_PUBLISHED);
                    context.sendBroadcast(actionIntent);
                    //}
                } catch (MqttException e) {
                    Log.d(TAG, ".run() received exception on publishEvent()");
                }
                app.setAccelData(thisG);
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.CRASH_EVENT);
                context.sendBroadcast(actionIntent);
            }

            //String runningActivity = app.getCurrentRunningActivity();
            //if (runningActivity != null && runningActivity.equals(IoTPagerFragment.class.getName())) {

            //}
        }
    }
    private class SendLocationTimerTask extends TimerTask {

        private void logToPage(String message){
            // Log message with the following format:
            // [yyyy-mm-dd hh:mm:ss.S] message
            Date date = new Date();
            String logMessage = "["+new Timestamp(date.getTime())+"]:"+message;
            app.getMessageLog().add(logMessage);
            // don't send updates for tracking events
//            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
//            actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
//            context.sendBroadcast(actionIntent);
        }


        /**
         * Publish a location event message.
         */
        @Override
        public void run() {
            Log.v(TAG, "SendTrackerTimerTask.run() entered");

            double lon = 0.0;
            double lat = 0.0;
            float heading = 0.0f;
            float speed = 0.0f;
            if (app.getCurrentLocation() != null) {
                lon = app.getCurrentLocation().getLongitude();
                lat = app.getCurrentLocation().getLatitude();
                heading = app.getCurrentLocation().getBearing();
                speed = app.getCurrentLocation().getSpeed() * 3.6f;
            }
            String messageData = MessageFactory.getAccelMessage(G, O, yaw, lon, lat, heading, speed, tripId);

            try {
                // create ActionListener to handle message published results
                MyIoTActionListener listener = new MyIoTActionListener(context, Constants.ActionStateStatus.PUBLISH);
                IoTClient iotClient = IoTClient.getInstance(context);
                iotClient.publishEvent(Constants.TRACKING_EVENT, "json", messageData, 0, false, listener);

                int count = app.getPublishCount();
                app.setPublishCount(++count);

                //String runningActivity = app.getCurrentRunningActivity();
                //if (runningActivity != null && runningActivity.equals(IoTPagerFragment.class.getName())) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_PUBLISHED);
                context.sendBroadcast(actionIntent);
                //}
            } catch (MqttException e) {
                Log.d(TAG, ".run() received exception on publishEvent()");
            }

            // app.setAccelData(G);

            //String runningActivity = app.getCurrentRunningActivity();
            //if (runningActivity != null && runningActivity.equals(IoTPagerFragment.class.getName())) {
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.TRACKING_EVENT);
            context.sendBroadcast(actionIntent);
            //}
        }
    }
}
