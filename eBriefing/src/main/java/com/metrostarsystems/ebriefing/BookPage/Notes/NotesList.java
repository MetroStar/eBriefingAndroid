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

package com.metrostarsystems.ebriefing.BookPage.Notes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.BookPage.ActivityPage;
import com.metrostarsystems.ebriefing.BookPage.Page.FragmentPage;
import com.metrostarsystems.ebriefing.BookPage.Print.DialogFragmentPrint;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

public class NotesList {
	
	private static final String TAG = NotesList.class.getSimpleName();

	private MainApplication				mApp;
	private ActivityPage 				mParent;
	
	private OnCloseNotesEditorListener	mCloseListener;
	
	private Book						mBook;

	private int 						mPageNumber = -1;

	private boolean		 				mShowNotes = false;
	
	private LinearLayout				mNotesLayout;
	private ImageView					mNotesAddButton;
	private ListView					mNotesListView;
	private NotesListAdapter 			mNotesListAdapter;
	
	public NotesList(ActivityPage page) {
		mApp = (MainApplication) page.getApplicationContext();
		mParent = page;
		mCloseListener = (OnCloseNotesEditorListener) page;
		mBook = mParent.book();
		
		// Notes Layouts
		mNotesLayout = (LinearLayout) mParent.readerLayout().findViewById(R.id.include_page_notes);

		mNotesListView = (ListView) mNotesLayout.findViewById(R.id.listView_notes);
		
		
		
		mNotesAddButton = (ImageView) mNotesLayout.findViewById(R.id.imageView_add_note);
		
		mNotesAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(mApp.serverConnection().isMultiNotes()) {
					DialogFragmentNote.newInstance(mBook.id(), mParent.currentFragment().pageNumber())
								.show(mParent.getSupportFragmentManager(), "New Note Fragment");
				} else {
					
					if(mNotesListAdapter.getCount() > 0) {
						Note note = getItem(0);
				    	
				    	DialogFragmentNote.newInstance(mBook.id(), mParent.currentFragment().pageNumber(), note.id())
				    				.show(mParent.getSupportFragmentManager(), "Edit Note Fragment");
					} else {
						DialogFragmentNote.newInstance(mBook.id(), mParent.currentFragment().pageNumber())
									.show(mParent.getSupportFragmentManager(), "New Note Fragment");
					}
				}
			}
			
		});
		
		Button close_button = (Button) mNotesLayout.findViewById(R.id.button_close);
		
		close_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close();
			}
			
		});
	}

	public void update() {
		if(mNotesListAdapter != null && mBook != null && mPageNumber != 0) {
			Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), mPageNumber);
			
			mNotesListAdapter.refresh(mBook, page.id());
			mNotesListAdapter.notifyDataSetChanged();
		}
	}
	
	public boolean isOpen() {
		return mShowNotes;
	}
	
	public Note getItem(int position) {
		return mNotesListAdapter.getItem(position);
	}
	
	public void open(int pageNumber) {
		mPageNumber = pageNumber;
		
		Page page = mApp.data().database().pagesDatabase().pageByNumber(mBook.id(), pageNumber);
		
		mNotesListAdapter = new NotesListAdapter(mParent, mBook, page.id());
		mNotesListView.setAdapter(mNotesListAdapter);
		
		mParent.registerForContextMenu(mNotesListView);
		
		mShowNotes = true;
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		
		animation.setDuration(500);
		mNotesLayout.startAnimation(animation);
		
		mNotesLayout.setVisibility(View.VISIBLE);
	}

	public void close() {
		mShowNotes = false;
		
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		
		animation.setDuration(500);
		mNotesLayout.startAnimation(animation);
		
		mNotesLayout.setVisibility(View.GONE);

		mCloseListener.OnCloseNotes(true);
	}
	
	public static interface OnCloseNotesEditorListener {
		public abstract void OnCloseNotes(boolean closed);
	}
}
