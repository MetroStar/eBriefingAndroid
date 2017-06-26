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

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.LoadBookImageTask;
import com.metrostarsystems.ebriefing.MainApplication;

public class FragmentFavoritesGridAdapter extends BaseAdapter {

	private MainApplication mApp;
	private Context mContext;
	private ArrayList<Book> mList;
	
	public FragmentFavoritesGridAdapter(Context context) {
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		if(mApp.data() == null) {
			mList = new ArrayList<Book>();
		} else {
			mList = mApp.data().database().booksDatabase().getFavoriteBooks();
		}
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}


	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.activity_dashboard_book_grid_list_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mBookImageView = (ImageView) convertView.findViewById(R.id.imageView_book);
			holder.mFavoriteImageView = (ImageView) convertView.findViewById(R.id.imageView_favorite);
			holder.mNewImageView = (ImageView) convertView.findViewById(R.id.imageView_new);
			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.textView_title);
			holder.mDescriptionTextView = (TextView) convertView.findViewById(R.id.textView_description);
			holder.mDownloadButton = (Button) convertView.findViewById(R.id.button_download);
			holder.mDataLayout = (LinearLayout) convertView.findViewById(R.id.include_data_layout);
			holder.mNotesTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_notes);
			holder.mBookmarksTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_bookmarks);
			holder.mAnnotationsTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_annotations);
			holder.mPagesTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_pages);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		Book book = (Book) getItem(position);
		
		try {
			if(mApp.data().imageManager().hasLargeImageFile(mApp, book)) {	
				if(holder.mImageTask != null) {
					if(holder.mImageTask.getStatus() == AsyncTask.Status.RUNNING) {
						holder.mImageTask.cancel(true);
						holder.mImageTask = null;
					}
				}
				
				holder.mImageTask = new LoadBookImageTask(mApp, holder.mBookImageView, book);
				holder.mImageTask.execute();
//				holder.mBookImageView.setImageBitmap(book.generateLargeImage());
			} else {
				mApp.imageLoader().displayImage(book.largeImageUrl(), holder.mBookImageView, mApp.imageOptions());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		holder.mTitleTextView.setText(book.title());
		
		holder.mNotesTextView.setText(String.valueOf(mApp.data().database().notesDatabase().countByBook(book.id())));
		holder.mBookmarksTextView.setText(String.valueOf(mApp.data().database().bookmarksDatabase().countByBook(book.id())));
		holder.mAnnotationsTextView.setText(String.valueOf(mApp.data().database().annotationsDatabase().countByBook(book.id())));
		holder.mPagesTextView.setText(String.valueOf(book.pageCount()));
		
		if(book.status() == BookStatus.STATUS_DEVICE) {
			holder.mDescriptionTextView.setText(book.description());
			holder.mDescriptionTextView.setVisibility(View.VISIBLE);
			holder.mDownloadButton.setVisibility(View.GONE);
			
			if(book.isFavorite()) {
				holder.mFavoriteImageView.setVisibility(View.VISIBLE);
			} else {
				holder.mFavoriteImageView.setVisibility(View.GONE);
			}
			
			if(book.isNew()) {
				holder.mNewImageView.setVisibility(View.VISIBLE);
			} else {
				holder.mNewImageView.setVisibility(View.GONE);
			}
			
			holder.mDownloadButton.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public void refresh() {
		mList = mApp.data().database().booksDatabase().getFavoriteBooks();
	}
	
	private class ViewHolder {
		public LoadBookImageTask mImageTask;
		public ImageView mBookImageView;
		public TextView mTitleTextView;
		public TextView mDescriptionTextView;
		public Button mDownloadButton;
		public LinearLayout mDataLayout;
		public TextView mNotesTextView;
		public TextView mBookmarksTextView;
		public TextView mAnnotationsTextView;
		public TextView mPagesTextView;
		
		public ImageView mNewImageView;
		public ImageView mFavoriteImageView;
	}
}
