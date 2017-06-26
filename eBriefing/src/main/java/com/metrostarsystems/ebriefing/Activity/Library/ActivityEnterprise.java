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
import org.ksoap2.serialization.SoapObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Data.Cache.Preferences.ObscuredPreferencesHandle;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerInfo;
import com.metrostarsystems.ebriefing.Services.AuthenticationService.AuthenticateTask;
import com.metrostarsystems.ebriefing.Services.AuthenticationService.AuthenticateTask.AuthenticateTaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2010Task;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2013Task;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2010Task.ServerInfo2010TaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfo2013Task.ServerInfo2013TaskListener;
import com.metrostarsystems.ebriefing.Services.SyncService.Server.ServerInfoObject2;

public class ActivityEnterprise extends Activity implements AuthenticateTaskListener,
															ServerInfo2010TaskListener,
															ServerInfo2013TaskListener {
	
	private static final String TAG = ActivityEnterprise.class.getSimpleName();

	private MainApplication	mApp;
	
	private ObscuredPreferencesHandle mPref;
	
	private ServerConnection mConnection;
	
	private ActionBar	mActionBar;
	
	private EditText mLibraryUrlEditText;
	private EditText mUserIdEditText;
	private EditText mPasswordEditText;
	private EditText mDomainEditText;
	
	private Button mConnectionButton;
	private Button mLearnMoreButton;
	
	private LinearLayout mErrorLayout;
	private TextView mErrorTextView;
	
	private String mLibraryUrl = "";
	private String mUserId = "";
	private String mPassword = "";
	private String mDomain = "";
	
	private Dialog mConnectionDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp = (MainApplication) getApplicationContext();
		mPref = mApp.preferences();
		
		mActionBar = getActionBar();
		
		// Load the saved enterprise information
		mLibraryUrl = (String) mPref.get("libraryurl", "");
		mUserId = (String) mPref.get("userid", "");
		mPassword = (String) mPref.get("password", "");
		mDomain = (String) mPref.get("domain", "");
		
		setContentView(R.layout.activity_enterprise);
		
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setTitle("Back");
		
		mLibraryUrlEditText = (EditText) findViewById(R.id.editText_libraryurl);
		mLibraryUrlEditText.setText(mLibraryUrl);
		
		mUserIdEditText 	= (EditText) findViewById(R.id.editText_userid);
		mUserIdEditText.setText(mUserId);
		
		mPasswordEditText 	= (EditText) findViewById(R.id.editText_password);
		mPasswordEditText.setText(mPassword);
		
		mDomainEditText 	= (EditText) findViewById(R.id.editText_domain);
		mDomainEditText.setText(mDomain);
		mDomainEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
		        if (actionId == EditorInfo.IME_ACTION_DONE) {
		            mConnectionButton.performClick();
		            handled = true;
		        }
		        return handled;
			}
			
		});
		
		mErrorLayout = (LinearLayout) findViewById(R.id.linearLayout_error);
		mErrorTextView = (TextView) findViewById(R.id.textView_error);

		mConnectionButton = (Button) findViewById(R.id.button_connect);
		mConnectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mConnectionDialog.show();
				mLibraryUrl = mLibraryUrlEditText.getText().toString();
				mUserId = mUserIdEditText.getText().toString();
				mPassword = mPasswordEditText.getText().toString();
				mDomain = mDomainEditText.getText().toString();
				
				if(!mLibraryUrl.isEmpty() && !mUserId.isEmpty() && !mPassword.isEmpty() && !mDomain.isEmpty()) {
					
					mConnection = ServerConnection.enterprise(mApp, mLibraryUrl, mUserId, mPassword, mDomain);
					
					if(!Utilities.isNetworkAvailable(ActivityEnterprise.this)) {
						
						mApp.terminateDashboard();
						
						mApp.initialize(mConnection, null);
						
						mConnectionDialog.dismiss();
						
						Intent intent = new Intent(ActivityEnterprise.this, ActivityDashboard.class);
						startActivity(intent);
						finish();
					} else {
                        if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Authenticating..."); }
						new AuthenticateTask(ActivityEnterprise.this).execute(mConnection);
					}
				} else {
					mErrorLayout.setVisibility(View.VISIBLE);
					mErrorTextView.setText("Error: Missing required information (-1)\nPlease enter missing information.");
				}
				
			}
			
		});
		
		mLearnMoreButton = (Button) findViewById(R.id.button_learn_more);
		mLearnMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "****"; // service url
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				
				startActivity(i);
			}
			
		});
		
		
		mConnectionDialog = new Dialog(this);
		mConnectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mConnectionDialog.setContentView(R.layout.activity_enterprise_connection_dialog);
	
		
	}

	@Override
	public void onAuthenticateTaskFinished(HttpResponse result) {
		
		
		if(result == null) {
			mConnectionDialog.dismiss();
			
			mErrorLayout.setVisibility(View.VISIBLE);
			mErrorTextView.setText("Error: Incorrect library url (-1)\nPlease enter correct url or check network connection.");
			
			return;
		}
		
		if(result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Checking for 2013"); }
			new ServerInfo2013Task(ActivityEnterprise.this, mConnection).execute();
			
			
			
			
		} else {
			mConnectionDialog.dismiss();
			
			mPref.remove("userid");
			mPref.remove("password");
			
			mErrorLayout.setVisibility(View.VISIBLE);
			mErrorTextView.setText( "Error: " + result.getStatusLine().getReasonPhrase() + 
											" (" + String.valueOf(result.getStatusLine().getStatusCode()) + ")" + 
											AuthenticateTask.responseCode(result.getStatusLine().getStatusCode()));
		}
		
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	Intent intent = new Intent(ActivityEnterprise.this, ActivityDemo.class);
				
				startActivity(intent);
				finish();
	            return true;
		    default:
		        break;
	    }

	    return false;
	}

	@Override
	public void onServerInfo2013TaskFinishedListener(ServerInfoObject2 result) {
		if(result != null && result.isValid()) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "2013 good..."); }
			go(result);
		} else {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Checking for 2010"); }
			new ServerInfo2010Task(this, mConnection).execute();
		}
	}

	@Override
	public void onServerInfo2010TaskFinishedListener(ServerInfoObject2 result) {
		if(result != null && result.isValid()) {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "2010 good..."); }
			go(result);
		} else {
            if(Settings.DEBUG_MESSAGES) { Log.i(TAG, "Checking for 2013"); }
			new ServerInfo2013Task(this, mConnection).execute();
		}
	}


	private void go(ServerInfoObject2 serverInfo) {
		mErrorLayout.setVisibility(View.GONE);
		
		mApp.terminateDashboard();
		
		if(serverInfo == null || !serverInfo.isValid()) {
			mErrorLayout.setVisibility(View.VISIBLE);
			mErrorTextView.setText("Error: Server is unreachable. Please try again later or contact server administration.");
			
			return;
		}
		
		// Initialize
		mApp.initialize(mConnection, serverInfo);
		
		// Save the enterprise information
		mConnection.saveConnectionPreferences(mPref);
		
		Intent intent = new Intent(ActivityEnterprise.this, ActivityDashboard.class);
		
		startActivity(intent);
		
		mConnectionDialog.dismiss();
		finish();
	}
}
