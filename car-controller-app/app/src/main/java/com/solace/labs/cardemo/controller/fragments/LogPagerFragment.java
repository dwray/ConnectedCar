/*******************************************************************************
 * Copyright (c) 2014-2015 IBM Corp.
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
 *    David Wray - updated for Solace CarDemo
 *******************************************************************************/
package com.solace.labs.cardemo.controller.fragments;

import android.app.AlertDialog;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.solace.labs.cardemo.controller.IoTStarterApplication;
import com.solace.labs.cardemo.controller.R;
import com.solace.labs.cardemo.controller.activities.MapsActivity;
import com.solace.labs.cardemo.controller.activities.ProfilesActivity;
import com.solace.labs.cardemo.controller.activities.TutorialPagerActivity;
import com.solace.labs.cardemo.controller.utils.Constants;

/**
 * The Log fragment displays text command messages that have been received by the application.
 */
public class LogPagerFragment extends ListFragment {
    private final static String TAG = LogPagerFragment.class.getName();
    private IoTStarterApplication app;
    private BroadcastReceiver broadcastReceiver;

    private ArrayAdapter<String> listAdapter;

    /**************************************************************************
     * Fragment functions for establishing the fragment
     **************************************************************************/

    public static LogPagerFragment newInstance() {
        return new LogPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log, container, false);
    }

    /**
     * Called when the fragment is resumed.
     */
    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        Context context = getActivity().getApplicationContext();

        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);

        app = (IoTStarterApplication) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);
        app.setUnreadCount(0);

        listAdapter = new ArrayAdapter<String>(context, R.layout.list_item, app.getMessageLog());
        listView.setAdapter(listAdapter);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering LogBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //              Log.d(TAG, ".onReceive() - Received intent for logBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_LOG));

        // Set color to black in case it somehow was leftover from iot fragment?
        int color = getResources().getColor(R.color.background_main);
        if (color != 0) {
            getView().setBackgroundColor(color);
        }
        // initialise
        initializeLogActivity();
    }

    /**
     * Called when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            // Do nothing
        }
        super.onDestroy();
    }

    /**
     * Initializing onscreen elements and shared properties
     */
    private void initializeLogActivity() {
        Log.d(TAG, ".initializeLogActivity() entered");
    }

    private void processIntent(Intent intent) {
        //   Log.d(TAG, ".processIntent() entered");

        app.setUnreadCount(0);

        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.TEXT_EVENT)) {
            listAdapter.notifyDataSetInvalidated();
        } else if (data.equals(Constants.ALERT_EVENT)) {
            listAdapter.notifyDataSetInvalidated();
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
    }

    /**************************************************************************
     * Functions to handle the iot_menu bar
     **************************************************************************/

    private void openProfiles() {
        Log.d(TAG, ".openProfiles() entered");
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion < Build.VERSION_CODES.HONEYCOMB) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Profiles Unavailable")
                    .setMessage("Android 3.0 or greater required for profiles.")
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        } else {
            Intent profilesIntent = new Intent(getActivity().getApplicationContext(), ProfilesActivity.class);
            startActivity(profilesIntent);
        }
    }

    void openTutorial() {
        Log.d(TAG, ".openTutorial() entered");
        Intent tutorialIntent = new Intent(getActivity().getApplicationContext(), TutorialPagerActivity.class);
        startActivity(tutorialIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, ".onCreateOptions() entered");
        getActivity().getMenuInflater().inflate(R.menu.menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Process the selected iot_menu item.
     *
     * @param item The selected iot_menu item.
     * @return true in all cases.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, ".onOptionsItemSelected() entered");
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profiles:
                if (!ProfilesActivity.class.getName().equals(app.getCurrentRunningActivity())) {
                    openProfiles();
                }
                return true;
            case R.id.action_tutorial:
                openTutorial();
                return true;
            case R.id.action_tracking:
                openTracking();
                return true;
            case R.id.action_crash_reset:
                app.setCrashed(false);
                app.setAccelData(new float[]{0.0f,0.0f,0.0f});
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.CRASH_EVENT);
                getActivity().getApplicationContext().sendBroadcast(actionIntent);
                // deliberately falling through to clear
            case R.id.clear:
                app.setUnreadCount(0);
                app.getMessageLog().clear();
                listAdapter.notifyDataSetInvalidated();
                return true;
            case R.id.action_clear_profiles:
                app.clearProfiles();
                return true;
            default:
                if (item.getTitle().equals(getResources().getString(R.string.app_name))) {
                    getActivity().openOptionsMenu();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
        }
    }

    private void openTracking() {
        Log.d(TAG, ".openTracking() entered");
        Intent trackingIntent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
        trackingIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getActivity().startActivityIfNeeded(trackingIntent, 0);
    }
}
