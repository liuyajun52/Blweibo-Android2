package com.blacklighting.blwebio_android;

import android.app.Activity;
import android.os.Bundle;

import com.blacklighting.blwebio_android.oauth.GetRequireTokenTask;

public class OauthActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);
//		new GetRequireTokenTask().execute();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.oauth, menu);
//		return true;
//	}

}
