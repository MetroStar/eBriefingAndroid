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

package com.metrostarsystems.ebriefing.Activity.Library;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Activity.About.ActivityAbout;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Services.AuthenticationService.AuthenticateTask;
import com.metrostarsystems.ebriefing.Services.AuthenticationService.AuthenticateTask.AuthenticateTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2010Task;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2013Task;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2010Task.ServerInfo2010TaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2013Task.ServerInfo2013TaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ActivityDemo extends Activity implements AuthenticateTaskListener,
														ServerInfo2010TaskListener,
														ServerInfo2013TaskListener {
	
	private static final String TAG = ActivityDemo.class.getSimpleName();
	
	private MainApplication 	mApp;
	private ActionBar			mActionBar;
	private ServerConnection	mConnection;
	private Dialog 				mConnectionDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_server);
		
		mApp = (MainApplication) getApplicationContext();
		mActionBar = getActionBar();
		
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setTitle("Select a Library");
		
		boolean showabout  = (Boolean) mApp.preferences().get("showabout", true);
		if(showabout) {
			Intent intent = new Intent(ActivityDemo.this, ActivityAbout.class);
			
			mApp.preferences().set("showabout", false);
			startActivity(intent);
		}
		
		LinearLayout helpLayout = (LinearLayout) findViewById(R.id.server_help);
		
		Button learnMoreButton = (Button) helpLayout.findViewById(R.id.button_learn_more);
		learnMoreButton.setOnClickListener(new OnClickListener() {
			
						@Override
						public void onClick(View v) {
							String url = "****"; // service url
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							
							startActivity(i);
						}
						
					});

		LinearLayout demoLibraryButton = (LinearLayout) findViewById(R.id.button_demo_library);
		demoLibraryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mConnectionDialog.show();
				mConnection = ServerConnection.demo(mApp);
				
				Log.i(TAG, "Authenticating...");
				new AuthenticateTask(ActivityDemo.this).execute(mConnection);
			}
			
		});

		LinearLayout enterpriseLibraryButton = (LinearLayout) findViewById(R.id.button_enterprise_library);
		enterpriseLibraryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityDemo.this, ActivityEnterprise.class);
				
				startActivity(intent);
				finish();
			}
			
		});
		
		mConnectionDialog = new Dialog(this);
		mConnectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mConnectionDialog.setContentView(R.layout.activity_enterprise_connection_dialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_server_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    	case R.id.activity_server_cancel:
	    		finish();
	    		return true;
		    default:
		        break;
	    }

	    return false;
	}
	
	@Override
	public void onAuthenticateTaskFinished(HttpResponse result) {
		if(result == null) {
			mConnectionDialog.dismiss();
			return;
		}
		
		if(result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			Log.i(TAG, "Checking for 2013");
			new ServerInfo2013Task(ActivityDemo.this, mConnection).execute();
		}
	}

	@Override
	public void onServerInfo2013TaskFinishedListener(ServerInfoObject2 result) {
		if(result != null && result.isValid()) {
			Log.i(TAG, "2013 good...");
			go(result);
		} else {
			Log.i(TAG, "Checking for 2010");
			new ServerInfo2010Task(ActivityDemo.this, mConnection).execute();
		}
	}

	@Override
	public void onServerInfo2010TaskFinishedListener(ServerInfoObject2 result) {
		if(result != null && result.isValid()) {
			Log.i(TAG, "2010 good...");
			go(result);
		} else {
			Log.i(TAG, "Checking for 2013");
			new ServerInfo2013Task(ActivityDemo.this, mConnection).execute();
		}
	}

	private void go(ServerInfoObject2 serverInfo) {
		
		mApp.terminateDashboard();
		
		// Initialize
		mApp.initialize(mConnection, serverInfo);
		
		Intent intent = new Intent(ActivityDemo.this, ActivityDashboard.class);
		
		startActivity(intent);

		
		mConnectionDialog.dismiss();
		finish();
	}
}
