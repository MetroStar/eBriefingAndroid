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

package com.metrostarsystems.ebriefing.Dashboard.TabMyBooks;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Dashboard.Tab.DashboardTabs.DashboardTab;
import com.metrostarsystems.ebriefing.Data.Framework.Annotations.Annotation;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Bookmarks.Bookmark;
import com.metrostarsystems.ebriefing.Data.Framework.Database.AnnotationsDatabase.AnnotationsDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BookmarksDatabase.BookmarksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Database.NotesDatabase.NotesDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.Notes.Note;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.Services.SyncService.SyncService;
import com.metrostarsystems.ebriefing.MainApplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentMyBooks extends AbstractPagerFragment implements BooksDatabaseChangedListener,
																	  BookmarksDatabaseChangedListener,
																	  NotesDatabaseChangedListener,
																	  AnnotationsDatabaseChangedListener {

	private static final String TAG = FragmentMyBooks.class.getSimpleName();
	
	private MainApplication				mApp;		
	private GridView 				 	mGridView;
	private FragmentMyBooksGridAdapter 	mGridAdapter;
	
	private int							mSelectedPosition;
	
	public static final FragmentMyBooks newInstance() {
		FragmentMyBooks fragment = new FragmentMyBooks();
		
	    return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (MainApplication) getActivity().getApplicationContext();
        
        if(mApp == null || mApp.data() == null) {
        	return;
        }

		mApp.data().database().booksDatabase().addListener(this);
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);
        
        setHasOptionsMenu(true);
        
        ((ActivityDashboard) getActivity()).update();
    }
	

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.book_grid, null);
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		TextView emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		emptyTextView.setText("You have not downloaded any books. Open the 'Available Tab' to select from the Library.");
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_books);

		mGridView.setEmptyView(emptyLayout);
		
		mGridAdapter = new FragmentMyBooksGridAdapter(rootView.getContext(), DashboardTab.TAB_MYBOOKS);

		mGridView.setAdapter(mGridAdapter);
		
		registerForContextMenu(mGridView);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Book book = (Book) mGridAdapter.getItem(position);
				
				if(book.status() == BookStatus.STATUS_DEVICE) {
					mApp.setCurrentBook(book);
					Book.generateBookChapterIntent(getActivity(), book.id());
				}
			}
			
		});
		
		setHasOptionsMenu(true);
		
		return rootView;
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
	
		mApp.data().database().booksDatabase().removeListener(this);
		mApp.data().database().notesDatabase().removeListener(this);
		mApp.data().database().bookmarksDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	
	@Override
	public void onStop() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().booksDatabase().removeListener(this);
		mApp.data().database().notesDatabase().removeListener(this);
		mApp.data().database().bookmarksDatabase().removeListener(this);
		mApp.data().database().annotationsDatabase().removeListener(this);
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		//mApp.data().addDashboardListener(this);
		mApp.data().database().booksDatabase().addListener(this);
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);
		
		((ActivityDashboard) getActivity()).update();
	}

	@Override
	public void onStart() {
		super.onStart();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		//mApp.data().addDashboardListener(this);
		mApp.data().database().booksDatabase().addListener(this);
		mApp.data().database().bookmarksDatabase().addListener(this);
		mApp.data().database().notesDatabase().addListener(this);
		mApp.data().database().annotationsDatabase().addListener(this);
		
		 ((ActivityDashboard) getActivity()).update();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.mybooks_context_menu, menu);
	    
	    mSelectedPosition = info.position;
	    
	    Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
	    
	    if(book.status() == BookStatus.STATUS_DEVICE) {
	    	menu.findItem(R.id.delete_book).setVisible(true);
	    	
		    if(!book.isFavorite()) {
		    	menu.findItem(R.id.add_favorite).setVisible(true);
		    	menu.findItem(R.id.remove_favorite).setVisible(false);
		    } else {
		    	menu.findItem(R.id.add_favorite).setVisible(false);
		    	menu.findItem(R.id.remove_favorite).setVisible(true);
		    }
	    } else if(book.status() == BookStatus.STATUS_SYNCING) {
	    	menu.findItem(R.id.delete_book).setVisible(true);
	    } else {
	    	menu.findItem(R.id.delete_book).setVisible(false);
	    	menu.findItem(R.id.add_favorite).setVisible(false);
	    	menu.findItem(R.id.remove_favorite).setVisible(false);
	    }
	    
	    if(book.status() == BookStatus.STATUS_DOWNLOADING_ACTIVE || 
	    		book.status() == BookStatus.STATUS_DOWNLOADING_PENDING) {
	    	menu.findItem(R.id.cancel_download).setVisible(true);
	    	menu.findItem(R.id.pause_download).setVisible(true);
	    } else {
	    	menu.findItem(R.id.cancel_download).setVisible(false);
	    	menu.findItem(R.id.pause_download).setVisible(false);
	    }
	    
	    if(book.status() == BookStatus.STATUS_DOWNLOADING_PAUSED) {
	    	menu.findItem(R.id.cancel_download).setVisible(true);
	    	menu.findItem(R.id.pause_download).setVisible(false);
	    	menu.findItem(R.id.resume_download).setVisible(true);
	    } else {
	    	menu.findItem(R.id.resume_download).setVisible(false);
	    }
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if(!getUserVisibleHint()) {
			return super.onContextItemSelected(item);
		}
		
		switch(item.getItemId()) {
			case R.id.add_favorite: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				if(book != null) {
					book.setFavorite(true);
                    book.setSynced(false);
					mApp.data().database().booksDatabase().update(book);

				}
				
				return true;
			}
			case R.id.remove_favorite: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				if(book != null) {
					book.setFavorite(false);
                    book.setSynced(false);
					mApp.data().database().booksDatabase().update(book);

				}
				
				return true;
			}
			case R.id.delete_book: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				if(book != null) {

                    book.setSynced(false);
                    Book.setStatusServer(mApp.data().database().booksDatabase(), book);
					SyncService.syncServiceRemoveMyBook(book.id());
					SyncService.syncServiceDeleteBook(book.id());


				}
				
				return true;
			}
			case R.id.cancel_download: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);

				if(book != null) {
					DownloadService.downloadServiceCancelBook(book.id());
				}
				
				return true;
			}
			case R.id.pause_download: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				if(book != null) {
					DownloadService.downloadServicePauseBook(book.id());
				}
				
				return true;
			}
			case R.id.resume_download: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				if(book != null) {
					DownloadService.downloadServiceResumeBook(book.id());
				}
				
				return true;
			}
		}

		return super.onContextItemSelected(item);                 
	}
	
	@Override
	public void OnBooksDatabaseChangedListener(Book book) {
		if(mGridAdapter != null) {
			mGridAdapter.refresh();
			mGridAdapter.notifyDataSetChanged();
		}
		
		((ActivityDashboard) getActivity()).update();
	}
	
	@Override
	public void refresh() {
		if(mGridAdapter != null) {
			mGridAdapter.refresh();
			mGridAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public int count(MainApplication main) {
		if(mApp == null) {
			return 0;
		}
		
		return mApp.data().database().booksDatabase().countMyBooks();
	}

	@Override
	public void OnAnnotationsDatabaseChangedListener(Annotation annotation) {
		refresh();
	}

	@Override
	public void OnNotesDatabaseChangedListener(Note note) {
		refresh();
	}

	@Override
	public void OnBookmarksDatabaseChangedListener(Bookmark bookmark) {
		refresh();
	}

	
}
