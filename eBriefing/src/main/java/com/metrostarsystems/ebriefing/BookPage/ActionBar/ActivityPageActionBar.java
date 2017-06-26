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

package com.metrostarsystems.ebriefing.BookPage.ActionBar;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContents;
import com.metrostarsystems.ebriefing.BookPage.Search.DialogFragmentSearch;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class ActivityPageActionBar {
	
	private static final String TAG = ActivityPageActionBar.class.getSimpleName();
	
	private MainApplication				mApp;
	private ActivityPage 				mParent;
	
	private Book						mBook;
	
	private LinearLayout				mActionBarLayout;
	
	private ImageView					mBackButton;
	
	private TextView					mTitleTextView;
	
	private Button						mSearchButton;
	private Button						mContentsButton;
	
	private Boolean						mShowActionBar = false;
	
	private DialogFragmentSearch				mSearch;
	
	public ActivityPageActionBar(ActivityPage page) {
		mApp = (MainApplication) page.getApplicationContext();
		mParent = page;
		mBook = mParent.book();
		
		// Tool Bar
		mActionBarLayout = (LinearLayout) mParent.readerLayout().findViewById(R.id.include_page_actionbar);
		
		mBackButton = (ImageView) mActionBarLayout.findViewById(R.id.imageView_back);
		mBackButton.setClickable(true);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		    	Utilities.hideKeyboard(mParent);
				mParent.finish();
			}
			
		});
		
		mTitleTextView = (TextView) mActionBarLayout.findViewById(R.id.textView_title);
		mTitleTextView.setText(mBook.title());
		
		mSearchButton = (Button) mActionBarLayout.findViewById(R.id.button_search);
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mSearch == null) {
		    		mSearch = DialogFragmentSearch.newInstance(mBook.id());
		    	}
				
		    	mSearch.show(mParent.getFragmentManager(), "Search Fragment");
			}
			
		});
		
		mContentsButton = (Button) mActionBarLayout.findViewById(R.id.button_contents);
		mContentsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mParent, ActivityContents.class);
				
				Bundle extras = new Bundle();
				extras.putString("bookid", mBook.id());
				intent.putExtras(extras);
				
				mParent.startActivityForResult(intent, 1);
			}
			
		});
	}
	
	public void toggle() {
		mShowActionBar = !mShowActionBar;
		
		if(mShowActionBar) {
			show();
		} else {
			hide();
		}
	}

	public void show() {
		mShowActionBar = true;
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1f,
				Animation.RELATIVE_TO_SELF, 0); //May need to check the direction you want.
		
		
		animation.setDuration(500);
		mActionBarLayout.setAnimation(animation);
		
		mActionBarLayout.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		mShowActionBar = false;
		
		Display display = mParent.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1f);

		
		animation.setDuration(500);
		mActionBarLayout.setAnimation(animation);
		
		mActionBarLayout.setVisibility(View.GONE);
	}
	
	public void invalidate() {
		mActionBarLayout.invalidate();
	}
	
	public boolean isVisible() {
		return mShowActionBar;
	}
}
