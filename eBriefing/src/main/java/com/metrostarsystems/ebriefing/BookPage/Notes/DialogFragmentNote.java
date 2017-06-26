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

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Settings;
import com.metrostarsystems.ebriefing.Tags;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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

public class DialogFragmentNote extends DialogFragment {

	private static final String TAG = DialogFragmentNote.class.getSimpleName();
	
	
	
	private MainApplication		mApp;
	
	private Book				mBook;
	private Page			mPage;
	private Note			mNote;
	
	private TextView			mSaveButton;
	private TextView			mCancelButton;
	
	private int					mMaxCount 					= Settings.MAX_NOTE_LENGTH;
	private int					mCharacterCount			 	= 0;
	
	private TextView 			mTitleTextView;
	private EditText			mContentEditText;
	private TextView			mCharacterCountTextView;
	
	public static final DialogFragmentNote newInstance(String bookId, int currentPage) {
		DialogFragmentNote fragment = new DialogFragmentNote();
	    Bundle bundle = new Bundle();
	    bundle.putString(Tags.BOOK_ID_TAG, bookId);
	    bundle.putInt(Tags.PAGE_NUMBER_TAG, currentPage);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	public static final DialogFragmentNote newInstance(String bookId, int currentPage, String noteId) {
		DialogFragmentNote fragment = new DialogFragmentNote();
	    Bundle bundle = new Bundle();
	    bundle.putString(Tags.BOOK_ID_TAG, bookId);
	    bundle.putInt(Tags.PAGE_NUMBER_TAG, currentPage);
	    bundle.putString(Tags.NOTE_ID_TAG, noteId);
	    fragment.setArguments(bundle);
	    return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.dialog_edit_note, container);
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
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
		
		if(getArguments().containsKey(Tags.NOTE_ID_TAG)) {
			String note_id = getArguments().getString(Tags.NOTE_ID_TAG);
			
			mNote = mApp.data().database().notesDatabase().noteById(note_id);
		}
		
		mTitleTextView = (TextView) view.findViewById(R.id.textView_title);
		mContentEditText = (EditText) view.findViewById(R.id.editText_content);
		mCharacterCountTextView = (TextView) view.findViewById(R.id.textView_character_count);
		mCancelButton = (TextView) view.findViewById(R.id.textView_cancel);
		mSaveButton = (TextView) view.findViewById(R.id.textView_save);
		
		mContentEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { }

			@Override
			public void afterTextChanged(Editable s) {
				refreshCharacterCount();
			}
			
		});
		
		mCharacterCountTextView.setText(String.valueOf(Settings.MAX_NOTE_LENGTH));
		
		if(mNote == null) {
			mTitleTextView.setText("New Note");
		} else {
			mTitleTextView.setText("Edit Note");
			mContentEditText.setText(mNote.content());
			refreshCharacterCount();
		}
		
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		
		mSaveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mNote != null) {
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
	    final Dialog dialog = super.onCreateDialog(savedInstanceState);
	    
	    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    dialog.getWindow().getAttributes().windowAnimations = R.style.NoteDialogAnimation;
	    
	    Utilities.openSoftKeyboard(getActivity(), mContentEditText);
		
	    return dialog;
	}

	@Override
	public void dismiss() {
		Utilities.hideKeyboardFrom(getActivity(), mContentEditText);
		
		super.dismiss();
	}

	private void refreshCharacterCount() {
		mCharacterCount = mMaxCount - mContentEditText.getText().toString().length();
		mCharacterCountTextView.setText(String.valueOf(mCharacterCount));
		
		if(mCharacterCount < 0) {
			mCharacterCountTextView.setTextColor(Color.RED);
			mSaveButton.setEnabled(false);
		} else if(mCharacterCount == mMaxCount) {
			mCharacterCountTextView.setTextColor(Color.BLACK);
			mSaveButton.setEnabled(false);
		} else if(mCharacterCount > 0 && mCharacterCount < mMaxCount){
			mCharacterCountTextView.setTextColor(Color.BLACK);
			mSaveButton.setEnabled(true);
		}
	}
	
	private void save() {
		String content = mContentEditText.getText().toString();
		
		if(!content.isEmpty()) {
			Note new_note = new Note.Builder()
										.id()
										.bookId(mBook.id())
										.bookVersion(mBook.bookVersion())
										.chapterId(mPage.chapterId())
										.pageId(mPage.id())
										.pageNumber(mPage.pageNumber())
										.content(content)
										.isSynced(false)
										.build();
			
			mApp.data().database().notesDatabase().insert(new_note);
		} else {
			if(mNote != null) {
				Note remove_note = new Note.Builder()
										.fromNote(mNote)
										.content(mNote.content())
										.isRemoved(true)
										.isSynced(false)
										.build();
				
				mApp.data().database().notesDatabase().updateById(remove_note);
			}
		}
	}
	
	private void update() {
		String content = mContentEditText.getText().toString();
		
		if(!content.isEmpty()) {
			if(mNote != null) {
				Note updated_note = new Note.Builder()
										.fromNote(mNote)
										.content(content)
										.isSynced(false)
										.build();
				
				mNote = updated_note;
				
				mApp.data().database().notesDatabase().updateById(updated_note);
			}
		} else {
			if(mNote != null) {
				Note remove_note = new Note.Builder()
										.fromNote(mNote)
										.content(mNote.content())
										.isRemoved(true)
										.isSynced(false)
										.build();
				
				mApp.data().database().notesDatabase().updateById(remove_note);
			}
		}
	}
}
