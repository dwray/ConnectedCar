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
package com.solace.labs.cardemo.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.solace.labs.cardemo.controller.R;
import com.solace.labs.cardemo.controller.activities.MainPagerActivity;

/**
 * Created by mprobert on 4/10/2015.
 */
public class TutorialFragmentA extends Fragment {
    private final static String TAG = TutorialFragmentA.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tutorial_fragment_a, container, false);

        Button button = (Button) v.findViewById(R.id.skipButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSkip();
            }
        });

        return v;
    }

    public static TutorialFragmentA newInstance() {

        TutorialFragmentA f = new TutorialFragmentA();

        return f;
    }

    private void handleSkip() {
        Log.d(TAG, ".handleSkip() entered");
        Intent loginIntent = new Intent(getActivity().getApplicationContext(), MainPagerActivity.class);
        startActivity(loginIntent);
    }
}
