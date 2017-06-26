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

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.BookPage.Contents.ActivityContentsPagerTab;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;
import com.metrostarsystems.ebriefing.Data.Framework.Page.Page;

public class FragmentAllGridAdapter extends ArrayAdapter<Page> {

	private MainApplication 			mApp;
	private Context 					mContext;
	private ActivityContentsPagerTab 	mTab;
	private Book 						mBook;
	
	public FragmentAllGridAdapter(Context context, ActivityContentsPagerTab tab, Book book) {
		super(context, R.layout.page_grid_list_item);
		
		mTab = tab;
		
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		mBook = book;
		
		addAll(mApp.data().database().pagesDatabase().pagesByBook(mBook.id()));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.page_grid_list_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mPageImageView = (ImageView) convertView.findViewById(R.id.imageView_thumbnail);
			holder.mBookmarkImageView = (ImageView) convertView.findViewById(R.id.imageView_bookmark);
			holder.mPageNumberTextView = (TextView) convertView.findViewById(R.id.textView_page_number);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		Page page = getItem(position);
		
		try {
			if(holder.mThumbnailTask != null) {
				if(holder.mThumbnailTask.getStatus() == AsyncTask.Status.RUNNING) {
					holder.mThumbnailTask.cancel(true);
					holder.mThumbnailTask = null;
				}
			}
			
			holder.mPageImageView.setImageResource(android.R.drawable.ic_menu_gallery);
			holder.mThumbnailTask = new GeneratePageThumbnailTask(holder.mPageImageView, mBook, true);
			holder.mThumbnailTask.execute(page.pageNumber());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if(mApp.data().database().bookmarksDatabase().has(page.bookId(), page.pageNumber())) {
			holder.mBookmarkImageView.setVisibility(View.VISIBLE);
		} else {
			holder.mBookmarkImageView.setVisibility(View.GONE);
		}
		
		holder.mPageNumberTextView.setText(String.valueOf(page.pageNumber()));
    	
		return convertView;
	}
	
	public void refresh(Book book) {
		clear();
		mBook = book;
		addAll(mApp.data().database().pagesDatabase().pagesByBook(mBook.id()));
	}
	
	private class ViewHolder {
		public GeneratePageThumbnailTask mThumbnailTask;
		public ImageView mPageImageView;
		public ImageView mBookmarkImageView;
		public TextView  mPageNumberTextView;
	}
}