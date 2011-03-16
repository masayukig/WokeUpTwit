package org.orzlabs.android.wokeup;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class WokeUpTwitActivity extends Activity implements
		OnItemSelectedListener, OnItemClickListener {
	private static final String TAG = "WokeUpTwitActivity";
	private static final HashMap<String, String> DEFAULT_MSGS = new HashMap<String, String>();
	private ListView listView;
	private List<String> contents;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
		setContentView(R.layout.main);

		// XMLで定義したandroid:idの値を指定してListViewを取得します。
		listView = (ListView) findViewById(R.id.list);

		// ListViewに表示する要素を保持するアダプタを生成します。
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1);

		Intent intent = getIntent();
		if ("jp.r246.twicca.ACTION_EDIT_TWEET".equals(intent
				.getAction())) {

			String recv = intent.getStringExtra(Intent.EXTRA_TEXT);
			contents = getWokeupContent(recv);
		} else {
			Intent sendIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			// Intent intent = new Intent(Intent.ACTION_VIEW,
			// Uri.parse("http://twitter.com"));
			sendIntent.setType("text/plain");
			contents = getWokeupContent("");
			// intent.putExtra(Intent.EXTRA_TEXT, content);
			try {
				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.share_msg)));
				// startActivity(intent);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "client not found",
						Toast.LENGTH_LONG).show();
			}
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

	@Override
	public void onResume() {
		super.onResume();
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
		msgs.add(wokeupMsg.get(getText(R.string.WOKEUP_MSG_KEY3)
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
				getText(R.string.WokeUpMsg2).toString());
		DEFAULT_MSGS.put(getText(R.string.WOKEUP_MSG_KEY3).toString(),
				getText(R.string.WokeUpMsg3).toString());
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
		retMap.put(getText(R.string.WOKEUP_MSG_KEY3).toString(),
				pref.getString(getText(R.string.WOKEUP_MSG_KEY3)
						.toString(),
						DEFAULT_MSGS.get(getText(
								R.string.WOKEUP_MSG_KEY3)
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

		finish();

	}
}