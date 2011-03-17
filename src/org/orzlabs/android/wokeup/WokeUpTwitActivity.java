package org.orzlabs.android.wokeup;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WokeUpTwitActivity extends Activity implements
		OnItemSelectedListener, OnItemClickListener {
	private static final String TAG = "WokeUpTwitActivity";
	private static final HashMap<String, String> DEFAULT_MSGS = new HashMap<String, String>();
	private ListView listView;
	private List<String> contents;

	private static final int SETTING_ITEM = 0;
	private static final int INFO_ITEM = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.list);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1);

		Intent intent = getIntent();
		if ("jp.r246.twicca.ACTION_EDIT_TWEET".equals(intent
				.getAction())) {
			String recv = intent.getStringExtra(Intent.EXTRA_TEXT);
			contents = getWokeupContent(recv);
		} else {
			contents = getWokeupContent("");
		}
		// 要素を追加
		for (String content : contents) {
			arrayAdapter.add(content);
		}

		// アダプタを設定
		listView.setAdapter(arrayAdapter);
		listView.setOnItemSelectedListener(this);
		listView.setOnItemClickListener(this);
	}

	private List<String> getWokeupContent(final String recv) {
		GregorianCalendar calendar = new GregorianCalendar();
		Map<String, String> wokeupMsg = getWokeupMsgMap();
		Log.d(TAG, "wokeupMsgMap:" + wokeupMsg);
		ArrayList<String> msgs = new ArrayList<String>();
		msgs.add(wokeupMsg.get(getText(R.string.WOKEUP_MSG_KEY)
				.toString()));
		msgs.add(wokeupMsg.get(getText(R.string.WOKEUP_MSG_KEY2)
				.toString()));
		ArrayList<String> contents = new ArrayList<String>();
		for (String msg : msgs) {
			try {
				msg = String.format(msg, calendar);
			} catch (Exception e) {
				Log.w(TAG, e.fillInStackTrace());
				msg = getText(R.string.CannotConvertMsg) + ":"
						+ e.getMessage();
			}
			contents.add(recv + msg);
		}

		return contents;
	}

	private Map<String, String> getWokeupMsgMap() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		DEFAULT_MSGS.put(getText(R.string.WOKEUP_MSG_KEY).toString(),
				getText(R.string.WokeUpMsg).toString());
		DEFAULT_MSGS.put(getText(R.string.WOKEUP_MSG_KEY2).toString(),
				getText(R.string.GoodNightMsg).toString());
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
		retMap.put(getText(R.string.WOKEUP_MSG_KEY2).toString(),
				pref.getString(getText(R.string.WOKEUP_MSG_KEY2)
						.toString(),
						DEFAULT_MSGS.get(getText(
								R.string.WOKEUP_MSG_KEY2)
								.toString())));

		return retMap;
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view,
			int position, long id) {
		Log.d(TAG, "onItemSelected():position:" + position + ", id:"
				+ id);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Log.d(TAG, "onItemClick():position:" + position + ", id:" + id);

		Intent intent = getIntent();
		intent.putExtra(Intent.EXTRA_TEXT, contents.get(position));
		setResult(RESULT_OK, intent);

		if (!"jp.r246.twicca.ACTION_EDIT_TWEET".equals(intent
				.getAction())) {
			try {
				Intent sendIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				// Intent intent = new
				// Intent(Intent.ACTION_VIEW,
				// Uri.parse("http://twitter.com"));
				sendIntent.setType("text/plain");
				sendIntent.putExtra(Intent.EXTRA_TEXT,
						contents.get(position));

				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.share_msg)));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "client not found",
						Toast.LENGTH_LONG).show();
			}
		}

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem itemQuit = menu.add(0, SETTING_ITEM, 0, "Setting");
		itemQuit.setIcon(android.R.drawable.ic_menu_preferences);
		MenuItem itemInfo = menu.add(0, INFO_ITEM, 1, "Info");
		itemInfo.setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	public String getVersionNumber() {
		String versionName = "";
		PackageManager pm = this.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(
					this.getPackageName(),
					PackageManager.GET_META_DATA);
			versionName += info.versionName;
		} catch (NameNotFoundException e) {
			versionName += "0";
		}
		return versionName;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case INFO_ITEM:
			TextView tv = new TextView(this);
			tv.setAutoLinkMask(Linkify.WEB_URLS);
			tv.setText("WokeUpTwit\n\n"
					+ getText(R.string.InfoMessage)
					+ getVersionNumber()
					+ "\n"
					+ "http://blog.orzlabs.org/search/label/wokeuptwit");
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle(R.string.InfoTitle);
			ad.setIcon(android.R.drawable.ic_menu_info_details);
			ad.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {

						}
					});
			ad.setView(tv);
			ad.create();
			ad.show();
			break;
		case SETTING_ITEM:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		default:
			// Do nothing. ignore.
			Log.d(TAG, "item id :" + item.getItemId());
		}
		return true;
	}
}