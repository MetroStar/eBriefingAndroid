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

package com.metrostarsystems.ebriefing.Dashboard.TabFavorites;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;

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

public class FragmentFavorites extends AbstractPagerFragment implements BooksDatabaseChangedListener {

	private static final String TAG = FragmentFavorites.class.getSimpleName();
	
	private MainApplication							mApp;	
	private GridView 				 				mGridView;
	private FragmentFavoritesGridAdapter 			mGridAdapter;
	
	private int							mSelectedPosition;
	
	public static final FragmentFavorites newInstance() {
		FragmentFavorites fragment = new FragmentFavorites();

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
		
		((ActivityDashboard) getActivity()).update();

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mApp = (MainApplication) getActivity().getApplicationContext();
		
		View rootView = inflater.inflate(R.layout.book_grid, null);
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		TextView emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		emptyTextView.setText("There are no Favorite books. To mark a book as favorite, long touch the book and select 'Add to Favorites'.");
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_books);

		mGridView.setEmptyView(emptyLayout);
		
		mGridAdapter = new FragmentFavoritesGridAdapter(rootView.getContext());

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
	}
	
	@Override
	public void onStop() {
		super.onDestroy();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().booksDatabase().removeListener(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		if(mApp == null) {
			mApp = (MainApplication) getActivity().getApplicationContext();
		}
		
		mApp.data().database().booksDatabase().addListener(this);
		
		((ActivityDashboard) getActivity()).update();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    mSelectedPosition = info.position;
	    
	    getActivity().getMenuInflater().inflate(R.menu.favorites_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(!getUserVisibleHint()) {
			return super.onContextItemSelected(item);
		}
		
		switch(item.getItemId()) {
			case R.id.remove_favorite: {
				Book book = (Book) mGridAdapter.getItem(mSelectedPosition);
				
				book.setFavorite(false);
				mApp.data().database().booksDatabase().update(book);
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
		
		return mApp.data().database().booksDatabase().countFavoriteBooks();
	}
}
