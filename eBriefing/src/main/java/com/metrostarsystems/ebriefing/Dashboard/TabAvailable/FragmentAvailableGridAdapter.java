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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book.BookStatus;
import com.metrostarsystems.ebriefing.Services.DownloadService.DownloadService;

public class FragmentAvailableGridAdapter extends BaseAdapter {
	
	private static final String TAG = FragmentAvailableGridAdapter.class.getSimpleName();
	
	private MainApplication mApp;
	private Context mContext;
	private ArrayList<Book> mList = new ArrayList<Book>();
	
	public FragmentAvailableGridAdapter(Context context) {
		
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		refresh();
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
	
	@Override
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
			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.textView_title);
			holder.mDescriptionTextView = (TextView) convertView.findViewById(R.id.textView_description);
			holder.mDownloadButton = (Button) convertView.findViewById(R.id.button_download);
			holder.mDataLayout = (LinearLayout) convertView.findViewById(R.id.include_data_layout);
			holder.mPagesTextView = (TextView) holder.mDataLayout.findViewById(R.id.textView_pages);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		Book book = (Book) getItem(position);
		
		if(book.status() == BookStatus.STATUS_SERVER) {
			holder.mTitleTextView.setText(book.title());
			
		//	holder.mBookImageView.setBackgroundResource(R.drawable.book_image_mask);
			mApp.imageLoader().displayImage(book.largeImageUrl(), holder.mBookImageView, mApp.imageOptions());
			
			
			
	//		UrlImageViewHelper.setUrlDrawable(holder.mBookImageView, 
	//											book.largeImageUrl(), 
	//											android.R.drawable.ic_menu_gallery, 
	//											Settings.IMAGE_CACHE_DURATION,
	//											myCallback);
			
			
			holder.mDescriptionTextView.setVisibility(View.GONE);
			holder.mDownloadButton.setVisibility(View.VISIBLE);
			holder.mDownloadButton.setOnClickListener(new DownloadClickListener(book));
			
			
			holder.mPagesTextView.setText(String.valueOf(book.pageCount()));
		}
		
		return convertView;
	}
	
	
	
	public void refresh() {
		mList.clear();
		
		if(mApp != null && mApp.data() != null && mApp.data().database() != null) {
			mList.addAll(mApp.data().database().booksDatabase().getAvailableBooks());
		}
	}
	
	private class ViewHolder {
		public ImageView 	mBookImageView;
		public TextView 	mTitleTextView;
		public TextView 	mDescriptionTextView;
		public Button 		mDownloadButton;
		public LinearLayout mDataLayout;
		public TextView 	mPagesTextView;
	}
	
	private class DownloadClickListener implements OnClickListener {

		private Book mBook;
		
		public DownloadClickListener(Book book) {
			mBook = book;
		}

		@Override
		public void onClick(View v) {
			DownloadService.downloadDeviceBookData(mBook);
		}
		
	}
}
