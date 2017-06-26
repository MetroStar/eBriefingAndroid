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

package com.metrostarsystems.ebriefing.Dashboard.TabAvailable;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Utilities;
import com.metrostarsystems.ebriefing.Dashboard.ActivityDashboard;
import com.metrostarsystems.ebriefing.Dashboard.Overview.FragmentOverview;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Database.BooksDatabase.BooksDatabaseChangedListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentAvailable extends AbstractPagerFragment implements BooksDatabaseChangedListener {

	private static final String TAG = FragmentAvailable.class.getSimpleName();
	
	private MainApplication					mApp;	
	private GridView 				 		mGridView;
	private FragmentAvailableGridAdapter 	mGridAdapter;
	
	private TextView 						mEmptyTextView;
	
	public static final FragmentAvailable newInstance() {
		FragmentAvailable fragment = new FragmentAvailable();

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
		
		View rootView = inflater.inflate(R.layout.book_grid, null);
		
		LinearLayout emptyLayout = (LinearLayout) rootView.findViewById(R.id.empty);
		mEmptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
		
		if(Utilities.isNetworkAvailable(getActivity())) {
			mEmptyTextView.setText("There are no books available in the Library.");
		} else {
			mEmptyTextView.setText("Network connection unavailable, please check your connection.");
		}
		
		mGridView = (GridView) rootView.findViewById(R.id.gridView_books);

		mGridView.setEmptyView(emptyLayout);
	
		mGridAdapter = new FragmentAvailableGridAdapter(rootView.getContext());

		mGridView.setAdapter(mGridAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Book book = (Book) mGridAdapter.getItem(position);
				
				FragmentOverview overview = FragmentOverview.newInstance(book.id());
				overview.show(getActivity().getFragmentManager(), "Overview Fragment");
			}
			
		});
		
		return rootView;
	}
	
	@Override
	public void OnBooksDatabaseChangedListener(Book book) {
		if(mEmptyTextView != null) {
			if(Utilities.isNetworkAvailable(getActivity())) {
				mEmptyTextView.setText("There are no books available in the Library.");
			} else {
				mEmptyTextView.setText("Network connection unavailable, please check your connection.");
			}
		}
		
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
	public void onDestroy() {
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
	public int count(MainApplication main) {
		if(mApp == null) {
			return 0;
		}
		
		return mApp.data().database().booksDatabase().countAvailableBooks();
	}


	
}
