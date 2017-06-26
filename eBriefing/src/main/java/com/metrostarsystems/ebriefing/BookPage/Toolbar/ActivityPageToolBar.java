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

package com.metrostarsystems.ebriefing.BookPage.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.PageMode;
import com.metrostarsystems.ebriefing.BookPage.Annotations.ActivityAnnotation;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContents;
import com.metrostarsystems.ebriefing.BookPage.Print.DialogFragmentPrint;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class ActivityPageToolBar {
	
	private static final String TAG = ActivityPageToolBar.class.getSimpleName();
	
	private MainApplication				mApp;
	private ActivityPage 				mParent;
	
	private Book						mBook;
	
	private RelativeLayout				mToolBarLayout;
	private boolean 					mShowToolBar = false;
	
	private ImageView					mDrawTool;
	private ImageView					mContentsTool;
	
	private boolean						mShowNotesTool = true;
	private ImageView					mNotesTool;

	private ImageView					mPrintTool;
	
	private ImageView					mCurrentModeButton;

	private LinearLayout				mBookmarkActionsLayout;
	private boolean						mShowBookmarkActionsBar = false;
	private ImageView					mSaveBookmarkAction;
	private ImageView					mCloseBookmarkAction;
	
	private LinearLayout				mContentsLayout;
	private boolean						mShowContents = false;
	private TextView					mContentsChaptersTextView;
	private TextView					mContentsBookmarksTextView;
	private TextView					mContentsAnnotationsTextView;
	private TextView					mContentsNotesTextView;
	
	public ActivityPageToolBar(ActivityPage page) {
		mApp = (MainApplication) page.getApplicationContext();
		mParent = page;
		mBook = mParent.book();
		
		// Tool Bar
		mToolBarLayout = (RelativeLayout) mParent.readerLayout().findViewById(R.id.include_page_toolbar);
		
		mDrawTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_draw);
		mDrawTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(mParent, ActivityAnnotation.class);
				intent.putExtra("bookid", mBook.id());
				intent.putExtra("pagenumber", mParent.currentFragment().pageNumber());
				
				mParent.startActivity(intent);
				
				
				
			}
			
		});
		
		mContentsTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_contents);
		mContentsTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleContents();
			}
			
		});
		
		mNotesTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_notes);
		mNotesTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleNotes();
			}
			
		});
		
		mPrintTool = (ImageView) mToolBarLayout.findViewById(R.id.imageView_print);
		mPrintTool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				DialogFragmentPrint.newInstance(mBook.id(), mParent.currentFragment().pageNumber()).show(mParent.getSupportFragmentManager(), "Print Fragment");
			}
			
		});
		
		if(Settings.ENABLE_PRINTING) {
			mPrintTool.setVisibility(View.VISIBLE);
		} else {
			mPrintTool.setVisibility(View.GONE);
		}

		mCurrentModeButton = (ImageView) mToolBarLayout.findViewById(R.id.imageView_current_mode);
		mCurrentModeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				togglePageModes();
			}
			
		});
		
		mContentsLayout = (LinearLayout) mParent.readerLayout().findViewById(R.id.include_page_contents);
		LinearLayout chaptersLayout = (LinearLayout) mContentsLayout.findViewById(R.id.linearLayout_chapters);
		chaptersLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityContents.class);
				
				Bundle extras = new Bundle();
				extras.putString("bookid", mBook.id());
				extras.putInt("whichgrid", 0);
				intent.putExtras(extras);
				
				mParent.startActivityForResult(intent, 1);
			}
			
		});
		
		LinearLayout bookmarksLayout = (LinearLayout) mContentsLayout.findViewById(R.id.linearLayout_bookmarks);
		bookmarksLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityContents.class);
				
				Bundle extras = new Bundle();
				extras.putString("bookid", mBook.id());
				extras.putInt("whichgrid", 1);
				intent.putExtras(extras);
				
				mParent.startActivityForResult(intent, 1);
			}
			
		});
		
		LinearLayout notesLayout = (LinearLayout) mContentsLayout.findViewById(R.id.linearLayout_notes);
		notesLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityContents.class);
				
				Bundle extras = new Bundle();
				extras.putString("bookid", mBook.id());
				extras.putInt("whichgrid", 2);
				intent.putExtras(extras);
				
				mParent.startActivityForResult(intent, 1);
			}
			
		});
		
		LinearLayout annotationsLayout = (LinearLayout) mContentsLayout.findViewById(R.id.linearLayout_annotations);
		annotationsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityContents.class);
				
				Bundle extras = new Bundle();
				extras.putString("bookid", mBook.id());
				extras.putInt("whichgrid", 3);
				intent.putExtras(extras);
				
				mParent.startActivityForResult(intent, 1);
			}
			
		});
		
		
		
		mContentsChaptersTextView = (TextView) mContentsLayout.findViewById(R.id.textView_chapters);
		mContentsChaptersTextView.setText(String.valueOf(mApp.data().database().chaptersDatabase().countByBook(mBook.id())));
		
		mContentsBookmarksTextView = (TextView) mContentsLayout.findViewById(R.id.textView_bookmarks);
		mContentsBookmarksTextView.setText(String.valueOf(mApp.data().database().bookmarksDatabase().countByBook(mBook.id())));
		
		mContentsAnnotationsTextView = (TextView) mContentsLayout.findViewById(R.id.textView_annotations);
		mContentsAnnotationsTextView.setText(String.valueOf(mApp.data().database().annotationsDatabase().countByBook(mBook.id())));
		
		mContentsNotesTextView = (TextView) mContentsLayout.findViewById(R.id.textView_notes);
		mContentsNotesTextView.setText(String.valueOf(mApp.data().database().notesDatabase().countByBook(mBook.id())));
	}

	public void toggle() {
		mShowToolBar = !mShowToolBar;
		
		if(mShowToolBar) {
			show();
		} else {
			hide();
		}
	}

	public void show() {
		mShowToolBar = true;

		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0);
		
		animation.setDuration(500);

		mToolBarLayout.setAnimation(animation);

		mToolBarLayout.setVisibility(View.VISIBLE);
		
	}
	
	public void hide() {
		mShowToolBar = false;
		
		if(isContentsOpen()) {
			hideContents();
		}

		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1f); //May need to check the direction you want.

		
		animation.setDuration(500);
		mToolBarLayout.setAnimation(animation);
		
		mToolBarLayout.setVisibility(View.GONE);
	}
	
	public void invalidate() {
		mToolBarLayout.invalidate();
	}
	
	public boolean isContentsOpen() {
		return mShowContents;
	}
	
	public void toggleContents() {
		mParent.resetBarTimeout();
		
		mShowContents = !mShowContents;
		
		if(mShowContents) {
			showContents();
		} else {
			hideContents();
		}
	}
	
	private void showContents() {
		mShowContents = true;
		
		updateContents();
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -1f,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		
		animation.setDuration(500);
		mContentsLayout.startAnimation(animation);
		
		mContentsLayout.setVisibility(View.VISIBLE);
	}
	
	public void hideContents() {
		mShowContents = false;
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1f,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		
		animation.setDuration(500);
		mContentsLayout.startAnimation(animation);
		
		mContentsLayout.setVisibility(View.GONE);
	}
	
	public void updateContents() {
		mContentsChaptersTextView.setText(String.valueOf(mApp.data().database().chaptersDatabase().countByBook(mBook.id())));
		mContentsBookmarksTextView.setText(String.valueOf(mApp.data().database().bookmarksDatabase().countByBook(mBook.id())));
		mContentsAnnotationsTextView.setText(String.valueOf(mApp.data().database().annotationsDatabase().countByBook(mBook.id())));
		mContentsNotesTextView.setText(String.valueOf(mApp.data().database().notesDatabase().countByBook(mBook.id())));
	}
	
	public void toggleNotes() {
		mParent.toggleNotes();
	}
	
	public void showNotesTool() {
		mShowNotesTool = true;
	
		mNotesTool.setVisibility(View.VISIBLE);
	}
	
	public void hideNotesTool() {
		mShowNotesTool = false;
		
		mNotesTool.setVisibility(View.GONE);
	}
	
	public void showDrawTool() {
		mDrawTool.setVisibility(View.VISIBLE);
	}
	
	public void hideDrawTool() {
		mDrawTool.setVisibility(View.GONE);
	}
	
	public boolean isVisible() {
		return mShowToolBar;
	}
	
	public void togglePageModes() {
		mParent.resetBarTimeout();
		
		if(mParent.mode() == PageMode.MODE_SINGLE_PAGE) {
			mParent.setMode(PageMode.MODE_TWO_PAGE);
		} else if(mParent.mode() == PageMode.MODE_TWO_PAGE) {
			mParent.setMode(PageMode.MODE_SINGLE_PAGE);
		}
		
		updatePageMode();
		
		if(mParent.configurationOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			mParent.recreate();
		}
	}
	
	public void updatePageMode() {
		if(mParent.mode() == PageMode.MODE_SINGLE_PAGE) {
			
			mParent.initalizeSinglePageMode();
			mCurrentModeButton.setImageResource(R.drawable.activity_page_bar_button_single_page);
			
			showDrawTool();

		} else if(mParent.mode() == PageMode.MODE_TWO_PAGE) {
			
			mParent.initalizeTwoPageMode();
			mCurrentModeButton.setImageResource(R.drawable.activity_page_bar_button_two_page);
		}
	}
	
	public void showBookmarkActionsBar() {
		mShowBookmarkActionsBar = true;

		mBookmarkActionsLayout.setVisibility(View.VISIBLE);
	}
	
	public void hideBookmarkActionsBar() {
		mShowBookmarkActionsBar = false;

		mBookmarkActionsLayout.setVisibility(View.GONE);
	}
}
