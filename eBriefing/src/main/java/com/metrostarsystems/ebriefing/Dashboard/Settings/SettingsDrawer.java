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

package com.metrostarsystems.ebriefing.Dashboard.Settings;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Activity.About.ActivityAbout;
import com.metrostarsystems.ebriefing.Activity.Library.ActivityDemo;
import com.metrostarsystems.ebriefing.Activity.Tutorial.ActivityTutorial;
import com.metrostarsystems.ebriefing.Dashboard.Policy.FragmentPrivacyPolicy;
import com.metrostarsystems.ebriefing.Data.Framework.Server.ServerConnection.ServerConnectionType;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsDrawer {
	
	private MainApplication			mApp;
	private Activity 				mParent;
	
	private DrawerLayout 			mDrawerLayout;
	private ActionBarDrawerToggle 	mDrawerToggle;
	
	
	private LinearLayout 			mDrawerSettingsLayout;
	private LinearLayout			mSettingsLayout;
	
	
	public SettingsDrawer(Activity activity) {
		mApp = (MainApplication) activity.getApplicationContext();
		mParent = activity;
		
		
		mDrawerLayout = (DrawerLayout) mParent.findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		
        mDrawerToggle = new ActionBarDrawerToggle(mParent, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                mParent.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                mParent.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerOpened(drawerView);
            }
        };
        
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        
        mDrawerSettingsLayout = (LinearLayout) mParent.findViewById(R.id.left_drawer);
        mSettingsLayout = (LinearLayout) mDrawerSettingsLayout.findViewById(R.id.include_dashboard_settings);
        
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        
        TextView librarySettingsTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_library_settings);
        librarySettingsTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityDemo.class);
				
				mParent.startActivity(intent);
			}
        	
        });
        
        Switch syncSwitch = (Switch) mSettingsLayout.findViewById(R.id.switch_sync);
        
        if(mApp == null || mApp.serverConnection() == null) {
        	syncSwitch.setChecked(false);
        } else {
        	syncSwitch.setChecked(SyncService.canSync());
        	
        	if(mApp.serverConnection().type() == ServerConnectionType.TYPE_DEMO) {
        		syncSwitch.setEnabled(false);
        	} else {
        		syncSwitch.setEnabled(true);
        	}
        }
        
        syncSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SyncService.toggleSync();
			}
        	
        });
        
        TextView tutorialTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_tutorial);
        tutorialTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityTutorial.class);
				
				mParent.startActivity(intent);
			}
        	
        });
        
        TextView aboutTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_about);
        aboutTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityAbout.class);
				
				mParent.startActivity(intent);
			}
        	
        });
        
        TextView privacyPolicyTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_privacy_policy);
        privacyPolicyTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentPrivacyPolicy fragment =  FragmentPrivacyPolicy.newInstance();
	    	
			
	    		fragment.show(mParent.getFragmentManager(), "Privacy Policy Fragment");
			}
        	
        });
        
        TextView feedbackTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_feedback);
        feedbackTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id=" + mApp.getPackageName()));
				mParent.startActivity(i);
			}
        	
        });
        
        TextView rateTextView = (TextView) mSettingsLayout.findViewById(R.id.textView_rate);
        rateTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id=" + mApp.getPackageName()));
				mParent.startActivity(i);
			}
        	
        });
        
	
	}
	
	public void syncDrawerToggleState() {
		mDrawerToggle.syncState();
	}
	
	public void toggleDrawer() {
		if(isDrawerOpened()) {
           close();
        } else {
           open();
        }
	}
	
	public void onDrawerToggleConfigurationChanged(Configuration newConfig) {
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	public void close() {
		mDrawerLayout.closeDrawer(mDrawerSettingsLayout);
	}
	
	public void open() {
		mDrawerLayout.openDrawer(mDrawerSettingsLayout);
	}
	
	public boolean isDrawerOpened() {
		return mDrawerLayout.isDrawerOpen(mDrawerSettingsLayout);
	}
	
	public boolean onDrawerToggleOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

}
