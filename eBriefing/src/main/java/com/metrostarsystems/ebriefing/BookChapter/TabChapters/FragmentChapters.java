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

package com.metrostarsystems.ebriefing.BookChapter.TabChapters;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs.ChapterTab;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Chapter.Chapter;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.ChaptersDatabase.ChaptersDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.NotesDatabase.NotesDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentChapters extends AbstractPagerFragment implements BooksDatabaseChangedListener,
																	   BookmarksDatabaseChangedListener,
																	   NotesDatabaseChangedListener,
																	   AnnotationsDatabaseChangedListener {

	private MainApplication					mApp;		
	private GridView 						mGridView;
	private FragmentChaptersGridAdapter 	mGridAdapter;
	private Book							mBook;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.chapter_grid, null);
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		TextView emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		emptyTextView.setText("This book has no chapters.");
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_chapters);

		mGridView.setEmptyView(emptyLayout);
		
		mApp.data().database().booksDatabase().addListener(this);
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);

		mBook = ((ActivityChapter) getActivity()).book();

		mGridAdapter = new FragmentChaptersGridAdapter(rootView.getContext(), ChapterTab.TAB_CHAPTERS, mBook);

		mGridView.setAdapter(mGridAdapter);
		
		mGridView.setOnItemClickListener(new OnChapterClickListener());

		return rootView;
	}
	
	@Override
	public void OnBooksDatabaseChangedListener(Book book) {
		if(book != null && mGridAdapter != null) {
			mGridAdapter.refresh(mBook);
			mGridAdapter.notifyDataSetChanged();
		}
		
		((ActivityChapter) getActivity()).update();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().booksDatabase().removeListener(this);
		mApp.data().database().bookmarksDatabase().removeListener(this);
		mApp.data().database().notesDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	

	private class OnChapterClickListener implements OnItemClickListener {
		
		public OnChapterClickListener() {
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Chapter chapter = mGridAdapter.getItem(position);
			
			Book.generateBookPageIntent((Activity) getActivity(), chapter.bookId(), chapter.id());
		}
	}
	
	@Override
	public void refresh() {
		if(mGridAdapter != null && mBook != null) {
			mGridAdapter.refresh(mBook);
			mGridAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public int count(MainApplication main) {
		return 0;
	}

	@Override
	public void OnBookmarksDatabaseChangedListener(Bookmark bookmark) {
		refresh();
	}

	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		refresh();
	}

	@Override
	public void OnNotesDatabaseChangedListener(Note note) {
		refresh();
	}
}
