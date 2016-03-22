/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.bruce.demo.facebook.stetho;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.bruce.demo.BuildConfig;
import com.bruce.demo.R;

public class StethoDemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stetho_demo_activity);

        // Demonstrate that it is removed from the release build...
        if (!isStethoPresent()) {
            Toast.makeText(this, String.format("Stetho missing in %s build!", BuildConfig.BUILD_TYPE), Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.settings_btn).setOnClickListener(mMainButtonClicked);
        findViewById(R.id.apod_btn).setOnClickListener(mMainButtonClicked);
    }

    private static boolean isStethoPresent() {
        try {
            Class.forName("com.facebook.stetho.Stetho");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrefs().registerOnSharedPreferenceChangeListener(mToastingPrefListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPrefs().unregisterOnSharedPreferenceChangeListener(mToastingPrefListener);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this /* context */);
    }

    private final View.OnClickListener mMainButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.settings_btn) {
                StethoDemoSettingsActivity.show(StethoDemoActivity.this);
            } else if (id == R.id.apod_btn) {
                APODActivity.show(StethoDemoActivity.this);
            }
        }
    };

    private final SharedPreferences.OnSharedPreferenceChangeListener mToastingPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Object value = sharedPreferences.getAll().get(key);
            Toast.makeText(StethoDemoActivity.this, String.format("%s is now \\'%s\\'", key, value), Toast.LENGTH_SHORT).show();
        }
    };
}
