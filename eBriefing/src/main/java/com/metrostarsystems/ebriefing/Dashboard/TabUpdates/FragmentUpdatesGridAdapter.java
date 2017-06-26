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

package com.metrostarsystems.ebriefing.Dashboard.TabUpdates;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;
import com.metrostarsystems.ebriefing.MainApplication;

public class FragmentUpdatesGridAdapter extends BaseAdapter {

	private MainApplication 				mApp;
	private Context 						mContext;
	private ArrayList<Book> 				mList;
	
	public FragmentUpdatesGridAdapter(Context context) {
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		mList = mApp.data().database().booksDatabase().getUpdatedBooks();
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
			holder.mDownloadingLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout_downloading);
			holder.mStatus = (TextView) convertView.findViewById(R.id.textView_status);
			holder.mDownloadProgressLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout_download_progress);
			holder.mDownloadButton = (Button) convertView.findViewById(R.id.button_download);
			holder.mDownloadProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_download);
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
			mApp.imageLoader().displayImage(book.largeImageUrl(), holder.mBookImageView, mApp.imageOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(book.status() == BookStatus.STATUS_UPDATING) {
			holder.mDescriptionTextView.setVisibility(View.GONE);
			holder.mStatus.setVisibility(View.VISIBLE);
			holder.mStatus.setText("Updating...");
			
			holder.mDownloadButton.setVisibility(View.GONE);
			holder.mDownloadProgressBar.setVisibility(View.VISIBLE);
			holder.mDownloadProgressBar.setIndeterminate(true);
			holder.mDownloadingLayout.setVisibility(View.VISIBLE);
			holder.mDownloadProgressLayout.setVisibility(View.VISIBLE);
		} else if(book.status() == BookStatus.STATUS_DEVICE) {
			holder.mDescriptionTextView.setVisibility(View.GONE);
			holder.mDownloadButton.setVisibility(View.VISIBLE);
			holder.mDownloadingLayout.setVisibility(View.GONE);
			holder.mDownloadProgressLayout.setVisibility(View.GONE);
			holder.mDownloadButton.setOnClickListener(new DownloadClickListener(mApp, book));
		}
		
		holder.mTitleTextView.setText(book.title());
		
		holder.mNotesTextView.setText(String.valueOf(0));
		holder.mBookmarksTextView.setText(String.valueOf(0));
		holder.mAnnotationsTextView.setText(String.valueOf(0));
		holder.mPagesTextView.setText(String.valueOf(book.pageCount()));
	
		holder.mNewImageView.setVisibility(View.VISIBLE);
		
		
		
		return convertView;
	}
	
	public void refresh() {
		mList = mApp.data().database().booksDatabase().getUpdatedBooks();
	}
	
	private class ViewHolder {
//		public LoadBookImageTask mImageTask;
		public ImageView mBookImageView;
		
		public TextView mTitleTextView;
		public TextView mDescriptionTextView;
		public LinearLayout mDownloadingLayout;
		public Button mDownloadButton;
		public LinearLayout mDataLayout;
		public TextView mNotesTextView;
		public TextView mBookmarksTextView;
		public TextView mAnnotationsTextView;
		public TextView mPagesTextView;
		
		public TextView mStatus;
		public LinearLayout	mDownloadProgressLayout;
		public ProgressBar mDownloadProgressBar;
		
		public ImageView mNewImageView;
		public ImageView mFavoriteImageView;
	}
	
	private class DownloadClickListener implements OnClickListener {

		private MainApplication mApp;
		private Book mBook;
		
		public DownloadClickListener(MainApplication main, Book book) {
			mBook = book;
			mApp = main;
		}

		@Override
		public void onClick(View v) {
			DownloadService.downloadUpdateBookData(mBook);
		}
		
	}
}
