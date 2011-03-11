package org.orzlabs.android.wokeup;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class WokeUpTwitActivity extends Activity {
	private static final String TAG = "WokeUpTwitActivity";
	private static final HashMap<String, String> DEFAULT_MSGS = new HashMap<String, String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent = getIntent();
		if ("jp.r246.twicca.ACTION_EDIT_TWEET".equals(intent
				.getAction())) {
			String recv = intent.getStringExtra(Intent.EXTRA_TEXT);
			String content = getWokeupContent(recv);
			intent.putExtra(Intent.EXTRA_TEXT, content);
		} else {
			Intent sendIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			// Intent intent = new Intent(Intent.ACTION_VIEW,
			// Uri.parse("http://twitter.com"));
			sendIntent.setType("text/plain");
			String content = getWokeupContent("");
			sendIntent.putExtra(Intent.EXTRA_TEXT, content);
			try {
				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.share_msg)));
				// startActivity(intent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "client not found",
						Toast.LENGTH_LONG).show();
			}
		}
		setResult(RESULT_OK, intent);

		finish();
	}

	private String getWokeupContent(final String recv) {
		GregorianCalendar calendar = new GregorianCalendar();
		Map<String, String> wokeupMsg = getWokeupMsgMap();
		Log.d(TAG, "wokeupMsgMap:" + wokeupMsg);
		String msgs = wokeupMsg.get(getText(R.string.WOKEUP_MSG_KEY)
				.toString());
		try {
			msgs = String.format(msgs, calendar);
		} catch (Exception e) {
			Log.w(TAG, e.fillInStackTrace());
			msgs = getText(R.string.CannotConvertMsg) + ":"
					+ e.getMessage();
		}
		String content = recv + msgs;

		return content;
	}

	private String normalizeMsgs(final String msgs) {
		if (msgs.indexOf("%") == msgs.lastIndexOf("%")) {
			return msgs;
		}
		int firstIndex = msgs.indexOf("%");
		String manyPctStr = msgs.substring(firstIndex + 1);
		String nonePctStr = manyPctStr.replace("%", "");

		return msgs.substring(0, firstIndex + 1) + nonePctStr;
	}

	private Map<String, String> getWokeupMsgMap() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		DEFAULT_MSGS.put(getText(R.string.WOKEUP_MSG_KEY).toString(),
				getText(R.string.WokeUpMsg).toString());
		if (pref == null) {
			Log.d(TAG, "pref is null.");
			return DEFAULT_MSGS;
		}
		HashMap<String, String> retMap = new HashMap<String, String>();
		retMap.put(getText(R.string.WOKEUP_MSG_KEY).toString(),
				pref.getString(getText(R.string.WOKEUP_MSG_KEY)
						.toString(),
						DEFAULT_MSGS.get(getText(
								R.string.WOKEUP_MSG_KEY)
								.toString())));

		return retMap;
	}
}