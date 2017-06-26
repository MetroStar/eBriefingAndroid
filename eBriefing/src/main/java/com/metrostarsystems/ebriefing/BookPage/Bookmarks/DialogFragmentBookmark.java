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
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogFragmentBookmark extends DialogFragment {

	private static final String TAG = DialogFragmentBookmark.class.getSimpleName();
	
	
	
	private MainApplication		mApp;
	private ActivityPage		mParent;
	
	private Book				mBook;
	private Page			    mPage;
	private Bookmark		    mBookmark;
	
	private TextView			mSaveButton;
	private TextView			mCancelButton;

	private int					mCharacterCount			 	= 0;
	
	private TextView 			mTitleTextView;
	private EditText			mTitleEditText;
	
	public static final DialogFragmentBookmark newInstance(String bookId, int currentPage) {
		DialogFragmentBookmark fragment = new DialogFragmentBookmark();
	    Bundle bundle = new Bundle();
	    bundle.putString(Tags.BOOK_ID_TAG, bookId);
	    bundle.putInt(Tags.PAGE_NUMBER_TAG, currentPage);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.dialog_edit_bookmark, container);
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		mParent = (ActivityPage) getActivity();
		
		if(mApp == null || !getArguments().containsKey(Tags.BOOK_ID_TAG) ||
				!getArguments().containsKey(Tags.PAGE_NUMBER_TAG)) {
			Log.i(TAG, "Dismiss");
			dismiss();
		}
		
		String book_id = getArguments().getString(Tags.BOOK_ID_TAG);
		
		mBook = mApp.data().database().booksDatabase().book(book_id);
		
		if(mBook == null) {
			Log.i(TAG, "Dismiss");
			dismiss();
		}
		
		int page_number = getArguments().getInt(Tags.PAGE_NUMBER_TAG);
		
		mPage = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), page_number);
		
		if(mPage == null) {
			Log.i(TAG, "Dismiss");
			dismiss();
		}
		
		mBookmark = mApp.data().database().bookmarksDatabase().bookmark(mBook.id(), mPage.pageNumber());
		
		mTitleTextView = (TextView) view.findViewById(R.id.textView_title);
		mTitleEditText = (EditText) view.findViewById(R.id.editText_title);
		mCancelButton = (TextView) view.findViewById(R.id.textView_cancel);
		mSaveButton = (TextView) view.findViewById(R.id.textView_save);
		
		
		if(mBookmark != null) {
			mTitleEditText.setText(mBookmark.value());
			refreshCharacterCount();
		}
		
		mTitleEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { 
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				refreshCharacterCount();
			}
			
		});
		
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		
		mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBookmark != null) {
					update();
				} else {
					save();
				}

				dismiss();
			}
			
		});
		
		return view;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		dialog.getWindow().getAttributes().windowAnimations = R.style.BookmarkDialogAnimation;

		return dialog;
	}
	
	@Override
	public void dismiss() {
		Utilities.hideKeyboardFrom(getActivity(), mTitleEditText);
		
		super.dismiss();
	}

	private void refreshCharacterCount() {
		mCharacterCount = mTitleEditText.getText().toString().length();
		
		if(mCharacterCount <= 0) {
			mSaveButton.setText("Delete");
		} else if(mCharacterCount > 0){
			mSaveButton.setText("Save");
		}
	}
	
	private void save() {
		String title = mTitleEditText.getText().toString();

		if(!title.isEmpty()) {
			
			Bookmark new_bookmark = new Bookmark.Builder()	
													.id()
													.bookId(mBook.id())
													.bookVersion(mBook.bookVersion())
													.chapterId(mPage.chapterId())
													.pageId(mPage.id())
													.pageNumber(mPage.pageNumber())
													.value(title)
													.isSynced(false)
													.isNew(true)
													.build();
	
			mBookmark = new_bookmark;
			
			
			mApp.data().database().bookmarksDatabase().insert(mBookmark);

            mParent.getFragment(mPage.pageNumber() - 1).updateBookmarkImage();
		} else {
			if(mBookmark != null) {
				Bookmark update_bookmark = new Bookmark.Builder()
														.fromBookmark(mBookmark)
														.isSynced(false)
														.isRemoved(true)
														.build();

				mApp.data().database().bookmarksDatabase().updateByNumber(update_bookmark);
                mParent.getFragment(mPage.pageNumber() - 1).updateBookmarkImage();
			}
		}
	}
	
	private void update() {
		String title = mTitleEditText.getText().toString();
		
		if(!title.isEmpty()) {
			if(mBookmark != null) {
				Bookmark updated_bookmark = new Bookmark.Builder()
													.fromBookmark(mBookmark)
													.value(title)
													.isSynced(false)
													.isNew(false)
													.build();
		
				mBookmark = updated_bookmark;
				
				mApp.data().database().bookmarksDatabase().updateByNumber(mBookmark);
				mParent.getFragment(mPage.pageNumber() - 1).updateBookmarkImage();
			}
		} else {
			if(mBookmark != null) {
				Bookmark update_bookmark = new Bookmark.Builder()
													.fromBookmark(mBookmark)
													.value(title)
													.isSynced(false)
													.isRemoved(true)
													.build();
				
				mApp.data().database().bookmarksDatabase().updateByNumber(update_bookmark);
                mParent.getFragment(mPage.pageNumber() - 1).updateBookmarkImage();
			}
		}
	}
}
