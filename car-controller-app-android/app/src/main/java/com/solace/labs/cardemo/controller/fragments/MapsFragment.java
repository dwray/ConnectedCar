/*******************************************************************************
 * Copyright (c) 2017 Solace Corporation
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
 *    David Wray - initial contribution
 *******************************************************************************/
package com.solace.labs.cardemo.controller.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.solace.labs.cardemo.controller.IoTStarterApplication;
import com.solace.labs.cardemo.controller.R;
import com.solace.labs.cardemo.controller.activities.MainPagerActivity;
import com.solace.labs.cardemo.controller.activities.ProfilesActivity;
import com.solace.labs.cardemo.controller.activities.TutorialPagerActivity;
import com.solace.labs.cardemo.controller.utils.Constants;

public class MapsFragment extends IoTStarterPagerFragment {

    private static final String TAG = MapsFragment.class.getName();
    private GoogleMap mMap;

    private IoTStarterApplication app;
    private Marker marker;
    private BroadcastReceiver broadcastReceiver;
    private boolean ready = false;
    private SupportMapFragment mSupportMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        if (mSupportMapFragment == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mSupportMapFragment = SupportMapFragment.newInstance();
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                /**
                 * Manipulates the map once available.
                 * This callback is triggered when the map is ready to be used.
                 * This is where we can add markers or lines, add listeners or move the camera.
                 * If Google Play services is not installed on the device, the user will be prompted to install
                 * it inside the SupportMapFragment. This method will only be triggered once the user has
                 * installed Google Play services and returned to the app.
                 */
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    /**
                     * Manipulates the map once available.
                     * This callback is triggered when the map is ready to be used.
                     * This is where we can add markers or lines, add listeners or move the camera.
                     * If Google Play services is not installed on the device, the user will be prompted to install
                     * it inside the SupportMapFragment. This method will only be triggered once the user has
                     * installed Google Play services and returned to the app.
                     */
                    if (googleMap != null) {

                        mMap = googleMap;
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.getUiSettings().setAllGesturesEnabled(true);

                        if (app == null) {
                            app = (IoTStarterApplication) getActivity().getApplication();
                        }

                        if (app.getCarLocation() != null) {
                            // create and add marker if  null
                            String title = "Car Location";
                            if (app.hasCrashed()) {
                                title = "Crash Location";
                            }
                            MarkerOptions a = new MarkerOptions().position(app.getCarLocation()).title(title);
                            marker = mMap.addMarker(a);
                            marker.showInfoWindow();
                            marker.setPosition(app.getCarLocation());
                            // add some embellishments
                            // remove regular marker if present after crash
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(app.getCarLocation(), 16.0f));
                        }
                        ready = true;

                    }

                }
            });

        }
//        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();

        return v;
    }

    /**
     * Called when the activity is destroyed.
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
        app = (IoTStarterApplication) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering LogBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_TRACKING));
    }

    void processIntent(Intent intent) {
        //intent passed in null from refresh so need to change if actually using it
        if (app == null) {
            app = (IoTStarterApplication) getActivity().getApplication();
        }
        if (mMap != null && app.getCarLocation() != null && ready) {
            // create and add marker if  null
            String title = "Car Location";
            if (app.hasCrashed()) {
                title = "Crash Location";
            }
            if (marker == null ) {
                MarkerOptions a = new MarkerOptions().position(app.getCarLocation()).title(title);
                marker = mMap.addMarker(a);
            }
            marker.setTitle(title);
            marker.showInfoWindow();
            marker.setPosition(app.getCarLocation());
            // add some embellishments
            // remove regular marker if present after crash
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(app.getCarLocation(), 16.0f));
        }
    }

    /**************************************************************************
     * Functions to handle the menu bar
     **************************************************************************/

    private void openProfiles() {
        Log.d(TAG, ".openProfiles() entered");
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion < Build.VERSION_CODES.HONEYCOMB) {
            new AlertDialog.Builder(getActivity().getApplicationContext())
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

    private void openHome() {
        Log.d(TAG, ".openHome() entered");
        Intent homeIntent = new Intent(getActivity().getApplicationContext(), MainPagerActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, ".onCreateOptions() entered");
        getActivity().getMenuInflater().inflate(R.menu.tracker_menu, menu);
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

        IoTStarterApplication app = (IoTStarterApplication) getActivity().getApplication();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_profiles:
                openProfiles();
                return true;
            case R.id.action_tutorial:
                openTutorial();
                return true;
            case R.id.action_home:
                openHome();
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
                getActivity().getApplicationContext().sendBroadcast(actionIntent);
                // deliberately falling through to clear
            case R.id.clear:
                app.setUnreadCount(0);
                app.getMessageLog().clear();
                return true;
            case R.id.action_tracking:
                processIntent(null);
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

    public static Fragment newInstance() {
        return new MapsFragment();
    }
}
