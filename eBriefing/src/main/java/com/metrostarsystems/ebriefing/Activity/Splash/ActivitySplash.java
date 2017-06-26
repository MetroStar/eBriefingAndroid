/*
Copyright (C) 2017 MetroStar Systems

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

The full license text can be found is the included LICENSE file.

You can freely use any of this software which you make publicly
available at no charge.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.metrostarsystems.ebriefing.Activity.Splash;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Activity.Library.ActivityDemo;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActivitySplash extends Activity {

	private MainApplication mApp;
	
	private ImageView mSplashImageView;
	
	private LinearLayout mOptionsLayout;
	private Button mRetryConnectionButton;
	private Button mConnectionSettingsButton;
	private Button mOfflineButton;
	private Button mExitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		mApp = (MainApplication) getApplicationContext();
		
		mSplashImageView = (ImageView) findViewById(R.id.imageView_splash);
		
		mOptionsLayout = (LinearLayout) findViewById(R.id.linearLayout_options);
		mConnectionSettingsButton = (Button) findViewById(R.id.button_connection_settings);
		mConnectionSettingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
			}
			
		});
		
		mRetryConnectionButton = (Button) findViewById(R.id.button_retry);
		mRetryConnectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideOptions();
				
				checkConnection();
			}
			
		});
		
		mOfflineButton = (Button) findViewById(R.id.button_offline);
		mOfflineButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivitySplash.this, ActivityDemo.class);
				
				startActivity(intent);
				finish();
			}
			
		});
		
		mExitButton = (Button) findViewById(R.id.button_exit);
		mExitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		checkConnection();
	}
	
	private void checkConnection() {
		if(Utilities.isNetworkAvailable(this)) {
			Intent intent = new Intent(ActivitySplash.this, ActivityDemo.class);
			
			startActivity(intent);
			finish();
		} else {
			showOptions();
		}
	}
	
	private void showOptions() {
		mSplashImageView.setVisibility(View.GONE);
		mOptionsLayout.setVisibility(View.VISIBLE);
	}
	
	private void hideOptions() {
		mSplashImageView.setVisibility(View.VISIBLE);
		mOptionsLayout.setVisibility(View.GONE);
	}
}
