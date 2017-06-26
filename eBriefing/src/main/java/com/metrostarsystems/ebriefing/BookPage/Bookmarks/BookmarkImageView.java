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

package com.metrostarsystems.ebriefing.BookPage.Bookmarks;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.Notes.DialogFragmentNote;
import com.metrostarsystems.ebriefing.BookPage.Page.FragmentPage;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


/**
 * BookmarkImageView due to ViewPager limitation is taking the imageview from under the viewpager, so we need to subtract 1 from the pagenumber
 */

public class BookmarkImageView extends ImageView implements /*OnClickListener,*/ OnTouchListener {
	
	private static final String TAG = BookmarkImageView.class.getSimpleName();
	
	private MainApplication	mApp;
	private Book 			mBook;
	private ActivityPage	mParent;
	private int             mPageNumber = 0;

	public BookmarkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mApp = (MainApplication) context.getApplicationContext();
		setClickable(true);
        setOnTouchListener(this);
	}

	public BookmarkImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mApp = (MainApplication) context.getApplicationContext();
		setClickable(true);
        setOnTouchListener(this);
	}

	public BookmarkImageView(Context context) {
		super(context);

		mApp = (MainApplication) context.getApplicationContext();
		setClickable(true);
        setOnTouchListener(this);
	}
	
	public void initialize(ActivityPage activityPage, int pageNumber) {
		mParent 		= activityPage;
        mPageNumber     = pageNumber;
        Log.i(TAG, "initialize page: " + String.valueOf(mPageNumber));
		mBook 			= mParent.book();
		
		updateImage();
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if(mParent == null || mBook == null) {
                    return true;
                }

                mParent.toggleBars();

                Log.i(TAG, "Opening bookmark for page: " + String.valueOf(mPageNumber));

                if(mPageNumber == mBook.pageCount()) {
                    DialogFragmentBookmark.newInstance(mBook.id(), mPageNumber)
                            .show(mParent.getSupportFragmentManager(), "Edit Bookmark Fragment");
                } else {
                    DialogFragmentBookmark.newInstance(mBook.id(), mPageNumber - 1)
                            .show(mParent.getSupportFragmentManager(), "Edit Bookmark Fragment");
                }

                break;
            }
            case MotionEvent.ACTION_DOWN:

                return true;
        }

        return true;
    }

    public void updateImage() {
		if(mBook == null) {
			return;
		}
		
		if(mApp.data().database().bookmarksDatabase().has(mBook.id(), mPageNumber)) {
			Bookmark bookmark = mApp.data().database().bookmarksDatabase().bookmark(mBook.id(), mPageNumber);
			
			if(bookmark.isRemoved()) {
				setImageResource(R.drawable.bookmark_add);
			} else {
				setImageResource(R.drawable.bookmark);
			}
			
		} else {
			setImageResource(R.drawable.bookmark_add);
		}

        invalidate();
	}

}
