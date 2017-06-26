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

package com.metrostarsystems.ebriefing.BookChapter.TabBookmarks;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookChapter.ActivityChapter;
import com.metrostarsystems.ebriefing.BookChapter.Tab.ChapterTabs.ChapterTab;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentBookmarks extends AbstractPagerFragment implements BookmarksDatabaseChangedListener,
																		AnnotationsDatabaseChangedListener {

	private MainApplication					mApp;
	private GridView 						mGridView;
	private FragmentBookmarksGridAdapter 	mGridAdapter;
	
	private Book							mBook;
	
	private int								mSelectedPosition;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		View rootView = inflater.inflate(R.layout.bookmark_grid, null);
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		TextView emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		emptyTextView.setText("There are no bookmarks to display!");
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_bookmarks);

		mGridView.setEmptyView(emptyLayout);

		mBook = ((ActivityChapter) getActivity()).book();
		
		
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);
		
		mGridAdapter = new FragmentBookmarksGridAdapter(rootView.getContext(), ChapterTab.TAB_BOOKMARKS, mBook);
		((ActivityChapter) getActivity()).update();
		
		mGridView.setAdapter(mGridAdapter);
		
		registerForContextMenu(mGridView);
		
		mGridView.setOnItemClickListener(new OnBookmarkClickListener());
		
		return rootView;
	}
	
	@Override
	public void OnBookmarksDatabaseChangedListener(Bookmark bookmark) {
		if(bookmark != null && mGridAdapter != null) {
			mGridAdapter.refresh(mBook);
			mGridAdapter.notifyDataSetChanged();
		}
		
		((ActivityChapter) getActivity()).update();
	}
	
	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		if(annotation != null && mGridAdapter != null) {
			mApp.data().imageManager().clearThumbnails();
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
		
		mApp.data().database().bookmarksDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    mSelectedPosition = info.position;
	    
	    getActivity().getMenuInflater().inflate(R.menu.bookmarks_context_menu, menu);

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.edit_bookmark: {
				Bookmark bookmark = mGridAdapter.getItem(mSelectedPosition);
				
				Book.generateBookPageEditBookmarkIntent((Activity) getActivity(), bookmark);
				break;
			}
			case R.id.remove_bookmark: {
				
				Bookmark bookmark = mGridAdapter.getItem(mSelectedPosition);
				
				Bookmark remove_bookmark = new Bookmark.Builder()
													.fromBookmark(bookmark)
													.value(bookmark.value())
													.isSynced(false)
													.isRemoved(true)
													.build();
				
				mApp.data().database().bookmarksDatabase().updateByNumber(remove_bookmark);
			
				break;
			}
		}

		return super.onContextItemSelected(item);             
	}
	
	private class OnBookmarkClickListener implements OnItemClickListener {
		
		public OnBookmarkClickListener() {
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Bookmark bookmark = mGridAdapter.getItem(position);

			Book.generateBookPageIntent((Activity) getActivity(), bookmark.bookId(), bookmark.pageNumber());
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
	public int count(MainApplication app) {
		if(mApp == null || mBook == null) {
			return 0;
		}
		
		return mApp.data().database().bookmarksDatabase().countByBook(mBook.id());
	}

	
	
	
}
