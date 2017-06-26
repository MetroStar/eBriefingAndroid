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

package com.metrostarsystems.ebriefing.BookPage.Contents.TabAll;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContents;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContentsPagerTab;
import com.metrostarsystems.ebriefing.Data.Framework.AbstractPagerFragment;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;

public class FragmentAll extends AbstractPagerFragment {

	private MainApplication			mApp;		
	private GridView 				mGridView;
	private FragmentAllGridAdapter 	mGridAdapter;
	
	private ActivityContents		mActivity;
	
	private Book					mBook;
	
	
	public static final FragmentAll newInstance() {
		FragmentAll fragment = new FragmentAll();

	    return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mApp = (MainApplication) getActivity().getApplicationContext();
        
        if(savedInstanceState != null) {
        	
        	mBook = mApp.data().database().booksDatabase().book(savedInstanceState.getString("bookid"));
        } else {
        	mBook = ((ActivityContents) getActivity()).book();
        }
        
        mActivity = (ActivityContents) getActivity();
        
        mActivity.update();

    }
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.page_grid, null);

		mGridView = (GridView) rootView.findViewById(R.id.gridView_pages);

		
		mGridAdapter = new FragmentAllGridAdapter(rootView.getContext(), ActivityContentsPagerTab.TAB_ALL, mBook);

		mGridView.setAdapter(mGridAdapter);
			
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent returnIntent = new Intent();
				
				Bundle extras = new Bundle();
				extras.putInt("pagenumber", position + 1);
				returnIntent.putExtras(extras);
				
				mActivity.setResult(1, returnIntent);        
				mActivity.finish();
			}
			
		});
		
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		outState.putString("bookid", mBook.id());
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
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
		if(main == null || mBook == null || mGridAdapter == null) {
			return 0;
		}
		
		return mGridAdapter.getCount();
	}
}
