package org.orzlabs.android.wokeup;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class SettingActivity extends PreferenceActivity {
	private static final String TAG = "SettingActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefsc);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen scr,
			Preference pref) {
		Log.d(TAG, "onPreferenceTreeClick() : key:" + pref.getKey());
		return super.onPreferenceTreeClick(scr, pref);
	}

	@Override
	public void onContentChanged() {
		Log.d(TAG, "onContentChanged() called.");
		super.onContentChanged();
	}

}
