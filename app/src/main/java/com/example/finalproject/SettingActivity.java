package com.example.finalproject;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextClock;


public class SettingActivity extends PreferenceActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        //set up too bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_name); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

    }

    public static class PrefsFragment extends PreferenceFragment {
        // PreferenceFragment is deprecated as of API 28
        // PreferenceFragmentCompat, it's replacement, will generate errors if you try to use it in the same way here
        // Something about framework fragments and support fragments not being compatible
        // If you'd like to try using PreferenceFragmentCompat, add this line to build.gradle
        // implementation 'com.android.support:preference-v7:28.0.0'

        //Docs say that fragments should have an empty constructor
        public PrefsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //This handy feature means you don't have to declare SharedPreferences
            PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.first_fragment);
        }

    }
}
