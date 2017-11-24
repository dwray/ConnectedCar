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
package com.solace.labs.cardemo.sensor.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.solace.labs.cardemo.sensor.IoTStarterApplication;
import com.solace.labs.cardemo.sensor.R;
import com.solace.labs.cardemo.sensor.fragments.IoTPagerFragment;
import com.solace.labs.cardemo.sensor.fragments.LogPagerFragment;
import com.solace.labs.cardemo.sensor.fragments.LoginPagerFragment;
import com.solace.labs.cardemo.sensor.utils.Constants;

/**
 * TutorialActivity provides a ViewPager with a few Fragments that provide
 * a brief overview of the application.
 */
public class MainPagerActivity extends FragmentActivity {
    public static final String TAG = MainPagerActivity.class.getName();
    private static final int PERMISSIONS_REQUEST_LOCATION = 101;

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pagertabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        if(savedInstanceState != null) {
            int tabIndex = savedInstanceState.getInt("tabIndex");
            pager.setCurrentItem(tabIndex, false);
            Log.d(TAG, "savedinstancestate != null: " + tabIndex);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.FLASHLIGHT
            }, PERMISSIONS_REQUEST_LOCATION);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IoTStarterApplication app = (IoTStarterApplication) getApplication();
        if (!app.isTutorialShown()) {
            Intent tutorialIntent = new Intent(getApplicationContext(), TutorialPagerActivity.class);
            startActivity(tutorialIntent);
            app.setTutorialShown(true);
        }
    }

    /**
     * Save the current state of the activity. This is used to store the index of the currently
     * selected tab.
     * @param outState The state of the activity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int tabIndex = pager.getCurrentItem();
        outState.putInt("tabIndex", tabIndex);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, ".onConfigurationChanged entered()");
        super.onConfigurationChanged(newConfig);
    }

    public void setCurrentItem(int item) {
        Log.d(TAG, ".setCurrentItem() entered");
        pager.setCurrentItem(item, true);
    }

    public int getCurrentItem() {
        Log.d(TAG, ".getCurrentItem() entered");
        return pager.getCurrentItem();
    }

    /**
     * Adapter for the ViewPager. Adds the tutorial fragments to the pager.
     */
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    Log.d(TAG, "init loginpagerfragment");
                    return LoginPagerFragment.newInstance();
                case 1:
                    Log.d(TAG, "init iotpagerfragment");
                    return IoTPagerFragment.newInstance();
                case 2:
                    Log.d(TAG, "init logpagerfragment");
                    return LogPagerFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return Constants.LOGIN_LABEL;
                case 1:
                    return Constants.IOT_LABEL;
                case 2:
                    return Constants.LOG_LABEL;
                default:
                    return null;
            }
        }
    }
}
