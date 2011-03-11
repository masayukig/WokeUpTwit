package org.orzlabs.android.wokeup.preference;

import org.orzlabs.android.wokeup.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

public class WokeUpDialogPreference extends DialogPreference {

	public WokeUpDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WokeUpDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(getContext());
			sp.edit().clear().commit();
			Toast.makeText(getContext(),
					getContext().getText(R.string.ResetMsg),
					Toast.LENGTH_LONG).show();
		}
		super.onDialogClosed(positiveResult);
	}
}
