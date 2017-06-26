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

package com.metrostarsystems.ebriefing.BookChapter.TabOverview;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.NotesDatabase.NotesDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.LoadBookImageTask;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentOverview extends AbstractPagerFragment implements NotesDatabaseChangedListener,
																	   BookmarksDatabaseChangedListener,
																	   AnnotationsDatabaseChangedListener {
	
	private MainApplication		mApp;	
	
	private Book 				mBook;
	
	private LinearLayout		mDataLayout;
	
	private TextView mNotesTextView;
	private TextView mBookmarksTextView;
	private TextView mAnnotationsTextView;
	private TextView mPagesTextView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_chapter_overview, null);
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);

		mBook = ((ActivityChapter) getActivity()).book();
		
		mDataLayout = (LinearLayout) rootView.findViewById(R.id.include_data_layout);
		
		ImageView bookImageView = (ImageView) rootView.findViewById(R.id.imageView_cover);
		
		try {
			new LoadBookImageTask(mApp, bookImageView, mBook).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mNotesTextView = (TextView) mDataLayout.findViewById(R.id.textView_notes);
		mBookmarksTextView = (TextView) mDataLayout.findViewById(R.id.textView_bookmarks);
		mAnnotationsTextView = (TextView) mDataLayout.findViewById(R.id.textView_annotations);
		mPagesTextView = (TextView) mDataLayout.findViewById(R.id.textView_pages);

		update();
		
		TextView descriptionTextView = (TextView) rootView.findViewById(R.id.textView_description);
		descriptionTextView.setText(mBook.description());
		
		TextView dateCreatedTextView = (TextView) rootView.findViewById(R.id.textView_date_created);
		dateCreatedTextView.setText("Date Created: " + mBook.dateAddedOverviewFormat());
		
		TextView dateModifiedTextView = (TextView) rootView.findViewById(R.id.textView_date_modified);
		dateModifiedTextView.setText("Date Modified: " + mBook.dateModifiedOverviewFormat());
		
		TextView versionTextView = (TextView) rootView.findViewById(R.id.textView_version);
		versionTextView.setText("Version: " + String.valueOf(mBook.bookVersion()));
		
		return rootView;
	}
	
	private void update() {
		mNotesTextView.setText(String.valueOf(mApp.data().database().notesDatabase().countByBook(mBook.id())));
		mBookmarksTextView.setText(String.valueOf(mApp.data().database().bookmarksDatabase().countByBook(mBook.id())));
		mAnnotationsTextView.setText(String.valueOf(mApp.data().database().annotationsDatabase().countByBook(mBook.id())));
		mPagesTextView.setText(String.valueOf(mApp.data().database().pagesDatabase().countByBook(mBook.id())));
	}

	@Override
	public int count(MainApplication main) {
		return 0;
	}
	
	@Override
	public void refresh() {
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().notesDatabase().removeListener(this);
		mApp.data().database().bookmarksDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}

	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		update();
	}

	@Override
	public void OnBookmarksDatabaseChangedListener(Bookmark bookmark) {
		update();
	}

	@Override
	public void OnNotesDatabaseChangedListener(Note note) {
		update();
	}
}
