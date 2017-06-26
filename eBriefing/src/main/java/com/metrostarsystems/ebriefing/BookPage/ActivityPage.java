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

package com.metrostarsystems.ebriefing.BookPage;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActionBar.ActivityPageActionBar;
import com.metrostarsystems.ebriefing.BookPage.Bookmarks.DialogFragmentBookmark;
import com.metrostarsystems.ebriefing.BookPage.Notes.DialogFragmentNote;
import com.metrostarsystems.ebriefing.BookPage.Notes.NotesList;
import com.metrostarsystems.ebriefing.BookPage.Notes.NotesList.OnCloseNotesEditorListener;
import com.metrostarsystems.ebriefing.BookPage.Page.FragmentPage;
import com.metrostarsystems.ebriefing.BookPage.PageTransformer.ZRotateTransformer;
import com.metrostarsystems.ebriefing.BookPage.Toolbar.ActivityPageToolBar;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Database.NotesDatabase.NotesDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

public class ActivityPage extends FragmentActivity implements OnPageChangeListener, 
																OnCloseNotesEditorListener,
																NotesDatabaseChangedListener/*,
																OnCloseBookmarksEditorListener*/ {
	
	private static final String TAG = ActivityPage.class.getSimpleName();
	
	private MainApplication				mApp;
	private Book						mBook;
	private Chapter					mChapter;
	
	private Bundle						mBundle;
	
	private int							mCurrentPage = 0;
	
	private ActivityPageViewPager 				mViewPager;
    private ActivityPageViewPagerAdapter		mViewPagerAdapter;
    
    private Button						mTurnPageLeft;
    private Button						mTurnPageRight;
	
	private FrameLayout					mReaderLayout;
	
	private static PageMode				mPageMode = PageMode.MODE_SINGLE_PAGE;


	private boolean 					mEditBookmark = false;
	public 	ActivityPageToolBar			mToolBar;

	private NotesList					mNotesWindow;
	private boolean 					mEditNotes = false;
	
	private ActivityPageActionBar		mActionBar;
	
	private Timer 						mHideBarsTimer;
	private TimerTask 					mHideBarsTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page);
		
		mApp = (MainApplication) getApplicationContext();
		
		if(mApp == null || mApp.data() == null) {
			finish();
		}
		
		mReaderLayout = (FrameLayout) findViewById(R.id.include_page_reader);
		
		if(savedInstanceState != null) {
			// There is a saved instance state, get information from the state
			String book_id = savedInstanceState.getString(Tags.BOOK_ID_TAG);
			int page_number = savedInstanceState.getInt(Tags.PAGE_NUMBER_TAG);
			
			mBook = mApp.data().database().booksDatabase().book(book_id);
			
			if(mBook == null) {
				finish();
			}
			
			mCurrentPage = page_number - 1;

			
		} else {
			// There is no saved instance state, get information from the intent
			Intent intent = getIntent();
			
			if(intent == null) {
				finish();
			}
			
			if(intent.hasExtra(Tags.BOOK_ID_TAG) && intent.hasExtra(Tags.CHAPTER_ID_TAG)) {
				String book_id = intent.getStringExtra(Tags.BOOK_ID_TAG);
				String chapter_id = intent.getStringExtra(Tags.CHAPTER_ID_TAG);
				
				mBook = mApp.data().database().booksDatabase().book(book_id);
				
				if(mBook == null) {
					finish();
				}
				
				mChapter = mApp.data().database().chaptersDatabase().chapter(chapter_id);
				
				if(mChapter == null) {
					finish();
				}
				
				Page page = mApp.data().database().pagesDatabase().page(mChapter.firstPageId());
				
				mCurrentPage = page.pageNumber() - 1;
				
			} else if(intent.hasExtra(Tags.BOOK_ID_TAG) && intent.hasExtra(Tags.PAGE_NUMBER_TAG)) {
				String book_id = intent.getStringExtra(Tags.BOOK_ID_TAG);
				int page_number = intent.getIntExtra(Tags.PAGE_NUMBER_TAG, 1);
				
				mBook = mApp.data().database().booksDatabase().book(book_id);
				
				mCurrentPage = page_number - 1;
			} else {
				finish();
			}
			
			if(intent.hasExtra(Tags.EDIT_NOTE_TAG)) {
				mEditNotes = intent.getBooleanExtra(Tags.EDIT_NOTE_TAG, false);
			}
			
			if(intent.hasExtra(Tags.EDIT_BOOKMARK_TAG)) {
				mEditBookmark = intent.getBooleanExtra(Tags.EDIT_BOOKMARK_TAG, false);
			}
		}
		
		// Setup bookmark editor
						
		// Setup notes editor
		mNotesWindow = new NotesList(this);
						
		// Setup toolbar
		mToolBar = new ActivityPageToolBar(this);
						
		mActionBar = new ActivityPageActionBar(this);
		
		// Setup view pager
		mViewPager = (ActivityPageViewPager) mReaderLayout.findViewById(R.id.viewPager_reader);
		mViewPager.setParent(this);
		mViewPagerAdapter = new ActivityPageViewPagerAdapter(getSupportFragmentManager(), this, mBook);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(mCurrentPage);
		
		
		
		if(mPageMode == PageMode.MODE_SINGLE_PAGE) {
			mViewPager.setPageTransformer(false, new ZRotateTransformer());
		}
		
		mTurnPageLeft = (Button) mReaderLayout.findViewById(R.id.button_left_change);
		mTurnPageLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(mCurrentPage - 1, true);
			}
			
		});
		
		mTurnPageRight = (Button) mReaderLayout.findViewById(R.id.button_right_change);
		mTurnPageRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(mCurrentPage + 1, true);
			}
			
		});
		
		if(mEditNotes) {
			mNotesWindow.open(mCurrentPage + 1);
//			}
			
			disablePaging();
		}
	
		if(mEditBookmark) {
			
			FragmentPage page = mViewPagerAdapter.getFragment(mCurrentPage); 
			
			if(page != null) {
				DialogFragmentBookmark.newInstance(mBook.id(),page.pageNumber())
					.show(getSupportFragmentManager(), "Edit Bookmark Fragment");
			}
		}
		
		
		
		mToolBar.updatePageMode();

		
		mApp.data().database().notesDatabase().addListener(this);

	}

	@Override
	public void OnNotesDatabaseChangedListener(Note note) {
		mNotesWindow.update();
	}

	public void initalizeSinglePageMode() {
		mToolBar.showNotesTool();
		
		if(configurationOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			mToolBar.hideDrawTool();
		} else {
			mToolBar.showDrawTool();
		}
	}
	
	public void initalizeTwoPageMode() {
		
		if(configurationOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			mToolBar.hideNotesTool();
			mToolBar.hideDrawTool();
		}
	}
	
	public int configurationOrientation() { 
		return getResources().getConfiguration().orientation;
	}
	
	public void recreate() {
        Intent intent = getIntent();

        intent.putExtra(Tags.BOOK_ID_TAG, mBook.id());
        intent.putExtra(Tags.PAGE_NUMBER_TAG, mCurrentPage + 1);
        intent.removeExtra(Tags.CHAPTER_ID_TAG);

		startActivity(intent);
        finish();
	}
	
	public void setMode(PageMode mode) {
		mPageMode = mode;
	}
	
	public Book book() {
		return mBook;
	}
	
	public FrameLayout readerLayout() {
		return (FrameLayout) mReaderLayout;
	}

	public FragmentPage getFragment(int position) {
		if(mViewPagerAdapter == null) {
			return null;
		}
		
		return mViewPagerAdapter.getFragment(position);
	}

	public FragmentPage currentFragment() {
		return getFragment(mCurrentPage);
	}
	
	public FragmentPage currentFragmentDuel() {
		return getFragment(mCurrentPage + 1);
	}
	
	public int currentPage() {
		return mCurrentPage;
	}
	
	public PageMode mode() {
		return mPageMode;
	}
	
// Bars ------------------------------------------------------------------------------------------------------
	public void hideBars() {
		if(isActionBarVisible() && isToolBarVisible()) {
			hideToolBar();
			hideActionBar();
		}
	}
	
	public void toggleBars() {
		toggleToolBar();
		toggleActionBar();
		
		if(isToolBarVisible() && isActionBarVisible()) {
			startBarTimeout();
		} else {
			stopBarTimeout();
		}
	}
	
	public void startBarTimeout() {
		mHideBarsTimer = new Timer();
		mHideBarsTask = new HideBarsTimerTask(this);
		mHideBarsTimer.schedule(mHideBarsTask, Settings.MINUTE * 2);
	}
	
	public void resetBarTimeout() {
		startBarTimeout();
	}
	
	public void stopBarTimeout() {
		if(mHideBarsTimer != null) {
			mHideBarsTimer.cancel();
		}
	}
// -----------------------------------------------------------------------------------------------------------
	
// Tool Bar --------------------------------------------------------------------------------------------------
	public ActivityPageToolBar toolBar() {
		return mToolBar;
	}
	
	public boolean isToolBarVisible() {
		return mToolBar.isVisible();
	}
	
	public void showToolBar() {
		mToolBar.show();
		mToolBar.invalidate();
	}
	
	public void hideToolBar() {
		mToolBar.hide();
		mToolBar.invalidate();
	}
	
	public void toggleToolBar() {
		mToolBar.toggle();
		mToolBar.invalidate();
	}
// -----------------------------------------------------------------------------------------------------------
	
// Action Bar ------------------------------------------------------------------------------------------------
	public ActivityPageActionBar actionBar() {
		return mActionBar;
	}
	
	public boolean isActionBarVisible() {
		return mActionBar.isVisible();
	}
	
	public void hideActionBar() {
		mActionBar.hide();
		mActionBar.invalidate();
	}
	
	public void toggleActionBar() {
		mActionBar.toggle();
		mActionBar.invalidate();
	}
// -----------------------------------------------------------------------------------------------------------	
	
	
	
	
	public void enablePaging() {
		mViewPager.setPagingEnabled(true);
		mTurnPageLeft.setEnabled(true);
		mTurnPageRight.setEnabled(true);
	}
	
	public void disablePaging() {
		mViewPager.setPagingEnabled(false);
		mTurnPageLeft.setEnabled(false);
		mTurnPageRight.setEnabled(false);
	}

	public void hideNotes() {
		mNotesWindow.close();
	}

	public NotesList notesEditor() {
		return mNotesWindow;
	}
	
	public void toggleNotes() {
		if(mNotesWindow.isOpen()) {
			mNotesWindow.close();
			enablePaging();
		} else {
			mNotesWindow.open(mCurrentPage + 1);
			disablePaging();
		}
	}
	
	public boolean isNotesOpen() {
		return mNotesWindow.isOpen();
	}
	
	public boolean isContentsOpen() {
		return mToolBar.isContentsOpen();
	}
	
	public void hideContents() {
		mToolBar.hideContents();
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mApp.data().database().notesDatabase().removeListener(this);
		stopBarTimeout();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mApp.data().database().notesDatabase().removeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mApp.data().database().notesDatabase().addListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
    public void supportInvalidateOptionsMenu() {
		
		if(mViewPager != null) {
	        mViewPager.post(new Runnable() {
	
	            @Override
	            public void run() {
	                ActivityPage.super.supportInvalidateOptionsMenu();
	            }
	        });
		}
    }
	
	@Override
	public void onBackPressed() {
	    //  Action to be performed 
	   finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_page_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	Utilities.hideKeyboard(this);
	            finish();
	            return true;
		    case R.id.activity_page_search:
		    	return true;
		    case R.id.activity_page_thumbnail:
		    	
		    	return true;
		    default:
		        break;
	    }

	    return false;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.notes_editor_context_menu, menu);
	}
	
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
	    switch (item.getItemId()) {
		    case R.id.edit_note: {
		    	
		    	int postion = info.position;
		    	
		    	Note note = mNotesWindow.getItem(postion);
		    	
		    	DialogFragmentNote.newInstance(mBook.id(), currentFragment().pageNumber(), note.id())
		    				.show(getSupportFragmentManager(), "Edit Note Fragment");
		    	
		        return true;
		    }
		    case R.id.delete_note: {
		    	
		    	int position = info.position;
		    	
		    	Note note = mNotesWindow.getItem(position);
		    	
		    	Note remove_note = new Note.Builder()
										.fromNote(note)
										.content(note.content())
										.isRemoved(true)
										.build();
		    	
		    	mApp.data().database().notesDatabase().updateById(remove_note);
		    	
		        return true;
		    }
		    default:
		        return super.onContextItemSelected(item);
	    }
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    mToolBar.updatePageMode();
	  
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt(Tags.PAGE_NUMBER_TAG, mCurrentPage + 1);
		outState.putString(Tags.BOOK_ID_TAG, mBook.id());
	}
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

		    if(resultCode == 1){      
		         int pagenumber = data.getIntExtra(Tags.PAGE_NUMBER_TAG, 1); 
		         gotoPage(pagenumber);
		     }
		  }
	}
	

	@Override public void onPageScrollStateChanged(int state) { }
	@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

	@Override
	public void onPageSelected(int position) {
		mCurrentPage = position;

	}

	public void gotoPage(int pageNumber) {
		mViewPager.setCurrentItem(pageNumber - 1);
	}

	
	private static class HideBarsTimerTask extends TimerTask {

		private ActivityPage mParent;
		private Handler mHandler = new Handler();
		
		public HideBarsTimerTask(ActivityPage parent) {
			mParent = parent;
		}
		
		@Override
		public void run() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mParent.hideBars();
						}
						
					});
				}
				
			}).start();
		}
		
	}


	@Override
	public void OnCloseNotes(boolean closed) {
		if(closed) {
			enablePaging();
		}
	}
}
