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

package com.metrostarsystems.ebriefing.BookPage.Search;

import java.util.ArrayList;

import com.metrostarsystems.ebriefing.MainApplication;
import com.metrostarsystems.ebriefing.R;
import com.metrostarsystems.ebriefing.Data.Framework.Book.Book;
import com.metrostarsystems.ebriefing.Data.Framework.Managers.ManagerImages.GeneratePageThumbnailTask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListAdapter extends ArrayAdapter<SearchObject> {
	
	private MainApplication mApp;
	private Context mContext;
	private Book mBook;
	
	public SearchListAdapter(Context context, Book book, ArrayList<SearchObject> list) {
		super(context, R.layout.dialog_search_list_item);
		
		mApp = (MainApplication) context.getApplicationContext();
		mContext = context;
		
		addAll(list);
		
		mBook = book;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		// If convertview is null create the layout
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.dialog_search_list_item, parent, false);
			
			// Create the view holder
			holder = new ViewHolder();
			holder.mThumbImageView = (ImageView) convertView.findViewById(R.id.imageView_thumb);
			holder.mPageTextView = (TextView) convertView.findViewById(R.id.textView_page);
			holder.mPhraseTextView = (TextView) convertView.findViewById(R.id.textView_phrase);
			
			convertView.setTag(holder);
		} else {
			// Reuse the view holder
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Otherwise recycle the layout
		
		SearchObject object = getItem(position);
		
		try {
			if(holder.mThumbnailTask != null) {
				if(holder.mThumbnailTask.getStatus() == AsyncTask.Status.RUNNING) {
					holder.mThumbnailTask.cancel(true);
				}
			}
			
			holder.mThumbnailTask = new GeneratePageThumbnailTask(holder.mThumbImageView, mBook, false);
			holder.mThumbnailTask.execute(object.pageNumber());
		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.mPageTextView.setText(String.valueOf(object.pageNumber()));
		holder.mPhraseTextView.setText(object.phrase().replace("\n", ""));
		
		return convertView;
	}
	
	
	
	public void refresh(ArrayList<SearchObject> list) {
		clear();
		addAll(list);
	}
	
	public void addResults(ArrayList<SearchObject> list) {
		addAll(list);
	}
	
	
	
	private static class ViewHolder {
		public GeneratePageThumbnailTask mThumbnailTask;
		public ImageView 		mThumbImageView;
		public TextView 		mPageTextView;
		public TextView 		mPhraseTextView;
	}
}
