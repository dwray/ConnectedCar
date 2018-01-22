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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.solace.labs.cardemo.controller.IoTStarterApplication;
import com.solace.labs.cardemo.controller.R;
import com.solace.labs.cardemo.controller.activities.MainPagerActivity;
import com.solace.labs.cardemo.controller.activities.MapsActivity;
import com.solace.labs.cardemo.controller.activities.TutorialPagerActivity;
import com.solace.labs.cardemo.controller.activities.ProfilesActivity;
import com.solace.labs.cardemo.controller.utils.Constants;

/**
 * This class provides common properties and functions for fragment subclasses used in the application.
 */
public class IoTStarterPagerFragment extends Fragment {
    private final static String TAG = IoTStarterPagerFragment.class.getName();
    Context context;
    IoTStarterApplication app;
    BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Update strings in the fragment based on IoTStarterApplication values.
     */
    void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        // TODO: Update badge value
        //int unreadCount = app.getUnreadCount();
        //((MainPagerActivity) getActivity()).updateBadge(getActivity().getActionBar().getTabAt(2), unreadCount);
    }

    /**************************************************************************
     * Functions to handle the menu bar
     **************************************************************************/

    /**
     * Switch to the IoT fragment.
     */
    void openIoT() {
        Log.d(TAG, ".openIoT() entered");
        ((MainPagerActivity) getActivity()).setCurrentItem(1);
    }

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

        if (((MainPagerActivity) getActivity()).getCurrentItem() == 0) {
            app.setDeviceId(((EditText) getActivity().findViewById(R.id.deviceIDValue)).getText().toString());
            app.setOrganization(((EditText) getActivity().findViewById(R.id.organizationValue)).getText().toString());
            app.setAuthToken(((EditText) getActivity().findViewById(R.id.authTokenValue)).getText().toString());
            app.setVIN(((EditText) getActivity().findViewById(R.id.VINValue)).getText().toString());
        }

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profiles:
                openProfiles();
                return true;
            case R.id.action_tutorial:
                openTutorial();
                return true;
            case R.id.action_tracking:
                openTracking();
                return true;
            case R.id.action_clear_profiles:
                app.clearProfiles();
                return true;
            case R.id.action_crash_reset:
                app.setCrashed(false);
                app.setAccelData(new float[]{0.0f,0.0f,0.0f});
                app.setReceiveCount(0);
                app.setPublishCount(0);
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.CRASH_EVENT);
                context.sendBroadcast(actionIntent);
                // deliberately falling through to clear
            case R.id.clear:
                app.setUnreadCount(0);
                app.getMessageLog().clear();
                updateViewStrings();
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
