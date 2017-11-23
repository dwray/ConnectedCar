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
 *******************************************************************************/
package com.solace.labs.cardemo.sensor.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.solace.labs.cardemo.sensor.R;
import com.solace.labs.cardemo.sensor.fragments.TutorialFragmentA;
import com.solace.labs.cardemo.sensor.fragments.TutorialFragmentB;
import com.solace.labs.cardemo.sensor.fragments.TutorialFragmentC;

/**
 * TutorialActivity provides a ViewPager with a few Fragments that provide
 * a brief overview of the application.
 */
public class TutorialPagerActivity extends FragmentActivity {
    public static final String TAG = TutorialPagerActivity.class.getName();

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_pagertabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        if(savedInstanceState != null) {
            int tabIndex = savedInstanceState.getInt("tabIndex");
            pager.setCurrentItem(tabIndex, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    return TutorialFragmentA.newInstance();
                case 1:
                    return TutorialFragmentB.newInstance();
                case 2:
                    return TutorialFragmentC.newInstance(getResources().getString(R.string.tutorial_pageC1_title), getResources().getString(R.string.tutorial_pageC1_text));
                case 3:
                    return TutorialFragmentC.newInstance(getResources().getString(R.string.tutorial_pageC2_title), getResources().getString(R.string.tutorial_pageC2_text));
                default:
                    return TutorialFragmentA.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Welcome";
                case 1:
                    return "How it works";
                case 2:
                    return "Quickstart Device";
                case 3:
                    return "Registered Device";
            }

            return null;
        }
    }
}
