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

package com.metrostarsystems.ebriefing.BookChapter.TabNotes;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs.ChapterTab;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.NotesDatabase.NotesDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentNotes extends AbstractPagerFragment implements NotesDatabaseChangedListener,
																	AnnotationsDatabaseChangedListener {

	private MainApplication				mApp;
	
	private Book						mBook;
	
	private FragmentNotesListView		mListView;
	private FragmentNotesListAdapter	mListAdapter;
	
//	private int							mSelectedPosition;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		View rootView = inflater.inflate(R.layout.activity_chapter_notes_list, null);
		
		mBook = ((ActivityChapter) getActivity()).book();
		
		mListView = new FragmentNotesListView(rootView);

		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);
		
		mListAdapter = new FragmentNotesListAdapter(getActivity(), rootView.getContext(), mBook);
		((ActivityChapter) getActivity()).update();
		
		mListView.setAdapter(mListAdapter);
		
		return rootView;
	}
	
	@Override
	public void OnNotesDatabaseChangedListener(Note note) {
		if(mApp != null && mBook != null && mListAdapter != null) {
			mListAdapter.refresh(mBook);
			mListAdapter.notifyDataSetChanged();
		}
		((ActivityChapter) getActivity()).update();
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().notesDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	
	@Override
	public void refresh() {
		if(mListAdapter != null && mBook != null) {
			mListAdapter.refresh(mBook);
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public int count(MainApplication main) {
		if(mApp == null || mBook == null) {
			return 0;
		}
		
		return mApp.data().database().notesDatabase().countByBook(mBook.id());
	}

	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		if(annotation != null && mListAdapter != null) {
			mApp.data().imageManager().clearThumbnails();
			mListAdapter.refresh(mBook);
			mListAdapter.notifyDataSetChanged();
		}
		((ActivityChapter) getActivity()).update();
	}
	
	
}
