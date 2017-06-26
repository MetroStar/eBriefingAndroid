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

package com.metrostarsystems.ebriefing.BookChapter;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.viewpagerindicator.TabPageIndicator;

public class ActivityChapter extends FragmentActivity /*implements ActionBar.TabListener*/ {
	
	private static final String TAG = ActivityChapter.class.getSimpleName();

	private MainApplication				mApp;
	
	private Book						mBook;
	
	private TabPageIndicator			mTitleIndicator;
	private ActivityChapterViewPager 	mViewPager;
    private ActivityChapterPagerAdapter	mViewPagerAdapter;
    

    
    private ActionBar 					mActionBar;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chapter);
		
		mApp = (MainApplication) getApplicationContext();
		
		if(mApp == null || mApp.data() == null) {
			finish();
		}
		
		if(savedInstanceState != null && savedInstanceState.containsKey("bookid")) {
			String book_id = savedInstanceState.getString("bookid");
			
			if(mApp.data() != null && mApp.data().database() != null) {
				mBook = mApp.data().database().booksDatabase().book(book_id);
			} else {
				finish();
			}
		} else {
			
			if(getIntent() != null && getIntent().hasExtra("bookid")) {
				
				String book_id = getIntent().getStringExtra("bookid");
				
				mBook = mApp.data().database().booksDatabase().book(book_id);
			}
		}
		
		if(mBook == null) {
			finish();
		}
	
		processIntent(getIntent());
		
	}
    
	private void processIntent(Intent intent) {
		if(mBook != null) {
			
			mBook.setNew(false);
			mApp.data().database().booksDatabase().update(mBook);
			

			mTitleIndicator = (TabPageIndicator) findViewById(R.id.chapter_indicator);

			
			mViewPager = (ActivityChapterViewPager) findViewById(R.id.viewPager_chapter);
			mViewPagerAdapter = new ActivityChapterPagerAdapter(getFragmentManager(), this);
			
			mViewPager.setAdapter(mViewPagerAdapter);
			
			mTitleIndicator.setViewPager(mViewPager);
			
			
			mActionBar = getActionBar();
	        
			mActionBar.setDisplayHomeAsUpEnabled(true);
	        mActionBar.setTitle(mBook.title());
	        mActionBar.setIcon(R.drawable.ic_book_logo);
	        mActionBar.show();
	     
		} else {
			finish();
		}
	}
	
	public Book book() { return mBook; }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString("bookid", mBook.id());
		
		super.onSaveInstanceState(outState);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_chapter_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
	            this.finish();
	            return true;
		    default:
		        break;
	    }

	    return false;
	}
	
	@Override
    public void supportInvalidateOptionsMenu() {
		
		if(mViewPager != null) {
	        mViewPager.post(new Runnable() {
	
	            @Override
	            public void run() {
	                ActivityChapter.super.supportInvalidateOptionsMenu();
	            }
	        });
		}
    }

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void update() {
		mTitleIndicator.notifyDataSetChanged();
	}
}
