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

package com.metrostarsystems.ebriefing.BookPage.Contents;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.viewpagerindicator.TabPageIndicator;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityContents extends FragmentActivity /*implements OnPageChangeListener, ActionBar.TabListener*/ {

	private MainApplication					mApp;
	private Book							mBook;
	
	private ActivityPage					mParent;
	
	//private SlidingTabLayout				mTabs;
	private TabPageIndicator				mTitleIndicator;
	private ActivityContentsViewPager 		mViewPager;
	private ActivityContentsPagerAdapter	mViewPagerAdapter;
	
	private ActionBar 						mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contents_grid);
		
		mApp = (MainApplication) getApplicationContext();
		
		mActionBar = getActionBar();
        mActionBar.show();
        mActionBar.setDisplayHomeAsUpEnabled(true);
		
        mApp.data().imageManager().clearThumbnails();

		mTitleIndicator = (TabPageIndicator) findViewById(R.id.grid_indicator);
		
		mViewPager = (ActivityContentsViewPager) findViewById(R.id.viewPager_pages);

		processIntent(getIntent());
		
		mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(mBook.title());
		
	}
	
	private void processIntent(Intent intent) {
		if(intent.hasExtra("bookid")) {
			String book_id = intent.getStringExtra("bookid");
		
			mBook = mApp.data().database().booksDatabase().book(book_id);
			
			mViewPagerAdapter = new ActivityContentsPagerAdapter(mApp, getFragmentManager());
			mViewPager.setAdapter(mViewPagerAdapter);
			
			mTitleIndicator.setViewPager(mViewPager);
			
			if(intent.hasExtra("whichgrid")) {
				mViewPager.setCurrentItem(intent.getIntExtra("whichgrid", 0));
			}
		} else {
			finish();
		}
	}

	public Book book() { return mBook; }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_page_grid_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
	            finish();
	            return true;
		    case R.id.activity_page_thumbnail:
		    	finish();
		    	
		    	return true;
		    default:
		        break;
	    }

	    return false;
	}
	
	public void update() {
		if(mTitleIndicator != null) {
			mTitleIndicator.notifyDataSetChanged();
		}
	}
}
