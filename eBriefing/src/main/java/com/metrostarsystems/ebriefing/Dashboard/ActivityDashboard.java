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

package com.metrostarsystems.ebriefing.Dashboard;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Dashboard.Search.ClearableAutoCompleteTextView;
import com.metrostarsystems.ebriefing.Dashboard.Search.ClearableAutoCompleteTextView.OnClearListener;
import com.metrostarsystems.ebriefing.Dashboard.Search.SearchAdapter;
import com.metrostarsystems.ebriefing.Dashboard.Settings.SettingsDrawer;
import com.metrostarsystems.ebriefing.Dashboard.Sort.FragmentSort;
import com.metrostarsystems.ebriefing.Services.CoreService.CoreService;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.MainApplication;
import com.viewpagerindicator.TabPageIndicator;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

public class ActivityDashboard extends FragmentActivity /* implements OnQueryTextListener, SearchView.OnCloseListener*/ /*implements ActionBar.TabListener*/ {

	private static final String TAG = ActivityDashboard.class.getSimpleName();
	
	private MainApplication				mApp;
	
	private TabPageIndicator			mTitleIndicator;
	private ActivityDashboardViewPager 	mViewPager;
    private ActivityDashboardPagerAdapter	mViewPagerAdapter;
    
    private ActionBar 					mActionBar;
    
    private SettingsDrawer				mSettingsDrawer;
    
    private MenuItem 					mSearchMenuItem;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp = (MainApplication) getApplicationContext();
		
		mApp.setDashboard(this);
		
		setContentView(R.layout.activity_dashboard);
		
		mTitleIndicator = (TabPageIndicator) findViewById(R.id.dashboard_indicator);
		
		mViewPager = (ActivityDashboardViewPager) findViewById(R.id.viewPager_dashboard);
		mViewPagerAdapter = new ActivityDashboardPagerAdapter(mApp, getFragmentManager());
		
		mViewPager.setAdapter(mViewPagerAdapter);
		
		mTitleIndicator.setViewPager(mViewPager);

		mActionBar = getActionBar();

		mActionBar.setTitle("eBriefing");
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setLogo(R.drawable.ic_action_bar_logo);
		
		SearchAdapter searchAdapter = new SearchAdapter(this, new ArrayList<String>());
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    // inflate the view that we created before
	    View v = inflater.inflate(R.layout.actionbar_search, null);


	    // the view that contains the new clearable autocomplete text view
		final ClearableAutoCompleteTextView searchBox =  (ClearableAutoCompleteTextView) v.findViewById(R.id.search_box);
		
		// start with the text view hidden in the action bar
		searchBox.setAdapter(searchAdapter);
		searchBox.setVisibility(View.INVISIBLE);
		
		searchBox.setOnClearListener(new OnClearListener() {
			
			@Override
			public void onClear() {
				toggleSearch(true);
			}
		});
		
		searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// handle clicks on search resaults here	
			}
			
		});
		
	    mActionBar.setCustomView(v);
        
        // Initialize Settings Drawer
        mSettingsDrawer = new SettingsDrawer(this);
        
        
        
        mActionBar.show();

	}
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		// Opens the updated tab when activated from the update notification
		if(intent.hasExtra("open_updated")) {
			boolean open_updated = intent.getBooleanExtra("open_updated", false);
			
			if(open_updated) {
				mViewPager.setCurrentItem(3);
			}
		}
	}



	protected void toggleSearch(boolean reset) {
		ClearableAutoCompleteTextView searchBox = (ClearableAutoCompleteTextView) findViewById(R.id.search_box);

		if(reset) {
			// hide search box and show search icon
			searchBox.setText("");
			searchBox.setVisibility(View.GONE);
			// hide the keyboard
			mSearchMenuItem.setVisible(true);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		} else {
			// hide search icon and show search box
			searchBox.setVisibility(View.VISIBLE);
			searchBox.requestFocus();
			mSearchMenuItem.setVisible(false);
			// show the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
		}
		
	}
	
	private void toggleSettings() {
		mSettingsDrawer.toggleDrawer();
	}

	@Override
    public void supportInvalidateOptionsMenu() {
		
		if(mViewPager != null) {
	        mViewPager.post(new Runnable() {
	
	            @Override
	            public void run() {
	                ActivityDashboard.super.supportInvalidateOptionsMenu();
	            }
	        });
		}
    }


	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		CoreService.setAllowAutoRefresh(false);
		SyncService.setAllowAutoSync(false);
		mApp.data().database().removeAllListeners();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		CoreService.setAllowAutoRefresh(false);
		SyncService.setAllowAutoSync(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(mSettingsDrawer.isDrawerOpened()) {
			mSettingsDrawer.close();
		}
		
		mApp.data().database().exportDB(mApp);
		
		CoreService.setAllowAutoRefresh(false);
		SyncService.setAllowAutoSync(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mSettingsDrawer.isDrawerOpened()) {
			mSettingsDrawer.close();
		}
		
		CoreService.setAllowAutoRefresh(true);
		SyncService.setAllowAutoSync(true);
		update();
	}
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		CoreService.setAllowAutoRefresh(true);
		SyncService.setAllowAutoSync(true);
		update();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_dashboard_menu, menu);
	    
	    mSearchMenuItem = (MenuItem) menu.findItem(R.id.search_dashboard);

	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(mSettingsDrawer.onDrawerToggleOptionsItemSelected(item)) {
	        return true;
		}
		
	    switch (item.getItemId()) {
	    	case R.id.sort_dashboard:
	    		FragmentSort sort = FragmentSort.newInstance();
				sort.show(getFragmentManager(), "Sort Fragment");
	    		return true;
	    		
	    	case R.id.search_dashboard:
	    		toggleSearch(false);
	    		return true;
	    	case R.id.settings:
	    		toggleSettings();
	    		return true;
		    default:
		        break;
	    }

	    return false;
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mSettingsDrawer.syncDrawerToggleState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSettingsDrawer.onDrawerToggleConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        mSettingsDrawer.isDrawerOpened();
 
        return super.onPrepareOptionsMenu(menu);
    }

	public void update() {
		if(mTitleIndicator != null) {
			mTitleIndicator.notifyDataSetChanged();
		}
	}

	public void refresh() {
		mApp.dashboardTabs().refresh();
		update();
	}
}
