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
package com.solace.labs.cardemo.sensor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.solace.labs.cardemo.sensor.R;
import com.solace.labs.cardemo.sensor.activities.MainPagerActivity;

/**
 * Created by mprobert on 4/10/2015.
 */
public class TutorialFragmentC extends Fragment {
    private final static String TAG = TutorialFragmentC.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tutorial_fragment_c, container, false);

        TextView tvTitle = (TextView) v.findViewById(R.id.tutorialCOptionTitle);
        TextView tvText = (TextView) v.findViewById(R.id.tutorialCOptionText);
        tvTitle.setText(getArguments().getString("title"));
        tvText.setText(getArguments().getString("msg"));

        Button button = (Button) v.findViewById(R.id.finishButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFinish();
            }
        });

        return v;
    }

    public static TutorialFragmentC newInstance(String title, String text) {

        TutorialFragmentC f = new TutorialFragmentC();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private void handleFinish() {
        Log.d(TAG, ".handleFinish() entered");
        Intent loginIntent = new Intent(getActivity().getApplicationContext(), MainPagerActivity.class);
        startActivity(loginIntent);
    }
}
